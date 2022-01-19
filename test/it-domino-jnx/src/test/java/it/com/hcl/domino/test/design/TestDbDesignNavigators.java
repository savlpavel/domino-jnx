package it.com.hcl.domino.test.design;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hcl.domino.DominoClient;
import com.hcl.domino.data.Database;
import com.hcl.domino.data.StandardColors;
import com.hcl.domino.design.DbDesign;
import com.hcl.domino.design.DesignType;
import com.hcl.domino.design.Navigator;
import com.hcl.domino.design.navigator.NavigatorLineStyle;
import com.hcl.domino.richtext.records.ViewmapActionRecord;
import com.hcl.domino.richtext.records.ViewmapDatasetRecord;
import com.hcl.domino.richtext.records.ViewmapHeaderRecord;
import com.hcl.domino.security.Acl;
import com.hcl.domino.security.AclEntry;
import com.hcl.domino.security.AclFlag;
import com.hcl.domino.security.AclLevel;

import it.com.hcl.domino.test.AbstractNotesRuntimeTest;

@SuppressWarnings("nls")
public class TestDbDesignNavigators extends AbstractDesignTest {
  public static final int EXPECTED_IMPORT_NAVIGATORS = 1;
  private static String dbPath;

  @AfterAll
  public static void termDesignDb() {
    try {
      Files.deleteIfExists(Paths.get(dbPath));
    } catch (final Throwable t) {
      System.err.println("Unable to delete database " + dbPath + ": " + t);
    }
  }

  private Database database;

  @BeforeEach
  public void initDesignDb() throws IOException, URISyntaxException {
    if (this.database == null) {
      final DominoClient client = this.getClient();
      if (dbPath == null) {
        this.database = AbstractNotesRuntimeTest.createTempDb(client);
        
        Acl acl = this.database.getACL();
        Optional<AclEntry> entry = acl.getEntry(client.getEffectiveUserName());
        if(entry.isPresent()) {
          acl.updateEntry(client.getEffectiveUserName(), null, null, Arrays.asList("[Admin]"), null);
        } else {
          acl.addEntry(client.getEffectiveUserName(), AclLevel.MANAGER, Arrays.asList("[Admin]"), EnumSet.allOf(AclFlag.class));
        }
        acl.save();
        
        dbPath = this.database.getAbsoluteFilePath();
        AbstractNotesRuntimeTest.populateResourceDxl("/dxl/testDbDesignNavigators", this.database);
      } else {
        this.database = client.openDatabase("", dbPath);
      }
    }
  }
  
  @Test
  public void testNavigators() {
    DbDesign design = database.getDesign();
    List<Navigator> navs = design.getNavigators().collect(Collectors.toList());
    assertEquals(1, navs.size());
    assertInstanceOf(Navigator.class, navs.get(0));
  }
  
  @Test
  public void testTestNav() {
    DbDesign design = database.getDesign();
    Navigator nav = design.getNavigator("testnav").get();
    
    {
      List<?> imagemap = nav.getImageMap();
      assertTrue(imagemap.stream().anyMatch(ViewmapHeaderRecord.class::isInstance));
    }
    {
      List<?> dataset = nav.getDataSet();
      assertTrue(dataset.stream().anyMatch(ViewmapDatasetRecord.class::isInstance));
    }
    {
      List<?> layout = nav.getLayout();
      assertTrue(layout.stream().anyMatch(ViewmapHeaderRecord.class::isInstance));
      
      {
        ViewmapActionRecord action = (ViewmapActionRecord)layout.get(2);
        assertEquals(StandardColors.DarkMagenta2, action.getHighlightOutlineColor().get());
        assertEquals(StandardColors.Heather, action.getHighlightFillColor().get());
        assertEquals(NavigatorLineStyle.SOLID, action.getHighlightOutlineStyle());
        assertEquals(ViewmapActionRecord.Action.NONE, action.getClickAction().get());
        assertEquals(DesignType.SHARED, action.getActionDataDesignType());
      }
      {
        ViewmapActionRecord action = (ViewmapActionRecord)layout.get(4);
        assertEquals(StandardColors.Lemon, action.getHighlightOutlineColor().get());
        assertEquals(StandardColors.LilacMist, action.getHighlightFillColor().get());
        assertEquals(NavigatorLineStyle.SOLID, action.getHighlightOutlineStyle());
        assertEquals(8, action.getHighlightOutlineWidth());
        assertEquals(ViewmapActionRecord.Action.NONE, action.getClickAction().get());
        assertEquals(DesignType.SHARED, action.getActionDataDesignType());
      }
      {
        ViewmapActionRecord action = (ViewmapActionRecord)layout.get(6);
        assertEquals(StandardColors.Red, action.getHighlightOutlineColor().get());
        assertEquals(StandardColors.Black, action.getHighlightFillColor().get());
        assertEquals(NavigatorLineStyle.SOLID, action.getHighlightOutlineStyle());
        assertEquals(ViewmapActionRecord.Action.RUNSCRIPT, action.getClickAction().get());
        assertTrue(action.getActionString().get().contains("I am a LotusScript rectangle"));
      }
      {
        ViewmapActionRecord action = (ViewmapActionRecord)layout.get(8);
        assertEquals(StandardColors.Red, action.getHighlightOutlineColor().get());
        assertEquals(StandardColors.Black, action.getHighlightFillColor().get());
        assertEquals(NavigatorLineStyle.SOLID, action.getHighlightOutlineStyle());
        assertEquals(ViewmapActionRecord.Action.RUNFORMULA, action.getClickAction().get());
        assertTrue(action.getActionFormula().get().equals("@StatusBar(\"I am a formula circle.\")"));
      }
      {
        ViewmapActionRecord action = (ViewmapActionRecord)layout.get(10);
        assertEquals(StandardColors.Red, action.getHighlightOutlineColor().get());
        assertEquals(StandardColors.Black, action.getHighlightFillColor().get());
        assertEquals(NavigatorLineStyle.SOLID, action.getHighlightOutlineStyle());
        assertEquals(ViewmapActionRecord.Action.SWITCHVIEW, action.getClickAction().get());
        assertTrue(action.getActionString().get().equals("other alias"));
      }
    }
    
  }
}
