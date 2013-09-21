package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Crisis;
import org.lcmmun.kiosk.Messages;

public class CrisisController extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The action command indicating that the crisis was removed.
	 */
	public final static String REMOVE = "REMOVE"; //$NON-NLS-1$

	/**
	 * The action command indicating that the crisis was deployed.
	 */
	public final static String DEPLOY = "DEPLOY"; //$NON-NLS-1$

	/**
	 * The crisis being controlled.
	 */
	private final Crisis crisis;

	/**
	 * The crisis view.
	 */
	private final CrisisView view;

	/**
	 * Creates the controller with the given crisis.
	 * 
	 * @param c
	 *            the crisis to control
	 */
	public CrisisController(Crisis c) {
		super(new MigLayout(new LC().flowY()));
		this.crisis = c;

		view = new CrisisView();
		add(view, new CC().growX().pushX().spanY().wrap());
		view.setCrisis(crisis);

		final JButton btnEdit = new JButton(Messages.getString("CrisisController.ButtonEdit")); //$NON-NLS-1$
		add(btnEdit, new CC().grow().pushY());
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				final JDialog dialog = new JDialog(SwingUtilities
						.getWindowAncestor(btnEdit), Messages.getString("CrisisController.EditCrisisDialogTitle")); //$NON-NLS-1$
				JPanel panel = new JPanel(new BorderLayout());
				dialog.setContentPane(panel);

				final CrisisEditor ce = new CrisisEditor(crisis);
				panel.add(ce, BorderLayout.CENTER);

				JPanel pnlButtons = new JPanel(new FlowLayout(
						FlowLayout.TRAILING));
				panel.add(pnlButtons, BorderLayout.SOUTH);

				JButton btnCancel = new JButton(Messages.getString("CrisisController.CancelButton")); //$NON-NLS-1$
				pnlButtons.add(btnCancel);
				btnCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});

				JButton btnOk = new JButton(Messages.getString("CrisisController.OKButton")); //$NON-NLS-1$
				pnlButtons.add(btnOk);
				dialog.getRootPane().setDefaultButton(btnOk);
				btnOk.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dialog.dispose();
						ce.applyChanges();
						view.setCrisis(crisis);
					}
				});

				dialog.pack();
				dialog.setLocationRelativeTo(SwingUtilities
						.getWindowAncestor(btnEdit));
				dialog.setModal(true);
				dialog.setVisible(true);

			}
		});

		final JButton btnRemove = new JButton(Messages.getString("CrisisController.RemoveButton")); //$NON-NLS-1$
		add(btnRemove, new CC().grow().pushY().wrap());
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fireActionEvent(REMOVE);
			}
		});

		final JButton btnDeploy = new JButton(Messages.getString("CrisisController.DeployButton")); //$NON-NLS-1$
		add(btnDeploy, new CC().grow().pushY().spanY());
		btnDeploy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fireActionEvent(DEPLOY);
			}
		});

	}

	/**
	 * Updates the {@link CrisisView}.
	 */
	public void updateDisplay() {
		view.setCrisis(crisis);
	}

	/**
	 * Fires an action event with the given command to all registered listeners.
	 * 
	 * @param command
	 *            the command string
	 */
	private void fireActionEvent(String command) {
		for (ActionListener listener : listenerList
				.getListeners(ActionListener.class)) {
			listener.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, command));
		}
	}

	/**
	 * Adds the given {@code ActionListener} to the list of listeners. This
	 * listener will be invoked when the crisis is deleted.
	 * 
	 * @param al
	 *            the listener to add
	 */
	public void addActionListener(ActionListener al) {
		listenerList.add(ActionListener.class, al);
	}

	/**
	 * Removes the given {@code ActionListener} from the list of listeners.
	 * 
	 * @param al
	 *            the listener to remove
	 */
	public void removeActionListener(ActionListener al) {
		listenerList.remove(ActionListener.class, al);
	}

	/**
	 * Gets the crisis being controlled.
	 * 
	 * @return the crisis
	 */
	public Crisis getCrisis() {
		return crisis;
	}

}
