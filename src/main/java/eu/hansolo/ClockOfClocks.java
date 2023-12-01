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

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Clock.ClockSkinType;
import eu.hansolo.medusa.ClockBuilder;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;



/**
 * Created by hansolo on 02.02.16.
 */
public class ClockOfClocks extends Application {
    private static int     noOfNodes  = 0;
    private LocalTime[]    upperLeft  = { LocalTime.of(6, 15), LocalTime.of(8, 40), LocalTime.of(3, 15), LocalTime.of(3, 15), LocalTime.of(6, 30),
                                          LocalTime.of(6, 15), LocalTime.of(6, 15), LocalTime.of(3, 15), LocalTime.of(6, 15), LocalTime.of(6, 15) };
    private LocalTime[]    upperRight = { LocalTime.of(6, 45), LocalTime.of(6, 30), LocalTime.of(9, 45), LocalTime.of(6, 45), LocalTime.of(6, 30),
                                          LocalTime.of(9, 45), LocalTime.of(9, 45), LocalTime.of(6, 45), LocalTime.of(6, 45), LocalTime.of(6, 45) };
    private LocalTime[]    midLeft    = { LocalTime.of(12, 30), LocalTime.of(8, 40), LocalTime.of(6, 15), LocalTime.of(3, 15), LocalTime.of(12, 15),
                                          LocalTime.of(12, 15), LocalTime.of(12, 30), LocalTime.of(8, 40), LocalTime.of(12, 15), LocalTime.of(12, 15) };
    private LocalTime[]    midRight   = { LocalTime.of(12, 30), LocalTime.of(12, 30), LocalTime.of(12, 45), LocalTime.of(12, 45), LocalTime.of(12, 45),
                                          LocalTime.of(6, 45), LocalTime.of(6, 45), LocalTime.of(12, 30), LocalTime.of(12, 45), LocalTime.of(12, 30) };
    private LocalTime[]    lowerLeft  = { LocalTime.of(12, 15), LocalTime.of(8, 40), LocalTime.of(12, 15), LocalTime.of(3, 15), LocalTime.of(8, 40),
                                          LocalTime.of(3, 15), LocalTime.of(12, 15), LocalTime.of(8, 40), LocalTime.of(12, 15), LocalTime.of(3, 15) };
    private LocalTime[]    lowerRight = { LocalTime.of(12, 45), LocalTime.of(12, 00), LocalTime.of(9, 45), LocalTime.of(12, 45), LocalTime.of(12, 00),
                                          LocalTime.of(12, 45), LocalTime.of(12, 45), LocalTime.of(12, 00), LocalTime.of(12, 45), LocalTime.of(12, 45) };
    private GridPane       hourLeft;
    private GridPane       hourRight;
    private GridPane       minLeft;
    private GridPane       minRight;
    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        hourLeft      = createNumberGrid();
        hourRight     = createNumberGrid();
        minLeft       = createNumberGrid();
        minRight      = createNumberGrid();
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 10_000_000_000l) {
                    LocalTime time   = LocalTime.now();
                    int       hour   = time.getHour();
                    int       minute = time.getMinute();
                    set(hour, hourLeft, hourRight);
                    set(minute, minLeft, minRight);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        Region spacer = new Region();
        spacer.setPrefWidth(30);
        GridPane pane = new GridPane();
        pane.add(hourLeft, 0, 0);
        pane.add(hourRight, 1, 0);
        pane.add(spacer, 2, 0);
        pane.add(minLeft, 3, 0);
        pane.add(minRight,4, 0);
        pane.setHgap(10);
        pane.setPadding(new Insets(20, 20, 25, 20));

        Scene scene = new Scene(pane);

        stage.setTitle("Medusa Clock of Clocks");
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


    // ******************** Methods *******************************************
    private void set(final int T, final GridPane LEFT, final GridPane RIGHT) {
        if (T < 10) {
            set(T, LEFT);
            set(T, RIGHT);
        } else {
            set(Integer.parseInt(Integer.toString(T).substring(0, 1)), LEFT);
            set(Integer.parseInt(Integer.toString(T).substring(1)), RIGHT);
        }
    }

    private void set(final int NUMBER, final GridPane NUMBER_GRID) {
        ((Clock) NUMBER_GRID.getChildren().get(0)).setTime(ZonedDateTime.of(LocalDate.now(), upperLeft[NUMBER], ZoneId.systemDefault()));
        ((Clock) NUMBER_GRID.getChildren().get(1)).setTime(ZonedDateTime.of(LocalDate.now(), upperRight[NUMBER], ZoneId.systemDefault()));
        ((Clock) NUMBER_GRID.getChildren().get(2)).setTime(ZonedDateTime.of(LocalDate.now(), midLeft[NUMBER], ZoneId.systemDefault()));
        ((Clock) NUMBER_GRID.getChildren().get(3)).setTime(ZonedDateTime.of(LocalDate.now(), midRight[NUMBER], ZoneId.systemDefault()));
        ((Clock) NUMBER_GRID.getChildren().get(4)).setTime(ZonedDateTime.of(LocalDate.now(), lowerLeft[NUMBER], ZoneId.systemDefault()));
        ((Clock) NUMBER_GRID.getChildren().get(5)).setTime(ZonedDateTime.of(LocalDate.now(), lowerRight[NUMBER], ZoneId.systemDefault()));
    }

    private GridPane createNumberGrid() {
        GridPane grid = new GridPane();
        grid.add(createClock(), 0, 0);
        grid.add(createClock(), 1, 0);
        grid.add(createClock(), 0, 1);
        grid.add(createClock(), 1, 1);
        grid.add(createClock(), 0, 2);
        grid.add(createClock(), 1, 2);
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    private Clock createClock() {
        Clock clock = ClockBuilder.create()
                                  .skinType(ClockSkinType.FAT)
                                  .backgroundPaint(Color.WHITE)
                                  .prefSize(100, 100)
                                  .animationDuration(7500)
                                  .animated(true)
                                  .discreteMinutes(false)
                                  .discreteHours(true)
                                  .hourTickMarkColor(Color.rgb(200, 200, 200))
                                  .minuteTickMarkColor(Color.rgb(200, 200, 200))
                                  .tickLabelColor(Color.rgb(200, 200, 200))
                                  .build();
        clock.setEffect(new DropShadow(5, 0, 5, Color.rgb(0, 0, 0, 0.65)));
        return clock;
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

