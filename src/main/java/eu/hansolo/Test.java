/*
 * Copyright (c) 2019 by Gerrit Grunwald
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
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.tools.Helper;
import eu.hansolo.templates.skin.Slim1Skin;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalDouble;


/**
 * User: hansolo
 * Date: 2019-01-24
 * Time: 13:52
 */
public class Test extends Application {
    private static int   noOfNodes = 0;
    private        Gauge gauge;

    @Override public void init() {
        gauge = GaugeBuilder.create().skinType(SkinType.SLIM)
                            .decimals(0)
                            .minValue(0)
                            .maxValue(399)
                            .autoScale(false)
                            .unit("Tickets")
                            .value(201)
                            .animated(true)
                            .build();

        gauge.setSkin(new Slim1Skin(gauge));

        System.out.println("------- calcNiceNumber -----------");
        System.out.println(calcNiceNumber(399, true));

        System.out.println("------- calcAutoScale ------------");
        calcAutoScale(gauge);

        System.out.println("------- getNiceScale ----------");
        double[] param = getNiceScale(0, 402, 10);
        System.out.println("nice min value: " + param[0]);
        System.out.println("nice max value: " + param[1]);
        System.out.println("nice range    : " + param[2]);
        System.out.println("nice step     : " + param[3]);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(gauge);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 50), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();

        // Calculate number of nodes
        calcNoOfNodes(pane);
        System.out.println(noOfNodes + " Nodes in SceneGraph");

    }

    @Override public void stop() {
        System.exit(0);
    }


    // ******************** Misc **********************************************
    public static final double calcNiceNumber(final double RANGE, final boolean ROUND) {
        double niceFraction;
        double exponent = Math.floor(Math.log10(RANGE));   // exponent of range
        double fraction = RANGE / Math.pow(10, exponent);  // fractional part of range

        if (ROUND) {
            if (Double.compare(fraction, 1.5) < 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 3)  < 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 7) < 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (Double.compare(fraction, 1) <= 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 2) <= 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 5) <= 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    public void calcAutoScale(final Gauge gauge) {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceRange         = Helper.calcNiceNumber(gauge.getRange(), false);
        double majorTickSpace    = Helper.calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        double niceMinValue      = (Math.floor(gauge.getMinValue() / gauge.getMajorTickSpace()) * gauge.getMajorTickSpace());
        double niceMaxValue      = (Math.ceil(gauge.getMaxValue() / gauge.getMajorTickSpace()) * gauge.getMajorTickSpace());
        double minorTickSpace    = Helper.calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);

        System.out.println("minorTickSpace: " + minorTickSpace);
        System.out.println("majorTickSpace: " + majorTickSpace);

        System.out.println("min           : " + gauge.getMinValue());
        System.out.println("max           : " + gauge.getMaxValue());

        System.out.println("nice min value: " + niceMinValue);
        System.out.println("nice max value: " + niceMaxValue);
        System.out.println("niceRange     : " + niceRange);
    }


    /**
     * Calculates nice minValue, maxValue and stepSize for given MIN and MAX values
     * @param MIN
     * @param MAX
     * @return array of doubles with [niceMin, niceMax, niceRange, niceStep]
     */
    public static final double[] getNiceScale(final double MIN, final double MAX) {
        return getNiceScale(MIN, MAX, 20);
    }
    /**
     * Calculates nice minValue, maxValue and stepSize for given MIN and MAX values
     * @param MIN
     * @param MAX
     * @param MAX_NO_OF_TICKS
     * @return array of doubles with [niceMin, niceMax, niceRange, niceStep]
     */
    public static final double[] getNiceScale(final double MIN, final double MAX, final int MAX_NO_OF_TICKS) {
        // Minimal increment to avoid round extreme values to be on the edge of the chart
        double minimum = MIN;
        double maximum = MAX;
        double epsilon = (MAX - MIN) / 1e6;
        maximum += epsilon;
        minimum -= epsilon;
        double range = maximum - minimum;

        // Target number of values to be displayed on the Y axis (it may be less)
        int stepCount = MAX_NO_OF_TICKS;
        // First approximation
        double roughStep = range / (stepCount - 1);

        // Set best niceStep for the range
        //double[] goodNormalizedSteps = { 1, 1.5, 2, 2.5, 5, 7.5, 10 }; // keep the 10 at the end
        double[] goodNormalizedSteps = { 1, 2, 5, 10 };

        // Normalize rough niceStep to find the normalized one that fits best
        double stepPower          = Math.pow(10, -Math.floor(Math.log10(Math.abs(roughStep))));
        double normalizedStep     = roughStep * stepPower;
        double goodNormalizedStep = Arrays.stream(goodNormalizedSteps).filter(n -> Double.compare(n, normalizedStep) >= 0).findFirst().getAsDouble();
        double niceStep           = goodNormalizedStep / stepPower;

        // Determine the scale limits based on the chosen niceStep.
        double niceMin = minimum < 0 ? Math.floor(minimum / niceStep) * niceStep : Math.ceil(minimum / niceStep) * niceStep;
        double niceMax = maximum < 0 ? Math.floor(maximum / niceStep) * niceStep : Math.ceil(maximum / niceStep) * niceStep;

        if (MIN % niceStep == 0) { niceMin = MIN; }
        if (MAX % niceStep == 0) { niceMax = MAX; }

        double niceRange = niceMax - niceMin;

        return new double[] { niceMin, niceMax, niceRange, niceStep };
    }


    private static void calcNoOfNodes(Node node) {
        if (node instanceof Parent) {
            if (((Parent) node).getChildrenUnmodifiable().size() != 0) {
                ObservableList<Node> tempChildren = ((Parent) node).getChildrenUnmodifiable();
                noOfNodes += tempChildren.size();
                for (Node n : tempChildren) { calcNoOfNodes(n); }
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
