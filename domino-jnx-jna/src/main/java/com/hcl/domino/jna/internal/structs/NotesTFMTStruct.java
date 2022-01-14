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
public class NotesTFMTStruct extends BaseStructure {
	/** Date Display Format */
	public byte Date;
	/** Time Display Format */
	public byte Time;
	/** Time Zone Display Format */
	public byte Zone;
	/** Overall Date/Time Structure */
	public byte Structure;
	public NotesTFMTStruct() {
		super();
	}
	
	public static NotesTFMTStruct newInstance() {
		return AccessController.doPrivileged((PrivilegedAction<NotesTFMTStruct>) () -> new NotesTFMTStruct());
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
			"Date", //$NON-NLS-1$
			"Time", //$NON-NLS-1$
			"Zone", //$NON-NLS-1$
			"Structure" //$NON-NLS-1$
		);
	}
	/**
	 * @param Date Date Display Format<br>
	 * @param Time Time Display Format<br>
	 * @param Zone Time Zone Display Format<br>
	 * @param Structure Overall Date/Time Structure
	 */
	public NotesTFMTStruct(byte Date, byte Time, byte Zone, byte Structure) {
		super();
		this.Date = Date;
		this.Time = Time;
		this.Zone = Zone;
		this.Structure = Structure;
	}
	
	public static NotesTFMTStruct newInstance(final byte Date, final byte Time, final byte Zone, final byte Structure) {
		return AccessController.doPrivileged((PrivilegedAction<NotesTFMTStruct>) () -> new NotesTFMTStruct(Date, Time, Zone, Structure));
	}
	
	public NotesTFMTStruct(Pointer peer) {
		super(peer);
	}
	
	public static NotesTFMTStruct newInstance(final Pointer peer) {
		return AccessController.doPrivileged((PrivilegedAction<NotesTFMTStruct>) () -> new NotesTFMTStruct(peer));
	}

	public static class ByReference extends NotesTFMTStruct implements Structure.ByReference {
		
	};
	public static class ByValue extends NotesTFMTStruct implements Structure.ByValue {
		
	};
}
