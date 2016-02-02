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
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.TickMarkType;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.Random;


/**
 * User: hansolo
 * Date: 01.02.16
 * Time: 10:04
 */
public class CombinedGauges extends Application {
    private static final Random   RND = new Random();
    private static int            noOfNodes = 0;
    private        Gauge          bigGauge;
    private        Gauge          smallGauge;
    private        long           lastTimerCall;
    private        AnimationTimer timer;


    @Override public void init() {
        bigGauge = GaugeBuilder.create()
                               .foregroundBaseColor(Color.WHITE)
                               .prefSize(400, 400)
                               .startAngle(270)
                               .angleRange(270)
                               .minValue(100)
                               .maxValue(1000)
                               .tickLabelLocation(TickLabelLocation.OUTSIDE)
                               .tickLabelOrientation(TickLabelOrientation.ORTHOGONAL)
                               .minorTickMarksVisible(false)
                               .majorTickMarkType(TickMarkType.BOX)
                               .valueVisible(false)
                               .knobType(KnobType.FLAT)
                               .needleShape(NeedleShape.FLAT)
                               .needleColor(Color.WHITE)
                               .sectionsVisible(true)
                               .sections(new Section(100, 450, Color.rgb(60, 130, 145, 0.7)),
                                         new Section(650, 1000, Color.rgb(200, 100, 0, 0.7)))
                               .animated(true)
                               .build();
        smallGauge = GaugeBuilder.create()
                                 .prefSize(170, 170)
                                 .foregroundBaseColor(Color.WHITE)
                                 .minValue(0)
                                 .maxValue(10)
                                 .minorTickMarksVisible(false)
                                 .mediumTickMarkType(TickMarkType.DOT)
                                 .majorTickMarkType(TickMarkType.BOX)
                                 .tickLabelOrientation(TickLabelOrientation.ORTHOGONAL)
                                 .knobType(KnobType.FLAT)
                                 .needleShape(NeedleShape.FLAT)
                                 .needleColor(Color.WHITE)
                                 .valueVisible(false)
                                 .customTickLabelsEnabled(true)
                                 .customTickLabels("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                                 .customTickLabelFontSize(28)
                                 .animated(true)
                                 .build();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
                    bigGauge.setValue(RND.nextDouble() * 900 + 100);
                    smallGauge.setValue(RND.nextDouble() * 10);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        Pane pane = new Pane(bigGauge, smallGauge);
        pane.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        bigGauge.relocate(0, 0);
        smallGauge.relocate(0, 230);

        Scene scene = new Scene(pane);

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
                tempChildren.forEach(n -> calcNoOfNodes(n));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
