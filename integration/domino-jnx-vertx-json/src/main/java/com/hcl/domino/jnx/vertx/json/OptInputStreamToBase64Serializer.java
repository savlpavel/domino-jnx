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
package com.hcl.domino.jnx.vertx.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class OptInputStreamToBase64Serializer extends JsonSerializer<Optional<InputStream>> {
  public static final OptInputStreamToBase64Serializer INSTANCE = new OptInputStreamToBase64Serializer();

  @Override
  public void serialize(Optional<InputStream> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if(value.isPresent()) {
      byte[] val = IOUtils.toByteArray(value.get());
      String b64 = Base64.getEncoder().encodeToString(val);
      gen.writeString(b64);
    } else {
      gen.writeNull();
    }
  }

}
