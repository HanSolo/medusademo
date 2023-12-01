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

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.List;


/**
 * Created by hansolo on 03.02.16.
 */
public class CustomGaugeSkin extends SkinBase<Gauge> implements Skin<Gauge> {
    private static final double          PREFERRED_WIDTH  = 20;
    private static final double          PREFERRED_HEIGHT = 229;
    private static final double          MINIMUM_WIDTH    = 10;
    private static final double          MINIMUM_HEIGHT   = 115;
    private static final double          MAXIMUM_WIDTH    = 1024;
    private static final double          MAXIMUM_HEIGHT   = 1024;
    private static final double          ASPECT_RATIO     = 11.45;
    private static final double          NO_OF_LEDS       = 20;
    private              double          width;
    private              double          height;
    private              Canvas          backgroundCanvas;
    private              GraphicsContext backgroundCtx;
    private              Canvas          foregroundCanvas;
    private              GraphicsContext foregroundCtx;
    private              Pane            pane;
    private              double          range;
    private              double          stepSize;
    private              Color           barBackgroundColor;
    private              Color           barColor;
    private              boolean         sectionsVisible;
    private              List<Section>   sections;
    private              double          ledSize;
    private              double          ledSpacer;
    private              double          ledBorder;
    private              InnerShadow     ledInnerShadow;
    private              DropShadow      ledDropShadow;


    // ******************** Constructors **************************************
    public CustomGaugeSkin(Gauge gauge) {
        super(gauge);
        if (gauge.isAutoScale()) gauge.calcAutoScale();
        range              = gauge.getRange();
        stepSize           = NO_OF_LEDS / range;
        barBackgroundColor = gauge.getBarBackgroundColor();
        barColor           = gauge.getBarColor();
        sectionsVisible    = gauge.getSectionsVisible();
        sections           = gauge.getSections();
        ledSize            = PREFERRED_WIDTH * 0.5;
        ledSpacer          = PREFERRED_WIDTH * 0.05;
        ledBorder          = PREFERRED_WIDTH * 0.25;

        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getSkinnable().getPrefWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getHeight(), 0.0) <= 0) {
            if (getSkinnable().getPrefWidth() < 0 && getSkinnable().getPrefHeight() < 0) {
                getSkinnable().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getSkinnable().getMinWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMinHeight(), 0.0) <= 0) {
            getSkinnable().setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getSkinnable().getMaxWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMaxHeight(), 0.0) <= 0) {
            getSkinnable().setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        backgroundCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        backgroundCtx    = backgroundCanvas.getGraphicsContext2D();

        foregroundCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        foregroundCtx    = foregroundCanvas.getGraphicsContext2D();

        ledInnerShadow   = new InnerShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 0.2 * PREFERRED_WIDTH, 0, 0, 0);
        ledDropShadow    = new DropShadow(BlurType.TWO_PASS_BOX, getSkinnable().getBarColor(), 0.3 * PREFERRED_WIDTH, 0, 0, 0);

        pane = new Pane(backgroundCanvas, foregroundCanvas);
        pane.setBorder(new Border(new BorderStroke(getSkinnable().getBorderPaint(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        pane.setBackground(new Background(new BackgroundFill(getSkinnable().getBackgroundPaint(), CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        getSkinnable().widthProperty().addListener(o -> handleEvents("RESIZE"));
        getSkinnable().heightProperty().addListener(o -> handleEvents("RESIZE"));
        getSkinnable().setOnUpdate(e -> handleEvents(e.eventType.name()));
        getSkinnable().currentValueProperty().addListener(o -> setBar(getSkinnable().getCurrentValue()));
    }


    // ******************** Methods *******************************************
    private void handleEvents(final String EVENT_TYPE) {
        if ("RESIZE".equals(EVENT_TYPE)) {
            resize();
            redraw();
        } else if ("REDRAW".equals(EVENT_TYPE)) {
            redraw();
        } else if ("VISIBILITY".equals(EVENT_TYPE)) {
            sectionsVisible = getSkinnable().getSectionsVisible();
            redraw();
        } else if ("RECALC".equals(EVENT_TYPE)) {
            range    = getSkinnable().getRange();
            range    = getSkinnable().getRange();
            sections = getSkinnable().getSections();
            stepSize = NO_OF_LEDS / range;
            redraw();
        } else if ("SECTIONS".equals(EVENT_TYPE)) {
            sections = getSkinnable().getSections();
            redraw();
        }
    }


    // ******************** Canvas ********************************************
    private void drawBackground() {
        backgroundCtx.clearRect(0, 0, width, height);
        backgroundCtx.setFill(barBackgroundColor);
        int listSize  = sections.size();
        Section currentSection;
        for (int i = 0 ; i < NO_OF_LEDS ; i++) {
            if (sectionsVisible) {
                double value = (i + 1) / stepSize;
                for (int j = 0 ; j < listSize ; j++) {
                    currentSection = sections.get(j);
                    if (currentSection.contains(value)) {
                        backgroundCtx.setFill(currentSection.getColor().darker().darker());
                        break;
                    } else {
                        backgroundCtx.setFill(barBackgroundColor);
                    }
                }
            }
            backgroundCtx.save();
            backgroundCtx.setEffect(ledInnerShadow);
            backgroundCtx.fillOval(ledBorder, height - ledSize - (i * (ledSpacer + ledSize)) - ledBorder, ledSize, ledSize);
            backgroundCtx.restore();
        }
    }

    private void setBar(final double VALUE) {
        foregroundCtx.clearRect(0, 0, width, height);
        int            activeLeds = (int) Math.floor(VALUE * stepSize);
        int            listSize   = sections.size();
        Section        currentSection;
        RadialGradient gradient;
        foregroundCtx.setFill(barColor);
        for (int i = 0 ; i < activeLeds ; i++) {
            if (sectionsVisible) {
                double value = (i + 1) / stepSize;
                for (int j = 0 ; j < listSize ; j++) {
                    currentSection = sections.get(j);
                    if (currentSection.contains(value)) {
                        gradient = new RadialGradient(0, 0, ledSize, height - ledSize - (i * (ledSpacer + ledSize)), ledBorder, false , CycleMethod.NO_CYCLE, new Stop(0, currentSection.getColor()), new Stop(0.55, currentSection.getColor()), new Stop(0.85, currentSection.getColor().darker()), new Stop(1, Color.rgb(0, 0, 0, 0.65)));
                        foregroundCtx.setFill(gradient);
                        ledDropShadow.setColor(currentSection.getColor());
                        break;
                    } else {
                        gradient = new RadialGradient(0, 0, ledSize, height - ledSize - (i * (ledSpacer + ledSize)), ledBorder, false , CycleMethod.NO_CYCLE, new Stop(0, barColor), new Stop(0.55, barColor), new Stop(0.85, barColor.darker()), new Stop(1, Color.rgb(0, 0, 0, 0.65)));
                        foregroundCtx.setFill(gradient);
                        ledDropShadow.setColor(barColor);
                    }
                }
            } else {
                gradient = new RadialGradient(0, 0, ledSize, height - ledSize - (i * (ledSpacer + ledSize)), ledBorder, false , CycleMethod.NO_CYCLE, new Stop(0, barColor), new Stop(0.55, barColor), new Stop(0.85, barColor.darker()), new Stop(1, Color.rgb(0, 0, 0, 0.65)));
                foregroundCtx.setFill(gradient);
                ledDropShadow.setColor(barColor);
            }
            foregroundCtx.save();
            foregroundCtx.setEffect(ledDropShadow);
            foregroundCtx.fillOval(ledBorder, height - ledSize - (i * (ledSpacer + ledSize)) - ledBorder, ledSize, ledSize);
            foregroundCtx.restore();
        }
    }


    // ******************** Resizing ******************************************
    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(getSkinnable().getBackgroundPaint(), CornerRadii.EMPTY, Insets.EMPTY)));
        sectionsVisible    = getSkinnable().getSectionsVisible();
        barBackgroundColor = getSkinnable().getBarBackgroundColor();
        barColor           = getSkinnable().getBarColor();
        drawBackground();
        setBar(getSkinnable().getCurrentValue());
    }

    private void resize() {
        width  = getSkinnable().getWidth() - getSkinnable().getInsets().getLeft() - getSkinnable().getInsets().getRight();
        height = getSkinnable().getHeight() - getSkinnable().getInsets().getTop() - getSkinnable().getInsets().getBottom();

        if (ASPECT_RATIO * width > height) {
            width = 1 / (ASPECT_RATIO / height);
        } else if (1 / (ASPECT_RATIO / height) > width) {
            height = ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.relocate((getSkinnable().getWidth() - width) * 0.5, (getSkinnable().getHeight() - height) * 0.5);

            ledInnerShadow.setRadius(0.2 * width);
            ledDropShadow.setRadius(0.25 * width);

            ledSize   = width * 0.5;
            ledSpacer = width * 0.05;
            ledBorder = 0.25 * width;

            backgroundCanvas.setWidth(width);
            backgroundCanvas.setHeight(height);

            foregroundCanvas.setWidth(width);
            foregroundCanvas.setHeight(height);
        }
    }
}
