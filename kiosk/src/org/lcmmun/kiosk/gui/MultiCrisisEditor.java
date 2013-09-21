package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Crisis;
import org.lcmmun.kiosk.Messages;

/**
 * A panel for editing multiple crises.
 * 
 * @author William Chargin
 * 
 */
public abstract class MultiCrisisEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The committee whose crises are being edited.
	 */
	private Committee committee;

	/**
	 * The panel of {@link CrisisController}s.
	 */
	private final JPanel pnlControllers;

	public MultiCrisisEditor(Committee committee) {
		super(new BorderLayout());

		final JButton btnAdd = new JButton(Messages.getString("MultiCrisisEditor.AddCrisis")); //$NON-NLS-1$
		add(btnAdd, BorderLayout.NORTH);
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				addBlankCrisis();
			}
		});

		pnlControllers = new JPanel(new MigLayout(new LC().flowY()));
		add(new JScrollPane(pnlControllers,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

		setCommittee(committee);
	}

	/**
	 * Adds a new, blank crisis to the committee, and adds a controller to the
	 * panel of controllers.
	 */
	private void addBlankCrisis() {
		Crisis c = Crisis.createDefaultCrisis(committee.crises.size() + 1);
		committee.crises.add(c);
		addCrisisController(c);
	}

	/**
	 * Adds a controller for the given crisis to the panel of controllers.
	 * 
	 * @param c
	 *            the crisis to control
	 */
	private void addCrisisController(final Crisis c) {
		final CrisisController cc = new CrisisController(c);
		cc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				final String actionCommand = ae.getActionCommand();
				if (actionCommand.equals(CrisisController.DEPLOY)) {
					deploy(c);
				} else if (actionCommand.equals(CrisisController.REMOVE)) {
					removeCrisis(c);
				}
			}
		});
		pnlControllers.add(cc, new CC().growX().pushX());
		pnlControllers.revalidate();
		pnlControllers.repaint();
	}

	/**
	 * Removes the given crisis from the committee and removes its controller
	 * from the controller holder.
	 * 
	 * @param c
	 *            the crisis to remove
	 */
	protected void removeCrisis(Crisis c) {
		committee.crises.remove(c);
		for (Component component : pnlControllers.getComponents()) {
			if (component instanceof CrisisController) {
				if (c.equals(((CrisisController) component).getCrisis())) {
					pnlControllers.remove(component);
				}
			}
		}
		pnlControllers.revalidate();
		pnlControllers.repaint();
	}

	/**
	 * Sets the committee to the given committee.
	 * 
	 * @param committee
	 *            the committee
	 */
	public void setCommittee(Committee committee) {
		this.committee = committee;
		pnlControllers.removeAll();
		if (committee != null) {
			for (Crisis crisis : committee.crises) {
				addCrisisController(crisis);
			}
		}
		pnlControllers.revalidate();
		pnlControllers.repaint();
	}

	/**
	 * Deploys the selected crisis.
	 * 
	 * @param crisis
	 *            the crisis to deploy
	 */
	protected abstract void deploy(Crisis crisis);

	/**
	 * Updates the crisis controller for this crisis.
	 * 
	 * @param crisis
	 *            the crisis
	 */
	public void updateCrisisController(Crisis crisis) {
		for (Component component : pnlControllers.getComponents()) {
			if (component instanceof CrisisController) {
				if (crisis.equals(((CrisisController) component).getCrisis())) {
					((CrisisController) component).updateDisplay();
				}
			}
		}
	}

}
