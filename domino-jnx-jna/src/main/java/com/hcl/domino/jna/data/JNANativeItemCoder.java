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
package com.hcl.domino.jna.data;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import com.hcl.domino.commons.util.NotesErrorUtils;
import com.hcl.domino.data.NativeItemCoder;
import com.hcl.domino.jna.internal.ItemDecoder;
import com.hcl.domino.jna.internal.Mem;
import com.hcl.domino.jna.internal.NotesStringUtils;
import com.hcl.domino.jna.internal.capi.NotesCAPI;
import com.hcl.domino.jna.internal.gc.handles.DHANDLE;
import com.hcl.domino.jna.internal.gc.handles.LockUtil;
import com.sun.jna.Memory;
import com.sun.jna.ptr.ShortByReference;

/**
 * @author Jesse Gallagher
 * @since 1.0.24
 */
public class JNANativeItemCoder implements NativeItemCoder {

	@SuppressWarnings("unchecked")
	@Override
	public List<String> decodeStringList(byte[] buf) {
		Memory mem = new Memory(buf.length);
		ByteBuffer dest = mem.getByteBuffer(0, mem.size());
		dest.put(buf);
		return (List<String>)(List<?>)ItemDecoder.decodeTextListValue(mem, false);
	}

	@Override
	public byte[] encodeStringList(List<String> values) {
		if(values == null || values.isEmpty()) {
			return new byte[] { 0, 0 };
		}
		
		List<Memory> lmbcs = values.stream()
			.map(s -> NotesStringUtils.toLMBCS(s, false))
			.collect(Collectors.toList());
		int totalSize = (int)lmbcs.stream()
			.mapToLong(Memory::size)
			.sum();
		
		short result;
		
		DHANDLE.ByReference rethList = DHANDLE.newInstanceByReference();
		ShortByReference retListSize = new ShortByReference();

		NotesErrorUtils.checkResult(
			NotesCAPI.get().ListAllocate((short) 0, 
				(short) 0,
				0, rethList, null, retListSize)
		);

		return LockUtil.lockHandle(rethList, (handleByVal) -> {
			for (int i=0; i < values.size(); i++) {
				String currStr = values.get(i);
				Memory currStrMem = NotesStringUtils.toLMBCS(currStr, false);
				
				NotesErrorUtils.checkResult(
					NotesCAPI.get().ListAddEntry(handleByVal, 0, retListSize, (short)i, currStrMem,
						(short) (currStrMem==null ? 0 : (currStrMem.size() & 0xffff))
				));
			}

			
			int size = Short.toUnsignedInt(retListSize.getValue());
			try {
				return Mem.OSLockObject(handleByVal, ptr -> {
					return ptr.getByteArray(0, size);
				});
			} finally {
				Mem.OSMemFree(handleByVal);
			}
		});
	}
}
