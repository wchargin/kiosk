package org.lcmmun.kiosk.gui.events;

import java.util.EventListener;

/**
 * A listener for {@link DelegateSelectedEvent}s.
 * 
 * @author William Chargin
 * 
 */
public interface DelegateSelectedListener extends EventListener {

	/**
	 * Invoked when a delegate is selected in an interface.
	 * 
	 * @param dse
	 *            the relevant {@link DelegateSelecteedEvent}
	 */
	public void delegateSelected(DelegateSelectedEvent dse);

}
