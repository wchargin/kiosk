package org.lcmmun.kiosk.motions;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

/**
 * A motion to close debate on the current topic and move into voting
 * procedures.
 * 
 * @author William Chargin
 */
public class CloseDebateMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the motion to close debate, as proposed by the given delegate.
	 * 
	 * @param proposingDelegate
	 *            the delegate
	 */
	public CloseDebateMotion(Delegate proposingDelegate) {
		super(proposingDelegate);
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.AGAINST_ONLY;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.QUALIFIED;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("CloseDebateMotion.CloseDebate"); //$NON-NLS-1$
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}

}
