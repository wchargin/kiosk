package org.lcmmun.kiosk.gui.events;

import java.util.EventObject;

import org.lcmmun.kiosk.motions.Motion;

/**
 * An event indicating that a motion has passed.
 * 
 * @author William Chargin
 * 
 * @param <T>
 *            the type of motion that passed
 */
public class MotionPassedEvent<T extends Motion> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The motion that passed.
	 */
	public final T motion;

	/**
	 * Creates the event with the given source and motion.
	 * 
	 * @param source
	 *            the source
	 * @param motion
	 *            the motion that passed
	 */
	public MotionPassedEvent(Object source, T motion) {
		super(source);
		this.motion = motion;
	}

}
