/*
 * Copyright (C) helloTRULY - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Krzysztof Czelusniak <krzysztof@czelusniak.com>, March 2015
 */
package com.hellotruly.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.hellotruly.backend.helloTruly.HelloTruly;
import com.hellotruly.backend.helloTruly.model.PersistentSigninInformation;

import java.io.IOException;

public final class PersistentSigninActivity extends Activity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = preferences.getString("userId", null);
        String persistentCookie = preferences.getString("persistentCookie", null);
        if (null == userId || null == persistentCookie)
        {
            startActivity(new Intent(this, RegistrationActivity.class));
            return;
        }
        PersistentSigninInformation persistentSigninInformation = new PersistentSigninInformation();
        persistentSigninInformation.setUserId(userId);
        persistentSigninInformation.setPersistentCookie(persistentCookie);
        new PersistentSigninRequest().execute(persistentSigninInformation);
    }

    private class PersistentSigninRequest
            extends AsyncTask<PersistentSigninInformation, Void, PersistentSigninInformation>
    {
        protected PersistentSigninInformation doInBackground(
                final PersistentSigninInformation... persistentSigninInformations)
        {
            HelloTruly helloTruly = new HelloTruly.Builder(new NetHttpTransport(),
                                                           new AndroidJsonFactory(), null).build();
            try
            {
                return helloTruly.authenticate(persistentSigninInformations[0]).execute();
            }
            catch (final IOException ioException)
            {
                return new PersistentSigninInformation();
            }
        }

        @Override
        protected void onPostExecute(final PersistentSigninInformation persistentSigninInformation)
        {
            if (persistentSigninInformation.getUserId() == null)
            {
                startActivity(new Intent(PersistentSigninActivity.this,
                                         RegistrationActivity.class));
                return;
            }
            final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                    PersistentSigninActivity.this).edit();
            editor.putString("userId", persistentSigninInformation.getUserId());
            editor.putString("persistentCookie", persistentSigninInformation.getPersistentCookie());
            editor.apply();
            startActivity(new Intent(PersistentSigninActivity.this, HomeActivity.class).putExtra(
                    "userId", persistentSigninInformation.getUserId()).putExtra("sessionCookie",
                                                                                persistentSigninInformation
                                                                                        .getSessionCookie()));
        }
    }
}
