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

import android.app.Application;

public class DiscGolfDriveMeter extends Application {
	private DriveManager throwManager;
	private Boolean gpsShouldBeOn;

	@Override
	public void onCreate() {
		super.onCreate();
		throwManager = new DriveManager();
		gpsShouldBeOn = false;
	}

	public DriveManager getThrowManager() {
		return throwManager;
	}

	public void setThrowManager(DriveManager throwManager) {
		this.throwManager = throwManager;
	}

	public String getUnit() {
		return "m";
	
	}
	
	public void gpsOn() {
	    this.gpsShouldBeOn = true;
	}
	public void gpsOff() {
        this.gpsShouldBeOn = false;
    }
	
	public Boolean shouldGPSBeOn() {
	    return this.gpsShouldBeOn;
	}
	

}
