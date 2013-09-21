package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.WorkingPaper;
import org.lcmmun.kiosk.gui.events.DelegateSelectedEvent;
import org.lcmmun.kiosk.gui.events.DelegateSelectedListener;
import org.lcmmun.kiosk.gui.events.EditingFinishedEvent;
import org.lcmmun.kiosk.gui.events.EditingFinishedListener;

import tools.customizable.FileProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TextProperty;

/**
 * A panel that facilitates the editing of a working paper or draft resolution.
 * 
 * @author William Chargin
 * 
 */
public class WorkingPaperEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The original working paper.
	 */
	private WorkingPaper original;

	/**
	 * The finished working paper. This will be {@code null} until the user
	 * selects "OK" (and if the user selects "Cancel," it will forever be
	 * {@code null}).
	 */
	private WorkingPaper workingPaper;

	private DelegateSelectionPanel dspSubmitters;

	private DelegateSelectionPanel dspSignatories;

	private DelegateModel comboModelSignatories;

	private DelegateModel comboModelSubmitters;

	private Vector<Delegate> presentWithRights;

	private FileProperty fpFile;

	/**
	 * The Google Docs ID property.
	 */
	private TextProperty tpGoogleDocs;

	/**
	 * Whether the paper has been modified since last save/discard.
	 */
	private boolean modified;

	/**
	 * Creates the editor, given the paper to edit and the relevant committee.
	 * 
	 * @param wp
	 *            the paper to edit
	 * @param committee
	 *            the committee in which the paper is being submitted
	 */
	public WorkingPaperEditor(final WorkingPaper wp, final Committee committee) {
		super(new MigLayout(new LC().flowY()));

		this.original = wp.clone();
		this.workingPaper = wp;

		PropertySet ps = new PropertySet();

		fpFile = new FileProperty(
				Messages.getString("WorkingPaperEditor.PropertyFileLocation"), wp.getFile()); //$NON-NLS-1$
		ps.add(fpFile);
		fpFile.setFilter(new FileNameExtensionFilter(Messages
				.getString("WorkingPaperEditor.PDFFileFilter"), "pdf")); //$NON-NLS-1$ //$NON-NLS-2$

		tpGoogleDocs = new TextProperty(Messages.getString("WorkingPaperEditor.GoogleDocsID"), //$NON-NLS-1$
				wp.getGoogleDocsId());
		ps.add(tpGoogleDocs);

		ChangeListener clModified = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				modified = true;
			}
		};

		fpFile.addChangeListener(clModified);
		tpGoogleDocs.addChangeListener(clModified);

		add(new PropertyPanel(ps, true, false), new CC().growX());

		final JButton btnOpen = new JButton(
				Messages.getString("WorkingPaperEditor.ButtonOpenFile")); //$NON-NLS-1$
		add(btnOpen, new CC().growX());
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					Desktop.getDesktop().open(fpFile.getValue());
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane
							.showMessageDialog(
									WorkingPaperEditor.this,
									Messages.getString("WorkingPaperEditor.ErrorOccurredMessage"), //$NON-NLS-1$
									Messages.getString("WorkingPaperEditor.ErrorOccurredTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		final Runnable setOpenEnabled = new Runnable() {
			@Override
			public void run() {
				btnOpen.setEnabled(Desktop.isDesktopSupported()
						&& fpFile.getValue() != null
						&& fpFile.getValue().exists());
			}
		};
		setOpenEnabled.run();
		fpFile.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				setOpenEnabled.run();
			}
		});

		add(new JSeparator(SwingConstants.HORIZONTAL), new CC().growX().pushX()
				.pad(3, 0, 3, 0));

		JPanel pnlConfiguration = new JPanel(new BorderLayout());
		add(pnlConfiguration, new CC().grow().push());

		presentWithRights = new Vector<Delegate>(
				committee.getPresentDelegates());
		Iterator<Delegate> it = presentWithRights.iterator();
		while (it.hasNext()) {
			Delegate d = it.next();
			if (!d.getStatus().canVoteSubstantive) {
				it.remove();
			}
		}

		final ListDataListener ldlModified = new ListDataListener() {
			@Override
			public void contentsChanged(ListDataEvent lde) {
				modified = true;
			}

			@Override
			public void intervalAdded(ListDataEvent lde) {
				modified = true;
			}

			@Override
			public void intervalRemoved(ListDataEvent lde) {
				modified = true;
			}
		};

		JPanel pnlSubmitters = new JPanel(new BorderLayout());
		{ // Configure the submitters panel
			dspSubmitters = new DelegateSelectionPanel(null, true);
			for (Delegate d : wp.submitters) {
				dspSubmitters.listModel.add(d);
			}
			pnlSubmitters.add(dspSubmitters, BorderLayout.CENTER);

			JLabel lblPleaseSelect = new JLabel(
					Messages.getString("WorkingPaperEditor.SelectSubmitters")); //$NON-NLS-1$
			pnlSubmitters.add(lblPleaseSelect, BorderLayout.NORTH);
			lblPleaseSelect.setHorizontalAlignment(SwingConstants.CENTER);

			comboModelSubmitters = new DelegateModel(presentWithRights, true);
			for (Delegate d : dspSubmitters.listModel.getList()) {
				comboModelSubmitters.remove(d);
			}
			final JComboBox cmbx = new JComboBox(comboModelSubmitters);
			pnlSubmitters.add(cmbx, BorderLayout.SOUTH);
			cmbx.setRenderer(new DelegateRenderer());
			cmbx.setSelectedItem(null);
			cmbx.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					Object o = cmbx.getSelectedItem();
					if (o == null) {
						return;
					}
					final Delegate d = (Delegate) o;
					comboModelSubmitters.setSelectedItem(null);
					if (dspSignatories != null
							&& dspSignatories.listModel.contains(d)) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(
										WorkingPaperEditor.this,
										d.getName()
												+ Messages
														.getString("WorkingPaperEditor.AlreadySignatorySuffix"), //$NON-NLS-1$
										Messages.getString("WorkingPaperEditor.AlreadySignatoryTitle"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
							}
						});
						return;
					}
					dspSubmitters.listModel.add(d);
					comboModelSubmitters.remove(d);
				}
			});
			dspSubmitters
					.addDelegateSelectedListener(new DelegateSelectedListener() {
						@Override
						public void delegateSelected(
								final DelegateSelectedEvent dse) {
							final Delegate delegate = dse.delegate;
							dspSubmitters.listModel.remove(delegate);
						}
					});

			dspSubmitters.listModel.addListDataListener(ldlModified);
			comboModelSubmitters.addListDataListener(ldlModified);
		}

		if (wp.isDraftResolution()) {
			JPanel pnlSignatories = new JPanel(new BorderLayout());
			{ // Configure the signatories panel
				pnlConfiguration.add(pnlSignatories);

				dspSignatories = new DelegateSelectionPanel(null, true);
				for (Delegate d : wp.getSignatories()) {
					dspSignatories.listModel.add(d);
				}
				pnlSignatories.add(dspSignatories, BorderLayout.CENTER);

				JLabel lblPleaseSelect = new JLabel(
						Messages.getString("WorkingPaperEditor.SelectSignatories")); //$NON-NLS-1$
				pnlSignatories.add(lblPleaseSelect, BorderLayout.NORTH);
				lblPleaseSelect.setHorizontalAlignment(SwingConstants.CENTER);

				comboModelSignatories = new DelegateModel(presentWithRights,
						true);
				for (Delegate d : dspSignatories.listModel.getList()) {
					comboModelSignatories.remove(d);
				}
				final JComboBox cmbx = new JComboBox(comboModelSignatories);
				pnlSignatories.add(cmbx, BorderLayout.SOUTH);
				cmbx.setRenderer(new DelegateRenderer());
				cmbx.setSelectedItem(null);
				cmbx.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						Object o = cmbx.getSelectedItem();
						if (o == null) {
							return;
						}
						final Delegate d = (Delegate) o;
						comboModelSignatories.setSelectedItem(null);
						if (dspSubmitters.listModel.contains(d)) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(
											WorkingPaperEditor.this,
											d.getName()
													+ Messages
															.getString("WorkingPaperEditor.AlreadySubmitterMessage"), //$NON-NLS-1$
											Messages.getString("WorkingPaperEditor.AlreadySubmitterTitle"), //$NON-NLS-1$
											JOptionPane.ERROR_MESSAGE);
								}
							});
							return;
						}
						dspSignatories.listModel.add(d);
						comboModelSignatories.remove(d);
					}
				});
				dspSignatories
						.addDelegateSelectedListener(new DelegateSelectedListener() {
							@Override
							public void delegateSelected(
									final DelegateSelectedEvent dse) {
								final Delegate delegate = dse.delegate;
								dspSignatories.listModel.remove(delegate);
							}
						});

				dspSignatories.listModel.addListDataListener(ldlModified);
				comboModelSignatories.addListDataListener(ldlModified);

				JTabbedPane tabbedPane = new JTabbedPane();
				tabbedPane
						.addTab(Messages
								.getString("WorkingPaperEditor.Submitters"), pnlSubmitters); //$NON-NLS-1$
				tabbedPane
						.addTab(Messages
								.getString("WorkingPaperEditor.Signatories"), pnlSignatories); //$NON-NLS-1$
				pnlConfiguration.add(tabbedPane, BorderLayout.CENTER);
			}
		} else {
			pnlConfiguration.add(pnlSubmitters, BorderLayout.CENTER);
		}

		JPanel pnlButtons = new JPanel(new GridLayout(1, 2));
		add(pnlButtons, new CC().growX());

		JButton btnDiscard = new JButton(
				Messages.getString("WorkingPaperEditor.DiscardChanges")); //$NON-NLS-1$
		pnlButtons.add(btnDiscard);
		btnDiscard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				discardChanges();
			}
		});

		JButton btnSave = new JButton(
				Messages.getString("WorkingPaperEditor.SaveChanges")); //$NON-NLS-1$
		pnlButtons.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				saveChanges();
			}
		});

	}

	/**
	 * Adds the given {@code EditingFinishedListener} to the list of listeners.
	 * 
	 * @param efl
	 *            the listener to add
	 */
	public void addEditingFinishedListener(EditingFinishedListener efl) {
		listenerList.add(EditingFinishedListener.class, efl);
	}

	/**
	 * Resets the editor to match the original.
	 */
	public void discardChanges() {
		fpFile.setValue(original.getFile());
		dspSubmitters.listModel.setContents(original.submitters);
		comboModelSubmitters.setContents(presentWithRights);
		for (Delegate delegate : original.submitters) {
			comboModelSubmitters.remove(delegate);
		}
		if (original.isDraftResolution()) {
			dspSignatories.listModel.setContents(original.getSignatories());
			comboModelSignatories.setContents(presentWithRights);
			for (Delegate delegate : original.getSignatories()) {
				comboModelSignatories.remove(delegate);
			}
		}

		editingFinished();
	}

	/**
	 * Fires editing finished events to all registered listeners.
	 */
	private void editingFinished() {
		original = workingPaper.clone();
		for (EditingFinishedListener listener : listenerList
				.getListeners(EditingFinishedListener.class)) {
			listener.editingFinished(new EditingFinishedEvent(this));
		}
		modified = false;
	}

	/**
	 * Gets the working paper being edited.
	 * 
	 * @return the paper
	 */
	public WorkingPaper getWorkingPaper() {
		return workingPaper;
	}

	/**
	 * Gets whether the paper has been modified since its changes were last
	 * saved/discarded.
	 * 
	 * @return whether it has been modified
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Removes the given {@code EditingFinishedListener} from the list of
	 * listeners.
	 * 
	 * @param efl
	 *            the listener to remove
	 */
	public void removeEditingFinishedListener(EditingFinishedListener efl) {
		listenerList.remove(EditingFinishedListener.class, efl);
	}

	/**
	 * Applies the changes.
	 */
	public void saveChanges() {
		workingPaper.setFile(fpFile.getValue());
		workingPaper.setGoogleDocsId(tpGoogleDocs.getValue());
		workingPaper.submitters.clear();
		workingPaper.submitters.addAll(dspSubmitters.listModel.getList());
		if (workingPaper.isDraftResolution()) {
			workingPaper.getSignatories().clear();
			workingPaper.getSignatories().addAll(
					dspSignatories.listModel.getList());
		}

		editingFinished();
	}

}
