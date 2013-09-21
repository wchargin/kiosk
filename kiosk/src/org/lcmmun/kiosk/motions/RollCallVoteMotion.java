package org.lcmmun.kiosk.motions;

import java.util.Arrays;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

import tools.customizable.MultipleChoiceProperty;

/**
 * A motion indicating that the proposing delegate wants to vote on a
 * substantive matter on a delegate-by-delegate basis (i.e., the chairs call out
 * the name of each delegate, and the delegate states his vote).
 * 
 * @author William Chargin
 * 
 */
public class RollCallVoteMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The majority type.
	 */
	private final MultipleChoiceProperty<MajorityType> mcpMajority;

	public RollCallVoteMotion(Delegate proposingDelegate) {
		super(proposingDelegate);

		mcpMajority = new MultipleChoiceProperty<MajorityType>(Messages.getString("RollCallVoteMotion.PropertyMajorityType"), //$NON-NLS-1$
				Arrays.<MajorityType> asList(MajorityType.values()),
				MajorityType.QUALIFIED);
		propertySet.add(mcpMajority);
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.NONE;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("RollCallVoteMotion.RollCallVote"); //$NON-NLS-1$
	}

	/**
	 * Gets the majority type for the motion that this
	 * {@code RollCallVoteMotion} suggests voting on.
	 * 
	 * @return the majority type
	 */
	public MajorityType getVotingMajority() {
		return mcpMajority.getValue();
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}

}
