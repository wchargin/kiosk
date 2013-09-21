package org.lcmmun.kiosk;

/**
 * The various ways a motion can end.
 * 
 * @author William Chargin
 * 
 */
public enum MotionResult {
	/**
	 * Indicates that a motion was voted on by the committee and passed.
	 */
	PASSED(true, false, Messages.getString("MotionResult.Passed")), //$NON-NLS-1$

	/**
	 * Indicates that the chair unilaterally passed the motion.
	 */
	UNILATERAL(true, true, Messages.getString("MotionResult.Unilateral")), //$NON-NLS-1$

	/**
	 * Indicates that a motion was voted on by the committee and failed.
	 */
	FAILED(false, false, Messages.getString("MotionResult.Failed")), //$NON-NLS-1$

	/**
	 * Indicates that a motion was ruled dilatory by the chair, and thus failed.
	 */
	DILATORY(false, true, Messages.getString("MotionResult.Dilatory")); //$NON-NLS-1$

	/**
	 * The human-readable form of this result.
	 */
	public final String humanReadableString;

	/**
	 * Whether the motion passed ({@code true}) or failed ({@code false}).
	 */
	public final boolean passed;

	/**
	 * Whether the motion's outcome was due to the chair's discretion (
	 * {@code true}) or due to a committee vote ({@code false}).
	 */
	public final boolean discretion;

	private MotionResult(boolean passed, boolean discretion,
			String humanReadableString) {
		this.passed = passed;
		this.discretion = discretion;
		this.humanReadableString = humanReadableString;
	}

	@Override
	public String toString() {
		return humanReadableString;
	}
}
