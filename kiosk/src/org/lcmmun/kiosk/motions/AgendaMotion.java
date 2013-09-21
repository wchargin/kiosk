package org.lcmmun.kiosk.motions;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;

import tools.customizable.MultipleChoiceProperty;

/**
 * A motion to set the agenda at a certain topic.
 * 
 * @author William Chargin
 * 
 */
public class AgendaMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The topic selected by the proposing delegate.
	 */
	private String topic;

	/**
	 * Creates the {@code AgendaMotion}.
	 * 
	 * @param proposingDelegate
	 *            the delegate who proposed the motion
	 * @param committee
	 *            the committee whose topic should be changed
	 * @throws IllegalStateException
	 *             if the committee has no topics
	 */
	public AgendaMotion(Delegate proposingDelegate, Committee committee)
			throws IllegalStateException {
		super(proposingDelegate);

		if (committee.getTopics().isEmpty()) {
			throw new IllegalStateException(
					Messages.getString("AgendaMotion.NoTopicsMessage")); //$NON-NLS-1$
		}

		final MultipleChoiceProperty<String> mcpTopics = new MultipleChoiceProperty<String>(
				Messages.getString("AgendaMotion.PropertyTopic"), committee.getTopics(), committee.getCurrentTopic()); //$NON-NLS-1$
		mcpTopics.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				topic = mcpTopics.getValue();
			}
		});

		topic = committee.getCurrentTopic();
		propertySet.add(mcpTopics);
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.FOR_AND_AGAINST;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("AgendaMotion.SetAgenda"); //$NON-NLS-1$
	}

	/**
	 * Gets the topic selected by the proposing delegate.
	 * 
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	@Override
	public boolean isSubstantive() {
		return true;
	}

	@Override
	public ArrayList<String> getDescriptions() {
		ArrayList<String> desc = super.getDescriptions();
		desc.add(topic);
		return desc;
	}

}
