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

import java.text.DecimalFormat;

import android.location.Location;

public class Drive {
	private double distance;

	public Drive(double distance) {
		this.distance = distance;
	}

	public Drive(Location start, Location current) {
		this.distance = start.distanceTo(current);
	}

	public Drive(Drive d) {
		this.distance = d.getDistance();
	}

	public void update(Location start, Location current) {
		this.distance = start.distanceTo(current);
	}

	public String getDistanceRoundedToTwoDecimals(Unit distanceUnit) {
		DecimalFormat twoDForm = new DecimalFormat("####.##");
		return twoDForm.format(distanceUnit.convert(distance));
	}

	public double getDistance() {
		return distance;
	}

}
