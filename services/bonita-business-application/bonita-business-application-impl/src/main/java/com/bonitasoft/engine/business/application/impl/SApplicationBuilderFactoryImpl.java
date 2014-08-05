/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.business.application.impl;

import com.bonitasoft.engine.business.application.SApplicationBuilder;
import com.bonitasoft.engine.business.application.SApplicationBuilderFactory;
import com.bonitasoft.engine.business.application.SApplicationState;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public class SApplicationBuilderFactoryImpl implements SApplicationBuilderFactory {

    @Override
    public SApplicationBuilder createNewInstance(final String name, final String version, final String path, final long createdBy) {
        final long currentDate = System.currentTimeMillis();
        return new SApplicationBuilderImpl(new SApplicationImpl(name, version, path, currentDate, createdBy, SApplicationState.DEACTIVATED.name()));
    }

    @Override
    public String getIdKey() {
        return "id";
    }

    @Override
    public String getNameKey() {
        return "name";
    }

    @Override
    public String getVersionKey() {
        return "version";
    }

    @Override
    public String getPathKey() {
        return "path";
    }

    @Override
    public String getDescriptionKey() {
        return "description";
    }

    @Override
    public String getIconPathKey() {
        return "iconPath";
    }

    @Override
    public String getCreationDateKey() {
        return "creationDate";
    }

    @Override
    public String getCreatedByKey() {
        return "createdBy";
    }

    @Override
    public String getLastUpdatedDateKey() {
        return "lastUpdateDate";
    }

    @Override
    public String getUpdatedByKey() {
        return "updatedBy";
    }

    @Override
    public String getStateKey() {
        return "state";
    }

}
