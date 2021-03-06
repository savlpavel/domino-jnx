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
package it.com.hcl.domino.test.data;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.hcl.domino.DominoClient;
import com.hcl.domino.data.CollectionEntry;
import com.hcl.domino.data.Database;
import com.hcl.domino.data.Database.OpenDocumentMode;
import com.hcl.domino.data.Document;
import com.hcl.domino.data.DocumentClass;
import com.hcl.domino.data.DominoDateTime;
import com.hcl.domino.data.IDTable;
import com.hcl.domino.dbdirectory.DirectorySearchQuery.SearchFlag;
import com.hcl.domino.exception.LotusScriptCompilationException;

import it.com.hcl.domino.test.AbstractNotesRuntimeTest;

@SuppressWarnings("nls")
public class TestDocuments extends AbstractNotesRuntimeTest {

  @Test
  public void testAppendItemValueVector() throws Exception {
    this.withTempDb(database -> {
      final Document testdoc = database.createDocument();
      final Vector<String> someobject = new Vector<>();
      someobject.add("cat1");
      someobject.add("cat2");
      testdoc.appendItemValue("categories", someobject);
      Assertions.assertEquals(someobject, testdoc.getAsList("categories", String.class, null));
    });
  }

  @Test
  public void testAppendToTextList() throws Exception {
    this.withTempDb(database -> {
      final Document testdoc = database.createDocument();

      final List<String> values = new ArrayList<>();
      values.add("A");
      values.add("B");
      testdoc.replaceItemValue("list", values);
      Assertions.assertEquals(values, testdoc.getAsList("list", String.class, null));

      testdoc.appendToTextList("list", "C", true);
      testdoc.appendToTextList("list", "D", true);
      testdoc.appendToTextList("list", "A", false);

      values.add("C");
      values.add("D");
      Assertions.assertEquals(values, testdoc.getAsList("list", String.class, null));
    });
  }

  @Test
  public void testCompileInvalidLotusScript() throws Exception {
    this.withResourceDxl("/dxl/testCompileLotusScript", database -> {
      final Document doc = database
          .queryFormula("$TITLE='Test LS'", null, EnumSet.noneOf(SearchFlag.class), null, EnumSet.of(DocumentClass.ALLNONDATA))
          .getDocuments()
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("Unable to find Test LS Library"));
      Assertions.assertFalse(doc.hasItem("$ScriptLib_O"), "Library should not yet have compiled LotusScript");
      Assertions.assertThrows(LotusScriptCompilationException.class, () -> doc.compileLotusScript());
      Assertions.assertFalse(doc.hasItem("$ScriptLib_O"), "Library should still not have compiled LotusScript");
    });
  }

  @Test
  public void testCompileLotusScript() throws Exception {
    this.withResourceDxl("/dxl/testCompileLotusScript", database -> {
      final Document doc = database
          .queryFormula("$TITLE='Test LS Form'", null, EnumSet.noneOf(SearchFlag.class), null, EnumSet.of(DocumentClass.ALLNONDATA))
          .getDocuments()
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("Unable to find Test LS Form"));
      Assertions.assertFalse(doc.hasItem("$SCRIPTOBJ_0"), "Form should not yet have compiled LotusScript");
      doc.compileLotusScript();
      Assertions.assertTrue(doc.hasItem("$SCRIPTOBJ_0"), "Form should now have compiled LotusScript");
    });
  }

  @Test
  public void testDocumentInvalidCreationDate() throws Exception {
    this.withResourceDb("/nsf/invalidtimedate.nsf", database -> {
      final CollectionEntry entry = database.queryDocuments()
          .computeValues("Created", "@Created")
          .collectEntries()
          .get(0);

      DominoDateTime dt = entry.get("Created", DominoDateTime.class, null);
      Assertions.assertNotNull(dt);
      Assertions.assertFalse(dt.isValid());

      final Document doc = database
          .queryFormula(" Subject='**** Calendaring and Scheduling Meta Data Doc - DO NOT MODIFY ****' ", null,
              Collections.emptySet(), null, EnumSet.of(DocumentClass.DOCUMENT))
          .getDocuments()
          .findFirst()
          .get();
      dt = doc.getCreated();
      Assertions.assertNotNull(dt);
      Assertions.assertFalse(dt.isValid());
    });
  }

  @Test
  public void testForEachItemName() throws Exception {
    this.withTempDb(database -> {
      final Document doc = database.createDocument();
      doc.replaceItemValue("foo", "bar");
      doc.replaceItemValue("bar", "baz");
      doc.forEachItem("foo", (item, loop) -> {
        Assertions.assertEquals("foo", item.getName());
      });
    });
  }

  @Test
  public void testNamesNsfAclNote() {
    final DominoClient client = this.getClient();
    final Database names = client.openDatabase("names.nsf"); //$NON-NLS-1$

    final Document acl = names.getDocumentById(0xFFFF0000 | 0x0040).get(); // ACL note by Special ID
    Assertions.assertNotEquals(null, acl);

    final Set<DocumentClass> classes = acl.getDocumentClass();
    Assertions.assertNotEquals(null, classes);
    Assertions.assertTrue(classes.contains(DocumentClass.ACL));
    Assertions.assertFalse(classes.contains(DocumentClass.DATA));
  }

  @Test
  public void testNamesNsfNewNote() {
    final DominoClient client = this.getClient();
    final Database names = client.openDatabase("names.nsf"); //$NON-NLS-1$

    final Document note = names.createDocument();
    Assertions.assertNotEquals(null, note);

    note.replaceItemValue("foo", "bar");
    Assertions.assertEquals("bar", note.get("foo", String.class, ""));

    Assertions.assertEquals("", note.get("does-not-exist", String.class, ""));

    note.setUNID("12345678901234567890123456789012");
    Assertions.assertEquals("12345678901234567890123456789012", note.getUNID());
  }

  /**
   * Ensures that doc.removeItem can be called directly without performing a
   * hasItem test
   *
   * @throws Exception if a test problem occurs
   */
  @Test
  public void testRemoveFakeItem() throws Exception {
    this.withTempDb(database -> {
      final Document doc = database.createDocument();
      doc.replaceItemValue("Heh", "Hey");
      doc.removeItem("FooBarBaz");
      Assertions.assertEquals("Hey", doc.getAsText("Heh", ' '));
    });
  }

  @Test
  public void testResponseCount() throws Exception {
    this.withTempDb(database -> {
      String parentUnid;
      {
        final Document parent = database.createDocument();
        parent.replaceItemValue("Form", "Parent");
        parent.save();
        Assertions.assertEquals("", parent.getParentDocumentUNID());
        parentUnid = parent.getUNID();
      }

      for (int i = 0; i < 10; i++) {
        final Document child = database.createDocument();
        child.replaceItemValue("Form", "Child");
        child.makeResponse(parentUnid);
        child.save();
      }

      final Document parent = database.getDocumentByUNID(parentUnid).get();
      Assertions.assertEquals(10, parent.getResponseCount());
    });
  }

  @Test
  public void testResponseParent() throws Exception {
    this.withTempDb(database -> {
      String parentUnid;
      {
        final Document parent = database.createDocument();
        parent.replaceItemValue("Form", "Parent");
        parent.save();
        Assertions.assertEquals("", parent.getParentDocumentUNID());
        parentUnid = parent.getUNID();
      }

      final Document child = database.createDocument();
      child.replaceItemValue("Form", "Child");
      child.makeResponse(parentUnid);
      child.save();

      final String unid = child.getParentDocumentUNID();
      Assertions.assertEquals(parentUnid, unid);
      final Document parent = database.getDocumentByUNID(unid).get();
      Assertions.assertEquals("Parent", parent.get("Form", String.class, null));
    });
  }

  @Test
  public void testResponseTable() throws Exception {
    this.withTempDb(database -> {
      String parentUnid;
      {
        final Document parent = database.createDocument();
        parent.replaceItemValue("Form", "Parent");
        parent.save();
        Assertions.assertEquals("", parent.getParentDocumentUNID());
        parentUnid = parent.getUNID();
      }

      final Set<String> childIds = new HashSet<>();

      for (int i = 0; i < 10; i++) {
        final Document child = database.createDocument();
        child.replaceItemValue("Form", "Child");
        child.makeResponse(parentUnid);
        child.save();
        childIds.add(child.getUNID());
      }

      final Document parent = database.getDocumentByUNID(parentUnid, EnumSet.of(OpenDocumentMode.LOAD_RESPONSES)).get();
      Assertions.assertEquals(10, parent.getResponseCount());
      final IDTable children = parent.getResponses();
      final Set<String> foundChildIds = children.stream()
          .map(database::getDocumentById)
          .map(Optional::get)
          .map(Document::getUNID)
          .collect(Collectors.toSet());
      Assertions.assertEquals(childIds, foundChildIds);
    });
  }

  @Test
  public void testSetGetBasicItemTypes() throws Exception {
    this.withTempDb(database -> {
      final Document testdoc = database.createDocument();

      {
        final String str = "testval";
        testdoc.replaceItemValue("strvalue", str);
        Assertions.assertEquals(str, testdoc.get("strvalue", String.class, null));
      }
      {
        final DominoDateTime dt = this.getClient().createDateTime(
            Instant.now().with(ChronoField.MILLI_OF_SECOND, 50) // Domino can only store 1/100 seconds
        );
        testdoc.replaceItemValue("datevalue", dt);
        Assertions.assertEquals(dt, testdoc.get("datevalue", DominoDateTime.class, null));
      }
      {
        final int nr = 1352;
        testdoc.replaceItemValue("intvalue", nr);
        Assertions.assertEquals(nr, testdoc.get("intvalue", Integer.class, null));
      }
      {
        final double nr = 13.52;
        testdoc.replaceItemValue("doublevalue", nr);
        Assertions.assertEquals(nr, testdoc.get("doublevalue", Double.class, null));
      }

      {
        final List<String> strValues = Arrays.asList("A", "B");
        testdoc.replaceItemValue("strvalues", strValues);
        Assertions.assertEquals(strValues, testdoc.getAsList("strvalues", String.class, null));
        Assertions.assertEquals(strValues.get(0), testdoc.get("strvalues", String.class, null));
      }
      {
        final List<Integer> intValues = Arrays.asList(1, 3, 5, 2);
        testdoc.replaceItemValue("intvalues", intValues);
        Assertions.assertEquals(intValues, testdoc.getAsList("intvalues", Integer.class, null));
        Assertions.assertEquals(intValues.get(0), testdoc.get("intvalues", Integer.class, null));
      }
      {
        final List<Double> doubleValues = Arrays.asList(1.5, 3.4, 5.3, 2.1);
        testdoc.replaceItemValue("doublevalues", doubleValues);
        Assertions.assertEquals(doubleValues, testdoc.getAsList("doublevalues", Double.class, null));
        Assertions.assertEquals(doubleValues.get(0), testdoc.get("doublevalues", Double.class, null));
      }
      {
        final DominoDateTime dt1 = this.getClient().createDateTime(
            Instant.now().with(ChronoField.MILLI_OF_SECOND, 40) // Domino can only store 1/100 seconds
        );
        final DominoDateTime dt2 = this.getClient().createDateTime(
            Instant.now().plusSeconds(10000).with(ChronoField.MILLI_OF_SECOND, 50) // Domino can only store 1/100 seconds
        );
        final List<DominoDateTime> dtValues = Arrays.asList(dt1, dt2);
        testdoc.replaceItemValue("datevalues", dtValues);
        Assertions.assertEquals(dtValues, testdoc.getAsList("datevalues", DominoDateTime.class, null));
        Assertions.assertEquals(dtValues.get(0), testdoc.get("datevalues", DominoDateTime.class, null));
      }
    });
  }

  @Test
  public void writeDesignNote() throws Exception {
    this.withTempDb(database -> {
      String docId;
      {
        final Document doc = database.createDocument();
        Assertions.assertFalse(doc.getDocumentClass().contains(DocumentClass.FORM));
        Assertions.assertTrue(doc.getDocumentClass().contains(DocumentClass.DATA));
        doc.setDocumentClass(DocumentClass.FORM);
        Assertions.assertTrue(doc.getDocumentClass().contains(DocumentClass.FORM));
        Assertions.assertFalse(doc.getDocumentClass().contains(DocumentClass.DATA));
        doc.save();
        docId = doc.getUNID();
      }
      {
        final Document doc = database.getDocumentByUNID(docId).get();
        Assertions.assertTrue(doc.getDocumentClass().contains(DocumentClass.FORM));
        Assertions.assertFalse(doc.getDocumentClass().contains(DocumentClass.DATA));
      }
    });
  }
}
