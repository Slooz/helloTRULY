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
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(
        name = "helloTruly",
        canonicalName = Location.APPLICATION_NAME,
        title = "helloTRULY Backend",
        description = "Backend for helloTRULY.",
        namespace = @ApiNamespace(ownerDomain = "backend.hellotruly.com",
                                  ownerName = Location.APPLICATION_NAME))
public final class Location
{
    static final String APPLICATION_NAME = "helloTruly";
    public final String userId = null;
    public final String sessionCookie = null;
    public final double latitude = 0;
    public final double longitude = 0;

    @ApiMethod(name = "updateUserLocation")
    public List<Hello> updateUserLocation(final Location location)
    {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Key key = KeyFactory.createKey("user", location.userId);
        final Entity entity;
        try
        {
            entity = datastore.get(key);
        }
        catch (final EntityNotFoundException entityNotFoundException)
        {
            return null;
        }
        final Document document = Document.newBuilder().setId(location.userId).addField(
                Field.newBuilder().setName("location").setGeoPoint(new GeoPoint(location.latitude,
                                                                                location.longitude)))
                                          .build();
        IndexSpec indexSpec = IndexSpec.newBuilder().setName("UserLocations").build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
        index.put(document);
        Results<ScoredDocument> results = index.search(
                "distance(geopoint(" + location.latitude + ", " + location.longitude +
                "), location) <= 38.1");
        final List<Key> locationsInRange = new ArrayList<>((int) results.getNumberFound());
        for (ScoredDocument result : results)
        {
            locationsInRange.add(KeyFactory.createKey("user", result.getId()));
        }
        final Map<Key, Entity> usersInRange = datastore.get(locationsInRange);
        final List<Hello> hellosInRange = new ArrayList<>(usersInRange.size());
        for (Entity user : usersInRange.values())
        {
            hellosInRange.add(new Hello(null, user.getProperty("fullName").toString(),
                                        user.getProperty("message").toString()));
        }
        return hellosInRange;
    }
}
