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
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/**
 * Created by hansolo on 25.01.16.
 */
public class KpiDashboard extends Application {
    private static final Color RED    = Color.rgb(255, 0, 39, 0.3);
    private static final Color YELLOW = Color.rgb(255, 146, 0, 0.3);
    private static final Color GREEN  = Color.rgb(34, 180, 11, 0.3);
    private static final Color GRAY   = Color.rgb(110, 110, 110);
    private static int   noOfNodes    = 0;
    private        Gauge revenue;
    private        Gauge profit;
    private        Gauge sales;
    private        VBox  pane;


    @Override public void init() {
        Label title = new Label("December 2015");
        title.setFont(Font.font(24));

        revenue = getBulletChart("Revenue", "($'000)", 600, 500, new Section(0, 200, RED), new Section(200, 400, YELLOW), new Section(400, 600, GREEN));
        profit  = getBulletChart("Profit", "($'000)", 100, 70, new Section(0, 20, RED), new Section(20, 60, YELLOW), new Section(60, 100, GREEN));
        sales   = getBulletChart("Sales", "(unit)", 1000, 700, new Section(0, 300, RED), new Section(300, 500, YELLOW), new Section(500, 1000, GREEN));

        HBox legend = new HBox(getLegendBox(RED, "Poor", 10),
                               getLegendBox(YELLOW, "Average", 10),
                               getLegendBox(GREEN, "Good", 10),
                               getLegendBox(GRAY, "Target", 5));
        legend.setSpacing(20);
        legend.setAlignment(Pos.CENTER);

        pane = new VBox(title, revenue, profit, sales, legend);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(20, 20, 20, 20));
        pane.setSpacing(10);
    }

    private Gauge getBulletChart(final String TITLE, final String UNIT,
                                 final double MAX_VALUE, final double THRESHOLD,
                                 final Section... SECTIONS) {
        return GaugeBuilder.create()
                           .skinType(SkinType.BULLET_CHART)
                           .animated(true)
                           .thresholdColor(GRAY)
                           .title(TITLE)
                           .unit(UNIT)
                           .maxValue(MAX_VALUE)
                           .threshold(THRESHOLD)
                           .sectionsVisible(true)
                           .sections(SECTIONS)
                           .build();
    }

    private HBox getLegendBox(final Color COLOR, final String TEXT, final double HEIGHT) {
        Rectangle rect = new Rectangle(20, HEIGHT);
        rect.setFill(COLOR);
        Label label = new Label(TEXT);
        label.setFont(Font.font(10));

        HBox hBox = new HBox(rect, label);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(2);
        return hBox;
    }


    @Override public void start(Stage stage) {
        Scene scene = new Scene(pane);

        stage.setTitle("Medusa KPI Dashboard");
        stage.setScene(scene);
        stage.show();

        revenue.setValue(104);
        profit.setValue(70);
        sales.setValue(966);

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

