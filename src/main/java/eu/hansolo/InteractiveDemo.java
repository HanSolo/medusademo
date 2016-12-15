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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 21.11.16
 * Time: 09:32
 */
public class InteractiveDemo extends Application {
    private Gauge gauge;

    @Override public void init() {
        gauge = GaugeBuilder.create()
                            .prefSize(250, 250)
                            .minValue(0)
                            .maxValue(100)
                            .animated(false)
                            .title("Title")
                            .unit("\u00B0C")
                            .subTitle("SubTitle")
                            .interactive(true)
                            .onButtonPressed(o -> System.out.println("Button pressed"))
                            .title("Title")
                            .sections(new Section(0, 33, Color.RED),
                                      new Section(33, 66, Color.YELLOW),
                                      new Section(66, 100, Color.LIME))
                            .sectionsVisible(true)
                            .build();
        gauge.setSkin(new InteractiveGaugeSkin(gauge));
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(gauge);

        Scene scene = new Scene(pane);

        stage.setTitle("Interactive Demo");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
