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
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * Created by hansolo on 09.02.16.
 */
public class ColoredClock extends Application {
    private static int noOfNodes = 0;
    private Clock clock1;
    private Clock clock2;

    @Override public void init() {
        clock1 = ClockBuilder.create()
                             .skinType(ClockSkinType.DB)
                             .titleVisible(true)
                             .title("Standard")
                             .secondsVisible(false)
                             .running(true)
                             .build();

        clock2 = ClockBuilder.create()
                             .skinType(ClockSkinType.DB)
                             .titleVisible(true)
                             .title("Colored")
                             .secondsVisible(false)
                             .borderPaint(Color.rgb(0, 96, 184))
                             .borderWidth(5)
                             .hourColor(Color.rgb(0, 96, 184))
                             .minuteColor(Color.rgb(103, 153, 206))
                             .knobColor(Color.rgb(103, 153, 206))
                             .minuteTickMarksVisible(false)
                             .hourTickMarkColor(Color.rgb(102, 102, 102))
                             .running(true)
                             .build();
    }

    @Override public void start(Stage stage) {
        HBox pane = new HBox(clock1, clock2);
        pane.setSpacing(20);

        Scene scene = new Scene(pane);

        stage.setTitle("Medusa Colored Clock");
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
}

