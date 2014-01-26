package org.lcmmun.kiosk.motions;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.WorkingPaper;

import tools.customizable.MultipleChoiceProperty;

/**
 * A motion to introduce a friendly amendment.
 * 
 * @author William Chargin
 */
public class FriendlyAmendmentMotion extends AbstractMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The working paper to be amended.
	 */
	private WorkingPaper paper;

	/**
	 * Creates the motion for a friendly amendment, as proposed by the given
	 * delegate.
	 * 
	 * @param proposingDelegate
	 *            the delegate
	 * @param workingPapers
	 *            the list of papers to choose from
	 */
	public FriendlyAmendmentMotion(Delegate proposingDelegate,
			Collection<WorkingPaper> workingPapers) {
		super(proposingDelegate);
		final MultipleChoiceProperty<WorkingPaper> mcpwp = new MultipleChoiceProperty<WorkingPaper>(
				Messages.getString("FriendlyAmendmentMotion.PropertyWorkingPaper"), workingPapers, null); //$NON-NLS-1$
		mcpwp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				paper = mcpwp.getValue();
			}
		});
		propertySet.add(mcpwp);
	}

	@Override
	public Debatability getDebatability() {
		return Debatability.NONE;
	}

	@Override
	public ArrayList<String> getDescriptions() {
		ArrayList<String> desc = super.getDescriptions();
		if (paper != null) {
			desc.add(paper.getIdentifier());
		}
		return desc;
	}

	@Override
	public MajorityType getMajorityType() {
		return MajorityType.SIMPLE;
	}

	@Override
	public String getMotionName() {
		return Messages
				.getString("FriendlyAmendmentMotion.IntroduceFriendlyAmendment"); //$NON-NLS-1$
	}

	/**
	 * Gets the working paper to be amended.
	 * 
	 * @return the working paper
	 */
	public WorkingPaper getPaper() {
		return paper;
	}

	@Override
	public boolean isSubstantive() {
		return true;
	}

	/**
	 * Sets the working paper to be amended.
	 * 
	 * @param paper
	 *            the new working paper
	 */
	public void setPaper(WorkingPaper paper) {
		this.paper = paper;
	}

}
