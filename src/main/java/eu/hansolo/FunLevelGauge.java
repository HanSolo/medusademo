/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo;

import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 23.02.16
 * Time: 13:19
 */
public class FunLevelGauge extends Region {
    private static final double          PREFERRED_WIDTH  = 250;
    private static final double          PREFERRED_HEIGHT = 250;
    private static final double          MINIMUM_WIDTH    = 50;
    private static final double          MINIMUM_HEIGHT   = 50;
    private static final double          MAXIMUM_WIDTH    = 1024;
    private static final double          MAXIMUM_HEIGHT   = 1024;
    private              double          size;
    private              double          width;
    private              double          height;
    private              Circle          ring;
    private              Canvas          canvas;
    private              GraphicsContext ctx;
    private              Circle          mask;
    private              Text           text;
    private              Pane           pane;
    private              Gauge          gauge;
    private              List<Point>    particles;
    private              Color          color;
    private              Color          brighterColor;
    private              Color          darkerColor;
    private              double         density;
    private              double         friction;
    private              double         detail;
    private              long           impulseInterval;
    private              long           updateInterval;
    private              long           lastUpdateCall;
    private              long           lastImpulseCall;
    private              AnimationTimer timer;



    // ******************** Constructors **************************************
    public FunLevelGauge() {
        gauge           = GaugeBuilder.create()
                                      .minValue(0)
                                      .maxValue(1)
                                      .animated(true)
                                      .build();
        color           = Color.rgb(2, 138, 204);
        darkerColor     = color.deriveColor(0, 1, 0.8, 1);
        brighterColor   = color.deriveColor(0, 0.3, 1.5, 1);
        density         = 0.9; //0.75;
        friction        = 1.1; //1.14;
        detail          = Math.round(PREFERRED_WIDTH / 20); // no of particles used to build up the wave
        particles       = new ArrayList<>();
        impulseInterval = 1_000_000_000l;  // Interval between random impulses being inserted into the wave to keep it moving
        updateInterval  = 40_000_000l;     // Wave update interval
        lastUpdateCall  = System.nanoTime();
        lastImpulseCall = System.nanoTime();
        timer           = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastUpdateCall + updateInterval) {
                    update();
                    lastUpdateCall = now;
                }
                if (now > lastImpulseCall + impulseInterval) {
                    impulse();
                    lastImpulseCall = now;
                }
            }
        };

        // Create wave particles
        for( int i = 0 ; i < detail + 1 ; i++ ) {
            particles.add(new Point(width / (detail - 4) * (i - 2), PREFERRED_HEIGHT * (1d - gauge.getCurrentValue()),
                                    0, PREFERRED_HEIGHT * (1d - gauge.getCurrentValue()),
                                    0, Math.random() * 3,
                                    0, 0,
                                    10));
        }

        init();
        initGraphics();
        registerListeners();

        timer.start();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        ring   = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5 , PREFERRED_WIDTH * 0.5);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(color);

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx = canvas.getGraphicsContext2D();
        ctx.setFill(color);

        mask = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.45);
        canvas.setClip(mask);

        text = new Text(String.format(Locale.US, "%.0f%%", gauge.getCurrentValue()));
        text.setFill(darkerColor);
        text.setTextOrigin(VPos.CENTER);

        pane = new Pane(ring, canvas, text);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        gauge.currentValueProperty().addListener(o -> setLevel(gauge.getCurrentValue()));
    }


    // ******************** Methods *******************************************
    public double getLevel() { return gauge.getCurrentValue(); }
    public void setLevel(final double LEVEL) {
        gauge.setValue(LEVEL);
        Point p;
        for( int i = 0 ; i < detail + 1 ; i++ ) {
            p = particles.get(i);
            p.y = size * (1d - LEVEL);
            p.originalY = p.y;
        }
        text.setText(String.format(Locale.US, "%.0f%%", 100 * LEVEL));
        text.setX((size - text.getLayoutBounds().getWidth()) * 0.5);
        text.setFill(LEVEL < 0.45 ? darkerColor : brighterColor);
    }

    public Color getColor() { return color; }
    public void setColor(final Color COLOR) {
        color         = COLOR;
        darkerColor   = color.deriveColor(0, 1, 0.8, 1);
        brighterColor = color.deriveColor(0, 0.3, 1.5, 1);
        ctx.setFill(color);
        ring.setStroke(color);
        text.setFill(darkerColor);
    }

    private void impulse() {
        int forceRange = 2; // -value to +value
        insertImpulse(Math.random() * width, (Math.random() * (forceRange * 2) - forceRange ));
    }

    private void insertImpulse(final double POSITION_X, final double FORCE_Y) {
        int pos = (int) Math.round(POSITION_X / size * particles.size());
        if (pos > particles.size() - 1) return;
        Point particle = particles.get(pos);
        particle.forceY += FORCE_Y;
    }

    private void update() {
        ctx.clearRect(0, 0, size, size);
        ctx.beginPath();
        ctx.moveTo(particles.get(0).x, particles.get(0).y);
        int listSize = particles.size();
        Point currentParticle, previousParticle, nextParticle;
        for(int i = 0; i < listSize; i++) {
            currentParticle  = particles.get(i);
            previousParticle = i - 1 < 0 ? null : particles.get(i - 1);
            nextParticle     = i + 1 > listSize  - 1 ? null : particles.get(i + 1);

            if (null != previousParticle && null != nextParticle) {
                double forceY = 0;
                forceY += -density * (previousParticle.y - currentParticle.y);
                forceY += density * (currentParticle.y - nextParticle.y);
                forceY += density / 15 * (currentParticle.y - currentParticle.originalY);

                currentParticle.velocityY += -(forceY / currentParticle.mass) + currentParticle.forceY;
                currentParticle.velocityY /= friction;
                currentParticle.forceY    /= friction;
                currentParticle.y         += currentParticle.velocityY;

                ctx.quadraticCurveTo(previousParticle.x,
                                     previousParticle.y,
                                     previousParticle.x + (currentParticle.x - previousParticle.x) / 2,
                                     previousParticle.y + (currentParticle.y - previousParticle.y) / 2);
            }
        }

        ctx.lineTo(particles.get(particles.size() - 1).x, particles.get(particles.size() - 1).y);
        ctx.lineTo(size, size);
        ctx.lineTo(0, size);
        ctx.lineTo(particles.get(0).x, particles.get(0).y);
        ctx.closePath();

        ctx.fill();
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            ring.setCenterX(size * 0.5);
            ring.setCenterY(size * 0.5);
            ring.setRadius(size * 0.5);
            ring.setStrokeWidth(size * 0.025);
            ring.setStrokeType(StrokeType.INSIDE);

            mask.setCenterX(size * 0.5);
            mask.setCenterY(size * 0.5);
            mask.setRadius(size * 0.45);

            canvas.setWidth(size);
            canvas.setHeight(size);
            canvas.relocate(0, 0);

            text.setFont(Fonts.robotoMedium(size * 0.25));
            text.setY(size * 0.5);
            text.setX((size - text.getLayoutBounds().getWidth()) * 0.5);

            for( int i = 0 ; i < detail + 1 ; i++ ) {
                Point p = particles.get(i);
                p.x = size / (detail - 4) * (i - 2);
                p.y = size * (1d - gauge.getCurrentValue());

                p.originalX = p.x;
                p.originalY = p.y;
            }
        }
    }


    // ******************** Inner Classes *************************************
    class Point {
        double x;
        double y;
        double originalX;
        double originalY;
        double velocityX;
        double velocityY;
        double forceX;
        double forceY;
        double mass;


        public Point(final double X, final double Y,
                     final double ORIGINAL_X, final double ORIGINAL_Y,
                     final double VELOCITY_X, final double VELOCITY_Y,
                     final double FORCE_X, final double FORCE_Y,
                     final double MASS) {
            x         = X;
            y         = Y;
            originalX = ORIGINAL_X;
            originalY = ORIGINAL_Y;
            velocityX = VELOCITY_X;
            velocityY = VELOCITY_Y;
            forceX    = FORCE_X;
            forceY    = FORCE_Y;
            mass      = MASS;
        }
    }
}
