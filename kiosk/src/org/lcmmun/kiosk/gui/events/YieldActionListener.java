package org.lcmmun.kiosk.gui.events;

import java.util.EventListener;

/**
 * A listener for {@link YieldActionEvent}s.
 * 
 * @author William Chargin
 * 
 */
public interface YieldActionListener extends EventListener {

	/**
	 * Invoked when a yield action occurs.
	 * 
	 * @param yae
	 *            the relevant yield action event
	 */
	public void yieldActionPerformed(YieldActionEvent yae);

}
