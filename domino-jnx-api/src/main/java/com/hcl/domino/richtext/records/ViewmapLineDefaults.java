/*
 * ==========================================================================
 * Copyright (C) 2019-2022 HCL America, Inc. ( http://www.hcl.com/ )
 *                            All rights reserved.
 * ==========================================================================
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may
 * not use this file except in compliance with the License.  You may obtain a
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.
 *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the  specific language  governing permissions  and limitations
 * under the License.
 * ==========================================================================
 */
package com.hcl.domino.richtext.records;

import java.util.Optional;

import com.hcl.domino.data.StandardColors;
import com.hcl.domino.design.navigator.NavigatorFillStyle;
import com.hcl.domino.design.navigator.NavigatorLineStyle;
import com.hcl.domino.richtext.annotation.StructureDefinition;
import com.hcl.domino.richtext.annotation.StructureGetter;
import com.hcl.domino.richtext.annotation.StructureMember;
import com.hcl.domino.richtext.annotation.StructureSetter;
import com.hcl.domino.richtext.structures.MemoryStructure;

/**
 * View Map (Navigator) Line Defaults
 * @author artcnot
 * @author Jesse Gallagher
 * @since 1.0.37
 */
@StructureDefinition(
  name = "VIEWMAP_LINE_DEFAULTS",
  members = {
    @StructureMember(name = "Highlight", type = ViewmapHighlightDefaults.class),
    @StructureMember(name = "LineColor", type = short.class, unsigned = true),
    @StructureMember(name = "FillFGColor", type = short.class, unsigned = true),
    @StructureMember(name = "FillBGColor", type = short.class, unsigned = true),
    @StructureMember(name = "LineStyle", type = NavigatorLineStyle.class),
    @StructureMember(name = "LineWidth", type = short.class, unsigned = true),
    @StructureMember(name = "FillStyle", type = NavigatorFillStyle.class),
  }
)
public interface ViewmapLineDefaults extends MemoryStructure {
  @StructureGetter("Highlight")
  ViewmapHighlightDefaults getHighlight();

  @StructureGetter("LineColor")
  int getLineColorRaw();
  
  @StructureGetter("LineColor")
  Optional<StandardColors> getLineColor();

  @StructureSetter("LineColor")
  ViewmapLineDefaults setLineColor(StandardColors lineColor);

  /**
   * Sets the line color as a raw {@code int}.
   * 
   * @param lineColor the value to set
   * @return this structure
   * @since 1.24.0
   */
  @StructureSetter("LineColor")
  ViewmapLineDefaults setLineColorRaw(int lineColor);
  
  @StructureGetter("FillFGColor")
  int getFillForegroundColorRaw();
  
  @StructureGetter("FillFGColor")
  Optional<StandardColors> getFillForegroundColor();

  @StructureSetter("FillFGColor")
  ViewmapLineDefaults setFillForegroundColor(StandardColors fillFGColor);

  /**
   * Sets the fill foreground color as a raw {@code int}.
   * 
   * @param fillFGColor the value to set
   * @return this structure
   * @since 1.24.0
   */
  @StructureSetter("FillFGColor")
  ViewmapLineDefaults setFillForegroundColorRaw(int fillFGColor);

  @StructureGetter("FillBGColor")
  int getFillBackgroundColorRaw();

  @StructureGetter("FillBGColor")
  Optional<StandardColors> getFillBackgroundColor();

  @StructureSetter("FillBGColor")
  ViewmapLineDefaults setFillBackgroundColor(StandardColors fillBGColor);

  /**
   * Sets the fill background color as a raw {@code int}.
   * 
   * @param fillBGColor the value to set
   * @return this structure
   * @since 1.24.0
   */
  @StructureSetter("FillBGColor")
  ViewmapLineDefaults setFillBackgroundColorRaw(int fillBGColor);

  @StructureGetter("LineStyle")
  Optional<NavigatorLineStyle> getLineStyle();

  /**
   * Retrieves the line style as a raw {@code short}.
   * 
   * @return the line style as a {@code short}
   * @since 1.24.0
   */
  @StructureGetter("LineStyle")
  short getLineStyleRaw();

  @StructureSetter("LineStyle")
  ViewmapLineDefaults setLineStyle(NavigatorLineStyle lineStyle);

  /**
   * Sets the line style as a raw {@code short}.
   * 
   * @param lineStyle the value to set
   * @return this structure
   * @since 1.24.0
   */
  @StructureSetter("LineStyle")
  ViewmapLineDefaults setLineStyleRaw(short lineStyle);

  @StructureGetter("LineWidth")
  int getLineWidth();

  @StructureSetter("LineWidth")
  ViewmapLineDefaults setLineWidth(int lineWidth);

  @StructureGetter("FillStyle")
  Optional<NavigatorFillStyle> getFillStyle();

  /**
   * Sets the fill style as a raw {@code short}.
   * 
   * @return the fill style as a {@code short}
   * @since 1.24.0
   */
  @StructureGetter("FillStyle")
  short getFillStyleRaw();

  @StructureSetter("FillStyle")
  ViewmapLineDefaults setFillStyle(NavigatorFillStyle fillStyle);

  /**
   * Sets the fill style as a raw {@code short}.
   * 
   * @param fillStyle the value to set
   * @return this structure
   * @since 1.24.0
   */
  @StructureSetter("FillStyle")
  ViewmapLineDefaults setFillStyleRaw(short fillStyle);
}
