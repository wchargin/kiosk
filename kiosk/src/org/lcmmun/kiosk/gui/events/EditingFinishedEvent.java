package org.lcmmun.kiosk.gui.events;

import java.util.EventObject;

/**
 * An event indicating that an editing process has finished.
 * 
 * @author William Chargin
 * 
 */
public class EditingFinishedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the event with the given source.
	 * 
	 * @param source
	 *            the source
	 */
	public EditingFinishedEvent(Object source) {
		super(source);
	}

}
