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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hcl.domino.design.agent.SimpleActionAgentContent;
import com.hcl.domino.design.simpleaction.SimpleAction;

/**
 * @author Jesse Gallagher
 * @since 1.0.24
 */
public class DefaultSimpleActionAgentContent implements SimpleActionAgentContent {
  private final List<SimpleAction> actions;

  public DefaultSimpleActionAgentContent(final Collection<SimpleAction> actions) {
    this.actions = new ArrayList<>(actions);
  }

  @Override
  public List<SimpleAction> getActions() {
    return Collections.unmodifiableList(this.actions);
  }

}
