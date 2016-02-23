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
import eu.hansolo.medusa.events.UpdateEvent;
import eu.hansolo.medusa.events.UpdateEventListener;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineJoin;
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
    private              Text            text;
    private              Pane            pane;
    private              Gauge           gauge;
    private              Options         opt;
    private              List<Point>     points;
    private              Color           color;
    private              Color           brighterColor;
    private              Color           darkerColor;
    private              long            lastTimerCall;
    private              AnimationTimer  timer;



    // ******************** Constructors **************************************
    public FunLevelGauge() {
        gauge         = GaugeBuilder.create()
                                    .minValue(0)
                                    .maxValue(1)
                                    .animated(true)
                                    .animationDuration(3000)
                                    .build();
        color         = Color.rgb(2, 138, 204);
        darkerColor   = color.deriveColor(0, 1, 0.8, 1);
        brighterColor = color.deriveColor(0, 0.3, 1.5, 1);

        final int    COUNT        = 3;  // No of waves
        final double RANGE_X      = 0;  // Range the wave vary in x-direction
        final double RANGE_Y      = 10; // Range the wave vary in y-direction
        final double DURATION_MIN = 30; // Minimum duration of the wave movement
        final double DURATION_MAX = 80; // Maximum duration of the wave movement
        final double THICKNESS    = 5;  // LineWidth that is used to stroke the surface. Has to be enabled in the renderShape() method
        opt                       = new Options(COUNT, RANGE_X, RANGE_Y,
                                                DURATION_MIN, DURATION_MAX,
                                                THICKNESS, darkerColor,
                                                gauge.getValue(), true);

        points        = new ArrayList<>();
        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 16_666_666l) {
                    loop();
                    lastTimerCall = now;
                }
            }
        };

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

        canvas = new Canvas(PREFERRED_WIDTH * 0.9, PREFERRED_HEIGHT * 0.9);
        canvas.relocate(PREFERRED_WIDTH * 0.1, PREFERRED_HEIGHT * 0.1);
        ctx    = canvas.getGraphicsContext2D();
        ctx.setLineJoin(StrokeLineJoin.ROUND);
        ctx.setLineWidth(opt.thickness);
        ctx.setStroke(darkerColor);

        mask = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.45);
        canvas.setClip(mask);

        text = new Text(String.format(Locale.US, "%.0f%%", gauge.getCurrentValue()));
        text.setFill(darkerColor);
        //text.setStroke(Color.WHITE);
        text.setTextOrigin(VPos.CENTER);

        pane = new Pane(ring, canvas, text);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        gauge.currentValueProperty().addListener(o -> redraw());
    }


    // ******************** Methods *******************************************
    public double getLevel() { return gauge.getValue(); }
    public void setLevel(final double LEVEL) { gauge.setValue(LEVEL); }

    public Color getColor() { return color; }
    public void setColor(final Color COLOR) {
        color         = COLOR;
        darkerColor   = color.deriveColor(0, 1, 0.8, 1);
        brighterColor = color.deriveColor(0, 0.3, 1.5, 1);
        ring.setStroke(color);
        ctx.setStroke(darkerColor);
        text.setFill(darkerColor);
        opt.strokeColor = darkerColor;
        redraw();
    }

    private double rand(double min, double max) {
        return Math.floor( (Math.random() * (max - min + 1) ) + min);
    }

    private double ease(double t, double b, double c, double d) {
        if ((t/=d/2) < 1) return c/2*t*t + b;
        return -c/2 * ((--t)*(t-2) - 1) + b;
    }

    private void updatePoints() { points.forEach(Point::update); }

    private void renderShape() {
        if (points.isEmpty()) return;
        ctx.setFill(color);
        ctx.beginPath();
        int pointCount = points.size();
        ctx.moveTo(points.get(0).x, points.get(0).y);;
        for (int i = 0; i < pointCount - 1 ; i++) {
            double c = (points.get(i).x + points.get(i+1).x) / 2;
            double d = (points.get(i).y + points.get(i+1).y) / 2;
            ctx.quadraticCurveTo(points.get(i).x, points.get(i).y, c, d);
        }
        ctx.lineTo(-opt.rangeX - opt.thickness, size + opt.thickness);
        ctx.lineTo(size + opt.rangeX + opt.thickness, size + opt.thickness);
        ctx.closePath();
        ctx.fill();
        //ctx.stroke(); // enable to see a line on top of the surface
    }

    private void loop() {
        ctx.clearRect(0, 0, size, size);
        updatePoints();
        renderShape();
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            timer.stop();

            pane.setMaxSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            ring.setCenterX(size * 0.5);
            ring.setCenterY(size * 0.5);
            ring.setRadius(size * 0.5);
            ring.setStrokeWidth(size * 0.025);
            ring.setStrokeType(StrokeType.INSIDE);

            canvas.setWidth(size);
            canvas.setHeight(size);
            canvas.relocate(0, 0);

            mask.setCenterX(size * 0.5);
            mask.setCenterY(size * 0.5);
            mask.setRadius(size * 0.45);

            text.setFont(Fonts.robotoMedium(size * 0.25));
            text.setY(size * 0.5);
            text.setX((size - text.getLayoutBounds().getWidth()) * 0.5);

            timer.start();
            redraw();
        }
    }

    private void redraw() {
        double currentValue = gauge.getCurrentValue();
        opt.level = currentValue;
        text.setText(String.format(Locale.US, "%.0f%%", 100 * currentValue));
        text.setX((size - text.getLayoutBounds().getWidth()) * 0.5);
        text.setFill(currentValue < 0.45 ? darkerColor : brighterColor);
        points.clear();
        double spacing = (size + (opt.rangeX * 2)) / (opt.count-1);
        for (int i = opt.count + 2 ; i >= 0 ; i--) { points.add(new Point((spacing * (i - 1)) - opt.rangeX, (size * 0.9) - (size * 0.9 * opt.level) + (size * 0.05))); }
    }


    // ******************** Inner Classes *************************************
    private class Options {
        int         count;
        double      rangeX;
        double      rangeY;
        double      durationMin;
        double      durationMax;
        double      thickness;
        Color       strokeColor;
        double      level;
        boolean     curved;

        public Options(final int COUNT, final double RANGE_X, final double RANGE_Y,
                       final double DURATION_MIN, final double DURATION_MAX,
                       final double THICKNESS, final Color STROKE_COLOR, final double LEVEL,
                       final boolean CURVED) {
            count       = COUNT;
            rangeX      = RANGE_X;
            rangeY      = RANGE_Y;
            durationMin = DURATION_MIN;
            durationMax = DURATION_MAX;
            thickness   = THICKNESS;
            strokeColor = STROKE_COLOR;
            level       = LEVEL;
            curved      = CURVED;
        }
    }

    private class Point {
        double initialX;
        double initialY;
        double targetX;
        double targetY;
        double anchorX;
        double anchorY;
        double x;
        double y;
        double tick;
        double duration;

        public Point(final double X, final double Y) {
            anchorX = X;
            anchorY = Y;
            x       = X;
            y       = Y;
            setTarget();
        }

        public void setTarget() {
            initialX = x;
            initialY = y;
            targetX  = anchorX + rand(0, opt.rangeX * 2) - opt.rangeX;
            targetY  = anchorY + rand(0, opt.rangeY * 2) - opt.rangeY;
            tick     = 0;
            duration = rand(opt.durationMin, opt.durationMax);
        }

        public void update() {
            double dx = targetX - x;
            double dy = targetY - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (Double.compare(Math.abs(dist), 0) <= 0) {
                setTarget();
            } else {
                double t = tick;
                double b = initialY;
                double c = targetY - initialY;
                double d = duration;
                y = ease(t, b, c, d);

                b = initialX;
                c = targetX - initialX;
                d = duration;
                x = ease(t, b, c, d);

                tick++;
            }
        }
    }
}
