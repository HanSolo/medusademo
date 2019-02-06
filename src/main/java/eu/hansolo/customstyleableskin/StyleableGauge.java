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

import eu.hansolo.medusa.Gauge;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;

import java.util.List;


public class StyleableGauge extends Gauge {

    private static final StyleablePropertyFactory<StyleableGauge> FACTORY         = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    private static final CssMetaData<StyleableGauge, Color>       TICKMARK_COLOR  = FACTORY.createColorCssMetaData("-tickmark-color", g -> g.styleableTickmarkColor, Color.rgb(220, 220, 220), false);
    private static final CssMetaData<StyleableGauge, Color>       TICKLABEL_COLOR = FACTORY.createColorCssMetaData("-ticklabel-color", g -> g.styleableTicklabelColor, Color.rgb(220, 220, 220), false);
    private        final StyleableProperty<Color>                 styleableTickmarkColor;
    private        final StyleableProperty<Color>                 styleableTicklabelColor;


    // ******************** Constructors **************************************
    public StyleableGauge() {
        this(SkinType.GAUGE);
    }
    public StyleableGauge(@NamedArg("SKIN_TYPE") final SkinType SKIN_TYPE) {
        super(SKIN_TYPE);
        styleableTickmarkColor  = new SimpleStyleableObjectProperty<>(TICKMARK_COLOR, this, "tickmark-color");
        styleableTicklabelColor = new SimpleStyleableObjectProperty<>(TICKLABEL_COLOR, this, "ticklabel-color");
    }


    // ******************** Methods *******************************************
    public Color getStyleableTickmarkColor() { return styleableTickmarkColor.getValue(); }
    public void setStyleableTickmarkColor(final Color COLOR) { styleableTickmarkColor.setValue(COLOR); }
    public ObjectProperty<Color> styleableTickmarkColorProperty() { return (ObjectProperty<Color>) styleableTickmarkColor; }

    public Color getStyleableTicklabelColor() { return styleableTicklabelColor.getValue(); }
    public void setStyleableTicklabelColor(final Color COLOR) { styleableTicklabelColor.setValue(COLOR); }
    public ObjectProperty<Color> styleableTicklabelColorProperty() { return (ObjectProperty<Color>) styleableTicklabelColor; }



    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return StyleableGauge.class.getResource("custom-plain-amp.css").toExternalForm();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData(); }
    @Override public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() { return FACTORY.getCssMetaData(); }
}
