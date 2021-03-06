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
package com.hcl.domino.design;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.hcl.domino.admin.idvault.UserId;
import com.hcl.domino.data.Document;
import com.hcl.domino.misc.INumberEnum;

/**
 * Access to forms, views, resources read/write
 *
 * @author t.b.d
 */
public interface DesignElement {
  interface NamedDesignElement extends DesignElement {
    List<String> getAliases();

    String getTitle();

    /**
     * Sets the title of the design element.
     *
     * @param title the new element title and any aliases
     */
    void setTitle(String... title);
  }

  /**
   * This mixin interface describes a design element element that has
   * "display XPage instead" capabilities for web viewers.
   *
   * @author Jesse Gallagher
   * @since 1.0.27
   */
  interface XPageAlternativeElement {
    Optional<String> getWebXPageAlternative();
  }

  /**
   * This mixin interface describes a design element element that has
   * "display XPage instead" capabilities for Notes viewers.
   *
   * @author Jesse Gallagher
   * @since 1.0.27
   */
  interface XPageNotesAlternativeElement {
    Optional<String> getNotesXPageAlternative();
  }
  
  /**
   * This mixin interface describes a non-XPages design element that can
   * have a setting for NSF-level theme behavior.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  interface ThemeableClassicElement {
    ClassicThemeBehavior getClassicThemeBehavior();
  }
  
  /**
   * This mixin interface describes design elements that have "Auto Frame"
   * behavior available.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  interface AutoFrameElement {
    /**
     * Retrieves the name of the frameset used for "Auto Frame" behavior in the
     * Notes client, if specified.
     * 
     * @return an {@link Optional} describing the auto-frame frameset name, or
     *         an empty one if this is unspecified
     */
    Optional<String> getAutoFrameFrameset();
    /**
     * Retrieves the target frame ID used for "Auto Frame" behavior in the
     * Notes client, if specified.
     * 
     * @return an {@link Optional} describing the auto-frame target frame, or
     *         an empty one if this is unspecified
     */
    Optional<String> getAutoFrameTarget();
  }
  
  /**
   * This enum describes the options available for elements conforming to
   * {@link ThemeableClassicElement}.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  enum ClassicThemeBehavior implements INumberEnum<Byte> {
    USE_DATABASE_SETTING(DesignConstants.THEME_DEFAULT),
    INHERIT_FROM_OS(DesignConstants.THEME_ENABLE),
    DONT_INHERIT_FROM_OS(DesignConstants.THEME_DISABLE);
    
    private final byte value;
    private ClassicThemeBehavior(byte value) {
      this.value = value;
    }

    @Override
    public long getLongValue() {
      return value;
    }

    @Override
    public Byte getValue() {
      return value;
    }
  }

  /**
   * @return the comment assigned to the design element
   * @since 1.0.24
   */
  String getComment();

  String getDesignerVersion();

  /**
   * @return the document underlying this design element
   * @since 1.0.18
   */
  Document getDocument();

  Collection<String> getItemNames();

  boolean isHideFromMobile();

  boolean isHideFromNotes();

  boolean isHideFromWeb();

  boolean isProhibitRefresh();

  boolean save();

  /**
   * Sets a comment for the design element
   *
   * @param comment the comment to set
   * @since 1.0.24
   */
  void setComment(String comment);

  void setHideFromMobile(boolean hideFromMobile);

  void setHideFromNotes(boolean hideFromNotes);

  void setHideFromWeb(boolean hideFromWeb);

  void setProhibitRefresh(boolean prohibitRefresh);

  void sign();

  void sign(UserId id);
  
  /**
   * Determines whether the design element should be accessible to
   * sub-Reader users with Public Access rights.
   * 
   * @return {@code true} if the element should allow public acces;
   *         {@code false} otherwise
   */
  boolean isAllowPublicAccess();

}
