package org.lcmmun.kiosk.gui.events;

import java.util.EventListener;

/**
 * A listener for {@link YieldEvent}s.
 * 
 * @author William Chargin
 * 
 */
public interface YieldListener extends EventListener {

	/**
	 * Invoked when a delegate yields.
	 * 
	 * @param ye
	 *            the relevant yield event
	 */
	public void yield(YieldEvent ye);

}
