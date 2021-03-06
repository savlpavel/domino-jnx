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
package it.com.hcl.domino.test.design;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hcl.domino.DominoClient;
import com.hcl.domino.data.CollectionColumn;
import com.hcl.domino.data.CollectionColumn.TotalType;
import com.hcl.domino.data.Database;
import com.hcl.domino.data.FontAttribute;
import com.hcl.domino.data.NotesFont;
import com.hcl.domino.data.StandardFonts;
import com.hcl.domino.design.CollectionDesignElement;
import com.hcl.domino.design.CollectionDesignElement.DisplaySettings;
import com.hcl.domino.design.DbDesign;
import com.hcl.domino.design.DesignElement;
import com.hcl.domino.design.EdgeWidths;
import com.hcl.domino.design.Folder;
import com.hcl.domino.design.ImageRepeatMode;
import com.hcl.domino.design.View;
import com.hcl.domino.design.format.ViewColumnFormat;
import com.hcl.domino.design.format.ViewLineSpacing;
import com.hcl.domino.exception.FileDoesNotExistException;
import com.hcl.domino.richtext.records.CDResource;
import com.hcl.domino.richtext.structures.ColorValue;
import com.hcl.domino.security.Acl;
import com.hcl.domino.security.AclEntry;
import com.hcl.domino.security.AclFlag;
import com.hcl.domino.security.AclLevel;

import it.com.hcl.domino.test.AbstractNotesRuntimeTest;

@SuppressWarnings("nls")
public class TestDbDesignCollections extends AbstractNotesRuntimeTest {
  public static final int EXPECTED_IMPORT_VIEWS = 8;
  public static final int EXPECTED_IMPORT_FOLDERS = 1;

  private static String dbPath;

  @AfterAll
  public static void termDesignDb() {
    try {
      Files.deleteIfExists(Paths.get(TestDbDesignCollections.dbPath));
    } catch (final Throwable t) {
      System.err.println("Unable to delete database " + TestDbDesignCollections.dbPath + ": " + t);
    }
  }

  private Database database;

  @BeforeEach
  public void initDesignDb() throws IOException, URISyntaxException {
    if (this.database == null) {
      final DominoClient client = this.getClient();
      if (TestDbDesignCollections.dbPath == null) {
        this.database = AbstractNotesRuntimeTest.createTempDb(client);
        
        Acl acl = this.database.getACL();
        Optional<AclEntry> entry = acl.getEntry(client.getEffectiveUserName());
        if(entry.isPresent()) {
          acl.updateEntry(client.getEffectiveUserName(), null, null, Arrays.asList("[Admin]"), null);
        } else {
          acl.addEntry(client.getEffectiveUserName(), AclLevel.MANAGER, Arrays.asList("[Admin]"), EnumSet.allOf(AclFlag.class));
        }
        acl.save();
        
        TestDbDesignCollections.dbPath = this.database.getAbsoluteFilePath();
        AbstractNotesRuntimeTest.populateResourceDxl("/dxl/testDbDesignCollections", this.database);
      } else {
        this.database = client.openDatabase("", TestDbDesignCollections.dbPath);
      }
    }
  }

  @Test
  public void testExampleView() {
    final DbDesign dbDesign = this.database.getDesign();
    final View view = dbDesign.getView("Example View").get();
    assertTrue(view.isAllowCustomizations());
    assertEquals(CollectionDesignElement.OnOpen.GOTO_TOP, view.getOnOpenUISetting());
    assertEquals(CollectionDesignElement.OnRefresh.REFRESH_DISPLAY, view.getOnRefreshUISetting());
    assertEquals(DesignElement.ClassicThemeBehavior.USE_DATABASE_SETTING, view.getClassicThemeBehavior());
    assertEquals(CollectionDesignElement.Style.STANDARD_OUTLINE, view.getStyle());
    assertFalse(view.isDefaultCollection());
    assertFalse(view.isDefaultCollectionDesign());
    assertFalse(view.isCollapseAllOnFirstOpen());
    assertTrue(view.isShowResponseDocumentsInHierarchy());
    assertFalse(view.isShowInViewMenu());
    assertTrue(view.isEvaluateActionsOnDocumentChange());
    assertFalse(view.isCreateDocumentsAtViewLevel());
    assertEquals("Home_1.xsp", view.getWebXPageAlternative().get());
    assertFalse(view.isAllowPublicAccess());
    assertEquals(Arrays.asList("[Admin]"), view.getReaders());
    
    CollectionDesignElement.CompositeAppSettings comp = view.getCompositeAppSettings();
    assertNotNull(comp);
    assertTrue(comp.isHideColumnHeader());
    assertFalse(comp.isShowPartialHierarchies());
    assertTrue(comp.isShowSwitcher());
    assertFalse(comp.isShowTabNavigator());
    assertEquals("viewers1", comp.getViewers());
    assertEquals("Trash", comp.getThreadView());
    assertFalse(comp.isAllowConversationMode());
    
    CollectionDesignElement.DisplaySettings disp = view.getDisplaySettings();
    ColorValue background = disp.getBackgroundColor();
    assertEquals(255, background.getRed());
    assertEquals(255, background.getGreen());
    assertEquals(255, background.getBlue());
    assertTrue(disp.isUseAlternateRowColor());
    
    Optional<CDResource> backgroundImage = disp.getBackgroundImage();
    assertTrue(backgroundImage.isPresent());
    assertTrue(backgroundImage.get().getFlags().contains(CDResource.Flag.FORMULA));
    assertEquals("\"hey.png\"", backgroundImage.get().getNamedElementFormula());
    
    assertEquals(ImageRepeatMode.SIZE_TO_FIT, disp.getBackgroundImageRepeatMode());
    
    assertEquals(CollectionDesignElement.GridStyle.SOLID, disp.getGridStyle());
    ColorValue gridColor = disp.getGridColor();
    assertEquals(255, gridColor.getRed());
    assertEquals(255, gridColor.getGreen());
    assertEquals(255, gridColor.getBlue());
    
    assertEquals(CollectionDesignElement.HeaderStyle.BEVELED, disp.getHeaderStyle());
    assertEquals(2, disp.getHeaderLines());
    ColorValue headerColor = disp.getHeaderColor();
    assertEquals(255, headerColor.getRed());
    assertEquals(255, headerColor.getGreen());
    assertEquals(255, headerColor.getBlue());
    
    assertEquals(5, disp.getRowLines());
    assertEquals(ViewLineSpacing.ONE_POINT_25_SPACE, disp.getLineSpacing());
    assertTrue(disp.isShrinkRowsToContent());
    assertTrue(disp.isHideEmptyCategories());
    assertFalse(disp.isColorizeViewIcons());
    
    ColorValue unreadColor = disp.getUnreadColor();
    assertEquals(0, unreadColor.getRed());
    assertEquals(0, unreadColor.getGreen());
    assertEquals(0, unreadColor.getBlue());
    assertFalse(unreadColor.getFlags().contains(ColorValue.Flag.NOCOLOR));
    
    assertTrue(disp.isUnreadBold());
    
    ColorValue totalColor = disp.getColumnTotalColor();
    assertEquals(192, totalColor.getRed());
    assertEquals(98, totalColor.getGreen());
    assertEquals(255, totalColor.getBlue());
    
    assertTrue(disp.isShowSelectionMargin());
    assertFalse(disp.isHideSelectionMarginBorder());
    assertTrue(disp.isExtendLastColumnToWindowWidth());
    
    EdgeWidths margin = disp.getMargin();
    assertEquals(1, margin.getTop());
    assertEquals(2, margin.getLeft());
    assertEquals(3, margin.getRight());
    assertEquals(4, margin.getBottom());
    assertEquals(5, disp.getBelowHeaderMargin());
    
    ColorValue marginColor = disp.getMarginColor();
    assertEquals(255, marginColor.getRed());
    assertEquals(255, marginColor.getGreen());
    assertEquals(255, marginColor.getBlue());
    
    assertFalse(view.getAutoFrameFrameset().isPresent());
    assertFalse(view.getAutoFrameTarget().isPresent());
    
    assertEquals(CollectionDesignElement.UnreadMarksMode.NONE, view.getUnreadMarksMode());
    
    CollectionDesignElement.IndexSettings index = view.getIndexSettings();
    assertEquals(CollectionDesignElement.IndexRefreshMode.AUTO_AT_MOST_EVERY, index.getRefreshMode());
    assertEquals(TimeUnit.HOURS.toSeconds(3), index.getRefreshMaxIntervalSeconds().getAsInt());
    assertEquals(CollectionDesignElement.IndexDiscardMode.INACTIVE_FOR, index.getDiscardMode());
    assertEquals(TimeUnit.DAYS.toHours(7), index.getDiscardAfterHours().getAsInt());
    assertFalse(index.isRestrictInitialBuildToDesigner());
    assertTrue(index.isGenerateUniqueKeysInIndex());
    assertFalse(index.isIncludeUpdatesInTransactionLog());
    
    CollectionDesignElement.WebRenderingSettings web = view.getWebRenderingSettings();
    assertFalse(web.isTreatAsHtml());
    assertFalse(web.isUseJavaApplet());
    assertFalse(web.isAllowSelection());
    assertColorEquals(web.getActiveLinkColor(), 255, 0, 0);
    assertColorEquals(web.getUnvisitedLinkColor(), 0, 0, 255);
    assertColorEquals(web.getVisitedLinkColor(), 128, 0, 128);
    assertFalse(web.isAllowWebCrawlerIndexing());
    assertFalse(view.isAllowDominoDataService());
    
    assertEquals("hello", view.getColumnProfileDocName().get());
    assertEquals(Collections.singleton("$9"), view.getUserDefinableNonFallbackColumns());

    final List<CollectionColumn> columns = view.getColumns();
    assertEquals(12, columns.size());
    {
      final CollectionColumn column = columns.get(0);
      assertEquals("Form", column.getTitle());
      assertEquals("Form", column.getItemName());
      assertEquals("", column.getFormula());
      assertFalse(column.isConstant());
      assertEquals(188, column.getDisplayWidth());
      assertEquals(ViewColumnFormat.ListDelimiter.SEMICOLON, column.getListDisplayDelimiter());
      assertEquals(TotalType.None, column.getTotalType());
      assertTrue(column.isResizable());
      assertFalse(column.isResponsesOnly());
      assertFalse(column.isIcon());
      assertFalse(column.isUserEditable());
      assertFalse(column.isColor());
      assertFalse(column.isUserDefinableColor());
      assertFalse(column.isHideTitle());
      assertFalse(column.isHideDetailRows());

      final CollectionColumn.SortConfiguration sortConfig = column.getSortConfiguration();
      assertTrue(sortConfig.isCategory());
      assertTrue(sortConfig.isSorted());
      assertTrue(sortConfig.isSortPermuted());
      assertFalse(sortConfig.getResortToViewUnid().isPresent());

      assertTrue(column.isShowTwistie());
      Optional<CDResource> twistie = column.getTwistieImage();
      assertTrue(twistie.isPresent());
      assertEquals("Untitled.gif", twistie.get().getNamedElement());
      assertFalse(twistie.get().getFlags().contains(CDResource.Flag.FORMULA));
      
      {
        NotesFont font = column.getRowFont();
        assertFalse(font.getStandardFont().isPresent());
        assertEquals("Courier New", font.getFontName().get());
        assertEquals(10, font.getPointSize());
        assertEquals(EnumSet.of(FontAttribute.UNDERLINE, FontAttribute.STRIKEOUT), font.getAttributes());
      }
      {
        NotesFont font = column.getHeaderFont();
        assertFalse(font.getStandardFont().isPresent());
        assertEquals("Georgia", font.getFontName().get());
        assertEquals(9, font.getPointSize());
        assertEquals(EnumSet.of(FontAttribute.UNDERLINE, FontAttribute.BOLD, FontAttribute.ITALIC), font.getAttributes());
      }
      
    }
    {
      final CollectionColumn column = columns.get(1);
      assertEquals("Size", column.getTitle());
      assertEquals("$2", column.getItemName());
      assertEquals("@AttachmentLengths", column.getFormula());
      assertFalse(column.isConstant());
      assertEquals(ViewColumnFormat.ListDelimiter.SPACE, column.getListDisplayDelimiter());
      assertEquals(TotalType.Total, column.getTotalType());
      assertTrue(column.isResizable());
      assertFalse(column.isResponsesOnly());
      assertFalse(column.isIcon());
      assertFalse(column.isUserEditable());
      assertFalse(column.isColor());
      assertFalse(column.isUserDefinableColor());
      assertFalse(column.isHideTitle());
      assertTrue(column.isHideDetailRows());

      final CollectionColumn.SortConfiguration sortConfig = column.getSortConfiguration();
      assertFalse(sortConfig.isCategory());
      assertFalse(sortConfig.isSorted());
      assertFalse(sortConfig.isSortPermuted());
      assertTrue(sortConfig.isResortToView());
      assertEquals("F7FAC064F4062A4885257BBE006FA09B", sortConfig.getResortToViewUnid().get());

      assertFalse(column.isShowTwistie());
      Optional<CDResource> twistie = column.getTwistieImage();
      assertFalse(twistie.isPresent());
      
      {
        NotesFont font = column.getRowFont();
        assertEquals(StandardFonts.SWISS, font.getStandardFont().get());
        assertFalse(font.getFontName().isPresent());
        assertEquals(10, font.getPointSize());
        assertEquals(EnumSet.noneOf(FontAttribute.class), font.getAttributes());
      }
      {
        NotesFont font = column.getHeaderFont();
        assertEquals(StandardFonts.SWISS, font.getStandardFont().get());
        assertFalse(font.getFontName().isPresent());
        assertEquals(9, font.getPointSize());
        assertEquals(EnumSet.of(FontAttribute.BOLD), font.getAttributes());
      }
    }
    {
      final CollectionColumn column = columns.get(2);
      assertEquals("Created", column.getTitle());
      assertEquals("$3", column.getItemName());
      assertEquals("@Created", column.getFormula());
      assertFalse(column.isConstant());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.Average, column.getTotalType());
      assertFalse(column.isResizable());
      assertFalse(column.isResponsesOnly());
      assertFalse(column.isIcon());
      assertTrue(column.isUserEditable());
      assertFalse(column.isColor());
      assertTrue(column.isUserDefinableColor());
      assertFalse(column.isHideTitle());

      final CollectionColumn.SortConfiguration sortConfig = column.getSortConfiguration();
      assertFalse(sortConfig.isCategory());
      assertFalse(sortConfig.isSorted());
      assertFalse(sortConfig.isSortPermuted());
      assertFalse(sortConfig.isResortToView());
      assertTrue(sortConfig.isResortAscending() && sortConfig.isResortDescending());
      assertTrue(sortConfig.isDeferResortIndexing());
      assertFalse(sortConfig.getResortToViewUnid().isPresent());
      
      NotesFont font = column.getRowFont();
      assertFalse(font.getStandardFont().isPresent());
      assertEquals("Consolas", font.getFontName().get());
      assertEquals(14, font.getPointSize());
      assertEquals(EnumSet.of(FontAttribute.UNDERLINE, FontAttribute.STRIKEOUT), font.getAttributes());
    }
    {
      final CollectionColumn column = columns.get(3);
      assertEquals("Modified", column.getTitle());
      assertEquals(ViewColumnFormat.ListDelimiter.COMMA, column.getListDisplayDelimiter());
      assertEquals(TotalType.AveragePerSubcategory, column.getTotalType());
      assertFalse(column.isResponsesOnly());
      assertFalse(column.isIcon());
      assertFalse(column.isColor());
      assertFalse(column.isHideTitle());
      
      NotesFont font = column.getRowFont();
      assertEquals(StandardFonts.UNICODE, font.getStandardFont().get());
      assertFalse(font.getFontName().isPresent());
      assertEquals(11, font.getPointSize());
      assertEquals(EnumSet.of(FontAttribute.ITALIC), font.getAttributes());
    }
    {
      final CollectionColumn column = columns.get(4);
      assertEquals("Static Value!", column.getTitle());
      assertFalse(column.isUseHideWhen());
      assertEquals("SecretHideWhen", column.getHideWhenFormula());
      assertEquals(ViewColumnFormat.ListDelimiter.NEWLINE, column.getListDisplayDelimiter());
      assertEquals(TotalType.PercentOfParentCategory, column.getTotalType());
      assertFalse(column.isResponsesOnly());
      assertFalse(column.isIcon());
      assertTrue(column.isColor());
      assertTrue(column.isHideTitle());
    }
    {
      final CollectionColumn column = columns.get(5);
      assertEquals("#", column.getTitle());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.None, column.getTotalType());
      assertFalse(column.isResponsesOnly());
      assertFalse(column.isIcon());
      assertTrue(column.isSharedColumn());
      assertEquals("testcol", column.getSharedColumnName().get());
    }
    {
      final CollectionColumn column = columns.get(6);
      assertEquals("I am test col 2", column.getTitle());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.None, column.getTotalType());
      assertFalse(column.isResponsesOnly());
      assertFalse(column.isIcon());
      assertTrue(column.isSharedColumn());
      assertEquals("testcol2", column.getSharedColumnName().get());
    }
    {
      final CollectionColumn column = columns.get(7);
      assertEquals("Names Guy", column.getTitle());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.Percent, column.getTotalType());
      assertTrue(column.isResponsesOnly());
      assertFalse(column.isIcon());

      assertTrue(column.isShowTwistie());
      Optional<CDResource> twistie = column.getTwistieImage();
      assertTrue(twistie.isPresent());
      assertEquals("tango/utilities-terminal.png", twistie.get().getNamedElement());
    }
    {
      final CollectionColumn column = columns.get(8);
      assertEquals("Names Guy 2", column.getTitle());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.None, column.getTotalType());
      assertFalse(column.isIcon());

      // Not enabled, but present
      assertFalse(column.isShowTwistie());
      Optional<CDResource> twistie = column.getTwistieImage();
      assertTrue(twistie.isPresent());
      assertEquals("Untitled 2.gif", twistie.get().getNamedElement());
    }
    {
      final CollectionColumn column = columns.get(9);
      assertEquals("I am test col 2", column.getTitle());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.None, column.getTotalType());
      assertFalse(column.isIcon());
      assertTrue(column.isSharedColumn());
      assertEquals("testcol2", column.getSharedColumnName().get());
    }
    {
      final CollectionColumn column = columns.get(10);
      assertEquals("Hidden Guy", column.getTitle());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.None, column.getTotalType());
      assertTrue(column.isIcon());
    }
    {
      final CollectionColumn column = columns.get(11);
      assertEquals("Column of constant value", column.getTitle());
      assertTrue(column.isConstant());
      assertEquals("\"hello\"", column.getFormula());
      assertEquals(ViewColumnFormat.ListDelimiter.NONE, column.getListDisplayDelimiter());
      assertEquals(TotalType.None, column.getTotalType());
      
      Optional<CDResource> twistie = column.getTwistieImage();
      assertTrue(twistie.isPresent());
      assertTrue(twistie.get().getFlags().contains(CDResource.Flag.FORMULA));
      assertEquals("\"foo.png\"", twistie.get().getNamedElementFormula());
    }

  }

  @Test
  public void testExampleView2() {
    final DbDesign dbDesign = this.database.getDesign();
    final View view = dbDesign.getView("Example View 2").get();
    assertEquals("Example View 2", view.getTitle());
    assertEquals(Arrays.asList("test alias for view 2", "other alias"), view.getAliases());
    assertEquals("I am a comment'", view.getComment());
    assertFalse(view.isAllowCustomizations());
    assertEquals(CollectionDesignElement.OnOpen.GOTO_LAST_OPENED, view.getOnOpenUISetting());
    assertEquals(CollectionDesignElement.OnRefresh.DISPLAY_INDICATOR, view.getOnRefreshUISetting());
    assertEquals(DesignElement.ClassicThemeBehavior.DONT_INHERIT_FROM_OS, view.getClassicThemeBehavior());
    assertEquals(CollectionDesignElement.Style.STANDARD_OUTLINE, view.getStyle());
    assertFalse(view.isDefaultCollection());
    assertTrue(view.isDefaultCollectionDesign());
    assertTrue(view.isCollapseAllOnFirstOpen());
    assertTrue(view.isShowResponseDocumentsInHierarchy());
    assertFalse(view.isShowInViewMenu());
    assertFalse(view.isEvaluateActionsOnDocumentChange());
    assertTrue(view.isCreateDocumentsAtViewLevel());
    assertFalse(view.getWebXPageAlternative().isPresent());
    assertTrue(view.isAllowPublicAccess());
    assertTrue(view.getReaders().isEmpty());
    assertFalse(view.getColumnProfileDocName().isPresent());
    assertTrue(view.getUserDefinableNonFallbackColumns().isEmpty());
    
    CollectionDesignElement.CompositeAppSettings comp = view.getCompositeAppSettings();
    assertNotNull(comp);
    assertFalse(comp.isHideColumnHeader());
    assertFalse(comp.isShowPartialHierarchies());
    assertTrue(comp.isShowSwitcher());
    assertFalse(comp.isShowTabNavigator());
    assertFalse(comp.isAllowConversationMode());
    
    DisplaySettings disp = view.getDisplaySettings();
    assertNotNull(disp);
    
    ColorValue background = disp.getBackgroundColor();
    assertEquals(0, background.getRed());
    assertEquals(255, background.getGreen());
    assertEquals(255, background.getBlue());

    assertTrue(disp.isUseAlternateRowColor());
    ColorValue altBackground = disp.getAlternateRowColor();
    assertEquals(192, altBackground.getRed());
    assertEquals(192, altBackground.getGreen());
    assertEquals(192, altBackground.getBlue());
    
    assertEquals(ImageRepeatMode.ONCE, disp.getBackgroundImageRepeatMode());
    
    assertEquals(CollectionDesignElement.GridStyle.DOTS, disp.getGridStyle());
    ColorValue gridColor = disp.getGridColor();
    assertEquals(191, gridColor.getRed());
    assertEquals(191, gridColor.getGreen());
    assertEquals(255, gridColor.getBlue());

    assertEquals(CollectionDesignElement.HeaderStyle.FLAT, disp.getHeaderStyle());
    assertEquals(1, disp.getHeaderLines());
    ColorValue headerColor = disp.getHeaderColor();
    assertEquals(255, headerColor.getRed());
    assertEquals(159, headerColor.getGreen());
    assertEquals(255, headerColor.getBlue());
    
    assertEquals(1, disp.getRowLines());
    assertEquals(ViewLineSpacing.SINGLE_SPACE, disp.getLineSpacing());
    assertFalse(disp.isShrinkRowsToContent());
    assertFalse(disp.isHideEmptyCategories());
    assertTrue(disp.isColorizeViewIcons());
    
    ColorValue unreadColor = disp.getUnreadColor();
    assertEquals(0, unreadColor.getRed());
    assertEquals(0, unreadColor.getGreen());
    assertEquals(255, unreadColor.getBlue());
    assertFalse(unreadColor.getFlags().contains(ColorValue.Flag.NOCOLOR));
    
    assertTrue(disp.isUnreadBold());
    
    ColorValue totalColor = disp.getColumnTotalColor();
    assertEquals(255, totalColor.getRed());
    assertEquals(0, totalColor.getGreen());
    assertEquals(128, totalColor.getBlue());
    
    assertTrue(disp.isShowSelectionMargin());
    assertFalse(disp.isHideSelectionMarginBorder());
    assertTrue(disp.isExtendLastColumnToWindowWidth());
    
    EdgeWidths margin = disp.getMargin();
    assertEquals(2, margin.getTop());
    assertEquals(2, margin.getLeft());
    assertEquals(10, margin.getRight());
    assertEquals(5, margin.getBottom());
    assertEquals(2, disp.getBelowHeaderMargin());
    
    ColorValue marginColor = disp.getMarginColor();
    assertEquals(130, marginColor.getRed());
    assertEquals(66, marginColor.getGreen());
    assertEquals(255, marginColor.getBlue());

    
    assertTrue(view.getAutoFrameFrameset().isPresent());
    assertEquals("Outer Frame", view.getAutoFrameFrameset().get());
    assertTrue(view.getAutoFrameTarget().isPresent());
    assertEquals("NotesView", view.getAutoFrameTarget().get());
    
    assertEquals(CollectionDesignElement.UnreadMarksMode.DOCUMENTS_ONLY, view.getUnreadMarksMode());
    
    CollectionDesignElement.IndexSettings index = view.getIndexSettings();
    assertEquals(CollectionDesignElement.IndexRefreshMode.AUTO_AFTER_FIRST_USE, index.getRefreshMode());
    assertFalse(index.getRefreshMaxIntervalSeconds().isPresent());
    assertEquals(CollectionDesignElement.IndexDiscardMode.INACTIVE_45_DAYS, index.getDiscardMode());
    assertFalse(index.getDiscardAfterHours().isPresent());
    assertFalse(index.isRestrictInitialBuildToDesigner());
    assertTrue(index.isGenerateUniqueKeysInIndex());
    assertFalse(index.isIncludeUpdatesInTransactionLog());
    
    CollectionDesignElement.WebRenderingSettings web = view.getWebRenderingSettings();
    assertFalse(web.isTreatAsHtml());
    assertFalse(web.isUseJavaApplet());
    assertTrue(web.isAllowSelection());
    assertColorEquals(web.getActiveLinkColor(), 0, 98, 225);
    assertColorEquals(web.getUnvisitedLinkColor(), 255, 64, 64);
    assertColorEquals(web.getVisitedLinkColor(), 255, 159, 255);
    assertFalse(web.isAllowWebCrawlerIndexing());
    assertTrue(view.isAllowDominoDataService());
  }

  @Test
  public void testExampleView3() {
    final DbDesign dbDesign = this.database.getDesign();
    final View view = dbDesign.getView("Example View 3").get();
    assertFalse(view.isAllowCustomizations());
    assertEquals(CollectionDesignElement.OnOpen.GOTO_BOTTOM, view.getOnOpenUISetting());
    assertEquals(CollectionDesignElement.OnRefresh.REFRESH_FROM_TOP, view.getOnRefreshUISetting());
    assertEquals(DesignElement.ClassicThemeBehavior.INHERIT_FROM_OS, view.getClassicThemeBehavior());
    assertFalse(view.isDefaultCollection());
    assertFalse(view.isDefaultCollectionDesign());
    assertFalse(view.isCollapseAllOnFirstOpen());
    assertFalse(view.isShowResponseDocumentsInHierarchy());
    assertFalse(view.isShowInViewMenu());
    assertFalse(view.isEvaluateActionsOnDocumentChange());
    assertFalse(view.getWebXPageAlternative().isPresent());
    assertFalse(view.isAllowPublicAccess());
    assertTrue(view.getReaders().isEmpty());
    assertFalse(view.getColumnProfileDocName().isPresent());
    assertTrue(view.getUserDefinableNonFallbackColumns().isEmpty());
    
    DisplaySettings disp = view.getDisplaySettings();
    assertNotNull(disp);
    
    ColorValue background = disp.getBackgroundColor();
    assertEquals(255, background.getRed());
    assertEquals(255, background.getGreen());
    assertEquals(255, background.getBlue());

    assertTrue(disp.isUseAlternateRowColor());
    ColorValue altBackground = disp.getAlternateRowColor();
    assertEquals(239, altBackground.getRed());
    assertEquals(239, altBackground.getGreen());
    assertEquals(239, altBackground.getBlue());
    
    Optional<CDResource> backgroundImage = disp.getBackgroundImage();
    assertFalse(backgroundImage.isPresent());
    assertEquals(ImageRepeatMode.ONCE, disp.getBackgroundImageRepeatMode());
    
    assertEquals(CollectionDesignElement.GridStyle.SOLID, disp.getGridStyle());
    ColorValue gridColor = disp.getGridColor();
    assertEquals(255, gridColor.getRed());
    assertEquals(95, gridColor.getGreen());
    assertEquals(255, gridColor.getBlue());

    assertEquals(CollectionDesignElement.HeaderStyle.SIMPLE, disp.getHeaderStyle());
    assertEquals(4, disp.getHeaderLines());
    ColorValue headerColor = disp.getHeaderColor();
    assertEquals(225, headerColor.getRed());
    assertEquals(225, headerColor.getGreen());
    assertEquals(64, headerColor.getBlue());
    
    assertEquals(1, disp.getRowLines());
    assertEquals(ViewLineSpacing.SINGLE_SPACE, disp.getLineSpacing());
    assertFalse(disp.isShrinkRowsToContent());
    assertFalse(disp.isHideEmptyCategories());
    assertFalse(disp.isColorizeViewIcons());
    
    ColorValue unreadColor = disp.getUnreadColor();
    assertEquals(255, unreadColor.getRed());
    assertEquals(0, unreadColor.getGreen());
    assertEquals(0, unreadColor.getBlue());
    assertTrue(unreadColor.getFlags().contains(ColorValue.Flag.NOCOLOR));
    
    assertTrue(disp.isUnreadBold());
    
    ColorValue totalColor = disp.getColumnTotalColor();
    assertEquals(0, totalColor.getRed());
    assertEquals(0, totalColor.getGreen());
    assertEquals(0, totalColor.getBlue());
    
    assertTrue(disp.isShowSelectionMargin());
    assertTrue(disp.isHideSelectionMarginBorder());
    assertTrue(disp.isExtendLastColumnToWindowWidth());
    
    EdgeWidths margin = disp.getMargin();
    assertEquals(0, margin.getTop());
    assertEquals(0, margin.getLeft());
    assertEquals(0, margin.getRight());
    assertEquals(0, margin.getBottom());
    assertEquals(0, disp.getBelowHeaderMargin());
    
    ColorValue marginColor = disp.getMarginColor();
    assertEquals(255, marginColor.getRed());
    assertEquals(255, marginColor.getGreen());
    assertEquals(255, marginColor.getBlue());

    
    assertFalse(view.getAutoFrameFrameset().isPresent());
    assertFalse(view.getAutoFrameTarget().isPresent());
    
    assertEquals(CollectionDesignElement.UnreadMarksMode.ALL, view.getUnreadMarksMode());
    
    CollectionDesignElement.IndexSettings index = view.getIndexSettings();
    assertEquals(CollectionDesignElement.IndexRefreshMode.AUTO, index.getRefreshMode());
    assertFalse(index.getRefreshMaxIntervalSeconds().isPresent());
    assertEquals(CollectionDesignElement.IndexDiscardMode.INACTIVE_FOR, index.getDiscardMode());
    assertEquals(TimeUnit.DAYS.toHours(19), index.getDiscardAfterHours().getAsInt());
    assertTrue(index.isRestrictInitialBuildToDesigner());
    assertFalse(index.isGenerateUniqueKeysInIndex());
    assertFalse(index.isIncludeUpdatesInTransactionLog());
    
    CollectionDesignElement.WebRenderingSettings web = view.getWebRenderingSettings();
    assertTrue(web.isTreatAsHtml());
    assertFalse(web.isUseJavaApplet());
    assertFalse(web.isAllowSelection());
    assertColorEquals(web.getActiveLinkColor(), 255, 0, 0);
    assertColorEquals(web.getUnvisitedLinkColor(), 0, 0, 255);
    assertColorEquals(web.getVisitedLinkColor(), 128, 0, 128);
    assertFalse(web.isAllowWebCrawlerIndexing());
    assertFalse(view.isAllowDominoDataService());
  }

  @Test
  public void testExampleView4() {
    final DbDesign dbDesign = this.database.getDesign();
    final View view = dbDesign.getView("Example View 4").get();
    assertFalse(view.isAllowCustomizations());
    assertEquals(CollectionDesignElement.OnOpen.GOTO_LAST_OPENED, view.getOnOpenUISetting());
    assertEquals(CollectionDesignElement.OnRefresh.REFRESH_FROM_BOTTOM, view.getOnRefreshUISetting());
    assertFalse(view.isDefaultCollection());
    assertFalse(view.isDefaultCollectionDesign());
    assertTrue(view.isShowInViewMenu());
    assertFalse(view.isEvaluateActionsOnDocumentChange());
    assertFalse(view.getWebXPageAlternative().isPresent());
    assertFalse(view.isAllowPublicAccess());
    assertTrue(view.getReaders().isEmpty());
    assertFalse(view.getColumnProfileDocName().isPresent());
    assertTrue(view.getUserDefinableNonFallbackColumns().isEmpty());
    
    assertEquals("1", view.getFormulaClass());
    view.setFormulaClass("2");
    assertEquals("2", view.getFormulaClass());
    
    CollectionDesignElement.CompositeAppSettings comp = view.getCompositeAppSettings();
    assertNotNull(comp);
    assertTrue(comp.isHideColumnHeader());
    assertTrue(comp.isShowPartialHierarchies());
    assertFalse(comp.isShowSwitcher());
    assertTrue(comp.isShowTabNavigator());
    assertEquals("foo,bar", comp.getViewers());
    assertEquals("test alias", comp.getThreadView());
    assertTrue(comp.isAllowConversationMode());
    
    DisplaySettings disp = view.getDisplaySettings();
    assertNotNull(disp);
    
    ColorValue background = disp.getBackgroundColor();
    assertEquals(255, background.getRed());
    assertEquals(255, background.getGreen());
    assertEquals(255, background.getBlue());

    assertTrue(disp.isUseAlternateRowColor());
    ColorValue altBackground = disp.getAlternateRowColor();
    assertEquals(239, altBackground.getRed());
    assertEquals(239, altBackground.getGreen());
    assertEquals(239, altBackground.getBlue());
    
    Optional<CDResource> backgroundImage = disp.getBackgroundImage();
    assertEquals("Untitled.gif", backgroundImage.get().getNamedElement());
    assertEquals(ImageRepeatMode.HORIZONTAL, disp.getBackgroundImageRepeatMode());
    
    assertEquals(CollectionDesignElement.GridStyle.NONE, disp.getGridStyle());
    ColorValue gridColor = disp.getGridColor();
    assertEquals(255, gridColor.getRed());
    assertEquals(255, gridColor.getGreen());
    assertEquals(255, gridColor.getBlue());

    assertEquals(CollectionDesignElement.HeaderStyle.NONE, disp.getHeaderStyle());
    assertEquals(1, disp.getHeaderLines());
    ColorValue headerColor = disp.getHeaderColor();
    assertEquals(255, headerColor.getRed());
    assertEquals(255, headerColor.getGreen());
    assertEquals(255, headerColor.getBlue());
    
    assertEquals(6, disp.getRowLines());
    assertEquals(ViewLineSpacing.ONE_POINT_75_SPACE, disp.getLineSpacing());
    assertFalse(disp.isShrinkRowsToContent());
    assertFalse(disp.isHideEmptyCategories());
    assertFalse(disp.isColorizeViewIcons());
    
    ColorValue unreadColor = disp.getUnreadColor();
    assertEquals(255, unreadColor.getRed());
    assertEquals(0, unreadColor.getGreen());
    assertEquals(0, unreadColor.getBlue());
    assertFalse(unreadColor.getFlags().contains(ColorValue.Flag.NOCOLOR));
    
    assertFalse(disp.isUnreadBold());
    
    ColorValue totalColor = disp.getColumnTotalColor();
    assertEquals(0, totalColor.getRed());
    assertEquals(0, totalColor.getGreen());
    assertEquals(0, totalColor.getBlue());
    
    assertFalse(disp.isShowSelectionMargin());
    assertFalse(disp.isHideSelectionMarginBorder());
    assertFalse(disp.isExtendLastColumnToWindowWidth());
    
    EdgeWidths margin = disp.getMargin();
    assertEquals(0, margin.getTop());
    assertEquals(0, margin.getLeft());
    assertEquals(0, margin.getRight());
    assertEquals(0, margin.getBottom());
    assertEquals(0, disp.getBelowHeaderMargin());
    
    ColorValue marginColor = disp.getMarginColor();
    assertEquals(255, marginColor.getRed());
    assertEquals(255, marginColor.getGreen());
    assertEquals(255, marginColor.getBlue());

    
    assertFalse(view.getAutoFrameFrameset().isPresent());
    assertFalse(view.getAutoFrameTarget().isPresent());
    
    assertEquals(CollectionDesignElement.UnreadMarksMode.NONE, view.getUnreadMarksMode());
    
    CollectionDesignElement.IndexSettings index = view.getIndexSettings();
    assertEquals(CollectionDesignElement.IndexRefreshMode.MANUAL, index.getRefreshMode());
    assertFalse(index.getRefreshMaxIntervalSeconds().isPresent());
    assertEquals(CollectionDesignElement.IndexDiscardMode.AFTER_EACH_USE, index.getDiscardMode());
    assertFalse(index.getDiscardAfterHours().isPresent());
    assertFalse(index.isRestrictInitialBuildToDesigner());
    assertFalse(index.isGenerateUniqueKeysInIndex());
    assertTrue(index.isIncludeUpdatesInTransactionLog());
    
    CollectionDesignElement.WebRenderingSettings web = view.getWebRenderingSettings();
    assertFalse(web.isTreatAsHtml());
    assertTrue(web.isUseJavaApplet());
    assertFalse(web.isAllowSelection());
    assertColorEquals(web.getActiveLinkColor(), 255, 0, 0);
    assertColorEquals(web.getUnvisitedLinkColor(), 0, 255, 0);
    assertColorEquals(web.getVisitedLinkColor(), 128, 0, 128);
    assertTrue(web.isAllowWebCrawlerIndexing());
    assertFalse(view.isAllowDominoDataService());
  }
  
  @Test
  public void testAllView() {
    final DbDesign dbDesign = this.database.getDesign();
    final View view = dbDesign.getView("All").get();
    assertFalse(view.isAllowCustomizations());
    assertTrue(view.isDefaultCollection());
  }

  @Test
  public void testFolders() {
    final DbDesign dbDesign = this.database.getDesign();
    final Collection<CollectionDesignElement> collections = dbDesign.getFolders().collect(Collectors.toList());
    assertEquals(TestDbDesignCollections.EXPECTED_IMPORT_FOLDERS, collections.size());

    {
      CollectionDesignElement view = collections.stream().filter(v -> "test folder".equals(v.getTitle())).findFirst().orElse(null);
      assertNotNull(view);

      view = dbDesign.getCollection("test folder").orElse(null);
      assertNotNull(view);
      assertInstanceOf(Folder.class, view);
      assertEquals("test folder", view.getTitle());
    }
  }

  @Test
  public void testFoldersAndViews() {
    final DbDesign dbDesign = this.database.getDesign();
    final Collection<CollectionDesignElement> collections = dbDesign.getCollections().collect(Collectors.toList());
    assertEquals(TestDbDesignCollections.EXPECTED_IMPORT_VIEWS + TestDbDesignCollections.EXPECTED_IMPORT_FOLDERS + 1,
        collections.size());

    {
      CollectionDesignElement view = collections.stream().filter(v -> "test view".equals(v.getTitle())).findFirst().orElse(null);
      assertNotNull(view);

      view = dbDesign.getCollection("test view").orElse(null);
      assertNotNull(view);
      assertInstanceOf(View.class, view);
      assertEquals("test view", view.getTitle());
    }
    {
      CollectionDesignElement view = collections.stream().filter(v -> "test folder".equals(v.getTitle())).findFirst().orElse(null);
      assertNotNull(view);

      view = dbDesign.getCollection("test folder").orElse(null);
      assertNotNull(view);
      assertInstanceOf(Folder.class, view);
      assertEquals("test folder", view.getTitle());
    }
  }

  /**
   * Iterates over all views and folders in mail12.ntf (if present) to test for
   * exceptions in
   * view-format reading
   */
  @Test
  public void testIterateMailNtf() {
    final DominoClient client = this.getClient();
    Database database;
    try {
      database = client.openDatabase("mail12.ntf");
    } catch (final FileDoesNotExistException e) {
      // That's fine - not on 12, so skip the test
      return;
    }

    database.getDesign()
        .getCollections()
        .forEach(collection -> {
          final String title = collection.getTitle();
          try {
            collection.getColumns().forEach(col -> {
              @SuppressWarnings("unused")
              final String colName = col.getItemName();
            });
          } catch (final Throwable t) {
            throw new RuntimeException(MessageFormat.format("Encountered exception in view \"{0}\"", title), t);
          }
        });
  }

  @Test
  public void testViews() {
    final DbDesign dbDesign = this.database.getDesign();
    final Collection<CollectionDesignElement> collections = dbDesign.getViews().collect(Collectors.toList());
    assertEquals(TestDbDesignCollections.EXPECTED_IMPORT_VIEWS + 1, collections.size());

    {
      CollectionDesignElement view = collections.stream().filter(v -> "test view".equals(v.getTitle())).findFirst().orElse(null);
      assertNotNull(view);

      view = dbDesign.getCollection("test view").orElse(null);
      assertNotNull(view);
      assertInstanceOf(View.class, view);
      assertEquals("test view", view.getTitle());
      assertEquals("8.5.3", view.getDesignerVersion());

      {
        assertTrue(view.isProhibitRefresh());
        view.setProhibitRefresh(false);
        assertFalse(view.isProhibitRefresh());
        view.setProhibitRefresh(true);
        assertTrue(view.isProhibitRefresh());
      }

      {
        assertFalse(view.isHideFromWeb());
        view.setHideFromWeb(true);
        assertTrue(view.isHideFromWeb());
        view.setHideFromWeb(false);
        assertFalse(view.isHideFromWeb());
      }
      {
        assertFalse(view.isHideFromNotes());
        view.setHideFromNotes(true);
        assertTrue(view.isHideFromNotes());
        view.setHideFromNotes(false);
        assertFalse(view.isHideFromNotes());
      }
      {
        assertFalse(view.isHideFromMobile());
        view.setHideFromMobile(true);
        assertTrue(view.isHideFromMobile());
        view.setHideFromMobile(false);
        assertFalse(view.isHideFromMobile());
      }
    }
  }
  
  @Test
  public void testDeletedSharedColumnView() {
    DbDesign design = this.database.getDesign();
    View view = design.getView("Shared Column Deletion View").get();
    List<CollectionColumn> columns = view.getColumns();
    assertEquals(6, columns.size());
    {
      CollectionColumn col = columns.get(0);
      assertEquals("Shared Col to Delete", col.getTitle());
      assertEquals("@NoteID + \"ghost column\"", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(1);
      assertEquals("Real col", col.getTitle());
      assertEquals("@DocNumber", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertTrue(col.isNameColumn());
      assertTrue(col.getOnlinePresenceNameColumn().isPresent());
      assertEquals("SomeOnlineCol", col.getOnlinePresenceNameColumn().get());
    }
    {
      CollectionColumn col = columns.get(2);
      assertEquals("#", col.getTitle());
      assertEquals("@DocNumber", col.getFormula());
      assertTrue(col.isSharedColumn());
      assertTrue(col.getSharedColumnName().isPresent());
      assertEquals("testcol", col.getSharedColumnName().get());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(3);
      assertEquals("Shared Col to Delete", col.getTitle());
      assertEquals("@NoteID + \"ghost column\"", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(4);
      assertEquals("Real col 2", col.getTitle());
      assertEquals("@NoteID + \"I am real col 2\"", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(5);
      assertEquals("I am test col 2", col.getTitle());
      assertEquals("Foo", col.getItemName());
      assertTrue(col.isSharedColumn());
      assertTrue(col.getSharedColumnName().isPresent());
      assertEquals("testcol2", col.getSharedColumnName().get());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
  }

  @Test
  public void testDeletedSharedColumnView2() {
    DbDesign design = this.database.getDesign();
    View view = design.getView("Shared Column Deletion View 2").get();
    List<CollectionColumn> columns = view.getColumns();
    assertEquals(6, columns.size());
    {
      CollectionColumn col = columns.get(0);
      assertEquals("Shared Col to Delete", col.getTitle());
      assertEquals("@NoteID + \"ghost column\"", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(1);
      assertEquals("Real col", col.getTitle());
      assertEquals("@DocNumber", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(2);
      assertEquals("#", col.getTitle());
      assertEquals("@DocNumber", col.getFormula());
      assertTrue(col.isSharedColumn());
      assertTrue(col.getSharedColumnName().isPresent());
      assertEquals("testcol", col.getSharedColumnName().get());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(3);
      assertEquals("Shared Col to Delete", col.getTitle());
      assertEquals("@NoteID + \"ghost column\"", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(4);
      assertEquals("Real col 2", col.getTitle());
      assertEquals("@NoteID + \"I am real col 2\"", col.getFormula());
      assertFalse(col.isSharedColumn());
      assertFalse(col.getSharedColumnName().isPresent());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
    {
      CollectionColumn col = columns.get(5);
      assertEquals("I am test col 2", col.getTitle());
      assertEquals("Foo", col.getItemName());
      assertTrue(col.isSharedColumn());
      assertTrue(col.getSharedColumnName().isPresent());
      assertEquals("testcol2", col.getSharedColumnName().get());
      assertFalse(col.isNameColumn());
      assertFalse(col.getOnlinePresenceNameColumn().isPresent());
    }
  }
  
  @Test
  public void testMail12NtfByCategory() {
    final DominoClient client = this.getClient();
    Database database;
    try {
      database = client.openDatabase("mail12.ntf");
    } catch (final FileDoesNotExistException e) {
      // That's fine - not on 12, so skip the test
      return;
    }
    
    DbDesign design = database.getDesign();
    View byCategory = design.getView("$ByCategory").get();
    byCategory.getColumns().forEach(col -> {
      System.out.println("read col " + col.getItemName());
    });
  }
  
  private void assertColorEquals(ColorValue color, int red, int green, int blue) {
    assertNotNull(color);
    assertEquals(red, color.getRed());
    assertEquals(green, color.getGreen());
    assertEquals(blue, color.getBlue());
  }
}
