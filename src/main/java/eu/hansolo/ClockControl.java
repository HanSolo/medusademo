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

import eu.hansolo.medusa.Alarm;
import eu.hansolo.medusa.Alarm.Repetition;
import eu.hansolo.medusa.AlarmBuilder;
import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.ClockBuilder;
import eu.hansolo.medusa.Command;
import eu.hansolo.medusa.TimeSection;
import eu.hansolo.medusa.TimeSectionBuilder;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.ZonedDateTime;


/**
 * Created by hansolo on 31.01.16.
 */
public class ClockControl extends Application{
    private static int noOfNodes = 0;
    private Clock clock1;
    private Clock clock2;

    @Override public void init() {
        TimeSection gardenLightOn = TimeSectionBuilder.create()
                                                      .start(LocalTime.of(19, 00, 00))
                                                      .stop(LocalTime.of(22, 00, 00))
                                                      .color(Color.rgb(200, 100, 0, 0.5))
                                                      .onTimeSectionEntered(event -> System.out.println("Garden light on"))
                                                      .onTimeSectionLeft(event -> System.out.println("Garden light off"))
                                                      .build();

        TimeSection lunchBreak = TimeSectionBuilder.create()
                                                   .start(LocalTime.of(12, 00, 00))
                                                   .stop(LocalTime.of(13, 00, 00))
                                                   .color(Color.rgb(200, 0, 0, 0.5))
                                                   .build();


        clock1 = ClockBuilder.create()
                             .titleVisible(true)
                             .title("Sections")
                             .sectionsVisible(true)
                             .sections(gardenLightOn)
                             .checkSectionsForValue(true)
                             .areasVisible(true)
                             .areas(lunchBreak)
                             .secondsVisible(true)
                             .running(true)
                             .build();


        LightOn  lightOn  = new LightOn();
        LightOff lightOff = new LightOff();

        Alarm alarmLightOn =
            AlarmBuilder.create()
                        .time(ZonedDateTime.now().plusSeconds(5))
                        .repetition(Repetition.ONCE)
                        .text("Light On")
                        .command(lightOn)
                        .build();

        Alarm alarmLightOff =
            AlarmBuilder.create()
                        .time(ZonedDateTime.now().plusSeconds(10))
                        .repetition(Repetition.ONCE)
                        .text("Light off")
                        .command(lightOff)
                        .build();

        clock2 = ClockBuilder.create()
                             .titleVisible(true)
                             .title("Alarms")
                             .alarmsEnabled(true)
                             .alarms(alarmLightOn, alarmLightOff)
                             .onAlarm(event -> System.out.println("Alarm: " + LocalTime.now() + " : " + event.ALARM.getText()))
                             .secondsVisible(true)
                             .running(true)
                             .build();
    }

    @Override public void start(Stage stage) {
        HBox pane = new HBox(clock1, clock2);
        pane.setSpacing(20);

        Scene scene = new Scene(pane);

        stage.setTitle("Medusa Clock Control");
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


    // ******************** Inner Classes *************************************
    class LightOn implements Command {
        @Override public void execute() {
            System.out.println("Here we will switch the light on");
        }
    }
    class LightOff implements Command {
        @Override public void execute() {
            System.out.println("Here we will switch the light off");
        }
    }
}
