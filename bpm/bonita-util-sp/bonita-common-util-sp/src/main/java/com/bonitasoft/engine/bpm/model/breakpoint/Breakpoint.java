/*******************************************************************************
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bpm.model.breakpoint;

import org.bonitasoft.engine.bpm.model.BaseElement;

/**
 * @author Baptiste Mesta
 */
public interface Breakpoint extends BaseElement {

    long getDefinitionId();

    long getInstanceId();

    boolean isInstanceScope();

    String getElementName();

    int getStateId();

    int getInterruptedStateId();

}
