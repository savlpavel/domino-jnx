package com.hcl.domino.commons.design;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.hcl.domino.commons.design.action.DefaultActionBarAction;
import com.hcl.domino.data.Document;
import com.hcl.domino.data.NotesFont;
import com.hcl.domino.design.ActionBar;
import com.hcl.domino.design.DesignElement.ClassicThemeBehavior;
import com.hcl.domino.design.action.ActionBarAction;
import com.hcl.domino.design.EdgeWidths;
import com.hcl.domino.design.format.ActionBarBackgroundRepeat;
import com.hcl.domino.design.format.ActionBarTextAlignment;
import com.hcl.domino.design.format.ActionButtonHeightMode;
import com.hcl.domino.design.format.ActionWidthMode;
import com.hcl.domino.design.format.BorderStyle;
import com.hcl.domino.design.format.ButtonBorderDisplay;
import com.hcl.domino.richtext.RichTextRecordList;
import com.hcl.domino.richtext.records.CDAction;
import com.hcl.domino.richtext.records.CDActionBar;
import com.hcl.domino.richtext.records.CDActionBarExt;
import com.hcl.domino.richtext.records.CDBorderInfo;
import com.hcl.domino.richtext.records.CDFontTable;
import com.hcl.domino.richtext.records.CDResource;
import com.hcl.domino.richtext.records.RichTextRecord;
import com.hcl.domino.richtext.structures.ColorValue;
import com.hcl.domino.richtext.structures.FontStyle;
import com.hcl.domino.richtext.structures.LengthValue;

/**
 * Default implementation of {@link ActionBar} that is based around having
 * a contextual document and an item name for the actions content.
 * 
 * @author Jesse Gallagher
 * @since 1.0.32
 */
public class DefaultActionBar implements ActionBar {
  private final Document doc;
  private final String itemName;

  public DefaultActionBar(Document doc, String itemName) {
    this.doc = doc;
    this.itemName = itemName;
  }

  @Override
  public ClassicThemeBehavior getClassicThemeBehavior() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getThemeSetting)
      .orElse(ClassicThemeBehavior.USE_DATABASE_SETTING);
  }

  @Override
  public Alignment getAlignment() {
    if(getActionBarRecord().getFlags().contains(CDActionBar.Flag.ALIGN_RIGHT)) {
      return Alignment.RIGHT;
    } else {
      return Alignment.LEFT;
    }
  }

  @Override
  public boolean isUseJavaApplet() {
    return getActionBarRecord().getFlags().contains(CDActionBar.Flag.USE_APPLET);
  }

  @Override
  public boolean isShowDefaultItemsInContextMenu() {
    return !getActionBarRecord().getFlags().contains(CDActionBar.Flag.SUPPRESS_SYS_POPUPS);
  }

  @Override
  public ActionButtonHeightMode getHeightMode() {
    return getActionBarExtRecord()
      .map(ext -> {
        switch(ext.getHeight().getUnit()) {
        case PIXELS:
          return ActionButtonHeightMode.FIXED;
        case EXS:
          return ActionButtonHeightMode.EXS;
        default:
          return ActionButtonHeightMode.DEFAULT;
        }
      })
      .orElse(ActionButtonHeightMode.DEFAULT);
  }

  @Override
  public double getHeightSpec() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getHeight)
      .map(LengthValue::getLength)
      .orElse(0d);
  }

  @Override
  public NotesFont getHeightSizingFont() {
    CDFontTable fontTable = getFontTable().orElse(null);
    FontStyle fontStyle = getActionBarExtRecord()
      .map(CDActionBarExt::getFontStyle)
      .orElseGet(DesignColorsAndFonts::defaultFont);
    return new FontTableNotesFont(fontStyle, fontTable);
  }

  @Override
  public ColorValue getBackgroundColor() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getBackgroundColor)
      .orElseGet(DesignColorsAndFonts::systemColor);
  }

  @Override
  public Optional<CDResource> getBackgroundImage() {
    // If the background flag is set, then the resource will follow the CDACTIONBAR record
    // If there's also a button background, that comes first
    RichTextRecordList rt = getRichTextItem();
    boolean hasButtonBackground = false;
    boolean foundButtonBackground = false;
    for(RichTextRecord<?> record : rt) {
      if(record instanceof CDActionBar) {
        CDActionBar ext = (CDActionBar)record;
        Set<CDActionBar.Flag> flags = ext.getFlags();
        // If there's no bar background image, end early
        if(!flags.contains(CDActionBar.Flag.BARBCK_IMGRSRC)) {
          return Optional.empty();
        }
        if(flags.contains(CDActionBar.Flag.BTNBCK_IMGRSRC)) {
          // Mark that we'll have to skip the first CDResource
          hasButtonBackground = true;
        }
      }
      
      if(record instanceof CDResource) {
        if(hasButtonBackground && !foundButtonBackground) {
          // Then this is the per-button background - flag as such and skip
          foundButtonBackground = true;
        } else {
          // This must be what we want
          return Optional.of((CDResource)record);
        }
      }
    }
    
    throw new IllegalStateException("Unable to find bar background image resource");
  }

  @Override
  public ActionBarBackgroundRepeat getBackgroundImageRepeatMode() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getBackgroundRepeat)
      .orElse(ActionBarBackgroundRepeat.REPEATONCE);
  }

  @Override
  public BorderStyle getBorderStyle() {
    return getBorderInfo()
      .map(CDBorderInfo::getBorderStyle)
      .orElse(BorderStyle.SOLID);
  }

  @Override
  public ColorValue getBorderColor() {
    return getBorderInfo()
      .map(CDBorderInfo::getColor)
      .orElseGet(DesignColorsAndFonts::blackColor);
  }

  @Override
  public boolean isUseDropShadow() {
    return getBorderInfo()
      .map(CDBorderInfo::getBorderFlags)
      .map(flags -> flags.contains(CDBorderInfo.BorderFlag.DROP_SHADOW))
      .orElse(false);
  }

  @Override
  public int getDropShadowWidth() {
    return getBorderInfo()
      .map(CDBorderInfo::getDropShadowWidth)
      .orElse(0);
  }

  @Override
  public EdgeWidths getInsideMargins() {
    return new LambdaEdgeWidths(
      () -> getBorderInfo().map(CDBorderInfo::getInnerWidthTop).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getInnerWidthLeft).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getInnerWidthRight).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getInnerWidthBottom).orElse(0)
    );
  }

  @Override
  public EdgeWidths getBorderWidths() {
    return new LambdaEdgeWidths(
      () -> getBorderInfo().map(CDBorderInfo::getBorderWidthTop).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getBorderWidthLeft).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getBorderWidthRight).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getBorderWidthBottom).orElse(0)
    );
  }

  @Override
  public EdgeWidths getOutsideMargins() {
    return new LambdaEdgeWidths(
      () -> getBorderInfo().map(CDBorderInfo::getOuterWidthTop).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getOuterWidthLeft).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getOuterWidthRight).orElse(0),
      () -> getBorderInfo().map(CDBorderInfo::getOuterWidthBottom).orElse(0)
    );
  }

  @Override
  public ButtonHeightMode getButtonHeightMode() {
    Set<CDActionBar.Flag> flags = getActionBarRecord().getFlags();
    if(flags.contains(CDActionBar.Flag.SET_HEIGHT)) {
      if(flags.contains(CDActionBar.Flag.ABSOLUTE_HEIGHT)) {
        return ButtonHeightMode.FIXED_SIZE;
      } else if(flags.contains(CDActionBar.Flag.BACKGROUND_HEIGHT)) {
        return ButtonHeightMode.BACKGROUND_SIZE;
      } else {
        return ButtonHeightMode.MINIMUM_SIZE;
      }
    } else {
      return ButtonHeightMode.DEFAULT;
    }
  }

  @Override
  public int getButtonHeightSpec() {
    return getActionBarRecord().getButtonHeight();
  }

  @Override
  public ActionWidthMode getButtonWidthMode() {
    Set<CDActionBar.Flag> flags = getActionBarRecord().getFlags();
    if(flags.contains(CDActionBar.Flag.SET_WIDTH)) {
      if(flags.contains(CDActionBar.Flag.BACKGROUND_WIDTH)) {
        return ActionWidthMode.BACKGROUND;
      } else {
        return ActionWidthMode.ABSOLUTE;
      }
    } else {
      return ActionWidthMode.DEFAULT;
    }
  }

  @Override
  public int getButtonWidth() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getButtonWidth)
      .orElse(0);
  }

  @Override
  public boolean isFixedSizeButtonMargin() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getFlags)
      .map(flags -> flags.contains(CDActionBarExt.Flag.WIDTH_STYLE_VALID))
      .orElse(false);
  }

  @Override
  public int getButtonVerticalMarginSize() {
    return getActionBarRecord().getHeightSpacing();
  }
  
  @Override
  public ButtonBorderDisplay getButtonBorderMode() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getBorderDisplay)
      .orElse(ButtonBorderDisplay.ALWAYS);
  }

  @Override
  public ActionBarTextAlignment getButtonTextAlignment() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getTextJustify)
      .orElse(ActionBarTextAlignment.LEFT);
  }

  @Override
  public int getButtonInternalMarginSize() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getButtonInternalMargin)
      .orElse(0);
  }

  @Override
  public boolean isAlwaysShowDropDowns() {
    return getActionBarRecord().getFlags().contains(CDActionBar.Flag.SHOW_HINKY_ALWAYS);
  }

  @Override
  public ColorValue getButtonBackgroundColor() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getButtonColor)
      .orElseGet(DesignColorsAndFonts::systemColor);
  }

  @Override
  public Optional<CDResource> getButtonBackgroundImage() {
    // If the background flag is set, then the resource will follow the CDACTIONBAR record
    RichTextRecordList rt = getRichTextItem();
    for(RichTextRecord<?> record : rt) {
      if(record instanceof CDActionBar) {
        CDActionBar ext = (CDActionBar)record;
        Set<CDActionBar.Flag> flags = ext.getFlags();
        // If there's no button background image, end early
        if(!flags.contains(CDActionBar.Flag.BTNBCK_IMGRSRC)) {
          return Optional.empty();
        }
      }
      
      if(record instanceof CDResource) {
        return Optional.of((CDResource)record);
      }
    }
    
    throw new IllegalStateException("Unable to find button background image resource");
  }

  @Override
  public NotesFont getFont() {
    CDFontTable fontTable = getFontTable().orElse(null);
    FontStyle fontStyle = getActionBarRecord().getFontStyle();
    return new FontTableNotesFont(fontStyle, fontTable);
  }
  
  @Override
  public ColorValue getFontColor() {
    return getActionBarExtRecord()
      .map(CDActionBarExt::getFontColor)
      .orElseGet(DesignColorsAndFonts::blackColor);
  }
  
  public List<ActionBarAction> getActions() {
    List<ActionBarAction> result = new ArrayList<>();
    List<RichTextRecord<?>> stash = new ArrayList<>();
    for(RichTextRecord<?> record : getRichTextItem()) {
      if(record instanceof CDAction) {
        // Then it's the start of a new action. Flush the old stash if needed
        if(!stash.isEmpty()) {
          result.add(new DefaultActionBarAction(new ArrayList<>(stash)));
          stash.clear();
        }
        stash.add(record);
      } else {
        // For any other type, add it to the stash if we have an open action
        if(!stash.isEmpty()) {
          stash.add(record);
        } 
      }
    }
    // Process the entries for the final action
    if(!stash.isEmpty()) {
      result.add(new DefaultActionBarAction(new ArrayList<>(stash)));
    }
    return result;
  }
  
  // *******************************************************************************
  // * Internal implementation utilities
  // *******************************************************************************
  
  private RichTextRecordList getRichTextItem() {
    return doc.getRichTextItem(itemName);
  }

  private CDActionBar getActionBarRecord() {
    return getRichTextItem()
      .stream()
      .filter(CDActionBar.class::isInstance)
      .map(CDActionBar.class::cast)
      .findFirst()
      .orElseThrow(() -> new IllegalStateException(MessageFormat.format("Unable to locate CDACTIONBAR record in item \"{0}\"", itemName)));
  }

  private Optional<CDActionBarExt> getActionBarExtRecord() {
    return getRichTextItem()
      .stream()
      .filter(CDActionBarExt.class::isInstance)
      .map(CDActionBarExt.class::cast)
      .findFirst();
  }
  
  private Optional<CDFontTable> getFontTable() {
    return getRichTextItem()
      .stream()
      .filter(CDFontTable.class::isInstance)
      .map(CDFontTable.class::cast)
      .findFirst();
  }
  
  private Optional<CDBorderInfo> getBorderInfo() {
    return getRichTextItem()
      .stream()
      .filter(CDBorderInfo.class::isInstance)
      .map(CDBorderInfo.class::cast)
      .findFirst();
  }
}