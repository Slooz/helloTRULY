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
import com.hellotruly.backend.helloTruly.model.SigninInformation;

import java.io.IOException;

public final class SigninActivity extends Activity
{
    public void signInButtonClick(final View view)
    {
        new SigninRequest().execute();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);
    }

    private class SigninRequest extends AsyncTask<Void, Void, PersistentSigninInformation>
    {
        protected PersistentSigninInformation doInBackground(final Void... arguments)
        {
            HelloTruly helloTruly = new HelloTruly.Builder(new NetHttpTransport(),
                                                           new AndroidJsonFactory(), null).build();
            SigninInformation signinInformation = new SigninInformation();
            signinInformation.setEmail(((EditText) findViewById(R.id.emailEditText)).getText()
                                                                                    .toString());
            signinInformation.setPassword(((EditText) findViewById(R.id.passwordEditText)).getText()
                                                                                          .toString());
            try
            {
                return helloTruly.signIn(signinInformation).execute();
            }
            catch (final IOException ioException)
            {
                return new PersistentSigninInformation();
            }
        }

        @Override
        protected void onPostExecute(final PersistentSigninInformation persistentSigninInformation)
        {
            if (persistentSigninInformation.getUserId() != null)
            {
                final SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SigninActivity.this).edit();
                editor.putString("userId", persistentSigninInformation.getUserId());
                editor.putString("persistentCookie",
                                 persistentSigninInformation.getPersistentCookie());
                editor.apply();
                startActivity(new Intent(SigninActivity.this, HomeActivity.class).putExtra("userId",
                                                                                           persistentSigninInformation
                                                                                                   .getUserId())
                                                                                 .putExtra(
                                                                                         "sessionCookie",
                                                                                         persistentSigninInformation
                                                                                                 .getSessionCookie()));
            }
        }
    }
}
