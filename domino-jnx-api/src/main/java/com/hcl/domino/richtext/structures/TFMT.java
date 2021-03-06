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
package com.hcl.domino.richtext.structures;

import com.hcl.domino.misc.INumberEnum;
import com.hcl.domino.richtext.RichTextConstants;
import com.hcl.domino.richtext.annotation.StructureDefinition;
import com.hcl.domino.richtext.annotation.StructureGetter;
import com.hcl.domino.richtext.annotation.StructureMember;
import com.hcl.domino.richtext.annotation.StructureSetter;

/**
 * @author Jesse Gallagher
 * @since 1.0.24
 */
@StructureDefinition(name = "TFMT", members = {
    @StructureMember(name = "Date", type = TFMT.DateFormat.class),
    @StructureMember(name = "Time", type = TFMT.TimeFormat.class),
    @StructureMember(name = "Zone", type = TFMT.ZoneFormat.class),
    @StructureMember(name = "Structure", type = TFMT.TimeStructure.class)
})
public interface TFMT extends MemoryStructure {
  enum DateFormat implements INumberEnum<Byte> {
    /** year, month, and day */
    FULL(RichTextConstants.TDFMT_FULL),
    /** month and day, year if not this year */
    CPARTIAL(RichTextConstants.TDFMT_CPARTIAL),
    /** month and day */
    PARTIAL(RichTextConstants.TDFMT_PARTIAL),
    /** year and month */
    DPARTIAL(RichTextConstants.TDFMT_DPARTIAL),
    /** year(4digit), month, and day */
    FULL4(RichTextConstants.TDFMT_FULL4),
    /** month and day, year(4digit) if not this year */
    CPARTIAL4(RichTextConstants.TDFMT_CPARTIAL4),
    /** year(4digit) and month */
    DPARTIAL4(RichTextConstants.TDFMT_DPARTIAL4);

    private final byte value;

    DateFormat(final byte value) {
      this.value = value;
    }

    @Override
    public long getLongValue() {
      return this.value;
    }

    @Override
    public Byte getValue() {
      return this.value;
    }
  }

  enum TimeFormat implements INumberEnum<Byte> {
    /** hour, minute, and second */
    FULL(RichTextConstants.TTFMT_FULL),
    /** hour and minute */
    PARTIAL(RichTextConstants.TTFMT_PARTIAL),
    /** hour */
    HOUR(RichTextConstants.TTFMT_HOUR);

    private final byte value;

    TimeFormat(final byte value) {
      this.value = value;
    }

    @Override
    public long getLongValue() {
      return this.value;
    }

    @Override
    public Byte getValue() {
      return this.value;
    }
  }

  enum TimeStructure implements INumberEnum<Byte> {
    /** DATE */
    DATE(RichTextConstants.TSFMT_DATE),
    /** TIME */
    TIME(RichTextConstants.TSFMT_TIME),
    /** DATE TIME */
    DATETIME(RichTextConstants.TSFMT_DATETIME),
    /** DATE TIME or TIME Today or TIME Yesterday */
    CDATETIME(RichTextConstants.TSFMT_CDATETIME);

    private final byte value;

    TimeStructure(final byte value) {
      this.value = value;
    }

    @Override
    public long getLongValue() {
      return this.value;
    }

    @Override
    public Byte getValue() {
      return this.value;
    }
  }

  enum ZoneFormat implements INumberEnum<Byte> {
    /** all times converted to THIS zone */
    NEVER(RichTextConstants.TZFMT_NEVER),
    /** show only when outside this zone */
    SOMETIMES(RichTextConstants.TZFMT_SOMETIMES),
    /** show on all times, regardless */
    ALWAYS(RichTextConstants.TZFMT_ALWAYS);

    private final byte value;

    ZoneFormat(final byte value) {
      this.value = value;
    }

    @Override
    public long getLongValue() {
      return this.value;
    }

    @Override
    public Byte getValue() {
      return this.value;
    }
  }

  @StructureGetter("Date")
  DateFormat getDateFormat();

  @StructureGetter("Time")
  TimeFormat getTimeFormat();

  @StructureGetter("Structure")
  TimeStructure getTimeStructure();

  @StructureGetter("Zone")
  ZoneFormat getZoneFormat();

  @StructureSetter("Date")
  TFMT setDateFormat(DateFormat format);

  @StructureSetter("Time")
  TFMT setTimeFormat(TimeFormat format);

  @StructureSetter("Structure")
  TFMT setTimeStructure(TimeStructure structure);

  @StructureSetter("Zone")
  TFMT setZoneFormat(ZoneFormat format);
}
