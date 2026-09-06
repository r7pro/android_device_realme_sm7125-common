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

import android.content.ComponentName;
import android.content.Context;
import android.service.quicksettings.TileService;

import org.aospextended.device.util.Utils;

public class OtgSettings {

    public static final String NODE = "/sys/class/power_supply/usb/otg_switch";
    public static final String KEY = "otg_enable";

    public static boolean isSupported() {
        return Utils.fileWritable(NODE);
    }

    public static boolean isEnabled(Context context) {
        return Utils.getSharedPreferences(context).getBoolean(KEY, true);
    }

    public static void setEnabled(Context context, boolean enabled) {
        Utils.writeLine(NODE, enabled ? "1" : "0");
        Utils.getSharedPreferences(context).edit().putBoolean(KEY, enabled).apply();
        try {
            TileService.requestListeningState(
                    context, new ComponentName(context, OtgTileService.class));
        } catch (Exception ignored) {}
    }

    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }
        boolean enabled = isEnabled(context);
        Utils.writeLine(NODE, enabled ? "1" : "0");
    }
}
