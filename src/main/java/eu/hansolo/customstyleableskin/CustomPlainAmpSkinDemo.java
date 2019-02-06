/*
 * Copyright (c) 2019 by Gerrit Grunwald
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

package eu.hansolo.customstyleableskin;

import eu.hansolo.medusa.Gauge.SkinType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 2019-02-06
 * Time: 11:24
 */
public class CustomPlainAmpSkinDemo extends Application {
    private StyleableGauge gauge;

    @Override public void init() {
        gauge = new StyleableGauge(SkinType.PLAIN_AMP);
        gauge.setPrefSize(600, 300);
        gauge.setUnit("V");
        gauge.setLcdVisible(true);
        gauge.setLedVisible(true);
        gauge.setSkin(new CustomPlainAmpSkin(gauge));
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(gauge);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Custom Plain Amp Skin Demo");
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
