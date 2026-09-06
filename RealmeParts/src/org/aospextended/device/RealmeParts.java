/*
 * Copyright (C) 2020 The AospExtended Project
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

package org.aospextended.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;

import org.aospextended.device.gestures.TouchGestures;
import org.aospextended.device.gestures.TouchGesturesActivity;
import org.aospextended.device.doze.DozeSettingsActivity;
import org.aospextended.device.vibration.VibratorStrengthPreference;
import org.aospextended.device.gpu.GpuBoostSettings;
import org.aospextended.device.battery.ChargeLimitSettings;
import org.aospextended.device.battery.ChargeLimitService;
import org.aospextended.device.display.DcDimmingSettings;
import org.aospextended.device.display.HbmSettings;
import org.aospextended.device.touch.GameTouchSettings;
import org.aospextended.device.touch.EdgeMistouchSettings;
import org.aospextended.device.speaker.ClearSpeakerHelper;
import org.aospextended.device.otg.OtgSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import android.util.Log;
import android.os.SystemProperties;
import java.io.*;
import android.widget.Toast;

import org.aospextended.device.R;
import org.aospextended.device.util.Utils;

public class RealmeParts extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {
    private static final boolean DEBUG = Utils.DEBUG;
    private static final String TAG = "RealmeParts";

    private Context mContext;
    private SharedPreferences mPreferences;

    private Preference mDozePref;
    private Preference mGesturesPref;
    private VibratorStrengthPreference mVibratorStrength;
    private ListPreference mGpuBoost;
    private SwitchPreference mChargeLimitEnable;
    private ListPreference mChargeLimitLevel;
    private SwitchPreference mDcDimming;
    private SwitchPreference mHbm;
    private SwitchPreference mGameTouch;
    private SwitchPreference mEdgeMistouch;
    private SwitchPreference mClearSpeaker;
    private SwitchPreference mOtg;

    @Override

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.RealmeParts, rootKey);

        PreferenceCategory display = (PreferenceCategory) getPreferenceScreen()
                 .findPreference("display_category");
        mDcDimming = (SwitchPreference) findPreference(DcDimmingSettings.KEY);
        if (DcDimmingSettings.isSupported()) {
            mDcDimming.setChecked(DcDimmingSettings.isEnabled(getContext()));
            mDcDimming.setOnPreferenceChangeListener(this);
        } else if (display != null && mDcDimming != null) {
            display.removePreference(mDcDimming);
        }

        mHbm = (SwitchPreference) findPreference(HbmSettings.KEY);
        if (HbmSettings.isSupported()) {
            mHbm.setChecked(HbmSettings.isEnabled(getContext()));
            mHbm.setOnPreferenceChangeListener(this);
        } else if (display != null && mHbm != null) {
            display.removePreference(mHbm);
        }

        if (display != null && display.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(display);
        }

        PreferenceCategory gestures = (PreferenceCategory) getPreferenceScreen()
                 .findPreference("gestures_category");
        mGesturesPref = findPreference("screen_gestures");
        mGesturesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), TouchGesturesActivity.class);
                startActivity(intent);
                return true;
            }
        });
        if (!TouchGestures.isSupported()) {
            getPreferenceScreen().removePreference(gestures);
        }

        mDozePref = findPreference("doze");
        mDozePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), DozeSettingsActivity.class);
                startActivity(intent);
                return true;
            }
        });

        PreferenceCategory touch = (PreferenceCategory) getPreferenceScreen()
                 .findPreference("touch_category");
        mGameTouch = (SwitchPreference) findPreference(GameTouchSettings.KEY);
        if (GameTouchSettings.isSupported()) {
            mGameTouch.setChecked(GameTouchSettings.isEnabled(getContext()));
            mGameTouch.setOnPreferenceChangeListener(this);
        } else if (touch != null && mGameTouch != null) {
            touch.removePreference(mGameTouch);
        }

        mEdgeMistouch = (SwitchPreference) findPreference(EdgeMistouchSettings.KEY);
        if (EdgeMistouchSettings.isSupported()) {
            mEdgeMistouch.setChecked(EdgeMistouchSettings.isEnabled(getContext()));
            mEdgeMistouch.setOnPreferenceChangeListener(this);
        } else if (touch != null && mEdgeMistouch != null) {
            touch.removePreference(mEdgeMistouch);
        }

        if (touch != null && touch.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(touch);
        }

        PreferenceCategory performance = (PreferenceCategory) getPreferenceScreen()
                 .findPreference("performance_category");
        mGpuBoost = (ListPreference) findPreference(GpuBoostSettings.KEY);
        if (GpuBoostSettings.isSupported()) {
            mGpuBoost.setValue(GpuBoostSettings.getValue(getContext()));
            // SimpleSummaryProvider shows the current entry as the summary and
            // updates itself on change. It also avoids the String.format crash
            // that the manual setSummary path hit on entries containing '%'.
            mGpuBoost.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            mGpuBoost.setOnPreferenceChangeListener(this);
        } else if (performance != null) {
            getPreferenceScreen().removePreference(performance);
        }

        PreferenceCategory battery = (PreferenceCategory) getPreferenceScreen()
                 .findPreference("battery_category");
        mChargeLimitEnable = (SwitchPreference) findPreference(ChargeLimitSettings.KEY_ENABLE);
        mChargeLimitLevel = (ListPreference) findPreference(ChargeLimitSettings.KEY_LEVEL);
        if (ChargeLimitSettings.isSupported()) {
            mChargeLimitEnable.setOnPreferenceChangeListener(this);
            mChargeLimitLevel.setValue(
                    String.valueOf(ChargeLimitSettings.getLevel(getContext())));
            // '%' in the entries ("80%") crashed the old manual-format summary;
            // SimpleSummaryProvider renders the entry verbatim and auto-updates.
            mChargeLimitLevel.setSummaryProvider(
                    ListPreference.SimpleSummaryProvider.getInstance());
            mChargeLimitLevel.setOnPreferenceChangeListener(this);
        } else if (battery != null) {
            getPreferenceScreen().removePreference(battery);
        }

        mClearSpeaker = (SwitchPreference) findPreference("clear_speaker");
        if (mClearSpeaker != null) {
            mClearSpeaker.setChecked(ClearSpeakerHelper.getInstance().isRunning());
            mClearSpeaker.setOnPreferenceChangeListener(this);
        }

        PreferenceCategory usb = (PreferenceCategory) getPreferenceScreen()
                 .findPreference("usb_category");
        mOtg = (SwitchPreference) findPreference(OtgSettings.KEY);
        if (OtgSettings.isSupported()) {
            mOtg.setChecked(OtgSettings.isEnabled(getContext()));
            mOtg.setOnPreferenceChangeListener(this);
        } else if (usb != null && mOtg != null) {
            usb.removePreference(mOtg);
        }

        if (usb != null && usb.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(usb);
        }

/*        PreferenceCategory vib_strength = (PreferenceCategory) getPreferenceScreen()
                 .findPreference("vib_strength_category");
        mVibratorStrength = (VibratorStrengthPreference) findPreference(VibratorStrengthPreference.KEY_VIBSTRENGTH);
        if (!VibratorStrengthPreference.isSupported()) {
            getPreferenceScreen().removePreference(vib_strength);
        }
*/
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        // ListPreference summaries refresh automatically via SimpleSummaryProvider
        // once the new value is applied, so no manual setSummary is needed here.
        if (GpuBoostSettings.KEY.equals(key)) {
            GpuBoostSettings.setValue((String) newValue);
        } else if (ChargeLimitSettings.KEY_ENABLE.equals(key)) {
            ChargeLimitSettings.onEnableChanged(getContext(), (Boolean) newValue);
        } else if (ChargeLimitSettings.KEY_LEVEL.equals(key)) {
            final String value = (String) newValue;
            // Persist now (the framework persists only after we return) so the
            // monitor re-evaluates against the new cap immediately.
            Utils.getSharedPreferences(getContext()).edit()
                    .putString(ChargeLimitSettings.KEY_LEVEL, value).apply();
            ChargeLimitService.start(getContext());
        } else if (DcDimmingSettings.KEY.equals(key)) {
            boolean enabled = (Boolean) newValue;
            DcDimmingSettings.setEnabled(enabled);
            Utils.getSharedPreferences(getContext()).edit()
                    .putBoolean(DcDimmingSettings.KEY, enabled).apply();
        } else if (HbmSettings.KEY.equals(key)) {
            boolean enabled = (Boolean) newValue;
            HbmSettings.setEnabled(getContext(), enabled);
        } else if (GameTouchSettings.KEY.equals(key)) {
            boolean enabled = (Boolean) newValue;
            GameTouchSettings.setEnabled(enabled);
            Utils.getSharedPreferences(getContext()).edit()
                    .putBoolean(GameTouchSettings.KEY, enabled).apply();
        } else if (EdgeMistouchSettings.KEY.equals(key)) {
            boolean enabled = (Boolean) newValue;
            EdgeMistouchSettings.setEnabled(enabled);
            Utils.getSharedPreferences(getContext()).edit()
                    .putBoolean(EdgeMistouchSettings.KEY, enabled).apply();
        } else if (OtgSettings.KEY.equals(key)) {
            boolean enabled = (Boolean) newValue;
            OtgSettings.setEnabled(getContext(), enabled);
        } else if ("clear_speaker".equals(key)) {

            boolean enable = (Boolean) newValue;
            if (enable) {
                ClearSpeakerHelper.getInstance().start(getContext(), new ClearSpeakerHelper.Listener() {
                    @Override
                    public void onProgress(int secondsRemaining) {
                        if (mClearSpeaker != null && isAdded()) {
                            mClearSpeaker.setSummary(getString(R.string.clear_speaker_running, secondsRemaining));
                        }
                    }

                    @Override
                    public void onFinished() {
                        if (mClearSpeaker != null && isAdded()) {
                            mClearSpeaker.setChecked(false);
                            mClearSpeaker.setSummary(R.string.clear_speaker_summary);
                        }
                    }
                });
            } else {
                ClearSpeakerHelper.getInstance().stop();
                mClearSpeaker.setSummary(R.string.clear_speaker_summary);
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ClearSpeakerHelper.getInstance().isRunning()) {
            ClearSpeakerHelper.getInstance().stop();
        }
    }
}
