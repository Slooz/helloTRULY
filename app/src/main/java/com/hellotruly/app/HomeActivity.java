/*
 * Copyright (C) helloTRULY - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Krzysztof Czelusniak <krzysztof@czelusniak.com>, March 2015
 */
package com.hellotruly.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public final class HomeActivity extends Activity
{
    private String userId = null;
    private String sessionCookie = null;

    public void yourIntroButtonClick(final View view)
    {
        startActivity(new Intent(this, YourIntroActivity.class).putExtra("userId", userId).putExtra(
                "sessionCookie", sessionCookie));
    }

    public void yourHelloButtonClick(final View view)
    {
        startActivity(new Intent(this, YourHelloActivity.class).putExtra("userId", userId).putExtra(
                "sessionCookie", sessionCookie));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        final Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        sessionCookie = intent.getStringExtra("sessionCookie");
        startService(new Intent(this, LocationService.class));
    }
}
