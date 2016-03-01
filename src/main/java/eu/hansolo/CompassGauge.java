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
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.NeedleBehavior;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Random;


/**
 * Created by hansolo on 02.02.16.
 */
public class CompassGauge extends Application {
    private static final Random RND   = new Random();
    private static int     noOfNodes  = 0;
    private Gauge          gauge;
    private Label          value;
    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        gauge = GaugeBuilder.create()
                            .prefSize(400, 400)
                            .borderPaint(Gauge.DARK_COLOR)
                            .minValue(0)
                            .maxValue(359)
                            .autoScale(false)
                            .startAngle(180)
                            .angleRange(360)
                            .minorTickMarksVisible(false)
                            .mediumTickMarksVisible(false)
                            .majorTickMarksVisible(false)
                            .customTickLabelsEnabled(true)
                            .customTickLabels("N", "", "", "", "", "", "", "", "",
                                              "E", "", "", "", "", "", "", "", "",
                                              "S", "", "", "", "", "", "", "", "",
                                              "W", "", "", "", "", "", "", "", "")
                            .customTickLabelFontSize(48)
                            .knobType(KnobType.FLAT)
                            .knobColor(Gauge.DARK_COLOR)
                            .needleShape(NeedleShape.FLAT)
                            .needleType(NeedleType.FAT)
                            .needleBehavior(NeedleBehavior.OPTIMIZED)
                            .tickLabelColor(Gauge.DARK_COLOR)
                            .animated(true)
                            .animationDuration(500)
                            .valueVisible(false)
                            .build();

        gauge.valueProperty().addListener(o -> {
            value.setText(String.format(Locale.US, "%.0f\u00B0", gauge.getValue()));
        });

        value = new Label("0\u00B0");
        value.setFont(Fonts.latoBold(72));
        value.setAlignment(Pos.CENTER);
        value.setPrefWidth(400);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
                    gauge.setValue(RND.nextDouble() * 359.9);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        VBox pane = new VBox(gauge, value);
        pane.setSpacing(20);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Medusa Compass");
        stage.setScene(scene);
        stage.show();

        timer.start();

        // Calculate number of nodes
        calcNoOfNodes(pane);
        System.out.println(noOfNodes + " Nodes in SceneGraph");
    }

    @Override public void stop() {
        System.exit(0);
    }


    // ******************** Misc **********************************************
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
