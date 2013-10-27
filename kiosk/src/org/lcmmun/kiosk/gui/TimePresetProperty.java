package org.lcmmun.kiosk.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.lcmmun.kiosk.Messages;

import net.miginfocom.layout.CC;
import tools.customizable.Time;
import tools.customizable.TimeProperty;

/**
 * A {@link TimeProperty} that provides a list of presets accessible from a
 * combo box.
 * 
 * @author William Chargin
 * 
 */
public class TimePresetProperty extends TimeProperty {

	private class TimePresetPanel extends TimePanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Creates the panel with the given time.
		 * 
		 * @param time
		 *            the starting time
		 */
		public TimePresetPanel(Time time) {
			super(time);

			DefaultComboBoxModel model = new DefaultComboBoxModel(
					new Vector<Time>(presets));
			final JComboBox cmbPresets = new JComboBox(model);
			cmbPresets.setSelectedItem(null);
			add(cmbPresets, new CC().growX());
			cmbPresets.setFocusable(false);
			cmbPresets.setToolTipText(Messages.getString("TimePresetProperty.TtSelectPresetTime")); //$NON-NLS-1$
			cmbPresets.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent ie) {
					if (ie.getStateChange() == ItemEvent.SELECTED) {
						Object item = ie.getItem();
						if (item != null) {
							if (item instanceof Time) {
								setValue((Time) item);
							}
							cmbPresets.setSelectedItem(null);
						}
					}
				}
			});
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A collection of short-time presets.
	 */
	public static final List<Time> SHORT_PRESETS = Collections
			.unmodifiableList(listFromSeconds(30, 45, 60, 60 * 5, 60 * 10));

	/**
	 * A collection of long-time presets.
	 */
	public static final List<Time> LONG_PRESETS = Collections
			.unmodifiableList(listFromSeconds(60, 60 * 5, 60 * 10, 60 * 20,
					60 * 30, 60 * 60));

	/**
	 * Creates a list of {@link Time} values with the given second counts.
	 * 
	 * @param seconds
	 *            the list of second counts
	 * @return a list of corresponding time values
	 */
	public static List<Time> listFromSeconds(int... seconds) {
		List<Time> list = new ArrayList<Time>();
		for (int i : seconds) {
			list.add(Time.fromSeconds(i));
		}
		return list;
	}

	/**
	 * The list of presets used in this property.
	 */
	private List<Time> presets;

	/**
	 * Creates the time preset property with the {@link #SHORT_PRESETS} as
	 * presets.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the initial value
	 * @param presets
	 *            the list of presets
	 */
	public TimePresetProperty(String name, Time value) {
		this(name, value, null);
	}

	/**
	 * Creates the time preset property with the given presets.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the initial value
	 * @param presets
	 *            the list of presets
	 */
	public TimePresetProperty(String name, Time value,
			List<? extends Time> presets) {
		super(name, value);
		this.presets = new ArrayList<Time>();
		if (presets == null) {
			presets = SHORT_PRESETS;
		}
		this.presets.addAll(new LinkedHashSet<Time>(presets));
	}

	@Override
	protected TimePanel createEditor() {
		return new TimePresetPanel(getValue());
	}

}
