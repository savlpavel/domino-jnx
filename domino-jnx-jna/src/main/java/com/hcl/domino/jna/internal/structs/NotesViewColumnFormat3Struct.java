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
public class NotesViewColumnFormat3Struct extends BaseStructure {
	/** VIEW_COLUMN_FORMAT_SIGNATURE3 */
	public short Signature;
	/** NPREF_xxx */
	public byte DTPref;
	/** DT_xxx */
	public int DTFlags;
	/** DT_xxx */
	public int DTFlags2;
	/** DT_WFMT_xxx */
	public byte DTDOWFmt;
	/** DT_YFMT_xxx */
	public byte DTYearFmt;
	/** DT_MFMT_xxx */
	public byte DTMonthFmt;
	/** DT_DFMT_xxx */
	public byte DTDayFmt;
	public byte DTDsep1Len;
	public byte DTDsep2Len;
	public byte DTDsep3Len;
	public byte DTTsepLen;
	/** DT_DSHOW_xxx */
	public byte DTDShow;
	/** DT_DSPEC_xxx */
	public byte DTDSpecial;
	/** DT_TSHOW_xxx */
	public byte DTTShow;
	/** TZFMT_xxx */
	public byte DTTZone;
	public short DatePreference;
	public byte bUnused;
	
	public int Unused;
	public NotesViewColumnFormat3Struct() {
		super();
		setAlignType(Structure.ALIGN_NONE);
	}
	
	public static NotesViewColumnFormat3Struct newInstance() {
		return AccessController.doPrivileged((PrivilegedAction<NotesViewColumnFormat3Struct>) () -> new NotesViewColumnFormat3Struct());
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
			"Signature", //$NON-NLS-1$
			"DTPref", //$NON-NLS-1$
			"DTFlags", //$NON-NLS-1$
			"DTFlags2", //$NON-NLS-1$
			"DTDOWFmt", //$NON-NLS-1$
			"DTYearFmt", //$NON-NLS-1$
			"DTMonthFmt", //$NON-NLS-1$
			"DTDayFmt", //$NON-NLS-1$
			"DTDsep1Len", //$NON-NLS-1$
			"DTDsep2Len", //$NON-NLS-1$
			"DTDsep3Len", //$NON-NLS-1$
			"DTTsepLen", //$NON-NLS-1$
			"DTDShow", //$NON-NLS-1$
			"DTDSpecial", //$NON-NLS-1$
			"DTTShow", //$NON-NLS-1$
			"DTTZone", //$NON-NLS-1$
			"DatePreference", //$NON-NLS-1$
			"bUnused", //$NON-NLS-1$
			"Unused" //$NON-NLS-1$
		);
	}
	public NotesViewColumnFormat3Struct(Pointer peer) {
		super(peer);
		setAlignType(Structure.ALIGN_NONE);
	}
	
	public static NotesViewColumnFormat3Struct newInstance(final Pointer peer) {
		return AccessController.doPrivileged((PrivilegedAction<NotesViewColumnFormat3Struct>) () -> new NotesViewColumnFormat3Struct(peer));
	}

	public static class ByReference extends NotesViewColumnFormat3Struct implements Structure.ByReference {
		
	};
	public static class ByValue extends NotesViewColumnFormat3Struct implements Structure.ByValue {
		
	};
}