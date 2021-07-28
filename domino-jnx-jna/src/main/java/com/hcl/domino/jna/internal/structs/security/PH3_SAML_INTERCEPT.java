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
import java.util.Arrays;
import java.util.List;

import com.hcl.domino.jna.internal.gc.handles.DHANDLE;
import com.hcl.domino.misc.NotesConstants;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class PH3_SAML_INTERCEPT extends Structure {
	public short Reserved;
	public boolean bIsPreprocessMode;
	/** C type : char[MAXUSERNAME] */
	public byte[] pszIDPUserName = new byte[NotesConstants.MAXUSERNAME];
	/** C type : char[MAX_IDPURL] */
	public byte[] pszIDPAuthURL = new byte[NotesConstants.MAX_IDPURL];
	public boolean bUseOSCred;
	/** C type : DHANDLE */
	public DHANDLE hTrustedSiteList;
	public int dwTrustedSiteListLen;
	public boolean bUseSSL;
	public boolean bSuppressErrorDisplay;
	public boolean bDoNameMapping;
	/** C type : char[NFL_MAX_FORMULA_LEN] */
	public byte[] pszMachineSpecificFormula = new byte[NotesConstants.NFL_MAX_FORMULA_LEN];
	public boolean fDebugTrace;
	/** C type : DHANDLE */
	public DHANDLE hSamlAssertion;
	/** C type : char* */
	public Pointer pszSamlAssertion;
	/** C type : DHANDLE */
	public DHANDLE hJWTToken;
	/** C type : char* */
	public Pointer pszJWTToken;
	public int dwJWTTokenSize;
	public short wVaultConfigType;
	/** C type : char[MAX_HOSTNAME] */
	public byte[] szClientHostname = new byte[NotesConstants.MAX_HOSTNAME];
	/** C type : char[MAX_HOSTNAME] */
	public byte[] szAudience = new byte[NotesConstants.MAX_HOSTNAME];
	/** C type : char[50] */
	public byte[] szReferenceID = new byte[50];
	/** C type : char[MAXUSERNAME] */
	public byte[] szAppID = new byte[NotesConstants.MAXUSERNAME];
	/** C type : char[MAX_IDPURL] */
	public byte[] szJWTVaultReq = new byte[NotesConstants.MAX_IDPURL];
	
	public PH3_SAML_INTERCEPT() {
		super();
	}
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
			"Reserved", //$NON-NLS-1$
			"bIsPreprocessMode", //$NON-NLS-1$
			"pszIDPUserName", //$NON-NLS-1$
			"pszIDPAuthURL", //$NON-NLS-1$
			"bUseOSCred", //$NON-NLS-1$
			"hTrustedSiteList", //$NON-NLS-1$
			"dwTrustedSiteListLen", //$NON-NLS-1$
			"bUseSSL", //$NON-NLS-1$
			"bSuppressErrorDisplay", //$NON-NLS-1$
			"bDoNameMapping", //$NON-NLS-1$
			"pszMachineSpecificFormula", //$NON-NLS-1$
			"fDebugTrace", //$NON-NLS-1$
			"hSamlAssertion", //$NON-NLS-1$
			"pszSamlAssertion", //$NON-NLS-1$
			"hJWTToken", //$NON-NLS-1$
			"pszJWTToken", //$NON-NLS-1$
			"dwJWTTokenSize", //$NON-NLS-1$
			"wVaultConfigType", //$NON-NLS-1$
			"szClientHostname", //$NON-NLS-1$
			"szAudience", //$NON-NLS-1$
			"szReferenceID", //$NON-NLS-1$
			"szAppID", //$NON-NLS-1$
			"szJWTVaultReq" //$NON-NLS-1$
		);
	}
	public PH3_SAML_INTERCEPT(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PH3_SAML_INTERCEPT implements Structure.ByReference {
		
	};
	public static class ByValue extends PH3_SAML_INTERCEPT implements Structure.ByValue {
		
	};
}