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

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.aospextended.device.R;

/**
 * Quick Settings Tile for USB OTG that cycles through:
 * Auto (10 min idle) -> Always On -> Off -> Auto
 */
public class OtgTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTileState();
    }

    @Override
    public void onClick() {
        super.onClick();
        if (!UsbModeSettings.isSupported()) {
            return;
        }
        String current = UsbModeSettings.getMode(this);
        String next;
        if (UsbModeSettings.MODE_OFF.equals(current)) {
            next = UsbModeSettings.MODE_AUTO;
        } else if (UsbModeSettings.MODE_AUTO.equals(current)) {
            next = UsbModeSettings.MODE_ON;
        } else {
            next = UsbModeSettings.MODE_OFF;
        }
        UsbModeSettings.setMode(this, next);
        updateTileState();
    }

    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile == null) {
            return;
        }
        if (!UsbModeSettings.isSupported()) {
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.setSubtitle(null);
            tile.updateTile();
            return;
        }

        String mode = UsbModeSettings.getMode(this);
        if (UsbModeSettings.MODE_AUTO.equals(mode)) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(getString(R.string.usb_mode_tile_auto));
        } else if (UsbModeSettings.MODE_ON.equals(mode)) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(getString(R.string.usb_mode_tile_on));
        } else {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle(getString(R.string.usb_mode_tile_off));
        }
        tile.updateTile();
    }
}
