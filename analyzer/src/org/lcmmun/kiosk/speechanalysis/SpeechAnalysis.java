package org.lcmmun.kiosk.speechanalysis;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.customizable.CounterProperty;
import tools.customizable.LongTextProperty;
import tools.customizable.MessageProperty;
import tools.customizable.MultipleChoiceProperty;
import tools.customizable.PropertySet;
import tools.customizable.TrueFalseProperty;
import tools.customizable.CounterEditor.EditorType;

/**
 * An analysis of a speech.
 * 
 * @author William Chargin
 * 
 */
public class SpeechAnalysis implements Serializable {

	/**
	 * The formatter for the speech time.
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"d MMM yyyy h:mm:ss a"); //$NON-NLS-1$

	/**
	 * An enumeration of the various contexts in which a delegate may make a
	 * speech.
	 * 
	 * @author William Chargin
	 * 
	 */
	public enum Context {
		/**
		 * Indicates that the relevant speech was made on the General Speakers'
		 * List.
		 */
		SPEAKERS_LIST(Messages.getString("SpeechAnalysis.GeneralSpeakersList")), //$NON-NLS-1$

		/**
		 * Indicates that the relevant speech was made during a moderated
		 * caucus.
		 */
		MODERATED_CAUCUS(Messages.getString("SpeechAnalysis.ModeratedCaucus")); //$NON-NLS-1$

		/**
		 * The name of this context.
		 */
		public final String name;

		/**
		 * Creates the {@code Context} with the given name.
		 * 
		 * @param name
		 *            the name of the criterion
		 */
		private Context(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * The lists of criteria by which a speech may be judged.
	 * 
	 * @author William Chargin
	 * 
	 */
	public enum Criterion {

		/**
		 * The overall persuasiveness of a speech.
		 */
		PERSUASIVENESS(Messages.getString("SpeechAnalysis.Persuasiveness")), //$NON-NLS-1$

		/**
		 * The quality of the delivery of a speech. This encapsulates tone,
		 * moderation, gesticulation, emphasis, etc.
		 */
		DELIVERY(Messages.getString("SpeechAnalysis.Delivery")), //$NON-NLS-1$

		/**
		 * The accuracy, relevance, or value of the content contained in the
		 * speech.
		 */
		CONTENT(Messages.getString("SpeechAnalysis.Content")), //$NON-NLS-1$

		/**
		 * The adherence to the delegation's policy as indicated by the speech.
		 */
		POLICY(Messages.getString("SpeechAnalysis.Policy")); //$NON-NLS-1$

		/**
		 * The minimum value for any criterion.
		 */
		public static final int MINIMUM = 0;

		/**
		 * The maximum value for any criterion.
		 */
		public static final int MAXIMUM = 10;

		/**
		 * The default value for any criterion.
		 */
		public static final int DEFAULT = 5;

		/**
		 * The name of this criterion.
		 */
		public final String name;

		/**
		 * Creates the {@code Criterion} with the given name.
		 * 
		 * @param name
		 *            the name of the criterion
		 */
		private Criterion(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * The name of the property indicating whether the analysis of this speech
	 * has been completed.
	 */
	public static final String COMPLETED_PROPERTY_NAME = Messages.getString("SpeechAnalysis.Completed"); //$NON-NLS-1$

	/**
	 * The delimiter used in generating <strong>C</strong>SV strings.
	 */
	public static final char DELIMITER = ',';

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The context in which the {@linkplain #delegate delegate} made this
	 * speech.
	 */
	public Context context = Context.SPEAKERS_LIST;

	/**
	 * The scores for the various criteria.
	 */
	public final LinkedHashMap<Criterion, Integer> scores = new LinkedHashMap<Criterion, Integer>();

	/**
	 * The property set associated with the scores.
	 */
	private final PropertySet ps = new PropertySet();

	/**
	 * The comments for the speech.
	 */
	private String comments = new String();

	/**
	 * Whether the analysis on this speech has been completed.
	 */
	public boolean complete = false;

	/**
	 * The date when this speech analysis was created.
	 */
	public final Date creationDate = new Date(/* now */);

	/**
	 * THe name of the delegate who gave the speech being analyzed.
	 */
	public final String delegateName;

	/**
	 * Creates the speech analysis.
	 */
	public SpeechAnalysis(String delegateName, String affiliation,
			Context context) {
		this.delegateName = delegateName;
		this.affiliation = affiliation;
		ps.add(new MessageProperty(Messages.getString("SpeechAnalysis.Delegate"), this.delegateName)); //$NON-NLS-1$
		ps.add(new MessageProperty(Messages.getString("SpeechAnalysis.DateAndTime"), DATE_FORMAT //$NON-NLS-1$
				.format(creationDate)));

		this.context = context;
		final MultipleChoiceProperty<Context> mcpContext = new MultipleChoiceProperty<SpeechAnalysis.Context>(
				Messages.getString("SpeechAnalysis.Context"), Arrays.<Context> asList(Context.values()), context); //$NON-NLS-1$
		mcpContext.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				SpeechAnalysis.this.context = mcpContext.getValue();
			}
		});
		ps.add(mcpContext);
		for (final Criterion criterion : Criterion.values()) {
			scores.put(criterion, Criterion.DEFAULT);
			final CounterProperty property = new CounterProperty(
					criterion.name, Criterion.MINIMUM, Criterion.DEFAULT,
					Criterion.MAXIMUM);
			property.setDefaultEditorType(EditorType.SLIDER);
			property.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent ce) {
					scores.put(criterion, property.getValue());
				}
			});
			ps.add(property);
		}
		final LongTextProperty ltpComments = new LongTextProperty(Messages.getString("SpeechAnalysis.Comments"), //$NON-NLS-1$
				comments);
		ltpComments.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				comments = ltpComments.getValue();
			}
		});
		ps.add(ltpComments);
		final TrueFalseProperty tfpComplete = new TrueFalseProperty(
				COMPLETED_PROPERTY_NAME, complete, Messages.getString("SpeechAnalysis.CompletedTrue"), Messages.getString("SpeechAnalysis.CompletedFalse")); //$NON-NLS-1$ //$NON-NLS-2$
		tfpComplete.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				complete = tfpComplete.getValue();
			}
		});
		ps.add(tfpComplete);
	}

	/**
	 * Gets the {@code PropertySet} to allow for editing and viewing of the
	 * speech analysis.
	 * 
	 * @return the property set
	 */
	public PropertySet getPropertySet() {
		return ps;
	}

	/**
	 * The delegate's affiliation.
	 */
	public final String affiliation;

	/**
	 * Converts the analysis in a CSV string, with values in the following
	 * order:
	 * <ol>
	 * <li>Speaker</li>
	 * <li>Time</li>
	 * <li>Criteria (in order)</li>
	 * <li>Comments</li>
	 * <li>Speakers' List or Caucus</li>
	 * </ol>
	 * 
	 * @return the CSV string
	 */
	public String generateCsv() {
		StringBuffer sb = new StringBuffer();
		sb.append(delegateName);
		sb.append(DELIMITER);
		sb.append(DATE_FORMAT.format(creationDate));
		sb.append(DELIMITER);
		Criterion[] criteria = Criterion.values();
		for (int i = 0; i < criteria.length; i++) {
			Criterion criterion = criteria[i];
			sb.append(Integer.toString(scores.get(criterion)));
			sb.append(DELIMITER);
		}
		sb.append('"' + comments.replace("\"", "\"\"") + '"'); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(DELIMITER);
		sb.append(context.toString());
		return sb.toString();
	}

	/**
	 * Gets the comments on this speech.
	 * 
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
}
