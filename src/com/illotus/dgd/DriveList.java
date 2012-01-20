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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DriveList extends ListActivity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	private DriveManager throwManager;
	private DriveArrayAdapter listAdapter;
	private Button shareThrows;
	private Unit distanceUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.throw_list);
		throwManager = ((DiscGolfDriveMeter) getApplicationContext())
				.getThrowManager();
		setUpShareButton();
		AppPreferences preferences = new AppPreferences(this);
		distanceUnit = preferences.getDistanceUnit();
		listAdapter = new DriveArrayAdapter(this,
				throwManager.getDiscGolfThrows(), distanceUnit);
		setListAdapter(listAdapter);

	}

	private void setUpShareButton() {
		shareThrows = (Button) findViewById(R.id.share);
		shareThrows.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/*");
				i.putExtra(Intent.EXTRA_SUBJECT, "Subject:");
				i.putExtra(Intent.EXTRA_TEXT, createShareMessage());
				try {
					startActivity(Intent.createChooser(i, "Share results ..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(
							DriveList.this,
							"There are no chooser options installed for the text/html "
									+ " + type.", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	public String createShareMessage() {
		StringBuffer sb = new StringBuffer();
		for (Drive d : this.throwManager.getDiscGolfThrows()) {
			sb.append(d.getDistanceRoundedToTwoDecimals(distanceUnit) + " "
					+ distanceUnit.getAbbreviation() + "\n");
		}
		this.throwManager.getDiscGolfThrows();

		return sb.toString();
	}

	public String getDistanceUnitAbbreviation() {
		return this.distanceUnit.getAbbreviation();
	}

}
