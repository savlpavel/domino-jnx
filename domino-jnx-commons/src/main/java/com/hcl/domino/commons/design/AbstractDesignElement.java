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
package com.hcl.domino.commons.design;

import java.util.EnumSet;
import java.util.Optional;

import com.hcl.domino.admin.idvault.UserId;
import com.hcl.domino.data.Document;
import com.hcl.domino.data.DominoCollection;
import com.hcl.domino.data.Item.ItemFlag;
import com.hcl.domino.design.DesignConstants;
import com.hcl.domino.design.DesignElement;
import com.hcl.domino.misc.NotesConstants;

/**
 * @param <T> the {@link DesignElement} interface implemented by the class
 * @since 1.0.18
 */
public abstract class AbstractDesignElement<T extends DesignElement> implements DesignElement {
  private final Document doc;

  public AbstractDesignElement(final Document doc) {
    this.doc = doc;
  }
  
  @Override
  public void delete() {
    delete(false);
  }
  
  @Override
  public void delete(boolean noStub) {
    this.doc.delete(noStub);
  }

  @Override
  public String getComment() {
    return this.doc.getAsText(NotesConstants.FILTER_COMMENT_ITEM, ' ');
  }

  @Override
  public String getDesignerVersion() {
    return this.doc.getAsText(DesignConstants.DESIGNER_VERSION, ' ');
  }

  @Override
  public Document getDocument() {
    return this.doc;
  }
  
  @Override
  public Optional<String> getTemplateName() {
    String val = this.doc.getAsText(NotesConstants.DESIGN_CLASS, ' ');
    if(val == null || val.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(val);
    }
  }
  
  @Override
  public boolean isAllowPublicAccess() {
    String pubAccess = getDocument().getAsText(NotesConstants.FIELD_PUBLICACCESS, ' ');
    return NotesConstants.FIELD_PUBLICACCESS_ENABLED.equals(pubAccess);
  }

  @Override
  public boolean isHideFromMobile() {
    return this.getFlags().contains(NotesConstants.DESIGN_FLAG_HIDE_FROM_MOBILE);
  }

  @Override
  public boolean isHideFromNotes() {
    return this.getFlags().contains(NotesConstants.DESIGN_FLAG_HIDE_FROM_NOTES);
  }

  @Override
  public boolean isHideFromWeb() {
    return this.getFlags().contains(NotesConstants.DESIGN_FLAG_HIDE_FROM_WEB);
  }

  @Override
  public boolean isProhibitRefresh() {
    return this.getFlags().contains(NotesConstants.DESIGN_FLAG_PRESERVE);
  }

  @Override
  public boolean save() {
    this.doc.save();
    
    DominoCollection designCollection = this.doc.getParentDatabase().openDesignCollection();
    if (designCollection!=null) {
      //we had issues seeing newly created views in Domino Designer without refreshing the design collection
      designCollection.refresh();
    }
    
    // TODO figure out if this should do something else or if the method signature
    // should change
    return true;
  }

  @Override
  public void setComment(final String comment) {
    this.doc.replaceItemValue(NotesConstants.FILTER_COMMENT_ITEM, EnumSet.of(ItemFlag.SIGNED, ItemFlag.SUMMARY), comment);
  }

  @Override
  public void setHideFromMobile(final boolean hideFromMobile) {
    this.setFlag(NotesConstants.DESIGN_FLAG_HIDE_FROM_MOBILE, hideFromMobile);
  }

  @Override
  public void setHideFromNotes(final boolean hideFromNotes) {
    this.setFlag(NotesConstants.DESIGN_FLAG_HIDE_FROM_NOTES, hideFromNotes);
  }

  @Override
  public void setHideFromWeb(final boolean hideFromWeb) {
    this.setFlag(NotesConstants.DESIGN_FLAG_HIDE_FROM_WEB, hideFromWeb);
  }

  @Override
  public void setProhibitRefresh(final boolean prohibitRefresh) {
    this.setFlag(NotesConstants.DESIGN_FLAG_PRESERVE, prohibitRefresh);
  }
  
  @Override
  public void setTemplateName(String templateName) {
    this.doc.replaceItemValue(NotesConstants.DESIGN_CLASS, templateName);
  }

  @Override
  public void sign() {
    this.doc.sign();
  }

  @Override
  public void sign(final UserId id) {
    this.doc.sign(id, true);
  }
  
  @Override
  public boolean isHideFromDesignList() {
    return this.getFlags().contains(NotesConstants.DESIGN_FLAG_HIDEFROMDESIGNLIST);
  }
  
  @Override
  public void setHideFromDesignList(boolean hideFromDesignList) {
    this.setFlag(NotesConstants.DESIGN_FLAG_HIDEFROMDESIGNLIST, hideFromDesignList);
  }
  
  @Override
  public boolean isHideFromNotesVersion(int version) {
    if(version < 3 || version > 9) {
      throw new IllegalArgumentException("version must be between 3 and 9");
    }
    // All of these flags are just the version number as a character
    return this.getFlags().contains(String.valueOf(version));
  }
  
  @Override
  public void setHideFromNotesVersion(int version, boolean hide) {
    if(version < 3 || version > 9) {
      throw new IllegalArgumentException("version must be between 3 and 9");
    }
    // All of these flags are just the version number as a character
    this.setFlag(String.valueOf(version), hide);
  }

  // *******************************************************************************
  // * Implementation utility methods
  // *******************************************************************************

  /**
   * Initializes the default values for a newly-created design note, such as
   * {@code "$Flags"}.
   */
  public abstract void initializeNewDesignNote();

  public void setFlag(final String flagConstant, final boolean value) {
    final String flags = this.getFlags();
    if (value && !flags.contains(flagConstant)) {
      this.setFlags(flags + flagConstant);
    } else if (!value && flags.contains(flagConstant)) {
      this.setFlags(flags.replace(flagConstant, "")); //$NON-NLS-1$
    }
  }

  public String getFlags() {
    return this.getDocument().getAsText(NotesConstants.DESIGN_FLAGS, ' ');
  }

  public String getFlagsExt() {
    return this.getDocument().getAsText(NotesConstants.DESIGN_FLAGS_EXTENDED, ' ');
  }

  public void setFlags(final String flags) {
    this.getDocument().replaceItemValue(NotesConstants.DESIGN_FLAGS, flags);
  }

  public void setFlagsExt(final String flags) {
    this.getDocument().replaceItemValue(NotesConstants.DESIGN_FLAGS_EXTENDED, EnumSet.of(ItemFlag.SIGNED, ItemFlag.SUMMARY), flags);
  }

  public void setFlagExt(final String flagConstant, final boolean value) {
    final String flags = this.getFlagsExt();
    if (value && !flags.contains(flagConstant)) {
      this.setFlagsExt(flags + flagConstant);
    } else if (!value && flags.contains(flagConstant)) {
      this.setFlagsExt(flags.replace(flagConstant, "")); //$NON-NLS-1$
    }
  }

  public String getWebFlags() {
    return getDocument().getAsText(NotesConstants.ITEM_NAME_WEBFLAGS, ' ');
  }
  
  public void setWebFlags(String flags) {
    this.getDocument().replaceItemValue(NotesConstants.ITEM_NAME_WEBFLAGS, flags);
  }
  
  public void setWebFlag(final String flagConstant, final boolean value) {
    final String flags = this.getWebFlags();
    if (value && !flags.contains(flagConstant)) {
      this.setWebFlags(flags + flagConstant);
    } else if (!value && flags.contains(flagConstant)) {
      this.setWebFlags(flags.replace(flagConstant, "")); //$NON-NLS-1$
    }
  }
}
