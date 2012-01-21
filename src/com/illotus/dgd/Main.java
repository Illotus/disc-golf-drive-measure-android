/*******************************************************************************
 * Copyright (c) 2012 Olavi Lehtola.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Olavi Lehtola - initial API and implementation
 ******************************************************************************/
package com.illotus.dgd;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements Observer {
	private TextView currentMeasuredDistance;
	private TextView lastSavedDistance;
	private MyLocationManager myLocationManager;
	private GPSLocationManager gpsLocationManager;
	private DriveManager driveManager;
	private ProgressDialog waitForGPSFix;
	private Unit distanceUnit;
	private AppPreferences preferences;
	private Boolean gpsShouldBeOn;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		instantiateTextViews();
		switchMeasuringButtons(false);
		myLocationManager = new MyLocationManager();
		myLocationManager.addObserver(this);
		gpsLocationManager = new GPSLocationManager(this, myLocationManager);
		gpsLocationManager.addObserver(this);
		driveManager = ((App) getApplicationContext()).getThrowManager();
		waitForGPSFix = null;
		preferences = new AppPreferences(this);
		distanceUnit = preferences.getDistanceUnit();
		gpsShouldBeOn = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		gpsLocationManager.stopGPS();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gpsShouldBeOn) {
			gpsLocationManager.activateGPS();
		}
	}

	private void instantiateTextViews() {
		currentMeasuredDistance = (TextView) findViewById(R.id.current_distance);
		lastSavedDistance = (TextView) findViewById(R.id.last_saved_distance);
	}

	public void markNewStartingSpot(View startSpotChangerButton) {
		if (!gpsShouldBeOn) {
			gpsShouldBeOn = true;
			createAndShowWaitForGPSFixDialog(startSpotChangerButton);
			gpsLocationManager.activateGPS();
			((Button) startSpotChangerButton).setText(R.string.mark_new_throwing_spot);
		} else {
			myLocationManager.resetStartLocationToCurrent();
			Toast.makeText(this, "Throwing spot changed to current location", Toast.LENGTH_LONG).show();
		}
	}

	public void showDriveList(View showDrivesButton) {
		Intent throwList = new Intent(Main.this, DriveList.class);
		startActivity(throwList);		
	}
	
	public void markDistance(View markDistanceButton) {
		lastSavedDistance.setText(myLocationManager.getCurrentDistance().getDistanceRoundedToTwoDecimals(distanceUnit) + " " + distanceUnit.getAbbreviation());
		driveManager.addDiscGolfThrow(new Drive(myLocationManager.getCurrentDistance()));
	}


	private void createAndShowWaitForGPSFixDialog(View startSpotChangerButton) {
		waitForGPSFix = new ProgressDialog(Main.this);
		waitForGPSFix.setMessage("Getting your position");
		waitForGPSFix.setOnCancelListener(getCancelListener(startSpotChangerButton));
		waitForGPSFix.setButton("Cancel", buildSearchGPSCancelListener(startSpotChangerButton));
		waitForGPSFix.show();
	}
	private OnCancelListener getCancelListener(final View startSpotChangerButton) {
		return new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				Main.this.cancelStartThrowing(startSpotChangerButton);
			}
		};
	}
	protected DialogInterface.OnClickListener buildSearchGPSCancelListener(final View startSpotChangerButton) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Main.this.cancelStartThrowing(startSpotChangerButton);
			}
		};
	}

	protected void cancelStartThrowing(View startSpotChangerButton) {
		gpsLocationManager.stopGPS();
		((Button) startSpotChangerButton).setText(R.string.mark_throwing_spot);
		this.gpsShouldBeOn = false;
	}

	private void switchMeasuringButtons(Boolean state) {
		findViewById(R.id.mark_distance).setEnabled(state);
		findViewById(R.id.show_current_session_throws).setEnabled(state);
	}

	public void update(Observable observable, Object data) {
		if (observable.getClass().equals(MyLocationManager.class)) {
			updateLocation(observable);
		} else {
			offerToTurnOnGPS();
		}
	}

	private void offerToTurnOnGPS() {
		AlertDialog.Builder builder = makeGPSNotAvailableBuilder();
		AlertDialog alert = builder.create();
		alert.show();
	}

	/*
	 * Based on example in Android in Practice
	 */
	private AlertDialog.Builder makeGPSNotAvailableBuilder() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("GPS is not enabled")
				.setMessage("Would you like to go to the location settings and enable GPS?")
				.setCancelable(true)
				.setPositiveButton("Yes", getOkListener())
				.setNegativeButton("No", getCancelListener());
		return builder;
	}
	
	private android.content.DialogInterface.OnClickListener getOkListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
			}
		};
	}
	
	private android.content.DialogInterface.OnClickListener getCancelListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		};
	}

	private void updateLocation(Observable observable) {
		MyLocationManager mlm = (MyLocationManager) observable;
		if (myLocationManager.currentLocationIsStartingLocation()) {
			waitForGPSFix.dismiss();
			switchMeasuringButtons(true);
		}
		currentMeasuredDistance.setText(mlm.getCurrentDistance().getDistanceRoundedToTwoDecimals(distanceUnit) + " "+ distanceUnit.getAbbreviation());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.choose_unit) {
			handlingChoosingDistanceUnit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void handlingChoosingDistanceUnit() {
		final CharSequence[] items = distanceUnit.getUnitNames();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick unit of measure");
		builder.setSingleChoiceItems(items, distanceUnit.getIndex(),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						distanceUnit = Unit.getUnit(item);
						preferences.saveDistanceUnit(distanceUnit);
						refreshUnitContainingTextViews();
						dialog.cancel();
					}
				});
		builder.create().show();		
	}
	
	

	protected void refreshUnitContainingTextViews() {
		if (!currentMeasuredDistance.getText().equals("N/A")) {
			this.myLocationManager.refresh();
			if (!lastSavedDistance.getText().equals("N/A")) {
				lastSavedDistance.setText(this.driveManager.getLastDistance(distanceUnit) + " " + distanceUnit.getAbbreviation());
			}
						
		}
	}

}
