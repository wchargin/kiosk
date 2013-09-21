package org.lcmmun.kiosk.motions;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

/**
 * A motion for division of the question: operative clauses may be split into
 * two voting groups, any number of which may be passed.
 * 
 * @author William Chargin
 * 
 */
public class DivisionOfTheQuestionMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the motion for division of the question, as proposed by the given
	 * delegate.
	 * 
	 * @param proposingDelegate
	 *            the proposing delegate
	 */
	public DivisionOfTheQuestionMotion(Delegate proposingDelegate) {
		super(proposingDelegate);
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.FOR_AND_AGAINST;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("DivisionOfTheQuestionMotion.DivisionOfTheQuestion"); //$NON-NLS-1$
	}

	@Override
	public boolean isSubstantive() {
		return true;
	}

}
