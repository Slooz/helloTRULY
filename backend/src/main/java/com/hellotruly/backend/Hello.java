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

@Api(
        name = "helloTruly",
        canonicalName = Hello.APPLICATION_NAME,
        title = "helloTRULY Backend",
        description = "Backend for helloTRULY.",
        namespace = @ApiNamespace(ownerDomain = "backend.hellotruly.com",
                                  ownerName = Hello.APPLICATION_NAME))
public final class Hello
{
    static final String APPLICATION_NAME = "helloTruly";
    public final String userId;
    public final String sessionCookie = null;
    public final String fullName;
    public final String message;

    public Hello()
    {
        userId = null;
        fullName = null;
        message = null;
    }

    public Hello(final String userId, final String message)
    {
        this.userId = userId;
        this.fullName = null;
        this.message = message;
    }

    public Hello(final String userId, final String fullName, final String message)
    {
        this.userId = null;
        this.fullName = fullName;
        this.message = message;
    }

    @ApiMethod(name = "retrieveYourIntro")
    public Hello retrieveYourIntro(final Hello intro)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", intro.userId);
        final Entity entity;
        try
        {
            entity = datastore.get(key);
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            return new Hello();
        }
        if (!entity.getProperty("sessionCookie").toString().equals(intro.sessionCookie))
        {
            return new Hello();
        }
        return new Hello(intro.userId, entity.getProperty("introMessage").toString());
    }

    @ApiMethod(name = "saveYourIntro")
    public void saveYourIntro(final Hello intro)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", intro.userId);
        final Entity entity;
        try
        {
            entity = datastore.get(key);
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            return;
        }
        if (!entity.getProperty("sessionCookie").toString().equals(intro.sessionCookie))
        {
            return;
        }
        entity.setUnindexedProperty("introMessage", intro.message);
        datastore.put(entity);
    }

    @ApiMethod(name = "retrieveYourHello")
    public Hello retrieveYourHello(final Hello hello)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", hello.userId);
        final Entity entity;
        try
        {
            entity = datastore.get(key);
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            return new Hello();
        }
        if (!entity.getProperty("sessionCookie").toString().equals(hello.sessionCookie))
        {
            return new Hello();
        }
        return new Hello(hello.userId, entity.getProperty("helloMessage").toString());
    }

    @ApiMethod(name = "saveYourHello")
    public void saveYourHello(final Hello hello)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", hello.userId);
        final Entity entity;
        try
        {
            entity = datastore.get(key);
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            return;
        }
        if (!entity.getProperty("sessionCookie").toString().equals(hello.sessionCookie))
        {
            return;
        }
        entity.setUnindexedProperty("helloMessage", hello.message);
        datastore.put(entity);
    }
}
