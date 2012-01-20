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

import android.location.Location;

public class MyLocationManager extends Observable {
	private Location startLocation;
	private Location currentLocation;
	private Boolean currentLocationIsStartingLocation;
	private Drive currentDistance;

	public MyLocationManager() {
		startLocation = null;
		currentLocation = null;
		currentDistance = null;
		currentLocationIsStartingLocation = true;
		currentDistance = new Drive(0);
	}

	public Boolean currentLocationIsStartingLocation() {
		return currentLocationIsStartingLocation;
	}
	
	public void refresh() {
		setChanged();
		notifyObservers();
		
	}

	public void updateLocation(Location location) {
		if (startLocation == null) {
			startLocation = location;
			currentLocation = location;
			currentDistance = new Drive(0);
		} else {
			currentLocation = location;
			currentDistance.update(startLocation, currentLocation);
			currentLocationIsStartingLocation = false;
		}
		setChanged();
		notifyObservers();
	}

	public void resetStartLocationToCurrent() {
		startLocation = currentLocation;
		currentLocationIsStartingLocation = true;
		currentDistance = new Drive(0);
		setChanged();
		notifyObservers();
	}

	public Drive getCurrentDistance() {
		return currentDistance;
	}
}
