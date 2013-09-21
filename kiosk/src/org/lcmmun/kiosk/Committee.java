package org.lcmmun.kiosk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import tools.customizable.Time;

/**
 * A committee in an MUN setting. The committee consists of a list of delegates,
 * and a list of topics.
 * 
 * @author William Chargin
 * 
 */
public class Committee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	/**
	 * The list of topics for this committee.
	 */
	private final LinkedHashSet<String> topics = new LinkedHashSet<String>();

	/**
	 * The list of affiliations of delegates in this committee.
	 */
	private final List<String> affiliations = new ArrayList<String>();

	/**
	 * The name of the committee.
	 */
	private String name;

	/**
	 * The current topic.
	 */
	private String currentTopic;

	/**
	 * The list of delegates participating in this committee.
	 */
	public final TreeMap<Delegate, QuorumStatus> delegates = new TreeMap<Delegate, QuorumStatus>();

	/**
	 * The general comments for the committee.
	 */
	public String comments;

	/**
	 * The current General Speakers' List.
	 */
	public final ArrayList<Delegate> speakersList = new ArrayList<Delegate>();

	/**
	 * The current speaking time. Defaults to 00:01:00.
	 */
	public Time speakingTime = new Time(0, 1, 0);

	/**
	 * The current state of this committee.
	 */
	public transient CommitteeState state = CommitteeState.SPEAKERS_LIST;

	/**
	 * The map of topics to lists of working papers.
	 */
	public final Map<String, ArrayList<WorkingPaper>> workingPapers = new LinkedHashMap<String, ArrayList<WorkingPaper>>();

	/**
	 * The crises of this committee.
	 */
	public final ArrayList<Crisis> crises = new ArrayList<Crisis>();

	/**
	 * Creates the committee.
	 */
	public Committee() {
		super();
		setCurrentTopic(Messages.getString("Committee.DefaultTopicName")); //$NON-NLS-1$
	}

	/**
	 * The number of comments allowed on GSL speeches. Default is 2.
	 */
	public int numComments = 2;

	/**
	 * The time of GSL speeches.
	 */
	public Time commentTime = Time.fromSeconds(30);

	/**
	 * Gets the current topic.
	 * 
	 * @return the current topic
	 */
	public String getCurrentTopic() {
		return currentTopic;
	}

	/**
	 * Gets the map of delegates to their current statuses.
	 * 
	 * @return the map
	 */
	public Map<Delegate, QuorumStatus> getDelegateMap() {
		return delegates;
	}

	/**
	 * Gets the committee name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Determines the delegates who are present (that is, not absent; either
	 * {@link QuorumStatus#PRESENT} or {@link QuorumStatus#PRESENT_AND_VOTING}).
	 * 
	 * @return those delegates, in a {@code List}
	 */
	public final List<Delegate> getPresentDelegates() {
		ArrayList<Delegate> list = new ArrayList<Delegate>();
		for (Delegate delegate : delegates.keySet()) {
			if (delegates.get(delegate) != QuorumStatus.ABSENT) {
				// They're either present or present and voting.
				list.add(delegate);
			}
		}
		java.util.Collections.sort(list);
		return list;
	}

	/**
	 * Gets a copy of the list of topic names.
	 * 
	 * @return a copy of the list of topic names
	 */
	public Set<String> getTopics() {
		return new LinkedHashSet<String>(topics);
	}

	/**
	 * Sets the topic to be debated, unless the topic is not in the list, in
	 * which case it is added.
	 * <p>
	 * In more technical terms: the list of topics is actually a {@link Set};
	 * {@link Set#add(Object) topics.add} will be invoked. If the topic already
	 * exists, it will not be added.
	 * 
	 * @param topic
	 *            the new topic
	 */
	public void setCurrentTopic(String topic) {
		if (topic != null) {
			topics.add(topic);
		}
		currentTopic = topic;
	}

	/**
	 * Sets the committee name.
	 * 
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the contents of the topic name list to the names provided in the
	 * given list.
	 * 
	 * @param topicNames
	 *            the list of topics
	 */
	public void setTopics(List<String> topicNames) {
		this.topics.clear();
		for (String string : topicNames) {
			if (string != null) {
				topics.add(string);
			}
		}
		if (!topics.contains(currentTopic)) {
			currentTopic = null;
		}
	}

	/**
	 * Gets a copy of the list of affiliations.
	 * 
	 * @return a copy of the list of affiliations
	 */
	public List<String> getAffiliations() {
		return new ArrayList<String>(affiliations);
	}

	/**
	 * Sets the contents of the affiliations list to those provided in the given
	 * list.
	 * 
	 * @param newAffiliations
	 *            the list of affiliations
	 */
	public void setAffiliations(List<String> newAffiliations) {
		this.affiliations.clear();
		for (String affiliation : newAffiliations) {
			if (affiliation != null && !affiliations.contains(affiliation)
					&& !affiliation.trim().isEmpty()) {
				affiliations.add(affiliation.trim());
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((affiliations == null) ? 0 : affiliations.hashCode());
		result = prime * result
				+ ((commentTime == null) ? 0 : commentTime.hashCode());
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((crises == null) ? 0 : crises.hashCode());
		result = prime * result
				+ ((currentTopic == null) ? 0 : currentTopic.hashCode());
		result = prime * result
				+ ((delegates == null) ? 0 : delegates.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + numComments;
		result = prime * result
				+ ((speakersList == null) ? 0 : speakersList.hashCode());
		result = prime * result
				+ ((speakingTime == null) ? 0 : speakingTime.hashCode());
		result = prime * result + ((topics == null) ? 0 : topics.hashCode());
		result = prime * result
				+ ((workingPapers == null) ? 0 : workingPapers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Committee)) {
			return false;
		}
		Committee other = (Committee) obj;
		if (affiliations == null) {
			if (other.affiliations != null) {
				return false;
			}
		} else if (!affiliations.equals(other.affiliations)) {
			return false;
		}
		if (commentTime == null) {
			if (other.commentTime != null) {
				return false;
			}
		} else if (!commentTime.equals(other.commentTime)) {
			return false;
		}
		if (comments == null) {
			if (other.comments != null) {
				return false;
			}
		} else if (!comments.equals(other.comments)) {
			return false;
		}
		if (crises == null) {
			if (other.crises != null) {
				return false;
			}
		} else if (!crises.equals(other.crises)) {
			return false;
		}
		if (currentTopic == null) {
			if (other.currentTopic != null) {
				return false;
			}
		} else if (!currentTopic.equals(other.currentTopic)) {
			return false;
		}
		if (delegates == null) {
			if (other.delegates != null) {
				return false;
			}
		} else if (!delegates.equals(other.delegates)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (numComments != other.numComments) {
			return false;
		}
		if (speakersList == null) {
			if (other.speakersList != null) {
				return false;
			}
		} else if (!speakersList.equals(other.speakersList)) {
			return false;
		}
		if (speakingTime == null) {
			if (other.speakingTime != null) {
				return false;
			}
		} else if (!speakingTime.equals(other.speakingTime)) {
			return false;
		}
		if (topics == null) {
			if (other.topics != null) {
				return false;
			}
		} else if (!topics.equals(other.topics)) {
			return false;
		}
		if (workingPapers == null) {
			if (other.workingPapers != null) {
				return false;
			}
		} else if (!workingPapers.equals(other.workingPapers)) {
			return false;
		}
		return true;
	}

}
