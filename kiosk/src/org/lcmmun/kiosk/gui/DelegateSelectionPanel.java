package org.lcmmun.kiosk.gui;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.gui.events.DelegateSelectedEvent;
import org.lcmmun.kiosk.gui.events.DelegateSelectedListener;

/**
 * A panel that allows for selection of delegates. When a delegate is
 * double-clicked in the list, a {@link DelegateSelected} event will be fired.
 * 
 * @author William Chargin
 * 
 */
public class DelegateSelectionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The model used for the list.
	 */
	public final DelegateModel listModel;

	/**
	 * The list for delegate selection.
	 */
	public final JList list;

	/**
	 * Creates the selection panel with an empty model.
	 * 
	 * @param alphabetizeList
	 *            whether the list should constantly be alphabetized
	 * @see DelegateModel#DelegateListModel(boolean)
	 */
	public DelegateSelectionPanel(boolean alphabetizeList) {
		this(null, alphabetizeList);
	}

	/**
	 * Creates the selection panel.
	 * 
	 * @param model
	 *            the model to use for the list
	 * @param alphabetizeList
	 *            whether the list should constantly be alphabetized
	 * 
	 * @see DelegateModel#DelegateListModel(boolean)
	 */
	public DelegateSelectionPanel(DelegateModel model, boolean alphabetizeList) {
		super(new GridLayout(1, 1));

		listModel = (model == null ? new DelegateModel(alphabetizeList) : model);
		list = new JList(listModel);
		add(new JScrollPane(list));

		list.setCellRenderer(new DelegateRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (isEnabled() && me.getClickCount() >= 2) {
					// It's a double (or triple, etc.) click.
					if (list.isSelectionEmpty()) {
						return;
					}
					int index = list.locationToIndex(me.getPoint());
					Delegate delegate = listModel.getElementAt(index);
					list.clearSelection();
					for (DelegateSelectedListener listener : listenerList
							.getListeners(DelegateSelectedListener.class)) {
						listener.delegateSelected(new DelegateSelectedEvent(
								DelegateSelectionPanel.this, delegate));
					}
				}
			}
		});
		
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_ENTER) {
					return;
				}
				if (list.isSelectionEmpty()) {
					return;
				}
				int index = list.getSelectedIndex();
				Delegate delegate = listModel.getElementAt(index);
				list.clearSelection();
				for (DelegateSelectedListener listener : listenerList
						.getListeners(DelegateSelectedListener.class)) {
					listener.delegateSelected(new DelegateSelectedEvent(
							DelegateSelectionPanel.this, delegate));
				}
			}

		});
	}

	/**
	 * Adds the given {@code DelegateSelectedListener} to the list of listeners.
	 * 
	 * @param dsl
	 *            the listener to add
	 */
	public void addDelegateSelectedListener(DelegateSelectedListener dsl) {
		listenerList.add(DelegateSelectedListener.class, dsl);
	}

	/**
	 * Removes the given {@code DelegateSelectedListener} from the list of
	 * listeners.
	 * 
	 * @param dsl
	 *            the listener to remove
	 */
	public void removeDelegateSelectedListener(DelegateSelectedListener dsl) {
		listenerList.remove(DelegateSelectedListener.class, dsl);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		list.setEnabled(enabled);
	}

}
