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

import java.util.ArrayList;

/*
 * Handling upcoming persistence features
 */
public class DriveManager {
	private ArrayList<Drive> discGolfThrows;

	public DriveManager() {
		discGolfThrows = new ArrayList<Drive>();
	}

	public void addDiscGolfThrow(Drive d) {
		discGolfThrows.add(d);

	}

	public ArrayList<Drive> getDiscGolfThrows() {
		return discGolfThrows;
	}

}
