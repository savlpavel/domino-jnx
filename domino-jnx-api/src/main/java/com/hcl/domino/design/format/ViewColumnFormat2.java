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
package com.hcl.domino.design.format;

import java.util.Collection;
import java.util.Set;

import com.hcl.domino.misc.INumberEnum;
import com.hcl.domino.misc.NotesConstants;
import com.hcl.domino.misc.StructureSupport;
import com.hcl.domino.richtext.annotation.StructureDefinition;
import com.hcl.domino.richtext.annotation.StructureGetter;
import com.hcl.domino.richtext.annotation.StructureMember;
import com.hcl.domino.richtext.annotation.StructureSetter;
import com.hcl.domino.richtext.structures.ColorValue;
import com.hcl.domino.richtext.structures.FontStyle;
import com.hcl.domino.richtext.structures.ResizableMemoryStructure;
import com.hcl.domino.richtext.structures.UNID;

/**
 * @author Jesse Gallagher
 * @since 1.0.24
 */
@StructureDefinition(name = "VIEW_COLUMN_FORMAT2", members = {
    @StructureMember(name = "Signature", type = short.class),
    @StructureMember(name = "HeaderFontID", type = FontStyle.class),
    @StructureMember(name = "ResortToViewUNID", type = UNID.class),
    @StructureMember(name = "wSecondResortColumnIndex", type = short.class, unsigned = true),
    @StructureMember(name = "Flags3", type = ViewColumnFormat2.Flag3.class, bitfield = true),
    @StructureMember(name = "wHideWhenFormulaSize", type = short.class, unsigned = true),
    @StructureMember(name = "wTwistieResourceSize", type = short.class, unsigned = true),
    @StructureMember(name = "wCustomOrder", type = short.class, unsigned = true),
    @StructureMember(name = "wCustomHiddenFlags", type = ViewColumnFormat2.HiddenFlag.class, bitfield = true),
    @StructureMember(name = "ColumnColor", type = ColorValue.class),
    @StructureMember(name = "HeaderFontColor", type = ColorValue.class),
})
public interface ViewColumnFormat2 extends ResizableMemoryStructure {
  enum Flag3 implements INumberEnum<Short> {
    FlatInV5(NotesConstants.VCF3_M_FlatInV5),
    CaseSensitiveSortInV5(NotesConstants.VCF3_M_CaseSensitiveSortInV5),
    AccentSensitiveSortInV5(NotesConstants.VCF3_M_AccentSensitiveSortInV5),
    HideWhenFormula(NotesConstants.VCF3_M_HideWhenFormula),
    TwistieResource(NotesConstants.VCF3_M_TwistieResource),
    Color(NotesConstants.VCF3_M_Color),
    /** column has extended date info */
    ExtDate(NotesConstants.VCF3_ExtDate),
    /** column has extended number format */
    NumberFormat(NotesConstants.VCF3_NumberFormat),
    /** V6 - color col and user definable color */
    IsColumnEditable(NotesConstants.VCF3_M_IsColumnEditable),
    UserDefinableColor(NotesConstants.VCF3_M_UserDefinableColor),
    HideInR5(NotesConstants.VCF3_M_HideInR5),
    NamesFormat(NotesConstants.VCF3_M_NamesFormat),
    HideColumnTitle(NotesConstants.VCF3_M_HideColumnTitle),
    /** Is this a shared column? */
    IsSharedColumn(NotesConstants.VCF3_M_IsSharedColumn),
    /** Use only the formula from shared column - let use modify everything else */
    UseSharedColumnFormulaOnly(NotesConstants.VCF3_M_UseSharedColumnFormulaOnly),
    /** Column has extended names format */
    ExtendedViewColFmt6(NotesConstants.VCF3_M_ExtendedViewColFmt6);

    private final short value;

    Flag3(final int value) {
      this.value = (short) value;
    }

    @Override
    public long getLongValue() {
      return this.value;
    }

    @Override
    public Short getValue() {
      return this.value;
    }
  }

  enum HiddenFlag implements INumberEnum<Short> {
    NormalView(NotesConstants.VCF_HIDE_M_NormalView),
    CalFormatTwoDay(NotesConstants.VCF_HIDE_M_CalFormatTwoDay),
    CalFormatOneWeek(NotesConstants.VCF_HIDE_M_CalFormatOneWeek),
    CalFormatTwoWeeks(NotesConstants.VCF_HIDE_M_CalFormatTwoWeeks),
    CalFormatOneMonth(NotesConstants.VCF_HIDE_M_CalFormatOneMonth),
    CalFormatOneYear(NotesConstants.VCF_HIDE_M_CalFormatOneYear),
    CalFormatOneDay(NotesConstants.VCF_HIDE_M_CalFormatOneDay),
    CalFormatWorkWeek(NotesConstants.VCF_HIDE_M_CalFormatWorkWeek),

    DB2_MAPPING(NotesConstants.VCF_HIDE_M_DB2_MAPPING),
    DB2_DATATYPE_TEXT(NotesConstants.VCF_HIDE_M_DB2_DATATYPE_TEXT),
    DB2_DATATYPE_NUMBER(NotesConstants.VCF_HIDE_M_DB2_DATATYPE_NUMBER),
    DB2_DATATYPE_TIMEDATE(NotesConstants.VCF_HIDE_M_DB2_DATATYPE_TIMEDATE),
    DB2_DATATYPE_NONE(NotesConstants.VCF_HIDE_M_DB2_DATATYPE_NONE),

    MOBILE(NotesConstants.VCF_HIDE_M_MOBILE);

    private final short value;

    HiddenFlag(final int value) {
      this.value = (short) value;
    }

    @Override
    public long getLongValue() {
      return this.value;
    }

    @Override
    public Short getValue() {
      return this.value;
    }
  }

  @StructureGetter("ColumnColor")
  ColorValue getColumnColor();

  @StructureGetter("wCustomHiddenFlags")
  Set<HiddenFlag> getCustomHiddenFlags();

  @StructureGetter("wCustomOrder")
  int getCustomOrder();

  @StructureGetter("Flags3")
  Set<Flag3> getFlags();

  @StructureGetter("HeaderFontColor")
  ColorValue getHeaderFontColor();

  @StructureGetter("HeaderFontID")
  FontStyle getHeaderFontStyle();

  default String getHideWhenFormula() {
    return StructureSupport.extractCompiledFormula(this,
        0,
        this.getHideWhenFormulaLength());
  }

  @StructureGetter("wHideWhenFormulaSize")
  int getHideWhenFormulaLength();

  @StructureGetter("ResortToViewUNID")
  UNID getResortToViewUNID();

  @StructureGetter("wSecondResortColumnIndex")
  int getSecondResortColumnIndex();

  @StructureGetter("Signature")
  short getSignature();

  default String getTwistieResource() {
    return StructureSupport.extractCompiledFormula(this,
        this.getHideWhenFormulaLength(),
        this.getTwistieResourceLength());
  }

  @StructureGetter("wTwistieResourceSize")
  int getTwistieResourceLength();

  @StructureSetter("wCustomHiddenFlags")
  ViewColumnFormat2 setCustomHiddenFlags(Collection<HiddenFlag> hiddenFlags);

  @StructureSetter("wCustomOrder")
  ViewColumnFormat2 setCustomOrder(int order);

  @StructureSetter("Flags3")
  ViewColumnFormat2 setFlags(Collection<Flag3> flags);

  default ViewColumnFormat2 setHideWhenFormula(final String formula) {
    return StructureSupport.writeCompiledFormula(
        this,
        0,
        this.getHideWhenFormulaLength(),
        formula,
        this::setHideWhenFormulaLength);
  }

  @StructureSetter("wHideWhenFormulaSize")
  ViewColumnFormat2 setHideWhenFormulaLength(int len);

  @StructureSetter("wSecondResortColumnIndex")
  ViewColumnFormat2 setSecondResortColumnIndex(int index);

  @StructureSetter("Signature")
  ViewColumnFormat2 setSignature(short signature);

  default ViewColumnFormat2 setTwistieResource(final String formula) {
    return StructureSupport.writeCompiledFormula(
        this,
        this.getHideWhenFormulaLength(),
        this.getTwistieResourceLength(),
        formula,
        this::setTwistieResourceLength);
  }

  @StructureSetter("wTwistieResourceSize")
  ViewColumnFormat2 setTwistieResourceLength(int len);
}
