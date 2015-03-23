/*
 * Copyright (C) helloTRULY - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Krzysztof Czelusniak <krzysztof@czelusniak.com>, March 2015
 */
package com.hellotruly.app;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.hellotruly.backend.helloTruly.HelloTruly;
import com.hellotruly.backend.helloTruly.model.Hello;

import java.io.IOException;
import java.util.List;

public final class LocationService extends Service
        implements GoogleApiClient.ConnectionCallbacks, LocationListener
{
    private GoogleApiClient googleApiClient = null;

    public void onConnected(final Bundle connectionHint)
    {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                                                                 new LocationRequest().setInterval(
                                                                         5000).setFastestInterval(0)
                                                                                      .setPriority(
                                                                                              LocationRequest.PRIORITY_HIGH_ACCURACY),
                                                                 this);
    }

    public void onConnectionSuspended(final int cause)
    {
    }

    public void onLocationChanged(final Location location)
    {
        new UpdateUserLocationRequest().execute(location);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addApi(
                LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onDestroy()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
        super.onDestroy();
    }

    public IBinder onBind(final Intent intent)
    {
        return null;
    }

    private class UpdateUserLocationRequest extends AsyncTask<Location, Void, List<Hello>>
    {
        protected List<Hello> doInBackground(final Location... locations)
        {
            HelloTruly helloTruly = new HelloTruly.Builder(new NetHttpTransport(),
                                                           new AndroidJsonFactory(), null).build();
            final com.hellotruly.backend.helloTruly.model.Location location
                    = new com.hellotruly.backend.helloTruly.model.Location();
            location.setUserId(PreferenceManager.getDefaultSharedPreferences(LocationService.this)
                                                .getString("userId", null));
            location.setLatitude(locations[0].getLatitude());
            location.setLongitude(locations[0].getLongitude());
            try
            {
                return helloTruly.updateUserLocation(location).execute().getItems();
            }
            catch (final IOException ioException)
            {
                return null;
            }
        }
    }
}
