package com.illotus.dgd;

public enum Unit {
	METER(0, 1.00, "meters", "m"), FOOT(1, 0.33, "feet", "f");

	private int index;
	private double modifier;
	private String name;

	public String getName() {
		return name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	private String abbreviation;

	private Unit(int index, double modifier, String name, String abbreviation) {
		this.index = index;
		this.modifier = modifier;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public double getModifier() {
		return modifier;
	}

	public static Unit getUnit(int unitNumber) {
		if (unitNumber == 0) {
			return Unit.METER;
		}
		return Unit.FOOT;
	}

	public int getPreferenceID() {
		return index;
	}

	public CharSequence[] getUnitNames() {
		CharSequence[] units = { METER.name, FOOT.name };
		return units;
	}

	public double convert(double distance) {
		return distance / modifier;
	}

	public int getIndex() {
		return index;
	}

}
