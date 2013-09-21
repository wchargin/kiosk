package org.lcmmun.kiosk.gui.events;

import java.util.EventListener;

/**
 * A listener for caucus events.
 * 
 * @author William Chargin
 * 
 */
public interface CaucusListener extends EventListener {

	/**
	 * Invoked when something happens regarding an in-progress caucus.
	 * 
	 * @param ce
	 *            the relevant {@link CaucusEvent}
	 */
	public void caucusActionPerformed(CaucusEvent ce);

}
