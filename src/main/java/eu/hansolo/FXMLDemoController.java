/*
 * Copyright (c) 2017 by Gerrit Grunwald
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
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


/**
 * Created by hansolo on 20.02.17.
 */
public class FXMLDemoController implements Initializable {
    private static final Random RND = new Random();

    @FXML private Gauge gauge;

    private long           lastTimerCall;
    private AnimationTimer timer;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 2_000_000_000l) {
                    gauge.setValue(RND.nextDouble() * 100);
                    lastTimerCall = now;
                }
            }
        };
        timer.start();
    }
}
