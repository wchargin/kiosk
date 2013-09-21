package org.lcmmun.kiosk.gui.events;

import java.util.EventListener;

import org.lcmmun.kiosk.motions.Motion;

/**
 * A listener for {@link MotionPassedEvents}s.
 * 
 * @author William Chargin
 * 
 * @param <T>
 *            the type of motion that passed
 */
public interface MotionPassedListener<T extends Motion> extends EventListener {
	/**
	 * Invoked when a motion is passed.
	 * 
	 * @param mpe
	 *            the related {@link MotionPassedEvent}
	 */
	public void motionPassed(MotionPassedEvent<T> mpe);
}
