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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.hcl.domino.admin.idvault.UserId;
import com.hcl.domino.commons.design.DesignUtil.DesignMapping;
import com.hcl.domino.commons.util.BufferingCallbackOutputStream;
import com.hcl.domino.commons.util.StringUtil;
import com.hcl.domino.data.CollectionEntry;
import com.hcl.domino.data.CollectionSearchQuery;
import com.hcl.domino.data.Database;
import com.hcl.domino.data.Database.Action;
import com.hcl.domino.data.Document;
import com.hcl.domino.data.DocumentClass;
import com.hcl.domino.data.DominoCollection;
import com.hcl.domino.dbdirectory.DirectorySearchQuery.SearchFlag;
import com.hcl.domino.design.AboutDocument;
import com.hcl.domino.design.CollectionDesignElement;
import com.hcl.domino.design.CompositeApplication;
import com.hcl.domino.design.CompositeComponent;
import com.hcl.domino.design.DatabaseScriptLibrary;
import com.hcl.domino.design.DbDesign;
import com.hcl.domino.design.DbProperties;
import com.hcl.domino.design.DesignAgent;
import com.hcl.domino.design.DesignElement;
import com.hcl.domino.design.FileResource;
import com.hcl.domino.design.Folder;
import com.hcl.domino.design.Form;
import com.hcl.domino.design.Frameset;
import com.hcl.domino.design.ImageResource;
import com.hcl.domino.design.JavaScriptLibrary;
import com.hcl.domino.design.NamedFileElement;
import com.hcl.domino.design.Navigator;
import com.hcl.domino.design.Outline;
import com.hcl.domino.design.Page;
import com.hcl.domino.design.ScriptLibrary;
import com.hcl.domino.design.ServerJavaScriptLibrary;
import com.hcl.domino.design.SharedActions;
import com.hcl.domino.design.SharedColumn;
import com.hcl.domino.design.SharedField;
import com.hcl.domino.design.StyleSheet;
import com.hcl.domino.design.Subform;
import com.hcl.domino.design.Theme;
import com.hcl.domino.design.UsingDocument;
import com.hcl.domino.design.View;
import com.hcl.domino.design.WiringProperties;
import com.hcl.domino.design.XPage;
import com.hcl.domino.exception.SpecialObjectCannotBeLocatedException;
import com.hcl.domino.misc.NotesConstants;

public abstract class AbstractDbDesign implements DbDesign {
  /**
   * Represents flag patterns to be considered filesystem resources, designed to match
   * XPages's conception of an NSF.
   */
  public static final List<String> FILERES_PATTERNS = Collections.unmodifiableList(Arrays.asList(
    "+g-K;`", //$NON-NLS-1$
    NotesConstants.DFLAGPAT_IMAGE_RESOURCE,
    NotesConstants.DFLAGPAT_STYLE_SHEET_RESOURCE,
    NotesConstants.DFLAGPAT_COMPDEF,
    NotesConstants.DFLAGPAT_STYLEKIT,
    NotesConstants.DFLAGPAT_SCRIPTLIB_SERVER_JS,
    NotesConstants.DFLAGPAT_SCRIPTLIB_JS
  ));

  public static class DesignEntry {
    private final int noteId;
    private final DocumentClass noteClass;
    private final CollectionEntry entry;

    public DesignEntry(final int noteId, final DocumentClass noteClass, final CollectionEntry entry) {
      this.noteId = noteId;
      this.noteClass = noteClass;
      this.entry = entry;
    }

    public String getComment() {
      return this.entry.get(7, String.class, ""); //$NON-NLS-1$
    }

    public String getFlags() {
      return this.entry.get(4, String.class, ""); //$NON-NLS-1$
    }

    public String getLanguage() {
      return this.entry.get(14, String.class, ""); //$NON-NLS-1$
    }

    public int getNoteId() {
      return this.noteId;
    }

    public List<String> getTitles() {
      return DesignUtil.toTitlesList(this.entry.getAsList(0, String.class, Collections.emptyList()));
    }

    public DesignElement toDesignElement(final Database database) {
      return DesignUtil.createDesignElement(database, this.noteId, this.noteClass, this.getFlags(), Optional.empty());
    }

    @Override
    public String toString() {
      return String.format("DesignEntry [noteId=%s, noteClass=%s, entry=%s]", this.noteId, this.noteClass, this.entry); //$NON-NLS-1$
    }
  }

  private final Database database;

  public AbstractDbDesign(final Database database) {
    this.database = database;
  }

  @Override
  public <T extends DesignAgent> T createAgent(Class<T> agentType, String agentName) {
    return this.createDesignNote(agentType, agentName);
  }

  @Override
  public <T extends ScriptLibrary> T createScriptLibrary(Class<T> libraryType, String libName) {
    return this.createDesignNote(libraryType, libName);
  }
  
  /**
   * Creates a new design note of the provided class. This creates the backing
   * {@link Document},
   * sets its class, constructs the {@link DesignElement} subclass, and calls
   * {@link AbstractDesignElement#initializeNewDesignNote()}.
   * 
   * @param <T>         the {@link DesignElement} interface implemented by the
   *                    design class
   * @param <I>         the specific {@link AbstractDesignElement} implementation
   *                    class used internally
   * @param designClass a {@link Class} object representing {@code <I>}
   * @return the newly-created and -initialized design element
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected <T extends DesignElement, I extends AbstractDesignElement<T>> T createDesignNote(final Class<T> designClass) {
    final Document doc = this.database.createDocument();
    
    //special case for agents and script libraries: here the
    //mappings are hashed by their base classes DesignAgent and ScriptLibrary
    
    final DesignMapping<T, I> mapping;
    if (DesignAgent.class.isAssignableFrom(designClass)) {
      mapping = (DesignMapping) DesignUtil.getDesignMapping(DesignAgent.class);
    }
    else if (ScriptLibrary.class.isAssignableFrom(designClass)) {
      mapping = (DesignMapping) DesignUtil.getDesignMapping(ScriptLibrary.class);
    }
    else {
      mapping = DesignUtil.getDesignMapping(designClass);
    }

    doc.setDocumentClass(mapping.getNoteClass());

    if (mapping.getDocInitializer()!=null) {
      mapping.getDocInitializer().accept(designClass, doc);
    }
    
    final I result = mapping.getConstructor().apply(doc);
    result.initializeNewDesignNote();
    return (T) result;
  }

  /**
   * Creates a new design note of the provided class. This creates the backing
   * {@link Document},
   * sets its class, constructs the {@link DesignElement} subclass, and calls
   * {@link AbstractDesignElement#initializeNewDesignNote()}. Additionally, this
   * method calls
   * {@link DesignElement.NamedDesignElement#setTitle(String...)} before returning
   * the object.
   * 
   * @param <T>         the {@link DesignElement} interface implemented by the
   *                    design class
   * @param designClass a {@link Class} object representing {@code <I>}
   * @param title the title of the {@link DesignElement.NamedDesignElement} to create
   * @return the newly-created and -initialized design element
   */
  protected <T extends DesignElement.NamedDesignElement> T createDesignNote(final Class<T> designClass, final String title) {
    final T element = this.createDesignNote(designClass);
    element.setTitle(title);
    return element;
  }
  
  @Override
  public FileResource createFileResource(String filePath) {
    String path = cleanFilePath(filePath);
    FileResource result = createDesignNote(FileResource.class, path);
    Document doc = result.getDocument();
    doc.replaceItemValue(NotesConstants.ITEM_NAME_FILE_NAMES, path);
    doc.replaceItemValue(NotesConstants.ITEM_NAME_FILE_MIMETYPE, "application/octet-stream"); //$NON-NLS-1$
    return result;
  }

  @Override
  public Folder createFolder(final String folderName) {
    return this.createDesignNote(Folder.class, folderName);
  }

  @Override
  public Form createForm(final String formName) {
    return this.createDesignNote(Form.class, formName);
  }

  @Override
  public Subform createSubform(final String subformName) {
    return this.createDesignNote(Subform.class, subformName);
  }

  @Override
  public View createView(final String viewName) {
    return this.createDesignNote(View.class, viewName);
  }

  @Override
  public SharedColumn createSharedColumn(String columnName) {
    return this.createDesignNote(SharedColumn.class, columnName);
  }
  
  @Override
  public Frameset createFrameset(String framesetName) {
    return this.createDesignNote(Frameset.class, framesetName);
  }
  
  @Override
  public Optional<DesignAgent> getAgent(final String name) {
    return this.getDesignElementByName(DesignAgent.class, name);
  }

  @Override
  public Stream<DesignAgent> getAgents() {
    return this.getDesignElements(DesignAgent.class);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <T extends CollectionDesignElement<?>> Optional<CollectionDesignElement<T>> getCollection(final String name) {
    Optional<CollectionDesignElement> el = this.getDesignElementByName(CollectionDesignElement.class, name);
    return el.isPresent() ? Optional.of(el.get()) : Optional.empty();
  }

  @Override
  public Stream<CollectionDesignElement<?>> getCollections() {
    @SuppressWarnings("rawtypes")
    Stream<CollectionDesignElement> stream = this.getDesignElements(CollectionDesignElement.class);
    return stream
        .map((el) -> {
          return (CollectionDesignElement<?>) el;
        });
  }

  @Override
  public Database getDatabase() {
    return this.database;
  }

  @Override
  public DbProperties getDatabaseProperties() {
    final Database database = this.getDatabase();
    // TODO check for cases where the icon note doesn't exist - I _think_ this is
    // possible with fresh DBs
    final Document iconNote = database.getDocumentById(NotesConstants.NOTE_ID_SPECIAL | NotesConstants.NOTE_CLASS_ICON).get();
    return (DbProperties) DesignUtil.getDesignMapping(DbProperties.class).getConstructor().apply(iconNote);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends DesignElement> Optional<T> getDesignElementByName(final Class<T> type, final String name) {
    final DesignMapping<T, ?> mapping = DesignUtil.getDesignMapping(type);
    final int noteId = this.findDesignNote(mapping.getNoteClass(), mapping.getFlagsPattern(), name, false);
    if (noteId == 0) {
      return Optional.empty();
    } else {
      return Optional.of((T) mapping.getConstructor().apply(this.database.getDocumentById(noteId).get()));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends DesignElement> Stream<T> getDesignElements(final Class<T> type) {
    final DesignMapping<T, ?> mapping = DesignUtil.getDesignMapping(type);
    return (Stream<T>) this.findDesignNotes(mapping.getNoteClass(), mapping.getFlagsPattern())
        .map(entry -> this.database.getDocumentById(entry.noteId))
        .map(Optional::get)
        .map(mapping.getConstructor());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends DesignElement> Stream<T> getDesignElementsByName(final Class<T> type, final String name) {
    final DesignMapping<T, ?> mapping = DesignUtil.getDesignMapping(type);
    return (Stream<T>) this.findDesignNotes(mapping.getNoteClass(), mapping.getFlagsPattern())
        .filter(entry -> DesignUtil.matchesTitleValues(name, entry.getTitles()))
        .map(entry -> this.database.getDocumentById(entry.noteId))
        .map(Optional::get)
        .map(mapping.getConstructor());
  }

  @Override
  public Optional<DatabaseScriptLibrary> getDatabaseScriptLibrary() {
    Optional<DatabaseScriptLibrary> lib = this.getDesignElementByName(DatabaseScriptLibrary.class, "Database Script"); //$NON-NLS-1$
    if (lib.isPresent()) {
      return Optional.of((DatabaseScriptLibrary) lib.get());
    }
    else {
      return Optional.empty();
    }
  }

  @Override
  public DatabaseScriptLibrary createDatabaseScriptLibrary() {
    return getDatabaseScriptLibrary()
        .orElseGet(() -> {
          return this.createDesignNote(DatabaseScriptLibrary.class, "Database Script"); //$NON-NLS-1$
        });
  }
  
  @Override
  public Optional<FileResource> getFileResource(final String name, boolean includeXsp) {
    // By default, FileResource is mapped to the Designer list only
    if(includeXsp) {
      final int noteId = this.findDesignNote(DocumentClass.FORM, NotesConstants.DFLAGPAT_FILE, name, false);
      if (noteId == 0) {
        return Optional.empty();
      } else {
        return Optional.of(new FileResourceImpl(this.database.getDocumentById(noteId).get()));
      }
    } else {
      return this.getDesignElementByName(FileResource.class, name);
    }
  }

  @Override
  public Stream<FileResource> getFileResources(boolean includeXsp) {
    // By default, FileResource is mapped to the Designer list only
    if(includeXsp) {
      return this.findDesignNotes(DocumentClass.FORM, NotesConstants.DFLAGPAT_FILE)
        .map(entry -> this.database.getDocumentById(entry.noteId))
        .map(Optional::get)
        .map(FileResourceImpl::new);
    } else {
      return this.getDesignElements(FileResource.class);
    }
  }

  @Override
  public Optional<Folder> getFolder(final String name) {
    return this.getDesignElementByName(Folder.class, name);
  }

  @Override
  public Stream<Folder> getFolders() {
    return this.getDesignElements(Folder.class);
  }

  @Override
  public Optional<Form> getForm(final String name) {
    return this.getDesignElementByName(Form.class, name);
  }

  @Override
  public Stream<Form> getForms() {
    return this.getDesignElements(Form.class);
  }

  @Override
  public Optional<ImageResource> getImageResource(final String name) {
    return this.getDesignElementByName(ImageResource.class, name);
  }

  @Override
  public Stream<ImageResource> getImageResources() {
    return this.getDesignElements(ImageResource.class);
  }
  
  @Override
  public Optional<Navigator> getNavigator(String name) {
    return this.getDesignElementByName(Navigator.class, name);
  }
  
  @Override
  public Stream<Navigator> getNavigators() {
    return this.getDesignElements(Navigator.class);
  }

  @Override
  public Stream<ScriptLibrary> getScriptLibraries() {
    return this.getDesignElements(ScriptLibrary.class);
  }

  @Override
  public Optional<ScriptLibrary> getScriptLibrary(final String name) {
    return this.getDesignElementByName(ScriptLibrary.class, name);
  }

  @Override
  public Optional<Subform> getSubform(final String name) {
    return this.getDesignElementByName(Subform.class, name);
  }

  @Override
  public Stream<Subform> getSubforms() {
    return this.getDesignElements(Subform.class);
  }
  
  @Override
  public Optional<Page> getPage(String name) {
    return this.getDesignElementByName(Page.class, name);
  }
  
  @Override
  public Stream<Page> getPages() {
    return this.getDesignElements(Page.class);
  }

  @Override
  public Optional<View> getView(final String name) {
    return this.getDesignElementByName(View.class, name);
  }

  @Override
  public Stream<View> getViews() {
    return this.getDesignElements(View.class);
  }
  
  @Override
  public Optional<Outline> getOutline(final String name) {
    return this.getDesignElementByName(Outline.class, name);
  }

  @Override
  public Stream<Outline> getOutlines() {
    return this.getDesignElements(Outline.class);
  }
  
  @Override
  public Stream<Frameset> getFramesets() {
    return this.getDesignElements(Frameset.class);
  }
  
  @Override
  public Optional<Frameset> getFrameset(String name) {
    return this.getDesignElementByName(Frameset.class, name);
  }
  
  @Override
  public Optional<AboutDocument> getAboutDocument() {
    try {
      return database.getDocumentById(NotesConstants.NOTE_ID_SPECIAL | NotesConstants.NOTE_CLASS_INFO)
        .map(DesignUtil::createDesignElement)
        .map(AboutDocument.class::cast);
    } catch(SpecialObjectCannotBeLocatedException e) {
      return Optional.empty();
    }
  }
  
  @Override
  public Optional<UsingDocument> getUsingDocument() {
    try {
      return database.getDocumentById(NotesConstants.NOTE_ID_SPECIAL | NotesConstants.NOTE_CLASS_HELP)
          .map(DesignUtil::createDesignElement)
          .map(UsingDocument.class::cast); 
    } catch(SpecialObjectCannotBeLocatedException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<SharedField> getSharedField(final String name) {
    // NB: it appears that looking up shared fields using NIFFindDesignNoteExt is unreliable in practice,
    //     so query from the design collection here
    DesignMapping<SharedField, ?> mapping = DesignUtil.getDesignMapping(SharedField.class);
    return this.findDesignNotes(mapping.getNoteClass(), mapping.getFlagsPattern())
      .filter(entry -> DesignUtil.matchesTitleValues(name, entry.getTitles()))
      .map(entry -> this.database.getDocumentById(entry.noteId))
      .findFirst()
      .map(Optional::get)
      .map(mapping.getConstructor()::apply)
      .map(SharedField.class::cast);
  }

  @Override
  public Stream<SharedField> getSharedFields() {
    return this.getDesignElements(SharedField.class);
  }
  
  @Override
  public Optional<SharedActions> getSharedActions() {
    return getDesignElements(SharedActions.class).findFirst();
  }
  
  @Override
  public Optional<SharedColumn> getSharedColumn(String name) {
    return getDesignElementByName(SharedColumn.class, name);
  }
  
  @Override
  public Stream<SharedColumn> getSharedColumns() {
    return getDesignElements(SharedColumn.class);
  }
  
  @Override
  public Optional<StyleSheet> getStyleSheet(String name) {
    return getDesignElementByName(StyleSheet.class, name);
  }
  
  @Override
  public Stream<StyleSheet> getStyleSheets() {
    return getDesignElements(StyleSheet.class);
  }
  
  @Override
  public Optional<WiringProperties> getWiringPropertiesElement(String name) {
    return getDesignElementByName(WiringProperties.class, name);
  }
  
  @Override
  public Stream<WiringProperties> getWiringPropertiesElements() {
    return getDesignElements(WiringProperties.class);
  }
  
  @Override
  public Optional<Theme> getTheme(String name) {
    return getDesignElementByName(Theme.class, name);
  }
  
  @Override
  public Stream<Theme> getThemes() {
    return getDesignElements(Theme.class);
  }
  
  @Override
  public Optional<CompositeComponent> getCompositeComponent(String name) {
    return getDesignElementByName(CompositeComponent.class, name);
  }
  
  @Override
  public Stream<CompositeComponent> getCompositeComponents() {
    return getDesignElements(CompositeComponent.class);
  }
  
  @Override
  public Optional<CompositeApplication> getCompositeApplication(String name) {
    return getDesignElementByName(CompositeApplication.class, name);
  }
  
  @Override
  public Stream<CompositeApplication> getCompositeApplications() {
    return getDesignElements(CompositeApplication.class);
  }
  
  @Override
  public Optional<XPage> getXPage(String name) {
    return getDesignElementByName(XPage.class, name);
  }
  
  @Override
  public Stream<XPage> getXPages() {
    return getDesignElements(XPage.class);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T extends DesignElement> Optional<T> getDesignElementByUNID(String unid) {
    return (Optional<T>)(Optional<?>)getDatabase()
      .getDocumentByUNID(unid)
      .map(DesignUtil::createDesignElement);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends DesignElement> Stream<T> queryDesignElements(final Class<T> type, final String formula) {
    final DesignMapping<T, ?> mapping = DesignUtil.getDesignMapping(type);
    return (Stream<T>) this.database
        .queryFormula(formula, null, EnumSet.noneOf(SearchFlag.class), null, EnumSet.of(mapping.getNoteClass()))
        .getNoteIds()
        .get()
        .stream()
        .map(this.database::getDocumentById)
        .map(Optional::get)
        .map(mapping.getConstructor()::apply);
  }

  @Override
  public Stream<DesignElement> queryDesignElements(final String formula) {
    return this.database.queryFormula(formula, null, EnumSet.noneOf(SearchFlag.class), null, EnumSet.of(DocumentClass.ALLNONDATA))
        .getNoteIds()
        .get()
        .stream()
        .map(this.database::getDocumentById)
        .map(Optional::get)
        .map(DesignUtil::createDesignElement);
  }
  
  @Override
  public Optional<InputStream> getResourceAsStream(String filePath) {
    return this.findFileElement(filePath)
      .map(element -> {
        if(element instanceof NamedFileElement) {
          return ((NamedFileElement) element).getFileData();
        } else if(element instanceof ServerJavaScriptLibrary) {
          String script = ((ServerJavaScriptLibrary)element).getScript();
          return new ByteArrayInputStream(script.getBytes());
        } else if(element instanceof JavaScriptLibrary) {
          String script = ((JavaScriptLibrary)element).getScript();
          return new ByteArrayInputStream(script.getBytes());
        } else {
          throw new UnsupportedOperationException(MessageFormat.format("Unable to get file data for element of type {0}", element.getClass().getName()));
        }
      });
  }
  
  @Override
  public OutputStream newResourceOutputStream(String filePath) {
    return newResourceOutputStream(filePath, null);
  }
  
  
  @Override
  public OutputStream newResourceOutputStream(String filePath, Consumer<DesignElement> callback) {
    Optional<DesignElement> existingElement = this.findFileElement(filePath);
    DesignElement element = existingElement.orElseGet(() -> createFileResource(filePath));
    
    if(element instanceof NamedFileElement) {
      return new BufferingCallbackOutputStream(bytes -> {
        try(OutputStream os = ((NamedFileElement) element).newOutputStream()) {
          os.write(bytes);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
        element.save();
        if(callback != null) {
          callback.accept((NamedFileElement)element);
        }
      });
    } else if(element instanceof ServerJavaScriptLibrary) {
      return new BufferingCallbackOutputStream(bytes -> {
        ServerJavaScriptLibrary lib = (ServerJavaScriptLibrary)element;
        String script = new String(bytes, StandardCharsets.UTF_8);
        lib.setScript(script);
        element.save();
        if(callback != null) {
          callback.accept(lib);
        }
      });
    } else if(element instanceof JavaScriptLibrary) {
      return new BufferingCallbackOutputStream(bytes -> {
        JavaScriptLibrary lib = (JavaScriptLibrary)element;
        String script = new String(bytes, StandardCharsets.UTF_8);
        lib.setScript(script);
        element.save();
        if(callback != null) {
          callback.accept(lib);
        }
      });
    } else {
      throw new UnsupportedOperationException(MessageFormat.format("Unable to get file data for element of type {0}", element.getClass().getName()));
    }
  }

  @Override
  public void signAll(final Set<DocumentClass> docClass, final UserId id, final SignCallback callback) {
    docClass.stream()
      .flatMap(c -> this.findDesignNotes(EnumSet.of(c), Collections.emptySet()))
      .map(entry -> entry.toDesignElement(this.database))
      .forEach(element -> {
        if (callback.shouldSign(element, id.getUsername())) {
          element.sign(id);
        }
      });
  }

  // *******************************************************************************
  // * Internal utility methods
  // *******************************************************************************

  public Stream<DesignEntry> findDesignNotes(final DocumentClass noteClass, final String pattern) {
    return findDesignNotes(Collections.singleton(noteClass), Collections.singleton(pattern));
  }
  
  /**
   * Looks up the provided file-type design element. If found, the returned element
   * will be an instance of one of:
   * 
   * <ul>
   *   <li>{@link NamedFileElement}</li>
   *   <li>{@link ServerJavaScriptLibrary}</li>
   *   <li>{@link JavaScriptLibrary}</li>
   * </ul>
   * 
   * @param filePath the path of the element to find
   * @return an {@link Optional} describing the file-type element, or an empty one
   *         if none was found
   * @since 1.0.39
   */
  public Optional<DesignElement> findFileElement(String filePath) {
    String path = cleanFilePath(filePath);
    return this.findDesignNotes(EnumSet.of(DocumentClass.FORM, DocumentClass.FILTER), FILERES_PATTERNS)
      .filter(entry -> DesignUtil.matchesTitleValues(path, entry.getTitles()))
      .map(entry -> this.database.getDocumentById(entry.noteId))
      .findFirst()
      .map(Optional::get)
      .map(DesignUtil::createDesignElement);
  }
  
  /**
   * Finds design notes matching a given class and any of a collection of pattern strings.
   *  
   * @param noteClasses the {@link DocumentClass}es of the design notes to find
   * @param patterns zero or more flags pattern strings
   * @return a {@link Stream} of {@link DesignEntry} objects for matching design elements
   * @since 1.0.38
   */
  public Stream<DesignEntry> findDesignNotes(final Collection<DocumentClass> noteClasses, final Collection<String> patterns) {
    /*
     * Design collection columns:
     *  - $TITLE (string)
     *  - $FormPrivs
     *  - $FormUsers
     *  - $Body
     *  - $Flags (string)
     *  - $Class
     *  - $Modified (TIMEDATE)
     *  - $Comment (string)
     *  - $AssistTrigger
     *  - $AssistType
     *  - $AssistFlags
     *  - $AssistFlags2
     *  - $UpdatedBy (string)
     *  - $$FormScript_0
     *  - $LANGUAGE
     *  - $Writers
     *  - $PWriters
     *  - $FlagsExt
     *  - $FileSize (number)
     *  - $MimeType
     *  - $DesignerVersion (string)
     */

    final DominoCollection designColl = this.database.openDesignCollection();
    final CollectionSearchQuery query = designColl.query()
        .readDocumentClass()
        .readColumnValues();
    boolean hasPattern;
    if(patterns == null || patterns.isEmpty()) {
      hasPattern = false;
    } else {
      if(patterns.size() == 1 && StringUtil.isEmpty(patterns.iterator().next())) {
        hasPattern = false;
      } else {
        hasPattern = true;
      }
    }

    return query.build(0, Integer.MAX_VALUE, new CollectionSearchQuery.CollectionEntryProcessor<List<DesignEntry>>() {
        @Override
        public List<DesignEntry> end(final List<DesignEntry> result) {
          return result;
        }
  
        @Override
        public Action entryRead(final List<DesignEntry> result, final CollectionEntry entry) {
          final DocumentClass entryClass = entry.getDocumentClass().orElse(null);
          if (noteClasses.contains(entryClass)) {
            if (hasPattern) {
              final String flags = entry.get(4, String.class, ""); //$NON-NLS-1$
              if(patterns.stream().anyMatch(pattern -> DesignUtil.matchesFlagsPattern(flags, pattern))) {
                result.add(new DesignEntry(entry.getNoteID(), entryClass, entry));
              }
            } else {
              result.add(new DesignEntry(entry.getNoteID(), entryClass, entry));
            }
          }
          return Action.Continue;
        }
  
        @Override
        public List<DesignEntry> start() {
          return new LinkedList<>();
        }
      })
      .stream();
  }

  /**
   * Queries the design collection for a single design note.
   * 
   * @param noteClass    the class of note to query (see <code>NOTE_CLASS_*</code>
   *                     in {@link NotesConstants})
   * @param pattern      the note flag pattern to query (see
   *                     <code>DFLAGPAT_*</code> in {@link NotesConstants})
   * @param name         the name or alias of the design note
   * @param partialMatch whether partial matches are allowed
   * @return the note ID of the specified design note, or <code>0</code> if the
   *         note was not found
   */
  protected abstract int findDesignNote(DocumentClass noteClass, String pattern, String name, boolean partialMatch);
  
  protected String cleanFilePath(String filePath) {
    String path = StringUtil.toString(filePath);
    if(path.startsWith("/")) { //$NON-NLS-1$
      path = path.substring(1);
    }
    if(StringUtil.isEmpty(path)) {
      throw new IllegalArgumentException("Unable to load empty path");
    }
    return path;
  }
  
}
