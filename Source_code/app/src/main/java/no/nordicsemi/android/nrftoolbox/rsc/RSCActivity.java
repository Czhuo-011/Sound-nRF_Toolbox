/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrftoolbox.rsc;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileServiceReadyActivity;
import no.nordicsemi.android.nrftoolbox.rsc.settings.SettingsActivity;
import no.nordicsemi.android.nrftoolbox.rsc.settings.SettingsFragment;

public class RSCActivity extends BleProfileServiceReadyActivity<RSCService.RSCBinder> {
    private TextView speedView;
    private TextView speedUnitView;
    private TextView cadenceView;
    private TextView distanceView;
    private TextView distanceUnitView;
    private TextView totalDistanceView;
    private TextView totalDistanceUnitView;
    private TextView stridesCountView;
    private TextView activityView;
    private TextView batteryLevelView;

    private int cad = 5; //variable to store cadence value
    private int currCad = 0;
    private int previousCad = 0;
    private List<Integer> cadList = new ArrayList<>();
    private int maxCad = 0;
    private boolean workOnCad = true;
    private boolean isFirstTimeRun = true;
    private int play_media = 0; //flag to control media playing

    private long per; //variable to store value of period
    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100); // tone generator object

    private final Handler mHandler = new Handler(); // handler to schedule runnable task

    private final Runnable mTask = new Runnable() {

        @Override
        public void run() {

            if (play_media == 1) {
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 25);//generate beep for 25 seconds. It can be changed.
            }
            //Log.i("This is my cadence",String.valueOf(cad));
            //Log.i("This is my period",String.valueOf(per));
            mHandler.postDelayed(mTask, per);
        }
    };


    @Override
    protected void onCreateView(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_feature_rsc);
        setGui();
    }

    @Override
    protected void onInitialize(final Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, makeIntentFilter());
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver_for_media, makeIntentFilter());
        mHandler.postDelayed(mTask, 1000);
        //play_media=1;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver_for_media);
        mHandler.removeCallbacks(mTask);
        play_media = 0;
    }

    private void setGui() {
        speedView = findViewById(R.id.speed);
        speedUnitView = findViewById(R.id.speed_unit);
        cadenceView = findViewById(R.id.cadence);
        distanceView = findViewById(R.id.distance);
        distanceUnitView = findViewById(R.id.distance_unit);
        totalDistanceView = findViewById(R.id.total_distance);
        totalDistanceUnitView = findViewById(R.id.total_distance_unit);
        stridesCountView = findViewById(R.id.strides);
        activityView = findViewById(R.id.activity);
        batteryLevelView = findViewById(R.id.battery);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefaultUI();
    }

    @Override
    protected void setDefaultUI() {
        speedView.setText(R.string.not_available_value);
        cadenceView.setText(R.string.not_available_value);
        distanceView.setText(R.string.not_available_value);
        totalDistanceView.setText(R.string.not_available_value);
        stridesCountView.setText(R.string.not_available_value);
        activityView.setText(R.string.not_available);
        batteryLevelView.setText(R.string.not_available);

        setUnits();
    }

    private void setUnits() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int unit = Integer.parseInt(preferences.getString(SettingsFragment.SETTINGS_UNIT, String.valueOf(SettingsFragment.SETTINGS_UNIT_DEFAULT)));

        switch (unit) {
            case SettingsFragment.SETTINGS_UNIT_M_S: // [m/s]
                speedUnitView.setText(R.string.rsc_speed_unit_m_s);
                distanceUnitView.setText(R.string.rsc_distance_unit_m);
                totalDistanceUnitView.setText(R.string.rsc_total_distance_unit_km);
                break;
            case SettingsFragment.SETTINGS_UNIT_KM_H: // [km/h]
                speedUnitView.setText(R.string.rsc_speed_unit_km_h);
                distanceUnitView.setText(R.string.rsc_distance_unit_m);
                totalDistanceUnitView.setText(R.string.rsc_total_distance_unit_km);
                break;
            case SettingsFragment.SETTINGS_UNIT_MPH: // [mph]
                speedUnitView.setText(R.string.rsc_speed_unit_mph);
                distanceUnitView.setText(R.string.rsc_distance_unit_yd);
                totalDistanceUnitView.setText(R.string.rsc_total_distance_unit_mile);
                break;
        }
    }

    @Override
    protected int getLoggerProfileTitle() {
        return R.string.rsc_feature_title;
    }

    @Override
    protected int getDefaultDeviceName() {
        return R.string.rsc_default_name;
    }

    @Override
    protected int getAboutTextId() {
        return R.string.rsc_about_text;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings_and_about, menu);
        return true;
    }

    @Override
    protected boolean onOptionsItemSelected(final int itemId) {
        switch (itemId) {
            case R.id.action_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected Class<? extends BleProfileService> getServiceClass() {
        return RSCService.class;
    }

    @Override
    protected UUID getFilterUUID() {
        return RSCManager.RUNNING_SPEED_AND_CADENCE_SERVICE_UUID;
    }

    @Override
    protected void onServiceBound(final RSCService.RSCBinder binder) {
        // not used
    }

    @Override
    protected void onServiceUnbound() {
        // not used
    }

    @Override
    public void onServicesDiscovered(@NonNull final BluetoothDevice device, final boolean optionalServicesFound) {
        // not used
    }

    @Override
    public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
        super.onDeviceDisconnected(device);
        batteryLevelView.setText(R.string.not_available);
        play_media = 0;

    }

    private void onMeasurementReceived(float speed, int cadence, long totalDistance, final boolean running) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int unit = Integer.parseInt(preferences.getString(SettingsFragment.SETTINGS_UNIT, String.valueOf(SettingsFragment.SETTINGS_UNIT_DEFAULT)));

        switch (unit) {
            case SettingsFragment.SETTINGS_UNIT_KM_H:
                speed = speed * 3.6f;
                // pass through intended
            case SettingsFragment.SETTINGS_UNIT_M_S:
                if (totalDistance == -1) {
                    totalDistanceView.setText(R.string.not_available);
                    totalDistanceUnitView.setText(null);
                } else {
                    totalDistanceView.setText(String.format(Locale.US, "%.2f", totalDistance / 1000.0f)); // 1 km in m
                    totalDistanceUnitView.setText(R.string.rsc_total_distance_unit_km);
                }
                break;
            case SettingsFragment.SETTINGS_UNIT_MPH:
                speed = speed * 2.2369f;
                if (totalDistance == -1) {
                    totalDistanceView.setText(R.string.not_available);
                    totalDistanceUnitView.setText(null);
                } else {
                    totalDistanceView.setText(String.format(Locale.US, "%.2f", totalDistance / 1609.31f)); // 1 mile in m
                    totalDistanceUnitView.setText(R.string.rsc_total_distance_unit_mile);
                }
                break;
        }

        speedView.setText(String.format(Locale.US, "%.1f", speed));
        cadenceView.setText(String.format(Locale.US, "%d", cad));
        ;


        activityView.setText(running ? R.string.rsc_running : R.string.rsc_walking);
    }

    private void onStripesUpdate(final long distance, final int strides) {
        if (distance == -1) {
            distanceView.setText(R.string.not_available);
            distanceUnitView.setText(R.string.rsc_distance_unit_m);
        } else {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final int unit = Integer.parseInt(preferences.getString(SettingsFragment.SETTINGS_UNIT, String.valueOf(SettingsFragment.SETTINGS_UNIT_DEFAULT)));

            switch (unit) {
                case SettingsFragment.SETTINGS_UNIT_KM_H:
                case SettingsFragment.SETTINGS_UNIT_M_S:
                    if (distance < 100000L) { // 1 km in cm
                        distanceView.setText(String.format(Locale.US, "%.1f", distance / 100.0f));
                        distanceUnitView.setText(R.string.rsc_distance_unit_m);
                    } else {
                        distanceView.setText(String.format(Locale.US, "%.2f", distance / 100000.0f));
                        distanceUnitView.setText(R.string.rsc_distance_unit_km);
                    }
                    break;
                case SettingsFragment.SETTINGS_UNIT_MPH:
                    if (distance < 160931L) { // 1 mile in cm
                        distanceView.setText(String.format(Locale.US, "%.1f", distance / 91.4392f));
                        distanceUnitView.setText(R.string.rsc_distance_unit_yd);
                    } else {
                        distanceView.setText(String.format(Locale.US, "%.2f", distance / 160931.23f));
                        distanceUnitView.setText(R.string.rsc_distance_unit_mile);
                    }
                    break;
            }
        }

        stridesCountView.setText(String.valueOf(strides));
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            if (RSCService.BROADCAST_RSC_MEASUREMENT.equals(action)) {
                final float speed = intent.getFloatExtra(RSCService.EXTRA_SPEED, 0.0f);
                final int cadence = intent.getIntExtra(RSCService.EXTRA_CADENCE, 0);
                final long totalDistance = intent.getLongExtra(RSCService.EXTRA_TOTAL_DISTANCE, -1);
                final boolean running = intent.getBooleanExtra(RSCService.EXTRA_ACTIVITY, false);
                // Update GUI
                onMeasurementReceived(speed, cadence, totalDistance, running);

            } else if (RSCService.BROADCAST_STRIDES_UPDATE.equals(action)) {
                final int strides = intent.getIntExtra(RSCService.EXTRA_STRIDES, 0);
                final long distance = intent.getLongExtra(RSCService.EXTRA_DISTANCE, -1);
                // Update GUI
                onStripesUpdate(distance, strides);
            }
        }
    };

    //this is broadcast receiver used for period calculation
    private final BroadcastReceiver broadcastReceiver_for_media = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            if (RSCService.BROADCAST_RSC_MEASUREMENT.equals(action)) {
                final int cadence = intent.getIntExtra(RSCService.EXTRA_CADENCE, 0);
                cad = cadence * 2;

                if (cad > 10) {                 //checking if cadence is greater than 10
                    float fraction;
                    fraction = 60f / cad;
                    per = (long) (fraction * 1000L);

                    if (isFirstTimeRun) {       //checking variable named "isFirstTimeRun" is true if True then it will enter into if loop
                        isFirstTimeRun = false; // Changing "isFirstTimeRun" to False when it runs for the first time
                        if (workOnCad) {        //checking variable named "isFirstTimeRun" is true if True then it will enter into if loop
                            workOnCad = false;  // Changing "workOnCad" to False once cadence start to increase

                            Handler handler = new Handler();    // Initializing handler object
                            play_media = 1;                     //enable sound
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    workOnCad = true;           // Changing "workOnCad" to True after 2 minutes beep sound
                                    maxCad = cad;               // Saving cadence at the end of 2 minutes in variable named "maxCad"
                                }
                            }, 2 * 60 * 1000);
                        }
                    } else {
                        if (workOnCad) {
                            if (maxCad - cad > 15 || maxCad - cad < -14) { //Checking both upper and lower difference of 15
                                workOnCad = false;

                                Handler handler = new Handler();
                                play_media = 1;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        workOnCad = true;
                                        maxCad = cad;
                                    }
                                }, 2 * 60 * 1000);

                            } else {
                                per = 1000L;
                                play_media = 0;     //Disable beep Sound
                            }

                        }
                    }


                } else if (cad == 0) {
                    isFirstTimeRun = true;
                    workOnCad = true;

                    per = 1000L;
                    play_media = 0;     //Disable beep Sound
                } else {
                    per = 1000L;
                    play_media = 0;
                }
            }
        }
    };

    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RSCService.BROADCAST_RSC_MEASUREMENT);
        intentFilter.addAction(RSCService.BROADCAST_STRIDES_UPDATE);
        return intentFilter;
    }
}
