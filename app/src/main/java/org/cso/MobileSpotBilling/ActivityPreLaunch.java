package org.cso.MobileSpotBilling;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActivityPreLaunch extends AppCompatActivity {

    private static final int PERMISSION_READ_STATE = 10;
    private static final int PERMISSION_INTERNET = 11;
    private static final int PERMISSION_BLUETOOTH = 12;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 13;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 14;
    private static final int PERMISSION_CHANGE_NETWORK_STATE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_launch);

        if (ContextCompat.checkSelfPermission(ActivityPreLaunch.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityPreLaunch.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
        }
        else
        {
            Intent in = new Intent(ActivityPreLaunch.this, ActvLaunchApp.class);
            startActivity(in);
            finish();
        }



        /*if (ContextCompat.checkSelfPermission(ActivityPreLaunch.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityPreLaunch.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET);
        }

        if (ContextCompat.checkSelfPermission(ActivityPreLaunch.this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityPreLaunch.this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        }

        if (ContextCompat.checkSelfPermission(ActivityPreLaunch.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityPreLaunch.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(ActivityPreLaunch.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityPreLaunch.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(ActivityPreLaunch.this, Manifest.permission.CHANGE_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityPreLaunch.this, new String[]{Manifest.permission.CHANGE_NETWORK_STATE}, PERMISSION_CHANGE_NETWORK_STATE);
        }*/


        /*String[] PermissionsLocation = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH,
                Manifest.permission.INTERNET};
        for (int i = 0; i < PermissionsLocation.length; i++) {

            if (ContextCompat.checkSelfPermission(ActivityPreLaunch.this, PermissionsLocation[i]) !=
                    PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityPreLaunch.this,
                        PermissionsLocation[i])) {

                } else {

                    ActivityCompat.requestPermissions(ActivityPreLaunch.this,
                            new String[]{PermissionsLocation[i]},
                            i);
                    if (PermissionsLocation[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }
                    if (PermissionsLocation[i] == Manifest.permission.ACCESS_FINE_LOCATION) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }

                    if (PermissionsLocation[i] == Manifest.permission.READ_PHONE_STATE) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }

                    if (PermissionsLocation[i] == Manifest.permission.CHANGE_NETWORK_STATE) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }

                    if (PermissionsLocation[i] == Manifest.permission.ACCESS_COARSE_LOCATION) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }

                    if (PermissionsLocation[i] == Manifest.permission.BLUETOOTH) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }

                    if (PermissionsLocation[i] == Manifest.permission.INTERNET) {
                        try {
                            //clog = CLogger.initLoggerInstance(SplashScreen.this);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }*/



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean blFlag1 = false, blFlag2 = false, blFlag3 = false, blFlag4 = false, blFlag5 = false, blFlag6 = false;

        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    Intent in = new Intent(ActivityPreLaunch.this, ActvLaunchApp.class);
                    startActivity(in);
                    finish();

                } else {
                    // permission denied
                    blFlag2 = false;
                }
                return;
            }

            case PERMISSION_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    blFlag2 = true;

                } else {
                    // permission denied
                    blFlag2 = false;
                }
                return;
            }

            case PERMISSION_BLUETOOTH: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    blFlag3 = true;

                } else {
                    // permission denied
                    blFlag3 = false;
                }
                return;
            }

            case PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    blFlag4 = true;

                } else {
                    // permission denied
                    blFlag4 = false;
                }
                return;
            }

            case PERMISSION_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    blFlag5 = true;

                } else {
                    // permission denied
                    blFlag5 = false;
                }
                return;
            }

            case PERMISSION_CHANGE_NETWORK_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    blFlag6 = true;

                } else {
                    // permission denied
                    blFlag6 = false;
                }
                return;
            }

        }

        if (blFlag1 && blFlag2 && blFlag3 && blFlag4 && blFlag5 && blFlag6) {

        }
    }

}
