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
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.Random;


/**
 * User: hansolo
 * Date: 03.02.16
 * Time: 11:18
 */
public class CustomGaugeSkinDemo extends Application {
    private static final Random         RND       = new Random();
    private static       int            noOfNodes = 0;
    private              Gauge          gauge0;
    private              Gauge          gauge1;
    private              Gauge          gauge2;
    private              Gauge          gauge3;
    private              Gauge          gauge4;
    private              Gauge          gauge5;
    private              Gauge          gauge6;
    private              Gauge          gauge7;
    private              Gauge          gauge8;
    private              Gauge          gauge9;
    private              long           lastTimerCall;
    private              AnimationTimer timer;


    @Override public void init() {
        gauge0 = createBar();
        gauge1 = createBar();
        gauge2 = createBar();
        gauge3 = createBar();
        gauge4 = createBar();
        gauge5 = createBar();
        gauge6 = createBar();
        gauge7 = createBar();
        gauge8 = createBar();
        gauge9 = createBar();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 50_000_000l) {
                    gauge0.setValue(RND.nextInt(100));
                    gauge1.setValue(RND.nextInt(100));
                    gauge2.setValue(RND.nextInt(100));
                    gauge3.setValue(RND.nextInt(100));
                    gauge4.setValue(RND.nextInt(100));
                    gauge5.setValue(RND.nextInt(100));
                    gauge6.setValue(RND.nextInt(100));
                    gauge7.setValue(RND.nextInt(100));
                    gauge8.setValue(RND.nextInt(100));
                    gauge9.setValue(RND.nextInt(100));
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        HBox pane = new HBox(gauge0, gauge1, gauge2, gauge3, gauge4, gauge5, gauge6, gauge7, gauge8, gauge9);
        pane.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Medusa Custom Gauge Skin");
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

    private Gauge createBar() {
        Gauge gauge = GaugeBuilder.create()
                                  .backgroundPaint(Gauge.DARK_COLOR)
                                  .barBackgroundColor(Color.DARKRED)
                                  .barColor(Color.RED)
                                  .minValue(0)
                                  .maxValue(100)
                                  .sectionsVisible(true)
                                  .sections(new Section(0, 70, Color.LIME),
                                            new Section(70,85, Color.YELLOW),
                                            new Section(85, 100, Color.RED))
                                  .build();

        gauge.setSkin(new CustomGaugeSkin(gauge));
        return gauge;
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
