package org.lcmmun.kiosk.gui;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.lcmmun.kiosk.Delegate;

import tools.customizable.AbstractSwingProperty;

public class DelegateProperty extends
		AbstractSwingProperty<Delegate, AutoDelegateComboBox, JLabel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of delegates for this property.
	 */
	private List<Delegate> list;

	/**
	 * Whether the list has been changed and needs to be updated.
	 */
	private boolean listDirty;

	/**
	 * The listener to add.
	 */
	private ActionListener toAdd;

	/**
	 * The listener to remove.
	 */
	private ActionListener toRemove;

	@Override
	protected AutoDelegateComboBox createEditor() {
		AutoDelegateComboBox box = new AutoDelegateComboBox(
				list == null ? new ArrayList<Delegate>() : list);
		box.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					Object item = ie.getItem();
					if (item instanceof Delegate) {
						setValue((Delegate) item);
					} else {
						if (item == null) {
							return;
						}
						String name = item.toString();
						for (Delegate delegate : list) {
							if (name.equalsIgnoreCase(delegate.getName())) {
								if (getValue() == delegate) {
									return;
								}
								setValue(delegate);
								break;
							}
						}
					}
				}
			}
		});
		return box;
	}

	public DelegateProperty(String name, Delegate value, List<Delegate> list) {
		super(name, value == null ? list == null || list.isEmpty() ? null
				: list.get(0) : value);
		this.list = list;
	}

	public DelegateProperty(String name, List<Delegate> list) {
		this(name, null, list);
	}

	@Override
	protected JLabel createViewer() {
		return new JLabel();
	}

	@Override
	protected void updateEditor(AutoDelegateComboBox editor) {
		if (listDirty) {
			editor.setDataList(list);
		}
		if (toAdd != null) {
			editor.addActionListener(toAdd);
		}
		if (toRemove != null) {
			editor.removeActionListener(toRemove);
		}
		editor.setSelectedValue(getValue());
	}

	@Override
	protected void updateViewer(JLabel viewer) {
		viewer.setText(getValue() == null ? new String() : getValue()
				.toString());
	}

	public List<Delegate> getList() {
		return list;
	}

	public void setList(List<Delegate> list) {
		listDirty = true;
		this.list = list;
		updateEditors();
		listDirty = false;
	}

	/**
	 * Adds the given action listener to this property.
	 * 
	 * @param al
	 *            the action listener
	 */
	public void addActionListener(ActionListener al) {
		toAdd = al;
		updateEditors();
		toAdd = null;
	}

	/**
	 * Removes the given action listener from this property.
	 * 
	 * @param al
	 *            the action listener
	 */
	public void removeActionListener(ActionListener al) {
		toRemove = al;
		updateEditors();
		toRemove = null;
	}
}
