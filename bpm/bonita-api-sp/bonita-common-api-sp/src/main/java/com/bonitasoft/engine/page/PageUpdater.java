/*******************************************************************************
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.page;

import java.io.Serializable;
import java.util.Map;

import com.bonitasoft.engine.converter.EnumConverter;

/**
 * @author Laurent Leseigneur
 * @author Emmanuel Duchastenier
 * @deprecated from version 7.0 on, use {@link org.bonitasoft.engine.page.PageUpdater} instead.
 */
@Deprecated
public class PageUpdater implements Serializable {

    private static final long serialVersionUID = 4295108162470507415L;

    public enum PageUpdateField {
        NAME, DISPLAY_NAME, DESCRIPTION, CONTENT_NAME;
    }

    private final org.bonitasoft.engine.page.PageUpdater delegate;

    public PageUpdater() {
        delegate = new org.bonitasoft.engine.page.PageUpdater();
    }

    public PageUpdater setName(final String name) {
        delegate.setName(name);
        return this;
    }

    public PageUpdater setDescription(final String description) {
        delegate.setDescription(description);
        return this;
    }

    public PageUpdater setDisplayName(final String displayName) {
        delegate.setDisplayName(displayName);
        return this;
    }

    public PageUpdater setContentName(final String contentName) {
        delegate.setContentName(contentName);
        return this;
    }

    public Map<PageUpdateField, Serializable> getFields() {
        return new EnumConverter().convert(delegate.getFields(), PageUpdateField.class);
    }

    public org.bonitasoft.engine.page.PageUpdater getDelegate() {
        return delegate;
    }

}