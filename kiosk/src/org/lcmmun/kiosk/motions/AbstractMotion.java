package org.lcmmun.kiosk.motions;

import java.util.ArrayList;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.MotionResult;

import tools.customizable.PropertySet;

/**
 * A skeleton for a motion.
 * 
 * @author William Chargin
 * 
 */
public abstract class AbstractMotion implements Motion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The delegate who proposed the motion (or {@code null} if a chair proposed
	 * it).
	 */
	protected Delegate proposingDelegate;

	/**
	 * The property set used in this motion.
	 */
	protected PropertySet propertySet = new PropertySet();

	/**
	 * The result of the motion.
	 */
	protected MotionResult result = MotionResult.PASSED;

	/**
	 * Creates the motion with the given proposing delegate.
	 * 
	 * @param proposingDelegate
	 *            the proposing delegate
	 */
	protected AbstractMotion(Delegate proposingDelegate) {
		super();
		this.proposingDelegate = proposingDelegate;
	}

	@Override
	public ArrayList<String> getDescriptions() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add(getProposingDelegate().getName() + " - " + getMotionName()); //$NON-NLS-1$
		return desc;
	}

	@Override
	public PropertySet getPropertySet() {
		return propertySet;
	}

	@Override
	public Delegate getProposingDelegate() {
		return proposingDelegate;
	}

	@Override
	public MotionResult getResult() {
		return result;
	}

	@Override
	public void setProposingDelegate(Delegate proposingDelegate) {
		this.proposingDelegate = proposingDelegate;
	}

	@Override
	public void setResult(MotionResult result) {
		this.result = result;
	}
}
