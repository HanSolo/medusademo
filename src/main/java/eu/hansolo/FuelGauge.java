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
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickMarkType;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * Created by hansolo on 20.01.16.
 */
public class FuelGauge extends Application {
    private static       int       noOfNodes = 0;
    private              Region    fuelIcon;
    private              Gauge     gauge;
    private              StackPane pane;


    @Override public void init() {
        fuelIcon = new Region();
        fuelIcon.getStyleClass().setAll("fuel-icon");

        gauge = GaugeBuilder.create()
                            .skinType(SkinType.HORIZONTAL)
                            .prefSize(500, 250)
                            .knobColor(Color.rgb(0, 0, 0))
                            .foregroundBaseColor(Color.rgb(249, 249, 249))
                            .animated(true)
                            .shadowsEnabled(true)
                            .valueVisible(false)
                            //.title("FUEL")
                            .needleColor(Color.rgb(255, 10, 1))
                            .needleShape(NeedleShape.ROUND)
                            .needleSize(NeedleSize.THICK)
                            .minorTickMarksVisible(false)
                            .mediumTickMarksVisible(false)
                            //.majorTickMarkType(TickMarkType.TRIANGLE)
                            .sectionsVisible(true)
                            .sections(new Section(0, 0.2, Color.rgb(255, 10, 1)))
                            .minValue(0)
                            .maxValue(1)
                            .angleRange(90)
                            .customTickLabelsEnabled(true)
                            .customTickLabels("E", "", "", "", "", "1/2", "", "", "", "", "F")
                            .build();

        pane = new StackPane(fuelIcon, gauge);
        pane.setPadding(new Insets(10));
        LinearGradient gradient = new LinearGradient(0, 0, 0, pane.getLayoutBounds().getHeight(),
                                                     false, CycleMethod.NO_CYCLE,
                                                     new Stop(0.0, Color.rgb(38, 38, 38)),
                                                     new Stop(1.0, Color.rgb(15, 15, 15)));
        pane.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(FuelGauge.class.getResource("fuel-gauge.css").toExternalForm());

        stage.setTitle("Medusa");
        stage.setScene(scene);
        stage.show();

        // Calculate number of nodes
        calcNoOfNodes(gauge);
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
