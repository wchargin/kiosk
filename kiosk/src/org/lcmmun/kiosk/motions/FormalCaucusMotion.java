package org.lcmmun.kiosk.motions;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.WorkingPaper;
import org.lcmmun.kiosk.gui.WorkingPaperRenderer;

import tools.customizable.MultipleChoiceProperty;

/**
 * A motion for a formal caucus (one in which a resolution is introduced).
 * 
 * @author William Chargin
 * 
 */
public class FormalCaucusMotion extends CaucusMotion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The working paper.
	 */
	private WorkingPaper paper;

	/**
	 * Creates the formal caucus with the given proposing delegate and one of
	 * the given papers.
	 * 
	 * @param proposingDelegate
	 *            the proposing delegate
	 * @param papersForTopic
	 *            the list of papers to choose from
	 * @throws Exception
	 *             if the list of working papers is {@code null} or
	 *             {@link Collection#isEmpty empty}
	 */
	public FormalCaucusMotion(Delegate proposingDelegate,
			Collection<WorkingPaper> papersForTopic)
			throws IllegalStateException {
		this(proposingDelegate, papersForTopic, null);
	}

	/**
	 * Creates the formal caucus with the given proposing delegate and one of
	 * the given papers.
	 * 
	 * @param proposingDelegate
	 *            the proposing delegate
	 * @param papersForTopic
	 *            the list of papers to choose from
	 * @param defaultWorkingPaper
	 *            the default working paper, or {@code null} to select the first
	 *            (zeroth) paper in the list
	 * @throws IllegalStateException
	 *             if the list of working papers is {@code null} or
	 *             {@link Collection#isEmpty empty}
	 */
	public FormalCaucusMotion(Delegate proposingDelegate,
			Collection<WorkingPaper> papersForTopic,
			WorkingPaper defaultWorkingPaper) throws IllegalStateException {
		super(proposingDelegate);
		if (papersForTopic == null || papersForTopic.isEmpty()) {
			throw new IllegalStateException(
					Messages.getString("FormalCaucusMotion.NoWPDRForTopic")); //$NON-NLS-1$
		}

		final MultipleChoiceProperty<WorkingPaper> mcpWorkingPaper = new MultipleChoiceProperty<WorkingPaper>(
				Messages.getString("FormalCaucusMotion.PropertyWorkingPaper"), papersForTopic, defaultWorkingPaper); //$NON-NLS-1$
		paper = mcpWorkingPaper.getValue();
		mcpWorkingPaper.setRenderer(new WorkingPaperRenderer());
		mcpWorkingPaper.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				paper = mcpWorkingPaper.getValue();
			}
		});
		propertySet.add(mcpWorkingPaper);
	}

	/**
	 * Gets the currently selected working paper.
	 * 
	 * @return the working paper
	 */
	public WorkingPaper getWorkingPaper() {
		return paper;
	}

	@Override
	public String getMotionName() {
		return Messages.getString("FormalCaucusMotion.FormalCaucus"); //$NON-NLS-1$
	}

	@Override
	public ArrayList<String> getDescriptions() {
		ArrayList<String> desc = super.getDescriptions();
		if (paper != null) {
			desc.add(paper.getIdentifier());
		}
		return desc;
	}

}
