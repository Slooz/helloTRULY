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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

@Api(
        name = "helloTruly",
        canonicalName = PersistentSigninInformation.APPLICATION_NAME,
        title = "helloTRULY Backend",
        description = "Backend for helloTRULY.",
        namespace = @ApiNamespace(ownerDomain = "backend.hellotruly.com",
                                  ownerName = PersistentSigninInformation.APPLICATION_NAME))
public final class PersistentSigninInformation
{
    static final String APPLICATION_NAME = "helloTruly";
    public final String userId;
    public final String persistentCookie;
    public final String sessionCookie;

    public PersistentSigninInformation()
    {
        userId = null;
        persistentCookie = null;
        sessionCookie = null;
    }

    public PersistentSigninInformation(final String userId)
    {
        this.userId = userId;
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] random128BitNumber = new byte[16];
        secureRandom.nextBytes(random128BitNumber);
        this.persistentCookie = DatatypeConverter.printBase64Binary(random128BitNumber);
        secureRandom.nextBytes(random128BitNumber);
        this.sessionCookie = DatatypeConverter.printBase64Binary(random128BitNumber);
    }

    @ApiMethod(name = "authenticate")
    public PersistentSigninInformation authenticate(
            final PersistentSigninInformation persistentSigninInformation)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", persistentSigninInformation.userId);
        final Entity entity;
        try
        {
            entity = datastore.get(key);
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            return new PersistentSigninInformation();
        }
        final List<String> persistentCookies = (ArrayList<String>) entity.getProperty(
                "persistentCookie");
        if (persistentCookies.contains(persistentSigninInformation.persistentCookie))
        {
            persistentCookies.remove(persistentSigninInformation.persistentCookie);
            final PersistentSigninInformation newPersistentSigninInformation
                    = new PersistentSigninInformation(persistentSigninInformation.userId);
            persistentCookies.add(newPersistentSigninInformation.persistentCookie);
            entity.setUnindexedProperty("persistentCookie", persistentCookies);
            entity.setUnindexedProperty("sessionCookie",
                                        newPersistentSigninInformation.sessionCookie);
            datastore.put(entity);
            return newPersistentSigninInformation;
        }
        return new PersistentSigninInformation();
    }
}
