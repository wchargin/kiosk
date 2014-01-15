package org.lcmmun.kiosk.gui.events;

import java.util.EventObject;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Speech;

/**
 * An event indicating that something has happened regarding a delegate's
 * speech.
 * 
 * @author William Chargin
 * 
 */
public class SpeechEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The various types of speech events.
	 * 
	 * @author William Chargin
	 * 
	 */
	public enum SpeechEventType {

		/**
		 * Indicates that a speech has started.
		 */
		STARTED,

		/**
		 * Indicates that the speech has been paused or resumed.
		 */
		PAUSED,

		/**
		 * Indicates that the speech has almost finished and a single tap of the
		 * gavel is in order.
		 * 
		 * @see Speech#THRESHOLD
		 */
		ALMOST_FINISHED,

		/**
		 * Indicates that the speech has finished.
		 */
		FINISHED,

		/**
		 * Indicates that a speech in progress has been canceled and that the
		 * delegate should be returned to the top of the speakers' list.
		 */
		CANCELED;
	}

	/**
	 * The delegate to whom this event applies.
	 */
	public final Delegate speaker;

	/**
	 * The speech event type.
	 */
	public final SpeechEventType type;

	/**
	 * Creates the speech event with the given source, speaker, and type.
	 * 
	 * @param source
	 *            the source of the event
	 * @param speaker
	 *            the relevant speaker
	 * @param type
	 *            the type of speech event
	 */
	public SpeechEvent(Object source, Delegate speaker, SpeechEventType type) {
		super(source);
		this.speaker = speaker;
		this.type = type;
	}

}
