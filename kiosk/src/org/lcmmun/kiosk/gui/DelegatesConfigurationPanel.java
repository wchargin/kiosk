package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.DelegateIcon;
import org.lcmmun.kiosk.IconType;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.resources.ImageFetcher;
import org.lcmmun.kiosk.resources.ImageType;
import org.lcmmun.kiosk.resources.flags.FileNameGuesser;

public class DelegatesConfigurationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The model used for the list.
	 */
	private final DelegateModel model;

	/**
	 * Whether the user has been warned (this time) about using the "Remove"
	 * button.
	 */
	private boolean removeMessageShown = false;

	/**
	 * The copy buffer for a delegate's icon that has been copied.
	 */
	private DelegateIcon copybufferIcon;

	/**
	 * A popup menu shown when a delegate's name is right-clicked on.
	 * 
	 * @author William Chargin
	 * 
	 */
	private class DelegateContextMenu extends JPopupMenu {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("serial")
		public DelegateContextMenu(final Delegate d, final Runnable onPaste) {
			JMenuItem mnCopyIcon = new JMenuItem(
					new AbstractAction(
							Messages.getString("DelegatesConfigurationPanel.PmiCopyIcon"), //$NON-NLS-1$
							ImageFetcher.fetchImageIcon(ImageType.COPY)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							copybufferIcon = d.getDelegateIcon();
						}
					});

			JMenuItem mnPasteIcon = new JMenuItem(
					new AbstractAction(
							Messages.getString("DelegatesConfigurationPanel.PmiPasteIcon"), //$NON-NLS-1$
							ImageFetcher.fetchImageIcon(ImageType.PASTE)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							d.getDelegateIcon().set(copybufferIcon);
							onPaste.run();
						}
					});
			mnPasteIcon.setEnabled(copybufferIcon != null);

			add(mnCopyIcon);
			add(mnPasteIcon);
		}
	}

	/**
	 * Creates the panel to configure the delegates of the given committee.
	 * 
	 * @param starting
	 *            the starting delegates
	 * @param committee
	 *            the committee
	 */
	@SuppressWarnings("serial")
	public DelegatesConfigurationPanel(Collection<Delegate> starting,
			final Committee committee) {
		super(new BorderLayout());
		setBorder(new EmptyBorder(0, 2, 0, 2));
		final ArrayList<Delegate> delegates = new ArrayList<Delegate>(starting);
		Collections.sort(delegates);

		final JLabel lblNumDelegates = new JLabel(
				getDelegateCountLabelText(delegates.size()));
		add(lblNumDelegates, BorderLayout.NORTH);
		lblNumDelegates.setBorder(new EmptyBorder(2, 0, 2, 0));

		model = new DelegateModel(starting, true);
		final JList list = new JList(model);
		list.setVisibleRowCount(10);
		list.setCellRenderer(new DelegateRenderer());
		add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel panel = new JPanel(new MigLayout());
		add(panel, BorderLayout.SOUTH);

		final JTextField txtEntry = new JTextField();
		panel.add(txtEntry, new CC().spanY().grow().pushX());
		txtEntry.setColumns(20);

		final AbstractAction actAdd = new AbstractAction(
				Messages.getString("DelegatesConfigurationPanel.AddDelegate")) { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent ae) {
				String text = txtEntry.getText().trim();
				boolean flagOK = false;
				if (!text.isEmpty() && !model.contains(text)) {
					txtEntry.setText(new String());

					FileNameGuesser.Result result = FileNameGuesser.guess(text);

					if (result != null) {
						if (result.confident == true) {
							flagOK = true;
						} else {
							int ret = JOptionPane
									.showConfirmDialog(
											DelegatesConfigurationPanel.this,
											Messages.getString("DelegatesConfigurationPanel.ConfirmFlagMessage"), Messages.getString("DelegatesConfigurationPanel.ConfirmFlagTitle"), //$NON-NLS-1$ //$NON-NLS-2$
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE,
											result.icon);
							if (ret == JOptionPane.YES_OPTION) {
								flagOK = true;
							}
						}
					}
					DelegateIcon di = new DelegateIcon(IconType.NONE);
					if (flagOK) {
						di.setFlag(text);
						di.setType(IconType.FLAG);
					}
					Delegate delegate = new Delegate(text, di);
					model.add(delegate);
					List<String> affiliations = committee.getAffiliations();
					if (!affiliations.isEmpty()) {
						delegate.setAffiliation(affiliations.get(0));
					}
					list.ensureIndexIsVisible(model.indexOf(delegate));
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
				lblNumDelegates.setText(getDelegateCountLabelText(model
						.getSize()));
			}
		};
		final JButton btnAdd = new JButton(actAdd);
		panel.add(btnAdd, new CC().grow().spanY());
		btnAdd.setEnabled(false);

		txtEntry.setAction(actAdd);

		final AbstractAction actEdit = new AbstractAction(
				Messages.getString("DelegatesConfigurationPanel.EditButton")) { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent ae) {
				final JDialog dialog = new JDialog(
						SwingUtilities
								.getWindowAncestor(DelegatesConfigurationPanel.this));
				final Delegate delegate = (Delegate) list.getSelectedValue();
				dialog.setTitle(Messages
						.getString("DelegatesConfigurationPanel.EditDialogTitlePrefix") + delegate.getName()); //$NON-NLS-1$
				final DelegateEditingPanel dep = new DelegateEditingPanel(
						delegate, committee, getCurrentNames());
				dialog.setResizable(false);
				dialog.setModalityType(ModalityType.APPLICATION_MODAL);

				JPanel panel = new JPanel(new BorderLayout());
				panel.add(dep, BorderLayout.CENTER);
				JPanel pnlButtons = new JPanel(new FlowLayout(
						FlowLayout.TRAILING));
				JButton btnCancel = new JButton(
						Messages.getString("DelegatesConfigurationPanel.CancelButton")); //$NON-NLS-1$
				btnCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dialog.dispose();
					}
				});
				pnlButtons.add(btnCancel);

				JButton btnSave = new JButton(
						Messages.getString("DelegatesConfigurationPanel.SaveButton")); //$NON-NLS-1$
				btnSave.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dialog.dispose();
						dep.applyChanges();
					}
				});
				pnlButtons.add(btnSave);
				panel.add(pnlButtons, BorderLayout.SOUTH);
				dialog.getRootPane().setDefaultButton(btnSave);
				dialog.setContentPane(panel);
				dialog.pack();
				dialog.setLocationRelativeTo(DelegatesConfigurationPanel.this);
				dialog.setVisible(true);
				list.repaint();
			}
		};
		final JButton btnEdit = new JButton(actEdit);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me)) {
					int index = list.locationToIndex(me.getPoint());
					if (index != -1) {
						new DelegateContextMenu(model.getElementAt(index),
								new Runnable() {
									public void run() {
										list.repaint();
									}
								}).show(list, me.getPoint().x, me.getPoint().y);
					}
				}
				if (me.getClickCount() > 1) {
					actEdit.actionPerformed(null);
				}
			}
		});
		panel.add(btnEdit, new CC().grow().spanY());
		btnEdit.setEnabled(false);

		final JButton btnRemove = new JButton(
				new AbstractAction(Messages
						.getString("DelegatesConfigurationPanel.RemoveButton")) { //$NON-NLS-1$
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (!removeMessageShown) {
							int ret = JOptionPane.showConfirmDialog(
									DelegatesConfigurationPanel.this,
									Messages.getString("DelegatesConfigurationPanel.RemoveDelegateMessage"), //$NON-NLS-1$
									Messages.getString("DelegatesConfigurationPanel.RemoveDelegateTitle"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
									JOptionPane.WARNING_MESSAGE);
							if (ret != JOptionPane.YES_OPTION) {
								return;
							} else {
								removeMessageShown = true;
							}
						}
						Object[] selected = list.getSelectedValues();
						for (Object delegate : selected) {
							if (delegate instanceof Delegate) {
								model.remove((Delegate) delegate);
							}
						}
						list.clearSelection();
						lblNumDelegates.setText(getDelegateCountLabelText(model
								.getSize()));
					}
				});
		panel.add(btnRemove, new CC().growX().wrap());
		btnRemove.setEnabled(false);

		final JButton btnRename = new JButton(
				new AbstractAction(Messages
						.getString("DelegatesConfigurationPanel.RenameButton")) { //$NON-NLS-1$
					@Override
					public void actionPerformed(ActionEvent ae) {
						Delegate delegate = (Delegate) list.getSelectedValue();
						list.clearSelection();
						delegate.setName(txtEntry.getText().trim());
						txtEntry.setText(new String());
					}
				});
		panel.add(btnRename, new CC().growX());
		btnRename.setEnabled(false);

		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				btnRemove.setEnabled(!list.isSelectionEmpty());
				btnRename.setEnabled(list.getSelectedIndices().length == 1
						&& !(txtEntry.getText().trim().isEmpty()));
				btnEdit.setEnabled(list.getSelectedIndices().length == 1);
			}
		});
		txtEntry.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				process();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				process();
			}

			private void process() {
				String text = txtEntry.getText().trim();
				btnAdd.setEnabled(!text.isEmpty() && !model.contains(text));
				btnRename.setEnabled(list.getSelectedIndices().length == 1
						&& btnAdd.isEnabled());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				process();
			}
		});
	}

	/**
	 * Gets the text of the delegate count label.
	 * 
	 * @param number
	 *            the number of delegates
	 * @return the text
	 */
	private String getDelegateCountLabelText(int number) {
		return Messages
				.getString("DelegatesConfigurationPanel.NumberOfDelegates") + number; //$NON-NLS-1$
	}

	/**
	 * Gets a list of the current delegate names.
	 * 
	 * @return the list of names
	 */
	protected List<String> getCurrentNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Delegate delegate : getDelegates()) {
			names.add(delegate.getName());
		}
		return names;
	}

	/**
	 * Gets the list of delegates, as configured by the user.
	 * 
	 * @return the list
	 */
	public List<Delegate> getDelegates() {
		return model.getList();
	}
}
