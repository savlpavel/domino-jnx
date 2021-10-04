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
package com.hcl.domino.commons.design.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hcl.domino.design.DesignElement;
import com.hcl.domino.design.agent.ImportedJavaAgentContent;

/**
 * @author Jesse Gallagher
 * @since 1.0.24
 */
public class DefaultImportedJavaAgentContent implements ImportedJavaAgentContent {
  private final DesignElement agent;
  private final String mainClassName;
  private final String codeFilesystemPath;
  private final List<String> fileNameList;

  public DefaultImportedJavaAgentContent(DesignElement agent, final String mainClassName, final String codeFilesystemPath,
      final List<String> fileNameList) {
    this.agent = agent;
    this.mainClassName = mainClassName;
    this.codeFilesystemPath = codeFilesystemPath;
    this.fileNameList = new ArrayList<>(fileNameList);
  }

  @Override
  public String getCodeFilesystemPath() {
    return this.codeFilesystemPath;
  }

  @Override
  public List<String> getFiles() {
    return this.fileNameList;
  }
  
  @Override
  public Optional<InputStream> getFile(String name) {
    // Do a basic check to make sure it's in the list
    List<String> jars = this.fileNameList;
    if(!jars.contains(name)) {
      return Optional.empty();
    }
    return agent.getDocument()
      .getAttachment(name)
      .map(t -> {
        try {
          return t.getInputStream();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      });
  }

  @Override
  public String getMainClassName() {
    return this.mainClassName;
  }

}
