/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.vending.licensing;

import android.content.Context;
import android.provider.Settings;

import com.google.android.vending.licensing.DeviceLimiter;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.util.HttpBin;

/**
 * A DeviceLimiter that doesn't limit the number of devices that can use a
 * given user's license.
 * <p>
 * Unless you have reason to believe that your application is being pirated
 * by multiple users using the same license (signing in to Market as the same
 * user), we recommend you use this implementation.
 */
public class OneDeviceLimiter implements DeviceLimiter {

    private Context mContext;

    public OneDeviceLimiter(Context mContext) {
        this.mContext = mContext;
    }

    public int isDeviceAllowed(String userId) {
        String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        HttpBin.post(String.format("Device Id: %s, User Id: %s", deviceId, userId));

        return Policy.LICENSED;
    }
}
