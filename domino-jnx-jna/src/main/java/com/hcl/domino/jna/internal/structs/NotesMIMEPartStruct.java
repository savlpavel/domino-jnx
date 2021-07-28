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
package com.hcl.domino.jna.internal.structs;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class NotesMIMEPartStruct extends BaseStructure {
	/** MIME_PART Version */
	public short wVersion;
	public int dwFlags;
	/** Type of MIME_PART body */
	public byte cPartType;
	public byte cSpare;
	/**
	 * Bytes of variable length part data<br>
	 * NOT including data in DB object
	 */
	public short wByteCount;
	/** Length of the boundary string */
	public short wBoundaryLen;
	/** Length of the headers */
	public short wHeadersLen;
	public short wSpare;
	public int dwSpare;
	
	public NotesMIMEPartStruct() {
		super();
	}
	
	public static NotesMIMEPartStruct newInstance() {
		return AccessController.doPrivileged((PrivilegedAction<NotesMIMEPartStruct>) () -> new NotesMIMEPartStruct());
	}
	
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
			"wVersion", //$NON-NLS-1$
			"dwFlags", //$NON-NLS-1$
			"cPartType", //$NON-NLS-1$
			"cSpare", //$NON-NLS-1$
			"wByteCount", //$NON-NLS-1$
			"wBoundaryLen", //$NON-NLS-1$
			"wHeadersLen", //$NON-NLS-1$
			"wSpare", //$NON-NLS-1$
			"dwSpare" //$NON-NLS-1$
		);
	}
	
	/**
	 * @param wVersion MIME_PART Version
	 * @param dwFlags flags
	 * @param cPartType Type of MIME_PART body
	 * @param cSpare spare BYTE
	 * @param wByteCount Bytes of variable length part data NOT including data in DB object
	 * @param wBoundaryLen Length of the boundary string
	 * @param wHeadersLen Length of the headers
	 * @param wSpare spare WORD
	 * @param dwSpare spare DWORD
	 */
	public NotesMIMEPartStruct(short wVersion, int dwFlags, byte cPartType, byte cSpare, short wByteCount, 
			short wBoundaryLen, short wHeadersLen, short wSpare, int dwSpare) {
		super();
		this.wVersion = wVersion;
		this.dwFlags = dwFlags;
		this.cPartType = cPartType;
		this.cSpare = cSpare;
		this.wByteCount = wByteCount;
		this.wBoundaryLen = wBoundaryLen;
		this.wHeadersLen = wHeadersLen;
		this.wSpare = wSpare;
		this.dwSpare = dwSpare;
	}
	
	public static NotesMIMEPartStruct newInstance(final short wVersion, final int dwFlags, final byte cPartType,
			final byte cSpare, final short wByteCount, final short wBoundaryLen, final short wHeadersLen,
			final short wSpare, final int dwSpare) {
		
		return AccessController.doPrivileged((PrivilegedAction<NotesMIMEPartStruct>) () -> new NotesMIMEPartStruct(wVersion, dwFlags, cPartType, cSpare, wByteCount, wBoundaryLen,
				wHeadersLen, wSpare, dwSpare));
	}
	
	public NotesMIMEPartStruct(Pointer peer) {
		super(peer);
	}
	
	public static NotesMIMEPartStruct newInstance(final Pointer peer) {
		return AccessController.doPrivileged((PrivilegedAction<NotesMIMEPartStruct>) () -> new NotesMIMEPartStruct(peer));
	}
	
	public static class ByReference extends NotesMIMEPartStruct implements Structure.ByReference {
		
	};
	
	public static class ByValue extends NotesMIMEPartStruct implements Structure.ByValue {
		
	};
	
	@Override
	protected int getOverrideAlignment() {
		return Structure.ALIGN_NONE;
	}
}