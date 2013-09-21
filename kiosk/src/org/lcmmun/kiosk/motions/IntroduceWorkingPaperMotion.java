package org.lcmmun.kiosk.motions;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

/**
 * A motion to introduce a working paper on the current topic.
 * 
 * @author William Chargin
 * 
 */
public class IntroduceWorkingPaperMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the motion.
	 * 
	 * @param proposingDelegate
	 *            the proposing delegate
	 */
	public IntroduceWorkingPaperMotion(Delegate proposingDelegate) {
		super(proposingDelegate);
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
		return Messages.getString("IntroduceWorkingPaperMotion.IntroduceWorkingPaper"); //$NON-NLS-1$
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}

}
