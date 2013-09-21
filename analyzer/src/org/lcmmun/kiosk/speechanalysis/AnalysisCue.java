package org.lcmmun.kiosk.speechanalysis;

import java.io.Serializable;

import org.lcmmun.kiosk.speechanalysis.SpeechAnalysis.Context;

/**
 * A cue to the speech analyzer to begin analyzing a speech from the given
 * delegate in the given context.
 * 
 * @author William Chargin
 * 
 */
public class AnalysisCue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the delegate whose speech should be analyzed.
	 */
	public final String name;

	/**
	 * The delegate's affiliation.
	 */
	public final String affiliation;

	/**
	 * The context in which the delegate delivered the speech.
	 */
	public final Context context;

	/**
	 * Creates the cue with all required parameters.
	 * 
	 * @param name
	 *            the name
	 * @param affiliation
	 *            the delegate affiliation
	 * @param context
	 *            the context
	 */
	public AnalysisCue(String name, String affiliation, Context context) {
		super();
		this.name = name;
		this.affiliation = affiliation;
		this.context = context;
	}

}
