package com.illotus.dgd;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DriveArrayAdapter extends ArrayAdapter<Drive> {
	private Unit distanceUnit;

	public DriveArrayAdapter(Context c, ArrayList<Drive> drives, Unit u) {
		super(c, R.layout.list_drive, drives);
		this.distanceUnit = u;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_drive, parent, false);
		}
		TextView text = (TextView) convertView
				.findViewById(R.id.drive_distance);
		Drive drive = getItem(position);
		if (drive != null) {
			text.setText(drive.getDistanceRoundedToTwoDecimals(distanceUnit)
					+ " " + distanceUnit.getAbbreviation());
		}

		return convertView;

	}

}
