package org.lcmmun.kiosk.gui.events;

import java.util.EventListener;

/**
 * A listener for {@link EditingFinishedEvent}s.
 * 
 * @author William Chargin
 * 
 */
public interface EditingFinishedListener extends EventListener {
	/**
	 * Invoked when the editing process is finished.
	 * 
	 * @param efe
	 *            the related {@link EditingFinishedEvent}
	 */
	public void editingFinished(EditingFinishedEvent efe);
}
