package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.gui.events.DelegateSelectedEvent;
import org.lcmmun.kiosk.gui.events.DelegateSelectedListener;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechEvent.SpeechEventType;
import org.lcmmun.kiosk.gui.events.SpeechListener;
import org.lcmmun.kiosk.resources.ImageFetcher;
import org.lcmmun.kiosk.resources.ImageType;

import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;

public class SpeakersListPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of delegates that functions as a speakers' list.
	 */
	private final DelegateSelectionPanel speakersList;

	/**
	 * The model used in the combo box.
	 */
	private final List<Delegate> comboModel;

	/**
	 * The {@code EventListenerList} for this object.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * The committee whose speakers' list is being configured. May be
	 * {@code null}.
	 */
	private Committee committee;

	/**
	 * Whether the speakers' list allows delegates to begin speeches.
	 */
	private boolean canSpeak = true;

	/**
	 * The property for adding delegates to the speakers' list.
	 */
	private DelegateProperty dpDelegate;

	/**
	 * Creates the speakers list panel.
	 * 
	 * @param delegates
	 *            the list of delegates
	 */
	public SpeakersListPanel(Collection<Delegate> delegates) {
		this(null, delegates);
	}

	/**
	 * Creates the speakers list panel with the given model.
	 * 
	 * @param model
	 *            the model to use
	 * @param delegates
	 *            the list of delegates
	 */
	@SuppressWarnings("serial")
	public SpeakersListPanel(DelegateModel model, Collection<Delegate> delegates) {
		super(new BorderLayout());

		comboModel = new ArrayList<Delegate>();
		if (delegates != null) {
			comboModel.addAll(delegates);
		}
		dpDelegate = new DelegateProperty(null, comboModel);

		speakersList = new DelegateSelectionPanel(model, false);

		if (delegates != null) {
			updateDelegates(delegates);
		}

		add(speakersList, BorderLayout.CENTER);

		speakersList
				.addDelegateSelectedListener(new DelegateSelectedListener() {
					@Override
					public void delegateSelected(DelegateSelectedEvent dse) {
						if (canSpeak) {
							speakersList.list.clearSelection();
							startSpeech(dse.delegate);
						}
					}
				});

		speakersList.listModel.addListDataListener(new ListDataListener() {

			@Override
			public void contentsChanged(ListDataEvent lde) {
				if (committee != null) {
					// We need to reconstruct the entire list. Things could have
					// been moved around, etc.
					committee.speakersList.clear();
					for (Delegate d : speakersList.listModel.getList()) {
						committee.speakersList.add(d);
					}
				}
				fireChangeListener();
			}

			@Override
			public void intervalAdded(ListDataEvent lde) {
				if (committee != null) {
					for (Delegate delegate : speakersList.listModel.getList()) {
						if (!committee.speakersList.contains(delegate)) {
							committee.speakersList.add(delegate);
						}
					}
				}
				fireChangeListener();
			}

			@Override
			public void intervalRemoved(ListDataEvent lde) {
				if (committee != null) {
					Iterator<Delegate> it = committee.speakersList.iterator();
					List<Delegate> correctList = speakersList.listModel
							.getList();
					while (it.hasNext()) {
						Delegate delegate = it.next();
						if (!correctList.contains(delegate)) {
							it.remove();
						}
					}
				}
				fireChangeListener();
			}
		});

		dpDelegate.setDescription(Messages
				.getString("SpeakersListPanel.SelectToAdd")); //$NON-NLS-1$
		add(new PropertyPanel(new PropertySet(dpDelegate), true, false),
				BorderLayout.SOUTH);

		dpDelegate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				Delegate selectedDelegate = dpDelegate.getValue();
				if (selectedDelegate != null) {
					speakersList.listModel.add(selectedDelegate);
					comboModel.remove(selectedDelegate);
					dpDelegate.setList(comboModel);
					dpDelegate.setValue(null);
				}
			}
		});

		// Keyboard delete.
		speakersList.list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE
						|| ke.getKeyCode() == KeyEvent.VK_DELETE) {
					removeSelectedDelegate();
				} else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					Delegate delegate = (Delegate) speakersList.list
							.getSelectedValue();
					if (delegate != null) {
						speakersList.list.clearSelection();
						removeFromSpeakersList(delegate);
						startSpeech(delegate);
					}
				}
			}
		});

		speakersList.list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me)) {
					final int index = speakersList.list.locationToIndex(me
							.getPoint());
					if (index == -1) {
						return;
					}
					final Delegate at = speakersList.listModel
							.getElementAt(index);
					JPopupMenu popup = new JPopupMenu();
					popup.add(new AbstractAction(String.format(
							"Recognize %s (#%d)", at.getName(), index + 1), at
							.getSmallIcon()) {
						@Override
						public void actionPerformed(ActionEvent e) {
							startSpeech(at);
						}
					});

					final boolean canMoveUp = index > 0;
					final boolean canMoveDown = index < speakersList.listModel
							.getSize() - 1;

					popup.add(new JSeparator());
					popup.add(new AbstractAction("Remove", ImageFetcher
							.fetchImageIcon(ImageType.DELETE)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							removeFromSpeakersList(at);
						}
					});
					popup.add(new AbstractAction("Move to Top", ImageFetcher
							.fetchImageIcon(ImageType.UP_STRONG)) {
						{
							setEnabled(canMoveUp);
						}

						@Override
						public void actionPerformed(ActionEvent ae) {
							for (int i = index; i > 0; i--) {
								speakersList.listModel.moveUp(i);
							}
						}
					});
					popup.add(new AbstractAction("Move Up", ImageFetcher
							.fetchImageIcon(ImageType.UP)) {
						{
							setEnabled(canMoveUp);
						}

						@Override
						public void actionPerformed(ActionEvent e) {
							speakersList.listModel.moveUp(index);
						}
					});
					popup.add(new AbstractAction("Move Down", ImageFetcher
							.fetchImageIcon(ImageType.DOWN)) {
						{
							setEnabled(canMoveDown);
						}

						@Override
						public void actionPerformed(ActionEvent e) {
							speakersList.listModel.moveDown(index);
						}
					});
					popup.add(new AbstractAction("Move to Bottom", ImageFetcher
							.fetchImageIcon(ImageType.DOWN_STRONG)) {
						{
							setEnabled(canMoveDown);
						}

						@Override
						public void actionPerformed(ActionEvent ae) {
							for (int i = index; i < speakersList.listModel
									.getSize(); i++) {
								speakersList.listModel.moveDown(i);
							}
						}
					});
					popup.show(speakersList, me.getX(), me.getY());
				}
			}
		});
	}

	/**
	 * Adds the given listener to the list of listeners.
	 * 
	 * @param cl
	 *            the listener to add
	 */
	public void addChangeListener(ChangeListener cl) {
		listenerList.add(ChangeListener.class, cl);
	}

	/**
	 * Adds the given {@code SpeechListener} to the list of listeners.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addSpeechListener(SpeechListener listener) {
		listenerList.add(SpeechListener.class, listener);
	}

	/**
	 * Fires {@link ChangeEvent}s to all registered listeners.
	 */
	protected void fireChangeListener() {
		for (ChangeListener cl : listenerList
				.getListeners(ChangeListener.class)) {
			cl.stateChanged(new ChangeEvent(this));
		}
	}

	/**
	 * Gets the committee currently being configured.
	 * 
	 * @return the committee
	 */
	public Committee getCommittee() {
		return committee;
	}

	/**
	 * Gets the first speaker on the speakers' list.
	 * 
	 * @return the first speaker, or {@code null} if the list is empty
	 */
	public Delegate getFirstSpeaker() {
		return speakersList.listModel.getSize() == 0 ? null
				: speakersList.listModel.getElementAt(0);
	}

	/**
	 * Gets the model used for the speakers' list.
	 * 
	 * @return the list model
	 */
	public DelegateModel getModel() {
		return speakersList.listModel;
	}

	/**
	 * Removes the given listener from the list of listeners.
	 * 
	 * @param cl
	 *            the listener to remove
	 */
	public void removeChangeListener(ChangeListener cl) {
		listenerList.remove(ChangeListener.class, cl);
	}

	/**
	 * Removes the given delegate from the speakers' list and replaces him in
	 * the list of possible speakers (the combo box).
	 * 
	 * @param delegate
	 *            the delegate to remove
	 */
	private void removeFromSpeakersList(Delegate delegate) {
		speakersList.listModel.remove(delegate);
		if (!comboModel.contains(delegate)) {
			// Just making sure.
			comboModel.add(delegate);
		}
		speakersList.list.clearSelection();
	}

	/**
	 * Removes the selected delegate from the speakers' list.
	 * 
	 * @see #removeFromSpeakersList(Delegate)
	 */
	private void removeSelectedDelegate() {
		int selectedIndex = speakersList.list.getSelectedIndex();
		if (!speakersList.list.isSelectionEmpty()) {
			removeFromSpeakersList(speakersList.listModel
					.getElementAt(selectedIndex));
			if (speakersList.listModel.getSize() > selectedIndex) {
				speakersList.list.setSelectedIndex(selectedIndex);
			}
		}
	}

	/**
	 * Removes the given {@code SpeechListener} from the list of listeners.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeSpeechStartedListener(SpeechListener listener) {
		listenerList.remove(SpeechListener.class, listener);
	}

	/**
	 * Sets the committee to be configured. This may be {@code null}.
	 * 
	 * @param committee
	 *            the new committee
	 */
	public void setCommittee(Committee committee) {
		this.committee = committee;
		updateDelegates(committee.getPresentDelegates());
		speakersList.listModel.setContents(committee.speakersList);
	}

	/**
	 * Starts the speech of the given delegate.
	 * 
	 * @param delegate
	 *            the delegate whose speech should be started
	 */
	protected void startSpeech(Delegate delegate) {
		removeFromSpeakersList(delegate);
		for (SpeechListener ssl : listenerList
				.getListeners(SpeechListener.class)) {
			ssl.speechActionPerformed(new SpeechEvent(this, delegate,
					SpeechEventType.STARTED));
		}
	}

	/**
	 * Updates the list and combo box so that they reflect the most recent
	 * delegate configurations.
	 */
	@SuppressWarnings("unchecked")
	public void updateDelegates(Collection<Delegate> newDelegateList) {
		// Remove if it's no longer there, for both models.
		for (List<Delegate> model : Arrays.<List<Delegate>> asList(
				speakersList.listModel.getList(), comboModel)) {
			Iterator<Delegate> iterator = model.iterator();
			while (iterator.hasNext()) {
				Delegate delegate = iterator.next();
				if (!newDelegateList.contains(delegate)) {
					// Ready to speak, but no longer here.
					iterator.remove();
				}
			}
		}

		// Add new options to the combo model.
		for (Delegate delegate : newDelegateList) {
			if (!comboModel.contains(delegate)
					&& !speakersList.listModel.contains(delegate)) {
				// Not already on the list, but not selectable either.
				comboModel.add(delegate);
			}
		}

		// If it's somehow on both lists, take it out of the combo box.
		for (Delegate delegate : speakersList.listModel.getList()) {
			if (comboModel.contains(delegate)) {
				comboModel.remove(delegate);
			}
		}
		dpDelegate.setList(comboModel);
	}

	/**
	 * Determines whether this speakers list allows delegates to begin speeches.
	 * 
	 * @return {@code true} if the user is allowed to start speeches with this
	 *         list, or {@code false} if not
	 */
	public boolean isCanSpeak() {
		return canSpeak;
	}

	/**
	 * Sets whether this speakers list allows delegates to begin speeches.
	 * 
	 * @param {@code true} if the user should allowed to start speeches with
	 *        this list, or {@code false} if not
	 */
	public void setCanSpeak(boolean canSpeak) {
		this.canSpeak = canSpeak;
	}

}
