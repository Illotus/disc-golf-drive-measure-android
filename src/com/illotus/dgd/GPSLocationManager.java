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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSLocationManager extends Observable implements LocationListener {
	private LocationManager locationManager;
	private MyLocationManager distance;
	private Context context;

	public GPSLocationManager(Context context, MyLocationManager distance) {
		this.context = context;
		locationManager = null;
		this.distance = distance;

	}

	public void onLocationChanged(Location location) {
		if (location != null) {
			distance.updateLocation(location);
		}
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void onProviderDisabled(String arg0) {
		setChanged();
		this.notifyObservers();
	}

	public void activateGPS() {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 1, this);
		} else {
			setChanged();
			this.notifyObservers();
		}
	}

	public void stopGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager = null;
		}
	}

}
