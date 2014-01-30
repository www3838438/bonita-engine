/*******************************************************************************
 * Copyright (C) 2011, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api.impl.transaction.profile;

import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.transaction.TransactionContentWithResult;
import org.bonitasoft.engine.profile.ProfileService;
import org.bonitasoft.engine.profile.model.SProfileEntry;

import com.bonitasoft.engine.profile.ProfileEntryCreator;
import com.bonitasoft.engine.service.SPModelConvertor;

/**
 * @author Julien Mege
 * @author Celine Souchet
 */
public class CreateProfileEntry implements TransactionContentWithResult<SProfileEntry> {

    private final ProfileService profileService;

    private final ProfileEntryCreator creator;

    private SProfileEntry profileEntry;

    public CreateProfileEntry(final ProfileService profileService, final ProfileEntryCreator creator) {
        super();
        this.profileService = profileService;
        this.creator = creator;
    }

    @Override
    public void execute() throws SBonitaException {
        profileEntry = profileService.createProfileEntry(SPModelConvertor.constructSProfileEntry(creator));
    }

    @Override
    public SProfileEntry getResult() {
        return this.profileEntry;
    }

}