/*
 * Copyright (C) helloTRULY - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Krzysztof Czelusniak <krzysztof@czelusniak.com>, March 2015
 */
package com.hellotruly.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent)
    {
        context.startService(new Intent(context, LocationService.class));
    }
}
