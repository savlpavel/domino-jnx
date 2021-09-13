/*
 * ==========================================================================
 * Copyright (C) 2019-2021 HCL America, Inc. ( http://www.hcl.com/ )
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
import com.hcl.domino.richtext.annotation.StructureDefinition;
import com.hcl.domino.richtext.annotation.StructureGetter;
import com.hcl.domino.richtext.annotation.StructureMember;
import com.hcl.domino.richtext.annotation.StructureSetter;
import com.hcl.domino.richtext.structures.FontStyle;
import com.hcl.domino.richtext.structures.WSIG;

/**
 * Rich text record of type CDTEXTEFFECT
 */
@StructureDefinition(name = "CDTEXTEFFECT", members = {
    @StructureMember(name = "Header", type = WSIG.class),
    @StructureMember(name = "FontID", type = FontStyle.class)
})
public interface CDTextEffect extends RichTextRecord<WSIG> {

  @StructureGetter("Header")
  @Override
  WSIG getHeader();

  /**
   * Returns the font style of the text
   *
   * @return style
   */
  @StructureGetter("FontID")
  FontStyle getFontStyle();
  
  default CDTextEffect setFontStyle(FontStyle style) {
    getFontStyle().getData().put(style.getData());
    return this;
  }
}
