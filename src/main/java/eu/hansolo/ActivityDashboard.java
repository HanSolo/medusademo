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

import eu.hansolo.colors.MaterialDesign;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.skins.SlimSkin;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


/**
 * Created by hansolo on 20.01.16.
 */
public class ActivityDashboard extends Application {
    private static int      noOfNodes = 0;
    private        Gauge    steps;
    private        Gauge    distance;
    private        Gauge    actvCalories;
    private        Gauge    foodCalories;
    private        Gauge    weight;
    private        Gauge    bodyFat;
    private        GridPane pane;


    @Override public void init() {
        GaugeBuilder builder = GaugeBuilder.create()
                                           .skinType(SkinType.SLIM)
                                           .barBackgroundColor(MaterialDesign.GREY_800.get())
                                           .animated(true)
                                           .animationDuration(1000);
        steps        = builder.decimals(0).maxValue(10000).unit("STEPS").build();
        distance     = builder.decimals(2).maxValue(10).unit("KM").build();
        actvCalories = builder.decimals(0).maxValue(2200).unit("KCAL").build();
        foodCalories = builder.decimals(0).maxValue(2200).unit("KCAL").build();
        weight       = builder.decimals(1).maxValue(85).unit("KG").build();
        bodyFat      = builder.decimals(1).maxValue(20).unit("%").build();

        VBox stepsBox        = getVBox("STEPS", MaterialDesign.CYAN_300.get(), steps);
        VBox distanceBox     = getVBox("DISTANCE", MaterialDesign.ORANGE_300.get(), distance);
        VBox actvCaloriesBox = getVBox("ACTIVE CALORIES", MaterialDesign.RED_300.get(), actvCalories);
        VBox foodCaloriesBox = getVBox("FOOD", MaterialDesign.GREEN_300.get(), foodCalories);
        VBox weightBox       = getVBox("WEIGHT", MaterialDesign.DEEP_PURPLE_300.get(), weight);
        VBox bodyFatBox      = getVBox("BODY FAT", MaterialDesign.PURPLE_300.get(), bodyFat);

        pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(15);
        pane.setBackground(new Background(new BackgroundFill(MaterialDesign.GREY_900.get(), CornerRadii.EMPTY, Insets.EMPTY)));
        pane.add(stepsBox, 0, 0);
        pane.add(distanceBox, 1, 0);
        pane.add(actvCaloriesBox, 0, 2);
        pane.add(foodCaloriesBox, 1, 2);
        pane.add(weightBox, 0, 4);
        pane.add(bodyFatBox, 1, 4);
    }

    private VBox getVBox(final String TEXT, final Color COLOR, final Gauge GAUGE) {
        Rectangle bar = new Rectangle(200, 3);
        bar.setArcWidth(6);
        bar.setArcHeight(6);
        bar.setFill(COLOR);

        Label label = new Label(TEXT);
        label.setTextFill(COLOR);
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(0, 0, 10, 0));

        GAUGE.setBarColor(COLOR);

        VBox vBox = new VBox(bar, label, GAUGE);
        vBox.setSpacing(3);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(pane);

        stage.setTitle("Medusa Activity Dashboard");
        stage.setScene(scene);
        stage.show();

        steps.setValue(8542);
        distance.setValue(9.2);
        actvCalories.setValue(1341);
        foodCalories.setValue(923);
        weight.setValue(78.8);
        bodyFat.setValue(14.03);

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
