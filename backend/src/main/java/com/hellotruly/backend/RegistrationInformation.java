/*
 * Copyright (C) helloTRULY - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Krzysztof Czelusniak <krzysztof@czelusniak.com>, March 2015
 */
package com.hellotruly.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

@Api(
        name = "helloTruly",
        canonicalName = RegistrationInformation.APPLICATION_NAME,
        title = "helloTRULY Backend",
        description = "Backend for helloTRULY.",
        namespace = @ApiNamespace(ownerDomain = "backend.hellotruly.com",
                                  ownerName = RegistrationInformation.APPLICATION_NAME))
public final class RegistrationInformation
{
    static final String APPLICATION_NAME = "helloTruly";
    public final String email = null;
    public final String fullName = null;
    public final String password = null;

    @ApiMethod(name = "register")
    public PersistentSigninInformation register(
            final RegistrationInformation registrationInformation)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", registrationInformation.email);
        try
        {
            datastore.get(key);
            return new PersistentSigninInformation();
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            final Entity entity = new Entity(key);
            entity.setUnindexedProperty("fullName", registrationInformation.fullName);
            entity.setUnindexedProperty("password", BCrypt.hashpw(registrationInformation.password,
                                                                  BCrypt.gensalt()));
            final PersistentSigninInformation persistentSigninInformation
                    = new PersistentSigninInformation(registrationInformation.email);
            final List<String> persistentCookies = new ArrayList<String>();
            persistentCookies.add(persistentSigninInformation.persistentCookie);
            entity.setUnindexedProperty("persistentCookie", persistentCookies);
            entity.setUnindexedProperty("sessionCookie", persistentSigninInformation.sessionCookie);
            datastore.put(entity);
            return persistentSigninInformation;
        }
    }
}
