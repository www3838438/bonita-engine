/*******************************************************************************
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine;

import com.bonitasoft.engine.test.TestEngineSP;

public class LocalServerTestsInitializerSP  {

    public static void beforeAll() throws Exception {
        TestEngineSP.getInstance().start();
    }

    public static void afterAll() throws Exception {
        TestEngineSP.getInstance().stop();
    }


}
