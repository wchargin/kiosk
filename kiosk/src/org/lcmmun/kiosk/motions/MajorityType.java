package org.lcmmun.kiosk.motions;

import org.lcmmun.kiosk.Messages;

/**
 * Different motions require different "majorities" in order to pass; this enum
 * represents those different options.
 * 
 * @author William Chargin
 * 
 */
public enum MajorityType {

	/**
	 * Indicates that a motion requires more "yes" votes than "no" votes to
	 * pass.
	 */
	SIMPLE(Messages.getString("MajorityType.Simple")), //$NON-NLS-1$

	/**
	 * Indicates that a motion requires at least twice as many "yes" votes as
	 * "no" votes to pass.
	 */
	QUALIFIED(Messages.getString("MajorityType.Qualified")); //$NON-NLS-1$

	/**
	 * The human-readable form of this {@code MajorityType}.
	 */
	public final String humanReadableString;

	private MajorityType(String humanReadableString) {
		this.humanReadableString = humanReadableString;
	}

	/**
	 * Determines if a motion with the given majority type and the given number
	 * of "yes" and "no" votes passes.
	 * 
	 * @param yes
	 *            the number of "yes" votes
	 * @param no
	 *            the number of "no" votes
	 * @return {@code true} if the motion passes, or {@code false} if it fails
	 */
	public boolean passes(int yes, int no) throws IllegalArgumentException {
		switch (this) {
		case SIMPLE:
			// Simple majority: more for than against.
			return yes > no;
		case QUALIFIED:
			// Qualified majority: at least twice as many for as against.
			return yes != 0 && yes >= no * 2;
		default:
			throw new AssertionError(this.toString());
		}
	}

	@Override
	public String toString() {
		return humanReadableString;
	}
}
