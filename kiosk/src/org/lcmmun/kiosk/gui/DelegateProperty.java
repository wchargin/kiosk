package org.lcmmun.kiosk.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

	@Override
	protected AutoDelegateComboBox createEditor() {
		AutoDelegateComboBox box = new AutoDelegateComboBox(list);
		box.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					Object item = ie.getItem();
					if (item instanceof Delegate) {
						setValue((Delegate) item);
					} else {
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
		super(name, value == null ? list.get(0) : value);
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
		editor.setSelectedValue(getValue());
	}

	@Override
	protected void updateViewer(JLabel viewer) {
		viewer.setText(getValue() == null ? new String() : getValue()
				.toString());
	}

}
