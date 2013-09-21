package org.lcmmun.kiosk.gui.events;

import java.util.EventListener;

/**
 * A listener for {@link SpeechEvent}s.
 * 
 * @author William Chargin
 * 
 */
public interface SpeechListener extends EventListener {

	/**
	 * Invoked when something happens regarding a delegate's speech.
	 * 
	 * @param se
	 *            the relevant {@link SpeechEvent}
	 */
	public void speechActionPerformed(SpeechEvent se);

}
