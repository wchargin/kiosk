package org.lcmmun.kiosk;

/**
 * The various possible statuses of a delegate.
 * 
 * @author William Chargin
 * 
 */
/**
 * @author William
 * 
 */
public enum MemberStatus {

	/**
	 * Indicates that the delegate is a full member and can veto substantive
	 * matters.
	 */
	VETO_MEMBER(true, true, Messages.getString("MemberStatus.VetoMember")), //$NON-NLS-1$

	/**
	 * Indicates that the delegate in question is a full member of the
	 * committee.
	 */
	FULL_MEMBER(true, false, Messages.getString("MemberStatus.FullMember")), //$NON-NLS-1$

	/**
	 * Indicates that the delegate in question is an observer, meaning that the
	 * delegate can speak, vote on procedural matters, and to sponsor/sign
	 * resolutions, but not to vote on substantive matters.
	 */
	OBSERVER(false, false, Messages.getString("MemberStatus.ObserverMember")); //$NON-NLS-1$

	/**
	 * The default status.
	 */
	public static final MemberStatus DEFAULT_STATUS = FULL_MEMBER;

	/**
	 * Whether delegates of this status can vote on substantive matters.
	 */
	public final boolean canVoteSubstantive;

	/**
	 * Whether delegates of this status have veto power on substantive matters.
	 */
	public final boolean hasVetoPower;

	/**
	 * The human-readable form of this member status.
	 */
	public final String humanReadableText;

	private MemberStatus(boolean canVoteSubstantive, boolean hasVetoPower,
			String humanReadableText) {
		this.canVoteSubstantive = canVoteSubstantive;
		this.hasVetoPower = hasVetoPower;
		this.humanReadableText = humanReadableText;
	}

	@Override
	public String toString() {
		return humanReadableText;
	}

}
