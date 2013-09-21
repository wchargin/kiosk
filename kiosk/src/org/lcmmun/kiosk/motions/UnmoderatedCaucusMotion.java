package org.lcmmun.kiosk.motions;

import java.util.ArrayList;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

/**
 * A motion for an unmoderated caucus, in which all rules except basic decorum
 * are suspended.
 * 
 * @author William Chargin
 * 
 */
public class UnmoderatedCaucusMotion extends CaucusMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the unmoderated caucus motion with the given proposing delegate.
	 * 
	 * @param proposingDelegate
	 *            the delegate who proposed the motion
	 */
	public UnmoderatedCaucusMotion(Delegate proposingDelegate) {
		super(proposingDelegate);
	}

	@Override
	public ArrayList<String> getDescriptions() {
		final ArrayList<String> desc = super.getDescriptions();
		desc.add(getTotalTime().toString());
		return desc;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("UnmoderatedCaucusMotion.UnmoderatedCaucus"); //$NON-NLS-1$
	}

	@Override
	public boolean isSubstantive() {
		return false;
	}

}
