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
import eu.hansolo.medusa.Section;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/**
 * Created by hansolo on 20.01.16.
 */
public class PollenDashboard extends Application {
    private static int   noOfNodes = 0;
    private        Gauge ragweed;
    private        Gauge birch;
    private        Gauge grass;
    private        Gauge olive;
    private        Gauge combined;
    private        VBox  pane;


    @Override public void init() {
        GaugeBuilder builder = GaugeBuilder.create()
                                           .skinType(SkinType.INDICATOR)
                                           .prefWidth(150)
                                           .animated(true)
                                           .decimals(0)
                                           .sectionsVisible(true)
                                           .sections(new Section(0, 33, Color.rgb(34, 180, 11)),
                                                     new Section(33, 66, Color.rgb(255, 146, 0)),
                                                     new Section(66, 100, Color.rgb(255, 0, 39)));
        ragweed  = builder.build();
        birch    = builder.build();
        grass    = builder.build();
        olive    = builder.build();
        combined = builder.build();

        HBox ragweedBox  = getHBox("RAGWEED", ragweed);
        HBox birchBox    = getHBox("BIRCH", birch);
        HBox grassBox    = getHBox("GRASS", grass);
        HBox oliveBox    = getHBox("OLIVE", olive);
        HBox combinedBox = getHBox("COMBINED", combined);

        pane = new VBox(ragweedBox, new Separator(Orientation.HORIZONTAL),
                        birchBox, new Separator(Orientation.HORIZONTAL),
                        grassBox, new Separator(Orientation.HORIZONTAL),
                        oliveBox, new Separator(Orientation.HORIZONTAL),
                        combinedBox);
        pane.setPadding(new Insets(20, 20, 0, 20));
        pane.setSpacing(10);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(242, 242, 242), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private HBox getHBox(final String TEXT, final Gauge GAUGE) {
        Label label = new Label(TEXT);
        label.setPrefWidth(150);
        label.setFont(Font.font(26));
        label.setTextFill(MaterialDesign.GREY_800.get());
        label.setAlignment(Pos.CENTER_LEFT);
        label.setPadding(new Insets(0, 10, 0, 0));

        GAUGE.setBarBackgroundColor(Color.rgb(232, 231, 223));
        GAUGE.setAnimated(true);
        GAUGE.setAnimationDuration(1000);

        HBox hBox = new HBox(label, GAUGE);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(pane);

        stage.setTitle("Medusa Pollen Dashboard");
        stage.setScene(scene);
        stage.show();

        ragweed.setValue(70);
        birch.setValue(45);
        grass.setValue(15);

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
