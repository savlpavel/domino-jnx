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
package com.hcl.domino.jna.internal.gc.allocations;

import java.lang.ref.ReferenceQueue;

import com.hcl.domino.commons.gc.APIObjectAllocations;
import com.hcl.domino.commons.gc.IAPIObject;
import com.hcl.domino.commons.gc.IGCDominoClient;
import com.hcl.domino.commons.util.NotesErrorUtils;
import com.hcl.domino.jna.admin.JNAUserId;
import com.hcl.domino.jna.internal.capi.NotesCAPI;
import com.sun.jna.ptr.PointerByReference;

public class JNAUserIDAllocations extends APIObjectAllocations<JNAUserId> {
	private boolean m_disposed;
	private boolean m_noDispose;
	private PointerByReference m_phKFC;
	
	@SuppressWarnings("rawtypes")
	public JNAUserIDAllocations(IGCDominoClient parentDominoClient, APIObjectAllocations parentAllocations,
			JNAUserId referent, ReferenceQueue<? super IAPIObject> queue) {
		super(parentDominoClient, parentAllocations, referent, queue);
	}

	@Override
	public boolean isDisposed() {
		return m_disposed;
	}

	public void setIDHandle(PointerByReference phKFC) {
		m_phKFC = phKFC;
	}
	
	public PointerByReference getIDHandle() {
		return m_phKFC;
	}
	
	@Override
	public void dispose() {
		if (isDisposed()) {
			return;
		}
		
		if (!m_noDispose) {
			if (m_phKFC!=null) {
				short result = NotesCAPI.get().SECKFMClose(m_phKFC, 0, 0, null);
				NotesErrorUtils.checkResult(result);
				m_phKFC = null;
			}
		}
		
		m_disposed = true;
	}

	public void setNoDispose() {
		m_noDispose = true;
	}

}
