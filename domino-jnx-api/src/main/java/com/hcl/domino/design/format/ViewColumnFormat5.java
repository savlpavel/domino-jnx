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
import com.hcl.domino.richtext.structures.ResizableMemoryStructure;

/**
 * @author Jesse Gallagher
 * @since 1.0.24
 */
@StructureDefinition(name = "VIEW_COLUMN_FORMAT5", members = {
    @StructureMember(name = "Signature", type = short.class),
    // NB: this is a WORD in the API despite the "dw" prefix
    @StructureMember(name = "dwLength", type = short.class, unsigned = true),
    @StructureMember(name = "dwFlags", type = ViewColumnFormat5.Flag.class, bitfield = true),
    @StructureMember(name = "wDistNameColLen", type = short.class, unsigned = true),
    @StructureMember(name = "wSharedColumnAliasLen", type = short.class, unsigned = true),
    @StructureMember(name = "dwReserved", type = int[].class, length = 4),
})
public interface ViewColumnFormat5 extends ResizableMemoryStructure {
  enum Flag implements INumberEnum<Integer> {
    /** Column contains a name. */
    IS_NAME(NotesConstants.VCF5_M_IS_NAME),
    /** Show IM online status in this column. */
    SHOW_IM_STATUS(NotesConstants.VCF5_M_SHOW_IM_STATUS),
    /** vertically show the icon on top line. */
    VERT_ORIENT_TOP(NotesConstants.VCF5_M_VERT_ORIENT_TOP),
    /** vertically middle of the line entry */
    VERT_ORIENT_MID(NotesConstants.VCF5_M_VERT_ORIENT_MID),
    /** show icon on the last line */
    VERT_ORIENT_BOTTOM(NotesConstants.VCF5_M_VERT_ORIENT_BOTTOM);

    private final int value;

    Flag(final int value) {
      this.value = value;
    }

    @Override
    public long getLongValue() {
      return this.value;
    }

    @Override
    public Integer getValue() {
      return this.value;
    }
  }

  default String getDnColumnName() {
    return StructureSupport.extractStringValue(this,
        0,
        this.getDnColumnNameLength());
  }

  @StructureGetter("wDistNameColLen")
  int getDnColumnNameLength();

  @StructureGetter("dwFlags")
  Set<Flag> getFlags();

  @StructureGetter("dwLength")
  int getLength();

  default String getSharedColumnAlias() {
    return StructureSupport.extractStringValue(this,
        this.getDnColumnNameLength(),
        this.getSharedColumnAliasLength());
  }

  @StructureGetter("wSharedColumnAliasLen")
  int getSharedColumnAliasLength();

  @StructureGetter("Signature")
  short getSignature();

  default ViewColumnFormat5 setDnColumnName(final String name) {
    return StructureSupport.writeStringValue(
        this,
        0,
        this.getDnColumnNameLength(),
        name,
        (final int newLen) -> {
          this.setLength(28 + newLen + this.getSharedColumnAliasLength());
          this.setDnColumnNameLength(newLen);
        });
  }

  @StructureSetter("wDistNameColLen")
  ViewColumnFormat5 setDnColumnNameLength(int len);

  @StructureSetter("dwFlags")
  ViewColumnFormat5 setFlags(Collection<Flag> flags);

  @StructureSetter("dwLength")
  ViewColumnFormat5 setLength(int length);

  default ViewColumnFormat5 setSharedColumnAlias(final String alias) {
    return StructureSupport.writeStringValue(
        this,
        this.getDnColumnNameLength(),
        this.getSharedColumnAliasLength(),
        alias,
        (final int newLen) -> {
          this.setLength(28 + this.getDnColumnNameLength() + newLen);
          this.setSharedColumnAliasLength(newLen);
        });
  }

  @StructureSetter("wSharedColumnAliasLen")
  ViewColumnFormat5 setSharedColumnAliasLength(int len);

  @StructureSetter("Signature")
  ViewColumnFormat5 setSignature(short signature);
}
