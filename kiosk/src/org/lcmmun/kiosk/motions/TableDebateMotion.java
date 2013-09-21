package org.lcmmun.kiosk.motions;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

/**
 * Motion to table debate on a given topic and move on to the next.
 * 
 * @author William Chargin
 * 
 */
public class TableDebateMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the motion to table debate, as proposed by the given delegate.
	 * 
	 * @param proposingDelegate
	 *            the proposing delegate
	 */
	public TableDebateMotion(Delegate proposingDelegate) {
		super(proposingDelegate);
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.FOR_AND_AGAINST;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.QUALIFIED;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("TableDebateMotion.TableDebate"); //$NON-NLS-1$
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}

}
