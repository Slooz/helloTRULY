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
import android.view.View;
import android.widget.EditText;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.hellotruly.backend.helloTruly.HelloTruly;
import com.hellotruly.backend.helloTruly.model.PersistentSigninInformation;
import com.hellotruly.backend.helloTruly.model.RegistrationInformation;

import java.io.IOException;

public final class RegistrationActivity extends Activity
{
    public void registerButtonClick(final View view)
    {
        new RegistrationRequest().execute();
    }

    public void switchToSigninButtonClick(final View view)
    {
        startActivity(new Intent(this, SigninActivity.class));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
    }

    private class RegistrationRequest extends AsyncTask<Void, Void, PersistentSigninInformation>
    {
        protected PersistentSigninInformation doInBackground(final Void... arguments)
        {
            HelloTruly helloTruly = new HelloTruly.Builder(new NetHttpTransport(),
                                                           new AndroidJsonFactory(), null).build();
            RegistrationInformation registrationInformation = new RegistrationInformation();
            registrationInformation.setEmail(((EditText) findViewById(R.id.newEmailEditText))
                                                     .getText().toString());
            registrationInformation.setFullName(((EditText) findViewById(R.id.newFullNameEditText))
                                                        .getText().toString());
            registrationInformation.setPassword(((EditText) findViewById(R.id.newPasswordEditText))
                                                        .getText().toString());
            try
            {
                return helloTruly.register(registrationInformation).execute();
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
                return;
            }
            final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                    RegistrationActivity.this).edit();
            editor.putString("userId", persistentSigninInformation.getUserId());
            editor.putString("persistentCookie", persistentSigninInformation.getPersistentCookie());
            editor.apply();
            startActivity(new Intent(RegistrationActivity.this, HomeActivity.class).putExtra(
                    "userId", persistentSigninInformation.getUserId()).putExtra("sessionCookie",
                                                                                persistentSigninInformation
                                                                                        .getSessionCookie()));
        }
    }
}
