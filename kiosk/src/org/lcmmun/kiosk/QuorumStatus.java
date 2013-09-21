package org.lcmmun.kiosk;

import java.awt.Color;

/**
 * The different quorum statuses a delegate may present himself as.
 * 
 * @author William Chargin
 * 
 */
public enum QuorumStatus {

	/**
	 * Indicates that the delegate is not present in the committee room.
	 */
	ABSENT(Messages.getString("QuorumStatus.Absent"), Color.RED), //$NON-NLS-1$

	/**
	 * Indicates that the delegate is present in the committee room.
	 */
	PRESENT(Messages.getString("QuorumStatus.Present"), Color.GREEN), //$NON-NLS-1$

	/**
	 * Indicates that the delegate is present in the committee room and has
	 * pledged to vote either "yes" or "no" on all substantive matters.
	 */
	PRESENT_AND_VOTING(Messages.getString("QuorumStatus.PresentVoting"), Color.BLUE); //$NON-NLS-1$

	/**
	 * The human-readable form of this status.
	 */
	public final String humanReadableText;

	/**
	 * The color associated with the icon associated with this quorum status.
	 */
	public final Color color;

	/**
	 * The default quorum status.
	 */
	public static final QuorumStatus DEFAULT = PRESENT;

	private QuorumStatus(String humanReadableText, Color color) {
		this.humanReadableText = humanReadableText;
		this.color = color;
	}

	@Override
	public String toString() {
		return humanReadableText;
	}

}
