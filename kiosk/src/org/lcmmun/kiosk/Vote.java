package org.lcmmun.kiosk;

/**
 * The different ways a delegate may vote in a roll call vote.
 * 
 * @author William Chargin
 * 
 */
public enum Vote {

	/**
	 * Indicates that the delegate is in favor of the matter being voted on.
	 */
	YES(Messages.getString("Vote.Yes"), true, true, false, false), //$NON-NLS-1$

	/**
	 * Indicates that the delegate is against the matter being voted on.
	 */
	NO(Messages.getString("Vote.No"), Messages.getString("Vote.Veto"), true, false, true, false), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Indicates that the delegate does not wish to cast a vote.
	 */
	ABSTAIN(Messages.getString("Vote.Abstain"), false, false, false, false), //$NON-NLS-1$

	/**
	 * Indicates that the delegate wishes to wait until all other votes have
	 * been cast before casting a vote.
	 */
	PASS(Messages.getString("Vote.Pass"), false, false, false, false), //$NON-NLS-1$

	/**
	 * Indicates that the delegate is in favor of the matter being voted on, and
	 * requests the right of explanation.
	 */
	YES_WITH_RIGHTS(Messages.getString("Vote.YesRights"), false, true, false, true), //$NON-NLS-1$

	/**
	 * Indicates that the delegate is against the matter being voted on, and
	 * requests the right of explanation.
	 */
	NO_WITH_RIGHTS(Messages.getString("Vote.NoRights"), Messages.getString("Vote.VetoRights"), false, false, true, //$NON-NLS-1$ //$NON-NLS-2$
			true);

	/**
	 * The human-readable form of this vote type in a non-veto case.
	 */
	public final String normalText;

	/**
	 * The human-readable form of this vote type in the case of a veto.
	 */
	public final String vetoText;

	/**
	 * Whether this vote type is available in the second round of voting.
	 */
	public final boolean inSecondRound;

	/**
	 * Whether the vote type indicates a vote for.
	 */
	public final boolean isVoteFor;

	/**
	 * Whether the vote type indicates a vote against.
	 */
	public final boolean isVoteAgainst;

	/**
	 * Whether the vote type has rights.
	 */
	public final boolean withRights;

	private Vote(String humanReadableText, boolean inSecondRound,
			boolean isVoteFor, boolean isVoteAgainst, boolean withRights) {
		this(humanReadableText, humanReadableText, inSecondRound, isVoteFor,
				isVoteAgainst, withRights);
	}

	private Vote(String normalText, String vetoText, boolean inSecondRound,
			boolean isVoteFor, boolean isVoteAgainst, boolean withRights) {
		this.normalText = normalText;
		this.vetoText = vetoText;
		this.inSecondRound = inSecondRound;
		this.isVoteFor = isVoteFor;
		this.isVoteAgainst = isVoteAgainst;
		this.withRights = withRights;
	}

}
