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
        canonicalName = SigninInformation.APPLICATION_NAME,
        title = "helloTRULY Backend",
        description = "Backend for helloTRULY.",
        namespace = @ApiNamespace(ownerDomain = "backend.hellotruly.com",
                                  ownerName = SigninInformation.APPLICATION_NAME))
public final class SigninInformation
{
    static final String APPLICATION_NAME = "helloTruly";
    public final String email = null;
    public final String password = null;

    @ApiMethod(name = "signIn")
    public PersistentSigninInformation signIn(final SigninInformation signinInformation)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", signinInformation.email);
        final Entity entity;
        try
        {
            entity = datastore.get(key);
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            return new PersistentSigninInformation();
        }
        if (!BCrypt.checkpw(signinInformation.password, entity.getProperty("password").toString()))
        {
            return new PersistentSigninInformation();
        }
        final PersistentSigninInformation persistentSigninInformation
                = new PersistentSigninInformation(signinInformation.email);
        final List<String> persistentCookies = (ArrayList<String>) entity.getProperty(
                "persistentCookie");
        persistentCookies.add(persistentSigninInformation.persistentCookie);
        entity.setUnindexedProperty("persistentCookie", persistentCookies);
        entity.setUnindexedProperty("sessionCookie", persistentSigninInformation.sessionCookie);
        datastore.put(entity);
        return persistentSigninInformation;
    }
}
