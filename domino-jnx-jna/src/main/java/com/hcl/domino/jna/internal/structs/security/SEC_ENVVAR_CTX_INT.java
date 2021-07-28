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
package com.hcl.domino.jna.internal.structs.security;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class SEC_ENVVAR_CTX_INT extends Structure {
	/** C type : const char* */
	public Pointer pVarName;
	public int DefaultValue;
	public int Scope;
	public int CurrentValue;
	public byte fSampled;
	public short SeqNo;
	
	public SEC_ENVVAR_CTX_INT() {
		super();
	}
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
			"pVarName", //$NON-NLS-1$
			"DefaultValue", //$NON-NLS-1$
			"Scope", //$NON-NLS-1$
			"CurrentValue", //$NON-NLS-1$
			"fSampled", //$NON-NLS-1$
			"SeqNo" //$NON-NLS-1$
		);
	}
	/**
	 * @param pVarName C type : const char*
	 * @param DefaultValue the default value
	 * @param Scope the scope of the value
	 * @param CurrentValue the current value
	 * @param fSampled something to do with sampling, I assume
	 * @param SeqNo a sequence number
	 */
	public SEC_ENVVAR_CTX_INT(Pointer pVarName, int DefaultValue, int Scope, int CurrentValue, byte fSampled, short SeqNo) {
		super();
		this.pVarName = pVarName;
		this.DefaultValue = DefaultValue;
		this.Scope = Scope;
		this.CurrentValue = CurrentValue;
		this.fSampled = fSampled;
		this.SeqNo = SeqNo;
	}
	public SEC_ENVVAR_CTX_INT(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends SEC_ENVVAR_CTX_INT implements Structure.ByReference {
		
	};
	public static class ByValue extends SEC_ENVVAR_CTX_INT implements Structure.ByValue {
		
	};
}