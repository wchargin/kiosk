package org.lcmmun.kiosk.broadcast;

import java.io.Serializable;

import org.lcmmun.kiosk.CommitteeState;
import org.lcmmun.kiosk.gui.Kiosk;

/**
 * An outline of a committee. Instances of this class are sent by {@link Kiosk}s
 * to OmniKiosks.
 * 
 * @author William Chargin
 * 
 */
public class CommitteeOutline implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The total number of committee outlines.
	 */
	public static int count;

	/**
	 * The name of the committee.
	 */
	public final String name;

	/**
	 * The state of the committee.
	 */
	public final CommitteeState state;

	/**
	 * The current topic.
	 */
	public final String topic;

	/**
	 * Any comments from the chairs of the committee regarding its status.
	 */
	public final String comments;

	/**
	 * The total number of working papers and resolutions for the current topic.
	 */
	public final int wprCount;

	/**
	 * Creates the committee outline with all required values.
	 * 
	 * @param name
	 *            the committee name
	 * @param state
	 *            the committee state
	 * @param topic
	 *            the current topic
	 * @param comments
	 *            the chairs' comments
	 * @param wprCount
	 *            the total number of working papers and resolutions for this
	 *            topic
	 */
	public CommitteeOutline(String name, CommitteeState state, String topic,
			String comments, int wprCount) {
		super();
		this.name = name == null ? new String() : name;
		this.state = state;
		this.topic = topic == null ? new String() : topic;
		this.comments = comments == null ? new String() : comments;
		this.wprCount = wprCount;
	}

	/**
	 * Gets the chairs' comments about this committee.
	 * 
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

}
