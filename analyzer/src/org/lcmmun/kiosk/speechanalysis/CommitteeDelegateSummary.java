package org.lcmmun.kiosk.speechanalysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class CommitteeDelegateSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of this committee.
	 */
	public final String committeeName;

	/**
	 * The map of affiliations to delegate summaries.
	 */
	public final Map<String, ArrayList<DelegateSummary>> summaries;

	/**
	 * Creates the committee delegate summary.
	 * 
	 * @param committeeName
	 *            the name of the committee
	 * @param summaries
	 *            the summaries
	 */
	public CommitteeDelegateSummary(String committeeName,
			Map<String, ArrayList<DelegateSummary>> summaries) {
		super();
		this.committeeName = committeeName;
		this.summaries = summaries;
	}

}
