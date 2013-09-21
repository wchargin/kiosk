package org.lcmmun.kiosk.gui.events;

import java.util.EventObject;

/**
 * An event indicating that something has happened regarding an in-progress
 * caucus.
 * 
 * @author William Chargin
 * 
 */
public class CaucusEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The various types of caucus events.
	 * 
	 * @author William Chargin
	 * 
	 */
	public enum CaucusEventType {

		/**
		 * Indicates that the time allotted for the relevant caucus has elapsed.
		 */
		ELAPSED,

		/**
		 * Indicates that the relevant caucus has been paused or resumed.
		 */
		PAUSED,

	}

	/**
	 * The caucus event type.
	 */
	public final CaucusEventType type;

	/**
	 * Creates the caucus event with the given source and type.
	 * 
	 * @param source
	 *            the event source
	 * @param type
	 *            the event type
	 */
	public CaucusEvent(Object source, CaucusEventType type) {
		super(source);
		this.type = type;
	}
}
