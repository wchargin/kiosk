package org.lcmmun.kiosk;

/**
 * Any sort of formal speech made by a delegate. The speech need not be made on
 * the General Speakers' List.
 * <p>
 * This class exists for constants, such as {@link #THRESHOLD}.
 * 
 * @author William Chargin
 * 
 */
public final class Speech {

	/**
	 * The various types of yields.
	 * 
	 * @author William Chargin
	 * 
	 */
	public enum YieldType {

		/**
		 * Indicates a yield of all remaining time back to the chair/dais.
		 */
		CHAIR(Messages.getString("Speech.YieldChair")), //$NON-NLS-1$

		/**
		 * Indicates a yield to two comments of thirty seconds in length each.
		 * No reponse is permitted.
		 */
		COMMENTS(Messages.getString("Speech.YieldComments")), //$NON-NLS-1$

		/**
		 * Indicates a yield to questions. The remainder of the time of this
		 * speech will be dedicated to answers of questions; questions
		 * themselves need not use the delegate's time.
		 */
		QUESTIONS(Messages.getString("Speech.YieldQuestions")), //$NON-NLS-1$

		/**
		 * Indicates a yield to another delegate for the remainder of the
		 * speech.
		 */
		DELEGATE(Messages.getString("Speech.YieldDelegate")); //$NON-NLS-1$

		/**
		 * The name of this yield type.
		 */
		public final String name;

		/**
		 * Creates the yield type with the given name.
		 * 
		 * @param name
		 *            the name
		 */
		private YieldType(String name) {
			this.name = name;
		}
	}

	/**
	 * When a speech has this many seconds remaining, it is considered
	 * "almost finished."
	 */
	public static final int THRESHOLD = 10;

	/**
	 * No instantiation.
	 */
	private Speech() {
	}

}
