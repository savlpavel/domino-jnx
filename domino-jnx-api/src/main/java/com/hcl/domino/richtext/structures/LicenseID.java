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
package com.hcl.domino.richtext.structures;

import com.hcl.domino.richtext.annotation.StructureDefinition;
import com.hcl.domino.richtext.annotation.StructureGetter;
import com.hcl.domino.richtext.annotation.StructureMember;
import com.hcl.domino.richtext.annotation.StructureSetter;

/**
 * @author Jesse Gallagher
 * @since 1.0.42
 */
@StructureDefinition(
  name = "LICENSEID",
  members = {
    @StructureMember(name = "ID", type = byte[].class, length = 5),
    @StructureMember(name = "Product", type = byte.class),
    @StructureMember(name = "Check", type = byte[].class, length = 2)
  }
)
public interface LicenseID extends MemoryStructure {

  @StructureGetter("ID")
  byte[] getId();
  
  @StructureSetter("ID")
  LicenseID setId(byte[] id);
  
  @StructureGetter("Product")
  byte getProductCode();
  
  @StructureSetter("Product")
  LicenseID setProductCode(byte product);
  
  @StructureGetter("Check")
  byte[] getCheck();
  
  @StructureSetter("Check")
  LicenseID setCheck(byte[] check);
}
