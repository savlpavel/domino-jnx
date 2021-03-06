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
package com.hcl.domino.commons.richtext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumSet;

import com.hcl.domino.DominoException;
import com.hcl.domino.commons.richtext.records.AbstractCDRecord;
import com.hcl.domino.commons.richtext.records.GenericBSIGRecord;
import com.hcl.domino.commons.richtext.records.GenericLSIGRecord;
import com.hcl.domino.commons.richtext.records.GenericWSIGRecord;
import com.hcl.domino.commons.richtext.records.MemoryStructureProxy;
import com.hcl.domino.richtext.RichTextConstants;
import com.hcl.domino.richtext.RichTextWriter;
import com.hcl.domino.richtext.records.CDBlobPart;
import com.hcl.domino.richtext.records.CDEvent;
import com.hcl.domino.richtext.records.CDGraphic;
import com.hcl.domino.richtext.records.CDImageHeader;
import com.hcl.domino.richtext.records.CDImageHeader2;
import com.hcl.domino.richtext.records.CDImageSegment;
import com.hcl.domino.richtext.records.RichTextRecord;

/**
 * Contains common routines for working with rich text in an
 * implementation-independent way.
 *
 * @since 1.0.15
 */
public enum RichTextUtil {
  ;

  public static final Charset LMBCS = Charset.forName("LMBCS-native"); //$NON-NLS-1$

  /**
   * Creates a {@link AbstractCDRecord} implementation wrapper for the provided
   * record data.
   * 
   * @param signature the signature of the record
   * @param data      a {@link ByteBuffer} referencing the record data
   * @return a {@link RichTextRecord} implementation wrapping the data
   */
  public static AbstractCDRecord<?> encapsulateRecord(final short signature, final ByteBuffer data) {
    AbstractCDRecord<?> record;
    final short highOrderByte = (short) (signature & 0xFF00);
    switch (highOrderByte) {
      case RichTextConstants.LONGRECORDLENGTH: /* LSIG */
        record = new GenericLSIGRecord(data);
        break;
      case RichTextConstants.WORDRECORDLENGTH: /* WSIG */
        record = new GenericWSIGRecord(data);
        break;
      default: /* BSIG */
        record = new GenericBSIGRecord(data);
    }

    return record;
  }

  /**
   * Re-encapsulates the provided record (such as one provided by
   * {@link #encapsulateRecord(short, ByteBuffer)}) with
   * the new encapsulation interface.
   * 
   * @param record             the {@link AbstractCDRecord} implementation to
   *                           re-encapsulate
   * @param encapsulationClass the new interface to use
   * @return a {@link RichTextRecord} implementation using the data from
   *         {@code record} and implementing the new interface
   */
  public static RichTextRecord<?> reencapsulateRecord(final AbstractCDRecord<?> record,
      final Class<? extends RichTextRecord<?>> encapsulationClass) {
    if (record instanceof GenericBSIGRecord) {
      return MemoryStructureProxy.forStructure(encapsulationClass, new GenericBSIGRecord(record.getData(), encapsulationClass));
    } else if (record instanceof GenericWSIGRecord) {
      return MemoryStructureProxy.forStructure(encapsulationClass, new GenericWSIGRecord(record.getData(), encapsulationClass));
    } else if (record instanceof GenericLSIGRecord) {
      return MemoryStructureProxy.forStructure(encapsulationClass, new GenericLSIGRecord(record.getData(), encapsulationClass));
    } else {
      throw new IllegalArgumentException(
          MessageFormat.format("Unable to determine encapsulation for {0}", record.getClass().getName()));
    }
  }

  public static void writeImageRecords(final RichTextWriter w, final InputStream is, final long fileSize, final int imgWidth,
      final int imgHeight, final int resizeToWidth, final int resizeToHeight, final CDImageHeader.ImageType imageType) {
    // write graphic header
    w.addRichTextRecord(CDGraphic.class, graphic -> {
      final boolean isResized = resizeToWidth != -1 && resizeToHeight != -1;

      // DestSize : RECTSIZE (Word/Word)
      if (isResized) {
        graphic.getDestSize().setWidth(resizeToWidth);
        graphic.getDestSize().setHeight(resizeToHeight);
      } else {
        graphic.getDestSize().setWidth(0);
        graphic.getDestSize().setHeight(0);
      }

      // CropSize : RECTSIZE (Word/Word)
      graphic.getCropSize().setWidth(resizeToWidth);
      graphic.getCropSize().setHeight(resizeToHeight);
      // CropOffset : CROPRECT
      graphic.getCropOffset().setTop(0);
      graphic.getCropOffset().setLeft(0);
      graphic.getCropOffset().setRight(0);
      graphic.getCropOffset().setBottom(0);
      // fResize : WORD
      if (isResized) {
        graphic.setResize((short) 1);
      } else {
        graphic.setResize((short) 0);
      }
      // Version: BYTE
      graphic.setVersion(CDGraphic.Version.VERSION3);

      // Flags:
      graphic.setFlags(EnumSet.of(CDGraphic.Flag.DESTSIZE_IS_PIXELS));
    });

    final int fullSegments = (int) (fileSize / RichTextConstants.IMAGE_SEGMENT_MAX);
    int segments = fullSegments;
    final int dataBytesInLastSegment = (int) (fileSize - fullSegments * RichTextConstants.IMAGE_SEGMENT_MAX);
    if (dataBytesInLastSegment > 0) {
      segments++;
    }
    final int fSegments = segments;

    // write image header
    w.addRichTextRecord(CDImageHeader.class, imageHeader -> {
      if (imageType == CDImageHeader.ImageType.PNG) {
        // R9 introduced PNG rendering support, but for the type they use BMP
        // and added a new (undocumented) CD record type right after CDIMAGEHEADER that
        // probably contains the actual image type (0x0004 for PNG) and the image size
        // as DWORD
        imageHeader.setImageType(CDImageHeader.ImageType.BMP);

        // for PNG, set filesize 0x00000000, probably to prevent older clients from
        // reading the data
        imageHeader.setImageDataSize(0);

        // for PNG, set segments to 0x00000000, probably to prevent older clients from
        // reading the data
        imageHeader.setSegCount(0);
      } else {
        imageHeader.setImageType(imageType);
        imageHeader.setImageDataSize(fileSize);
        imageHeader.setSegCount(fSegments);
      }

      imageHeader.setWidth(imgWidth);
      imageHeader.setHeight(imgHeight);
    });

    if (imageType == CDImageHeader.ImageType.PNG) {
      // for PNG we add an undocumented CD record type to define the image type,
      // number of chunks and filesize

      w.addRichTextRecord(CDImageHeader2.class, imageHeader2 -> {
        imageHeader2.setImageType(CDImageHeader.ImageType.PNG);
        imageHeader2.setImageDataSize(fileSize);
        imageHeader2.setSegCount(fSegments);
      });
    }

    final byte[] buf = new byte[RichTextConstants.IMAGE_SEGMENT_MAX];
    int len;
    int bytesRead = 0;

    try {
      for (int i = 0; i < segments; i++) {
        Arrays.fill(buf, (byte) 0);

        len = is.read(buf);
        if (i < segments - 1) {
          if (len < RichTextConstants.IMAGE_SEGMENT_MAX) {
            throw new IllegalStateException(
                MessageFormat.format("The InputStream returned {0} instead of {1}", bytesRead + len, fileSize));
          }
        } else {
          // last segment
          if (len < 0) {
            throw new IllegalStateException(
                MessageFormat.format("The InputStream returned {0} instead of {1}", bytesRead, fileSize));
          }
        }
        bytesRead += len;
        final int fLen = len;

        // write image segment
        int segSize = len;
        if ((segSize & 1L) == 1) {
          segSize++;
        }

        final int fSegSize = segSize;

        w.addRichTextRecord(CDImageSegment.class, segSize, imageSeg -> {
          imageSeg.setDataSize(fLen);
          imageSeg.setSegSize(fSegSize);
          imageSeg.setImageSegmentData(buf, fLen);
        });
      }
    } catch (final IOException e1) {
      throw new DominoException("Error reading image data from stream", e1);
    }
  }

  public static void writeScriptLibrary(final RichTextWriter w, final String script, final CDEvent.ActionType libraryType) {
    final byte[] bytes = script.getBytes(RichTextUtil.LMBCS);

    final int fileLength = bytes.length;
    int segCount = fileLength / RichTextConstants.BLOBPART_SIZE_CAP;
    if (fileLength % RichTextConstants.BLOBPART_SIZE_CAP > 0) {
      segCount++;
    }

    final int paddedLength = fileLength + 1; // Make sure there's at least one \0 at the end
    w.addRichTextRecord(CDEvent.class, event -> {
      event.setEventType(CDEvent.EventType.LIBRARY);
      event.setActionType(libraryType);
      event.setActionLength(paddedLength + paddedLength % 2);
    });
    for (int i = 0; i < segCount; i++) {
      final int dataOffset = RichTextConstants.BLOBPART_SIZE_CAP * i;
      final short dataSize = (short) Math.min(paddedLength - dataOffset, RichTextConstants.BLOBPART_SIZE_CAP);
      final short segSize = (short) (dataSize + dataSize % 2);
      final byte[] segData = Arrays.copyOfRange(bytes, dataOffset, dataOffset + dataSize);

      w.addRichTextRecord(CDBlobPart.class, segSize, part -> {
        part.setOwnerSig(RichTextConstants.SIG_CD_EVENT);
        part.setLength(dataSize);
        part.setBlobMax(RichTextConstants.BLOBPART_SIZE_CAP);
        part.setBlobPartData(segData);
      });
    }
  }
}
