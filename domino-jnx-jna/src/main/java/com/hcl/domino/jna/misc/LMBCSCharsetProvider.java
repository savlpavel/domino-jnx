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
package com.hcl.domino.jna.misc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hcl.domino.jna.internal.NotesStringUtils;
import com.sun.jna.Memory;

public class LMBCSCharsetProvider extends CharsetProvider {
	public static final String NAME = "LMBCS"; //$NON-NLS-1$
	public static final List<String> ALIASES = Arrays.asList("LMBCS-native"); //$NON-NLS-1$
	
	public static class LMBCSCharset extends Charset {
		public static final LMBCSCharset INSTANCE = new LMBCSCharset(NAME, ALIASES.toArray(new String[ALIASES.size()]));

		protected LMBCSCharset(String canonicalName, String[] aliases) {
			super(canonicalName, aliases);
		}

		@Override
		public boolean contains(Charset cs) {
			return this.equals(cs);
		}

		@Override
		public CharsetDecoder newDecoder() {
			return new CharsetDecoder(this, 1, 2) {
				@Override
				protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
					if(!in.hasRemaining()) {
						return CoderResult.UNDERFLOW;
					}
					byte[] bytes = new byte[in.remaining()];
					in.get(bytes, 0, in.remaining());
					String decoded = NotesStringUtils.fromLMBCS(bytes);
					out.put(decoded);
					return CoderResult.UNDERFLOW;
				}
			};
		}

		@Override
		public CharsetEncoder newEncoder() {
			return new CharsetEncoder(this, 1, 3) {
				@Override
				protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
					if(!in.hasRemaining()) {
						return CoderResult.UNDERFLOW;
					}
					char[] chars = new char[in.remaining()];
					in.get(chars);
					Memory encoded = NotesStringUtils.toLMBCS(new String(chars), false);
					if(out.remaining() < encoded.size()) {
						return CoderResult.OVERFLOW;
					}
					out.put(encoded.getByteBuffer(0, encoded.size()));
					return CoderResult.UNDERFLOW;
				}
				
				@Override
				public boolean isLegalReplacement(byte[] repl) {
					// TODO check if we should properly implement this
					return true;
				}
			};
		}
		
	}

	@Override
	public Iterator<Charset> charsets() {
		return Arrays.asList((Charset)LMBCSCharset.INSTANCE).iterator();
	}

	@Override
	public Charset charsetForName(String charsetName) {
		if(NAME.equals(charsetName) || ALIASES.contains(charsetName)) {
			return LMBCSCharset.INSTANCE;
		} else {
			return null;
		}
	}

}
