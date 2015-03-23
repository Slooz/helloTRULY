/*
 * Copyright (C) helloTRULY - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Krzysztof Czelusniak <krzysztof@czelusniak.com>, March 2015
 */
package com.hellotruly.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.hellotruly.backend.helloTruly.HelloTruly;
import com.hellotruly.backend.helloTruly.model.Hello;

import java.io.IOException;

public class YourHelloActivity extends Activity
{
    private String userId = null;
    private String sessionCookie = null;

    public void saveButtonClick(final View view)
    {
        new SaveYourHelloRequest().execute();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_layout);
        final Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        sessionCookie = intent.getStringExtra("sessionCookie");
        new GetYourHelloRequest().execute();
    }

    private class GetYourHelloRequest extends AsyncTask<Void, Void, Hello>
    {
        protected Hello doInBackground(final Void... arguments)
        {
            HelloTruly helloTruly = new HelloTruly.Builder(new NetHttpTransport(),
                                                           new AndroidJsonFactory(), null).build();
            final Hello intro = new Hello();
            intro.setUserId(userId);
            intro.setSessionCookie(sessionCookie);
            try
            {
                return helloTruly.retrieveYourHello(intro).execute();
            }
            catch (final IOException ioException)
            {
            }
            return new Hello();
        }

        @Override
        protected void onPostExecute(final Hello hello)
        {
            if (hello.getUserId() != null)
            {
                ((EditText) findViewById(R.id.messageEditText)).setText(hello.getMessage());
            }
        }
    }

    private class SaveYourHelloRequest extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(final Void... arguments)
        {
            HelloTruly helloTruly = new HelloTruly.Builder(new NetHttpTransport(),
                                                           new AndroidJsonFactory(), null).build();
            Hello intro = new Hello();
            intro.setUserId(userId);
            intro.setSessionCookie(sessionCookie);
            intro.setMessage(((EditText) findViewById(R.id.messageEditText)).getText().toString());
            try
            {
                return helloTruly.saveYourHello(intro).execute();
            }
            catch (final IOException ioException)
            {
            }
            return null;
        }
    }
}
