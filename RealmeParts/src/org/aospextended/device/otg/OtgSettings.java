/*
 * Copyright (C) 2026 The LineageOS Project
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

package org.aospextended.device.otg;

import android.content.Context;

public class OtgSettings {

    public static boolean isSupported() {
        return UsbModeSettings.isSupported();
    }

    public static boolean isEnabled(Context context) {
        return UsbModeSettings.isOtgSwitchEnabled();
    }

    public static void setEnabled(Context context, boolean enabled) {
        UsbModeSettings.setMode(context, enabled ? UsbModeSettings.MODE_AUTO : UsbModeSettings.MODE_OFF);
    }

    public static void restore(Context context) {
        UsbModeSettings.restore(context);
    }
}
