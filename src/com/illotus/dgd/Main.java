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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity implements Observer {
	private Button startThrowing;
	private Button markDistance;
	private Button showThrows;
	private TextView currentMeasuredDistance;
	private TextView lastSavedDistance;
	private MyLocationManager myLocationManager;
	private GPSLocationManager gpsLocationManager;
	private TextView currentDistanceLabel;
	private DriveManager throwManager;
	private ProgressDialog waitForGPSFix;
	private Unit distanceUnit;
	private AppPreferences preferences;
	private AlertDialog chooseDistanceUnit;
	private Boolean gpsShouldBeOn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		instantiateButtons();
		instantiateTextViews();
		switchMeasuringButtons(false);
		myLocationManager = new MyLocationManager();
		myLocationManager.addObserver(this);
		gpsLocationManager = new GPSLocationManager(this, myLocationManager);
		gpsLocationManager.addObserver(this);
		throwManager = ((App) getApplicationContext()).getThrowManager();
		waitForGPSFix = null;
		preferences = new AppPreferences(this);
		distanceUnit = preferences.getDistanceUnit();
		instantiateChooseDistanceUnit();
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

	private void instantiateButtons() {
		instantiateStartThrowing();
		instantiateMarkDistance();
		instantiateShowThrows();
	}

	private void instantiateShowThrows() {
		showThrows = (Button) findViewById(R.id.show_current_session_throws);
		showThrows.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent throwList = new Intent(Main.this, DriveList.class);
				startActivity(throwList);
			}
		});

	}

	private void instantiateMarkDistance() {
		markDistance = (Button) findViewById(R.id.mark_distance);
		markDistance.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				lastSavedDistance.setText(myLocationManager
						.getCurrentDistance().getDistanceRoundedToTwoDecimals(
								distanceUnit)
						+ " " + distanceUnit.getAbbreviation());
				throwManager.addDiscGolfThrow(new Drive(myLocationManager
						.getCurrentDistance()));
			}
		});
	}

	private OnClickListener getResetStartingPositionListener() {
		return new OnClickListener() {
			public void onClick(View v) {
				myLocationManager.resetStartLocationToCurrent();

			}
		};
	}

	private void instantiateStartThrowing() {
		startThrowing = (Button) findViewById(R.id.start_throwing);
		currentDistanceLabel = (TextView) findViewById(R.id.current_distance_label);
		currentDistanceLabel.setVisibility(View.VISIBLE);
		startThrowing.setOnClickListener(getInitialListener());

	}

	private OnClickListener getInitialListener() {
		return new OnClickListener() {
			public void onClick(View v) {
				Main.this.gpsShouldBeOn = true;
				createAndShowWaitForGPSFixDialog();
				gpsLocationManager.activateGPS();

			}

			private void createAndShowWaitForGPSFixDialog() {
				waitForGPSFix = new ProgressDialog(Main.this);
				waitForGPSFix.setTitle("Please wait...");
				waitForGPSFix.setMessage("Getting your position");
				waitForGPSFix.setOnCancelListener(getCancelListener());
				waitForGPSFix.setButton("Cancel",
						buildSearchGPSCancelListener());
				waitForGPSFix.show();
			}

			private OnCancelListener getCancelListener() {
				return new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						Main.this.cancelStartThrowing(true);
						finish();
					}
				};
			}

			protected DialogInterface.OnClickListener buildSearchGPSCancelListener() {
				DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						waitForGPSFix.dismiss();
						gpsLocationManager.stopGPS();
						startThrowing.setEnabled(true);
					}
				};
				return l;
			}
		};
	}

	protected void cancelStartThrowing(boolean b) {
		currentDistanceLabel.setVisibility(View.GONE);
		gpsLocationManager.stopGPS();
		startThrowing.setEnabled(true);
	}

	private void switchMeasuringButtons(Boolean state) {
		markDistance.setEnabled(state);
		showThrows.setEnabled(state);
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
				.setMessage(
						"Would you like to go to the location settings and enable GPS?")
				.setCancelable(true)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startActivity(new Intent(
										Settings.ACTION_SECURITY_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finish();
					}
				});
		return builder;
	}

	private void updateLocation(Observable observable) {
		MyLocationManager mlm = (MyLocationManager) observable;
		if (myLocationManager.currentLocationIsStartingLocation()) {
			waitForGPSFix.dismiss();
			switchMeasuringButtons(true);
			startThrowing.setText(R.string.mark_new_starting_spot);
			startThrowing
					.setOnClickListener(getResetStartingPositionListener());
		}
		currentMeasuredDistance.setText(mlm.getCurrentDistance()
				.getDistanceRoundedToTwoDecimals(distanceUnit)
				+ " "
				+ distanceUnit.getAbbreviation());
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
			chooseDistanceUnit.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void instantiateChooseDistanceUnit() {
		final CharSequence[] items = distanceUnit.getUnitNames();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick unit of measure");
		builder.setSingleChoiceItems(items, distanceUnit.getIndex(),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

					}
				});
		builder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int distanceUnitNumber) {
						distanceUnit = Unit
								.getUnit(Main.this.chooseDistanceUnit
										.getListView().getCheckedItemPosition());
						preferences.saveDistanceUnit(distanceUnit);
						System.out.println(distanceUnit.getName());
						refreshUnitContainingTextViews();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int distanceUnitNumber) {
						dialog.cancel();
					}
				});
		chooseDistanceUnit = builder.create();
	}

	protected void refreshUnitContainingTextViews() {
		lastSavedDistance.setText(myLocationManager.getCurrentDistance()
				.getDistanceRoundedToTwoDecimals(distanceUnit)
				+ " "
				+ distanceUnit.getAbbreviation());
		currentMeasuredDistance.setText(myLocationManager.getCurrentDistance()
				.getDistanceRoundedToTwoDecimals(distanceUnit)
				+ " "
				+ distanceUnit.getAbbreviation());
	}

}
