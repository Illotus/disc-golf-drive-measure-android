package com.illotus.dgd;

import android.content.Context;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
	private static final int UNIT_DEFAULT_PREFERENCE_ID = 0;
	private SharedPreferences appSharedPrefs;
	private Editor prefsEditor;

	public AppPreferences(Context context) {
		this.appSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.prefsEditor = appSharedPrefs.edit();
	}

	public Unit getDistanceUnit() {
		return Unit.getUnit(appSharedPrefs.getInt("Unit",
				UNIT_DEFAULT_PREFERENCE_ID));
	}

	public void saveDistanceUnit(Unit u) {
		prefsEditor.putInt("Unit", u.getPreferenceID());
		prefsEditor.commit();
	}

}
