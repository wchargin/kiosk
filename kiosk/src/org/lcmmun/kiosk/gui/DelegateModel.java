package org.lcmmun.kiosk.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.lcmmun.kiosk.Delegate;

/**
 * A list model for {@link Delegate}s.
 * 
 * @author William Chargin
 * 
 */
public class DelegateModel extends AbstractListModel implements ComboBoxModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of delegates.
	 */
	private final ArrayList<Delegate> list = new ArrayList<Delegate>();

	/**
	 * Whether the list should always be alphabetized. This should be
	 * {@code true} when order is unimportant, such as for a roll call vote, but
	 * {@code false} for something like a speakers' list.
	 */
	private final boolean alphabetize;

	/**
	 * The listener added to all incoming delegates so that when their name
	 * changes, the list can be alphabetized.
	 */
	private final PropertyChangeListener nameListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pcl) {
			if (alphabetize) {
				alphabetize();
			}
		}
	};

	/**
	 * The currently selected item.
	 */
	private Object selectedItem;

	/**
	 * Creates the delegate list model with no starting elements.
	 * 
	 * @param alphabetize
	 *            whether the list should be alphabetized
	 */
	public DelegateModel(boolean alphabetize) {
		this(null, alphabetize);
	}

	/**
	 * Creates the delegate list model with the given starting elements.
	 * 
	 * @param startingList
	 *            the starting elements
	 * @param alphabetize
	 *            whether the list should be alphabetized
	 */
	public DelegateModel(Collection<? extends Delegate> startingList,
			boolean alphabetize) {
		super();
		if (startingList != null) {
			list.addAll(startingList);
		}
		this.alphabetize = alphabetize;
	}

	/**
	 * Adds the given delegate to the list.
	 * 
	 * @param d
	 *            the delegate to add
	 */
	public void add(Delegate d) {
		list.add(d);
		d.addPropertyChangeListener(Delegate.PROP_NAME, nameListener);
		int index = list.indexOf(d);
		fireIntervalAdded(this, index, index);
		alphabetizeIfNecessary();
	}

	/**
	 * Alphabetizes the list. If the list was set to always be alphabetized,
	 * this method will be invoked whenever the contents are changed.
	 */
	public void alphabetize() {
		Collections.sort(list);
		fireContentsChanged(this, 0, getSize() - 1);
	}

	/**
	 * Alphabetizes the list if {@link #alphabetize} is {@code true}.
	 */
	private void alphabetizeIfNecessary() {
		if (alphabetize) {
			alphabetize();
		}
	}

	/**
	 * Checks if the list contains the given delegate.
	 * 
	 * @param delegate
	 *            the delegate to check
	 * @return {@code true} if the delegate is already in the list, or
	 *         {@code false} otherwise
	 */
	public boolean contains(Delegate delegate) {
		return list.contains(delegate);
	}

	/**
	 * Checks if the list contains a delegate by the given name.
	 * 
	 * @param name
	 *            the name to check
	 * @return {@code true} if a delegate by the given name is already in the
	 *         list, or {@code false} otherwise
	 */
	public boolean contains(String name) {
		for (Delegate delegate : list) {
			if (delegate.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Delegate getElementAt(int index) {
		return list.get(index);
	}

	/**
	 * Gets a copy of the list used in this model. The list will not be backed
	 * by the model; any changes made to one will not be reflected in the other.
	 * 
	 * @return a copy of the list
	 */
	public List<Delegate> getList() {
		return new ArrayList<Delegate>(this.list);
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	@Override
	public int getSize() {
		return list.size();
	}

	/**
	 * Gets the index of the given delegate in the list.
	 * 
	 * @param delegate
	 *            the delegate whose index is required
	 * @return the index
	 */
	public int indexOf(Delegate delegate) {
		return list.indexOf(delegate);
	}

	/**
	 * Moves the delegate at the given index down (unless the index is equal to
	 * the expression <code>{@link #getSize()} - 1</code>, in which case it does
	 * nothing).
	 * 
	 * @param index
	 *            the index to move down
	 */
	public void moveDown(int index) {
		if (index < getSize() - 1) {
			Collections.swap(list, index, index + 1);
		}
		fireContentsChanged(this, index, index + 1);
	}

	/**
	 * Moves the delegate at the given index up (unless the index is {@code 0},
	 * in which case it does nothing).
	 * 
	 * @param index
	 *            the index to move up
	 */
	public void moveUp(int index) {
		if (index > 0) {
			Collections.swap(list, index, index - 1);
		}
		fireContentsChanged(this, index - 1, index);
	}

	/**
	 * Removes the given delegate from the list.
	 * 
	 * @param d
	 *            the delegate to remove
	 */
	public void remove(Delegate d) {
		if (list.contains(d)) {
			int index = list.indexOf(d);
			list.remove(d);
			d.removePropertyChangeListener(Delegate.PROP_NAME, nameListener);
			fireIntervalRemoved(this, index, index);
			alphabetizeIfNecessary();
		}
	}

	/**
	 * Sets the contents of the list. This method removes all delegates and then
	 * adds those in this list.
	 * 
	 * @param newContents
	 *            the new contents
	 */
	public void setContents(Collection<Delegate> newContents) {
		int oldSize = list.size();
		list.clear();
		list.addAll(newContents);
		int newSize = list.size();
		fireContentsChanged(this, 0, oldSize > newSize ? oldSize : newSize);
		if (!list.contains(selectedItem)) {
			// It must go!
			setSelectedItem(null);
		}
	}

	@Override
	public void setSelectedItem(Object selectedItem) {
		this.selectedItem = selectedItem;
	}
}
