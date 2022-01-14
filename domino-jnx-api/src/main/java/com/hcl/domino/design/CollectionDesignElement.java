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
package com.hcl.domino.design;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import com.hcl.domino.data.CollectionColumn;
import com.hcl.domino.data.NotesFont;
import com.hcl.domino.data.StandardColors;
import com.hcl.domino.design.action.EventId;
import com.hcl.domino.design.format.CalendarLayout;
import com.hcl.domino.design.format.ViewLineSpacing;
import com.hcl.domino.richtext.records.CDResource;
import com.hcl.domino.richtext.structures.ColorValue;
import com.hcl.domino.richtext.structures.ColorValue.Flag;
import com.hcl.domino.richtext.structures.FontStyle;
import com.hcl.domino.richtext.structures.RawColorValue;
import com.hcl.domino.security.AclLevel;

/**
 * Describes a collection design element, i.e. a view or folder
 */
public interface CollectionDesignElement extends DesignElement.NamedDesignElement, DesignElement.XPageAlternativeElement,
  DesignElement.ThemeableClassicElement, DesignElement.AutoFrameElement, DesignElement.ActionBarElement,
  DesignElement.ReadersRestrictedElement {

  public enum OnOpen {
    GOTO_LAST_OPENED,
    GOTO_TOP,
    GOTO_BOTTOM
  }

  public enum OnRefresh {
    DISPLAY_INDICATOR,
    REFRESH_DISPLAY,
    REFRESH_FROM_TOP,
    REFRESH_FROM_BOTTOM
  }
  
  public enum Style {
    STANDARD_OUTLINE, CALENDAR
  }
  
  /**
   * Represents the options for the displayed grid in a outline-format view or
   * folder.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  public enum GridStyle {
    NONE, SOLID, DASHED, DOTS, DASHES_AND_DOTS
  }
  
  /**
   * Represents the options for displaying the column headers in an outline-format
   * view or folder.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   *
   */
  public enum HeaderStyle {
    NONE, FLAT, SIMPLE, BEVELED
  }
  
  /**
   * Represents the options for how to display unread marks in the view or folder.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  public enum UnreadMarksMode {
    NONE, DOCUMENTS_ONLY, ALL
  }
  
  /**
   * Represents the options for how the view or folder index should be updated.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  public enum IndexRefreshMode {
    AUTO_AFTER_FIRST_USE, AUTO, MANUAL, AUTO_AT_MOST_EVERY
  }
  
  /**
   * Represents the options for how the view or folder index should be discarded.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  public enum IndexDiscardMode {
    INACTIVE_45_DAYS, AFTER_EACH_USE, INACTIVE_FOR
  }
  
  /**
   * Represents the tabs and panes available for display in calendar-format collections.
   * 
   * @author Jesse Gallagher
   * @since 1.0.41
   */
  public enum CalendarTab {
    DAY, WEEK, MONTH, MEETINGS, TRASH, CURRENT_MONTH, GOTO_TODAY,
    FORMAT_OPTIONS, OWNER_NAME
  }
  
  /**
   * Represents the available options for displaying the calendar header.
   * 
   * @author Jesse Gallagher
   * @since 1.0.41
   */
  public enum CalendarHeaderStyle {
    NONE, PLAIN, TABS
  }
  
  /**
   * Represents settings related to view/folder display when used inside of
   * a composite application in the Notes Standard client.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  interface CompositeAppSettings {
    boolean isHideColumnHeader();
    boolean isShowPartialHierarchies();
    boolean isShowSwitcher();
    boolean isShowTabNavigator();
    String getViewers();
    String getThreadView();
    boolean isAllowConversationMode();
  }
  
  /**
   * Represents settings specific to calendar-format views and folders.
   * 
   * @author Jesse Gallagher
   * @since 1.0.41
   */
  interface CalendarSettings {
    /**
     * Retrieves the color used for the grid separating days in the calendar.
     * 
     * @return a {@link ColorValue} instance
     */
    ColorValue getDaySeparatorColor();
    
    /**
     * Retrieves the color used for the background of the calendar header.
     * 
     * @return a {@link RawColorValue} instance
     */
    RawColorValue getHeaderBackgroundColor();
    
    /**
     * Retrieves the display style for the calendar header.
     * 
     * @return a {@link CalendarHeaderStyle} instance
     */
    CalendarHeaderStyle getHeaderStyle();
    
    /**
     * Retrieves the tabs and options configured to display in the header of
     * the calendar.
     * 
     * @return a {@link Set} of {@link CalendarTab} instances
     */
    Set<CalendarTab> getTabs();
    
    /**
     * Retrieves the background color for the date area.
     * 
     * @return a {@link ColorValue} instance
     */
    ColorValue getDateBackgroundColor();
    
    /**
     * Retrieves the color used for the current day in the date area.
     * 
     * @return a {@link StandardColors} instance
     */
    StandardColors getTodayColor();
    
    /**
     * Retrieves the color used for the to-do area in the date area.
     * 
     * @return a {@link RawColorValue} instance
     */
    RawColorValue getToDoAreaColor();
    
    /**
     * Determines whether the date area should display large numbers.
     * 
     * @return {@code true} if the date area should use large numbers;
     *         {@code false} otherwise
     */
    boolean isDisplayLargeNumbers();
    
    /**
     * Retrieves the color used for work hours in the daily view.
     * 
     * @return a {@link RawColorValue} instance
     */
    RawColorValue getDailyWorkHoursColor();
    
    /**
     * Retrieves the color used for non-work hours in the daily view.
     * 
     * @return a {@link ColorValue} instance
     */
    RawColorValue getDailyOtherHoursColor();
    
    /**
     * Retrieves the color used to display months outside the current month
     * in the monthly view.
     * 
     * @return a {@link ColorValue} instance
     */
    ColorValue getNonCurrentMonthColor();
    
    /**
     * Retrieves the color used for text in the monthly view.
     * 
     * @return a {@link ColorValue} instance
     */
    ColorValue getMonthlyTextColor();
    
    /**
     * Retrieves the background color used for entries.
     * 
     * @return a {@link ColorValue} instance
     */
    ColorValue getEntryBackgroundColor();
    
    /**
     * Determines whether the calendar should display save-conflict marks.
     * 
     * @return {@code true} to display conflict marks in the calendar;
     *         {@code false} otherwise
     */
    boolean isShowConflictMarks();
    
    /**
     * Retrieves the font style used for displaying time slots and grouping
     * in the calendar.
     * 
     * @return a {@link FontStyle} instance
     */
    NotesFont getTimeSlotsFont();
    
    /**
     * Retrieves the font style used for displaying header in the calendar.
     * 
     * @return a {@link FontStyle} instance
     */
    NotesFont getHeaderFont();
    
    /**
     * Retrieves the font style used for displaying day and date in the calendar.
     * 
     * @return a {@link FontStyle} instance
     */
    NotesFont getDayAndDateFont();
    
    /**
     * Retrieves the font style used for displaying the day and month in weekly
     * views in the calendar.
     * 
     * @return a {@link FontStyle} instance
     */
    NotesFont getWeeklyDayAndMonthFont();
    
    /**
     * Retrieves the calendar formats available for users to select.
     * 
     * @return a {@link Set} of {@link CalendarLayout} instance
     */
    Set<CalendarLayout> getUserCalendarFormats();
    
    /**
     * Retrieves the default calendar format for users viewing the collection.
     * 
     * @return an {@link Optional} describing {@link CalendarLayout} instance
     *         for the default format, or an empty one to use the last format
     *         viewed by the user
     */
    Optional<CalendarLayout> getInitialUserCalendarFormat();
    
    /**
     * Determines whether users are able to see the time-slot display.
     * 
     * @return {@code true} if the time slot display is user-visible;
     *         {@code false} otherwise
     */
    boolean isTimeSlotDisplayAvailable();
    
    /**
     * Retrieves the start time to use for displaying time slots when
     * {@link #isTimeSlotDisplayAvailable()} is {@code true}.
     * 
     * @return a {@link LocalTime} instance
     */
    LocalTime getTimeSlotStart();
    
    /**
     * Retrieves the end time to use for displaying time slots when
     * {@link #isTimeSlotDisplayAvailable()} is {@code true}.
     * 
     * @return a {@link LocalTime} instance
     */
    LocalTime getTimeSlotEnd();
    
    /**
     * Retrieves the duration to use for displaying time slots when
     * {@link #isTimeSlotDisplayAvailable()} is {@code true}.
     * 
     * @return a {@link Duration} instance
     */
    Duration getTimeSlotDuration();
    
    /**
     * Determines whether users can override the time-slot display settings
     * when {@link #isTimeSlotDisplayAvailable()} is {@code true}.
     * 
     * @return {@code true} if the time-slot settings are overridable;
     *         {@code false} otherwise
     */
    boolean isTimeSlotsOverridable();
    
    /**
     * Determines whether users are able to toggle time slots on and off
     * for each day when {@link #isTimeSlotDisplayAvailable()} is {@code true}.
     * 
     * @return {@code true} if users can toggle time slots on and off;
     *         {@code false} otherwise
     */
    boolean isAllowUserTimeSlotToggle();
    
    /**
     * Determines whether entries should be grouped together by time slot.
     * 
     * @return {@code true} if entries should be grouped by time slot;
     *         {@code false} otherwise
     */
    boolean isGroupEntriesByTimeSlot();
  }
  
  /**
   * Represents settings related to the visual view/folder display, including
   * coloration and sizing behavior.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  interface DisplaySettings {
    /**
     * Retrieves the primary background color of the collection.
     * 
     * @return a {@link ColorValue} representing the background color
     */
    ColorValue getBackgroundColor();
    /**
     * Retrieves the background color for alternating rows.
     * 
     * @return a {@link ColorValue} representing the background color for
     *         alternating rows
     */
    ColorValue getAlternateRowColor();
    
    /**
     * Determines whether the value of {@link #getAlternateRowColor()} is used.
     * 
     * @return {@code true} if the alternating row color is used;
     *         {@code false} otherwise
     */
    boolean isUseAlternateRowColor();
    
    /**
     * Retrieves the background image for the collection, if specified.
     * 
     * @return an {@link Optional} describing the background image, if this
     *         has been specified; an empty one otherwise
     */
    Optional<CDResource> getBackgroundImage();
    /**
     * Retrieves the repeat mode for the background image.
     * 
     * @return an {@link ImageRepeatMode} for the background image
     */
    ImageRepeatMode getBackgroundImageRepeatMode();
    
    /**
     * Retrieves the specified grid style for the view or folder.
     * 
     * @return a {@link GridStyle} instance for the collection
     */
    GridStyle getGridStyle();
    
    /**
     * Retrieves the color used for the grid.
     * 
     * @return a {@link ColorValue} representing the color of the grid
     */
    ColorValue getGridColor();
    
    /**
     * Retrieves the specified header display style for table-format views and
     * folders.
     * 
     * @return a {@link HeaderStyle} instance for the collection
     */
    HeaderStyle getHeaderStyle();
    
    /**
     * Retrieves the specified header display color for table-format views and
     * folders.
     * 
     * @return a {@link ColorValue} instance for the header color
     */
    ColorValue getHeaderColor();
    
    /**
     * Retrieves the number of lines used to display the header in table-format
     * views and folders.
     * 
     * @return the number of lines to display the headers
     */
    int getHeaderLines();
    
    /**
     * Retrieves the number of lines used to display each row in a view or folder.
     * 
     * @return the number of lines to display rows
     */
    int getRowLines();
    
    /**
     * Retrieves the line-spacing mode used for each row in a view or folder.
     * 
     * @return a {@link ViewLineSpacing} instance for the row spacing mode
     */
    ViewLineSpacing getLineSpacing();
    
    /**
     * Determines whether the view or folder should shrink rows to fit the actual content
     * when smaller than the rows specified in {@link #getRowLines()}.
     * 
     * @return {@code true} if view display should shrink rows to fit;
     *         {@code false} otherwise
     */
    boolean isShrinkRowsToContent();
    
    /**
     * Determines whether empty categories (e.g. those where the only entries are hidden
     * due to reader-field restrictions) should be hidden when displayed.
     * 
     * @return {@code true} if the collection will hide empty categories on display;
     *         {@code false} otherwise
     */
    // TODO determine whether this also affects API access and consider moving to the top level if so
    boolean isHideEmptyCategories();
    
    /**
     * Determines whether view icons should be colorized when displayed.
     * 
     * @return {@code true} if view icons should be colorized;
     *         {@code false} otherwise
     */
    boolean isColorizeViewIcons();
    
    /**
     * Retrieves the color used for text in unread-document rows in Notes 5.
     * 
     * <p>The Domino Designer property "Transparent" corresponds to this color
     * value having the {@link Flag NOCOLOR} flag set.</p>
     * 
     * @return a {@link ColorValue} representing the unread-document color
     */
    ColorValue getUnreadColor();
    
    /**
     * Determines whether unread-document rows should use bold text in Notes
     * 6 and newer.
     * 
     * @return {@code true} if unread-document rows use bold text;
     *         {@code false} otherwise
     */
    boolean isUnreadBold();
    
    /**
     * Retrieves the color used for total-row text.
     * 
     * @return a {@link ColorValue} representing the total-row color
     */
    ColorValue getColumnTotalColor();
    
    /**
     * Determines whether the view or folder should be displayed with a margin for
     * selection and unread marks.
     * 
     * @return {@code true} if the view or folder should have a selection margin;
     *         {@code false} otherwise
     */
    boolean isShowSelectionMargin();
    
    /**
     * Determines whether, when {@link #isShowSelectionMargin()} is {@code true}, the
     * border between the margin and body should be hidden.
     * 
     * @return {@code true} if the selection margin border should be hidden;
     *         {@code false} otherwise
     */
    boolean isHideSelectionMarginBorder();
    
    /**
     * Determines whether the last column in the view or folder should be extended to
     * take any remaining window width.
     * 
     * <p>When any column has the "extend to use available window width" property set,
     * this setting applies instead to the first such column.</p>
     * 
     * @return {@code true} whether the last column should use remaining window width;
     *         {@code false} otherwise
     */
    boolean isExtendLastColumnToWindowWidth();
    
    /**
     * Retrieves an object representing the top, left, right, and bottom margins around
     * the edge of the view or folder.
     * 
     * @return an {@link EdgeWidths} instance representing the margins
     * @see #getBelowHeaderMargin()
     */
    EdgeWidths getMargin();
    
    /**
     * Retrieves the setting for the margin below the view or folder header.
     * 
     * @return the size of the header below the view or folder header
     * @see #getMargin()
     */
    int getBelowHeaderMargin();
    
    /**
     * Retrieves the color used for the view or folder margin.
     * 
     * @return a {@link ColorValue} representing the view or folder margin
     */
    ColorValue getMarginColor();
  }
  
  /**
   * Represents settings related to the indexing rules of the view or folder.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  interface IndexSettings {
    /**
     * Retrieves the index refresh mode.
     * 
     * @return a {@link IndexRefreshMode} instance for the view or folder
     */
    IndexRefreshMode getRefreshMode();
    
    /**
     * Retrieves the "refresh at most X seconds" value.
     * 
     * <p><strong>Note:</strong> Domino Designer represents this value as hours, not
     * seconds. However, as the value is stored in seconds, that is how it is represented
     * here.</p>
     * 
     * @return an {@link OptionalInt} describing the "at most every" second count,
     *         or an empty one if the collection uses a different mode
     * @see IndexRefreshMode#AUTO_AT_MOST_EVERY
     */
    OptionalInt getRefreshMaxIntervalSeconds();
    
    /**
     * Retrieves the index discard mode.
     * 
     * @return a {@link IndexDiscardMode} instance for the view or folder
     */
    IndexDiscardMode getDiscardMode();
    
    /**
     * Retrieves the "discard if inactive for X hours" value.
     * 
     * <p><strong>Note:</strong> Domino Designer represents this value as days, not
     * hours. However, as the value is stored in hours, that is how it is represented
     * here.</p>
     * 
     * @return an {@link OptionalInt} describing the "if inactive for" hour count,
     *         or an empty one if the collection uses a different mode
     * @see IndexDiscardMode#INACTIVE_FOR
     */
    OptionalInt getDiscardAfterHours();
    
    /**
     * Determines whether the initial index build should be restricted to an ID
     * with {@link AclLevel#DESIGNER Designer} or
     * {@link AclLevel#MANAGER Manager} access.
     * 
     * @return whether initial index building is restricted
     */
    boolean isRestrictInitialBuildToDesigner();
    
    /**
     * Determines whether the indexer should limit entries to one per unique collation
     * key.
     * 
     * @return {@code true} if the indexer should enforce unique entry keys;
     *         {@code false} otherwise
     */
    boolean isGenerateUniqueKeysInIndex();
    
    /**
     * Determines whether index updates should be included in the server transaction
     * log.
     * 
     * @return {@code true} if updates should be included in the transaction log;
     *         {@code false} otherwise
     */
    boolean isIncludeUpdatesInTransactionLog();
  }
  
  /**
   * Represents settings related to the rendering of the view or folder when rendered
   * using the classic web renderer.
   * 
   * @author Jesse Gallagher
   * @since 1.0.32
   */
  interface WebRenderingSettings {
    /**
     * Determines whether the collection contents should be treated as raw HTML, without
     * rendering surrounding controls.
     * 
     * @return {@code true} if the collection contents should be treated as HTML;
     *         {@code false} otherwise
     */
    boolean isTreatAsHtml();
    
    /**
     * Determines whether the collection should be rendered using a Java applet instead of
     * HTML-based controls.
     * 
     * @return {@code true} if the collection should be rendered using a Java applet;
     *         {@code false} otherwise
     */
    boolean isUseJavaApplet();
    
    /**
     * Determines whether the collection should include selection checkboxes when rendered
     * using HTML controls.
     * 
     * @return {@code true} if the collection should allow selection;
     *         {@code false} otherwise
     */
    boolean isAllowSelection();
    
    /**
     * Retrieves the color used for active links when using HTML controls.
     * 
     * @return a {@link ColorValue} representing the active link color
     */
    ColorValue getActiveLinkColor();
    
    /**
     * Retrieves the color used for unvisited links when using HTML controls.
     * 
     * @return a {@link ColorValue} representing the unvisited link color
     */
    ColorValue getUnvisitedLinkColor();
    
    /**
     * Retrieves the color used for visited links when using HTML controls.
     * 
     * @return a {@link ColorValue} representing the visited link color
     */
    ColorValue getVisitedLinkColor();
    
    /**
     * Determines whether the collection should allow web crawler indexing
     * using HTML controls.
     * 
     * @return {@code true} if the collection should allow crawler indexing;
     *         {@code false} otherwise
     */
    boolean isAllowWebCrawlerIndexing();
  }

  CollectionDesignElement addColumn();

  com.hcl.domino.data.DominoCollection getCollection();

  List<CollectionColumn> getColumns();

  OnOpen getOnOpenUISetting();

  OnRefresh getOnRefreshUISetting();

  boolean isAllowCustomizations();

  CollectionDesignElement removeColumn(CollectionColumn column);

  CollectionDesignElement setOnRefreshUISetting(OnRefresh onRefreshUISetting);

  CollectionDesignElement swapColumns(CollectionColumn a, CollectionColumn b);

  CollectionDesignElement swapColumns(int a, int b);
  
  /**
   * Retrieves the style of the collection - namely, whether it is displayed in
   * "outline" format or as a calendar.
   * 
   * @return the overall visual style of the collection as a {@link Style} instance
   * @since 1.0.32
   */
  Style getStyle();

  /**
   * Determines whether the view or folder is marked as the default to display on
   * database open.
   * 
   * @return {@code true} if the collection is marked as the default on DB open;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isDefaultCollection();
  
  /**
   * Determines whether the view or folder is marked as the default design for new
   * views and folders in the database.
   * 
   * @return {@code true} if the collection is marked as the default for design;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isDefaultCollectionDesign();
  
  /**
   * Determines whether all non-leaf entries should be collapsed when the user first
   * opens the database.
   * 
   * @return {@code true} if non-leaf entries should be collapsed by default;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isCollapseAllOnFirstOpen();
  
  /**
   * Determines whether response documents selected by the view will be indexed in
   * a hierarchy beneath their parents.
   * 
   * @return {@code true} if response documents should be beneath their parents;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isShowResponseDocumentsInHierarchy();
  
  /**
   * Determines whether the view or folder should be shown in the client View menu
   * as its own entry.
   * 
   * @return {@code true} if the collection should appear in the View menu;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isShowInViewMenu();
  
  /**
   * Determines whether computed attributes of action-bar actions should be evaluated each
   * time the user changes document selection.
   * 
   * @return {@code true} if actions should be re-evaluated;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isEvaluateActionsOnDocumentChange();
  
  /**
   * Determines whether the view or folder is configured to allow the user to create new
   * documents within the displayed table.
   * 
   * @return {@code true} if the user can create new documents within the view;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isCreateDocumentsAtViewLevel();
  
  /**
   * Retrieves an object that provides a view onto this collection's settings for use in
   * Composite Applications.
   * 
   * @return a {@link CompositeAppSettings} instance
   * @since 1.0.32
   */
  CompositeAppSettings getCompositeAppSettings();
  
  /**
   * Retrieves an object that provides a view onto this collection's display settings.
   * 
   * @return a {@link DisplaySettings} instance
   * @since 1.0.32
   */
  DisplaySettings getDisplaySettings();
  
  /**
   * Retrieves the mode to display unread marks in the view or folder.
   * 
   * @return an {@link UnreadMarksMode} instance representing the configured mode
   * @since 1.0.32
   */
  UnreadMarksMode getUnreadMarksMode();
  
  /**
   * Retrieves an object that provides a view onto this collection's indexing settings.
   * 
   * @return a {@link IndexSettings} instance
   * @since 1.0.32
   */
  IndexSettings getIndexSettings();
  
  /**
   * Retrieves an object that provides a view onto this collection's web rendering
   * settings.
   * 
   * @return a {@link WebRenderingSettings} instance
   * @since 1.0.32
   */
  WebRenderingSettings getWebRenderingSettings();
  
  /**
   * Determines whether the collection allows Domino Data Service (DAS) operations.
   * 
   * @return {@code true} if this collection should be accessible via DAS;
   *         {@code false} otherwise
   * @since 1.0.32
   */
  boolean isAllowDominoDataService();
  
  /**
   * Retrieves the name of the profile document used to define the colors for user-
   * definable color columns, if set.
   * 
   * @return an {@link Optional} describing the color profile-document name, or an
   *         empty one if this is not set
   * @since 1.0.32
   * @see CollectionColumn#isUserDefinableColor()
   */
  Optional<String> getColumnProfileDocName();
  
  /**
   * Retrieves the programmatic names of columns set as user-definable color columns
   * and with "use column formula as backup" disabled.
   * 
   * @return a {@link Set} of column programmatic names
   * @since 1.0.32
   */
  Set<String> getUserDefinableNonFallbackColumns();
  
  /**
   * Retrieves the form formula for the view or folder, if specified.
   * 
   * @return an {@link Optional} describing the collection's form formula, or an empty
   *         one if this is not specified
   * @since 1.0.34
   */
  Optional<String> getFormFormula();
  
  /**
   * Retrieves the help-request formula for the view or folder, if specified.
   * 
   * @return an {@link Optional} describing the collection's help-request formula, or
   *         an empty one if this is not specified
   * @since 1.0.34
   */
  Optional<String> getHelpRequestFormula();
  
  /**
   * Retrieves the single-click target-frame formula for the view or folder, if specified.
   * 
   * @return an {@link Optional} describing the collection's single-click target-frame
   *         formula, or an empty one if this is not specified
   * @since 1.0.34
   */
  Optional<String> getSingleClickTargetFrameFormula();
  
  /**
   * Retrieves the double-click target-frame formula for the view or folder, if specified.
   * 
   * @return an {@link Optional} describing the collection's double-click target-frame
   *         formula, or an empty one if this is not specified
   * @since 1.0.34
   */
  Optional<String> getDoubleClickTargetFrameFormula();

  /**
   * Retrieves the formulas for UI events that are specified for this view or folder.
   * 
   * @return a {@link Map} of {@link EventId} instances to corresponding formulas
   * @since 1.0.34
   */
  Map<EventId, String> getFormulaEvents();
  
  /**
   * Retrieves the element-global LotusScript associated with the view or folder.
   * 
   * @return a {@link String} representing the IDE-formatted LotusScript for the element
   * @since 1.0.34
   * @see #getLotusScriptGlobals()
   */
  String getLotusScript();
  
  /**
   * Retrieves the "Globals" portion of the LotusScript associated with the view or folder.
   * 
   * @return a {@link String} representing the IDE-formatted LotusScript Globals content
   *         for the element
   * @since 1.0.41
   * @see #getLotusScript()
   */
  String getLotusScriptGlobals();
  
  /**
   * Determines whether the view or folder should be presented as a calendar instead
   * of the normal "outline" format.
   * 
   * @return {@code true} if the collection is calendar format;
   *         {@code false} otherwise
   * @since 1.0.41
   */
  boolean isCalendarFormat();
  
  /**
   * Retrieves the calendar-specific settings for this view or folder. This only applies
   * when {@link #isCalendarFormat()} is {@code true}.
   * 
   * @return an {@link Optional} describing a {@link CalendarSettings} instance, or an
   *         empty one if this is not a calendar-format collection
   * @since 1.0.41
   */
  Optional<CalendarSettings> getCalendarSettings();
}