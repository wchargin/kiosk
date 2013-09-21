package org.lcmmun.kiosk.speechanalysis;

import java.io.Serializable; 
import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lcmmun.kiosk.speechanalysis.SpeechAnalysis.Criterion;

/**
 * A summary of a delegate.
 * 
 * @author William Chargin
 * 
 */
public class DelegateSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The delegate name.
	 */
	public final String name;

	/**
	 * The delegate's affiliation.
	 */
	public final String affiliation;

	/**
	 * The map of criteria to scores in that criterion.
	 */
	public final Map<Criterion, Double> scores;

	/**
	 * The number of speeches this delegate has made.
	 */
	public final int speechCount;

	/**
	 * The list of comments on this delegate's speeches.
	 */
	public final List<String> comments;
	
	/**
	 * The date of this delegate's last speech.
	 */
	public final Date lastSpeech;
	
	/**
	 * Creates the summary with all required fields.
	 * 
	 * @param name
	 *            the delegate name
	 * @param affiliation
	 *            the delegate affiliation
	 * @param scores
	 *            the delegate scores
	 * @param speechCount
	 *            the delegate speech count
	 */
	public DelegateSummary(String name, String affiliation,
			Map<Criterion, Double> scores, int speechCount,
			List<String> comments, Date lastSpeech) {
		super();
		this.name = name;
		this.affiliation = affiliation;
		this.scores = Collections.unmodifiableMap(scores);
		this.comments = Collections.unmodifiableList(comments);
		this.speechCount = speechCount;
		this.lastSpeech = lastSpeech;
	}

}
