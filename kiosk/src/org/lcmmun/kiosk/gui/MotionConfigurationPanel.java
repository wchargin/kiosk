package org.lcmmun.kiosk.gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.motions.Motion;

import tools.customizable.MessageProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;

public class MotionConfigurationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the configuration panel.
	 * 
	 * @param motion
	 *            the motion to configure
	 */
	public MotionConfigurationPanel(final Motion motion, Committee committee) {
		super(new MigLayout(new LC().flowY()));

		JLabel lblMotion = new JLabel(
				Messages.getString("MotionConfigurationPanel.MotionSuperTitle")); //$NON-NLS-1$
		add(lblMotion, new CC().growX().pushX());
		lblMotion.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMotion.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblMotionName = new JLabel(motion.getMotionName());
		add(lblMotionName, new CC().growX().pushX());
		lblMotionName.setFont(lblMotionName.getFont()
				.deriveFont((float) lblMotionName.getFont().getSize() * 2)
				.deriveFont(Font.BOLD));
		lblMotionName.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMotionName.setHorizontalAlignment(SwingConstants.CENTER);

		JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
		add(separator1, new CC().growX());

		PropertySet propertySet = new PropertySet();

		final DelegateProperty dpProposingDelegate = new DelegateProperty(
				Messages.getString("MotionConfigurationPanel.PropertyProposingDelegate"), //$NON-NLS-1$;
				motion.getProposingDelegate(), committee.getPresentDelegates());
		propertySet.add(dpProposingDelegate);
		dpProposingDelegate.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				motion.setProposingDelegate(dpProposingDelegate.getValue());
			}
		});

		propertySet.addAll(motion.getPropertySet());

		propertySet.add(null); // separator

		MessageProperty mpSubstantive = new MessageProperty(
				Messages.getString("MotionConfigurationPanel.PropertyMotionType"), //$NON-NLS-1$
				motion.isSubstantive() ? Messages
						.getString("MotionConfigurationPanel.PropertyValueSubstantive") : Messages.getString("MotionConfigurationPanel.PropertyValueProcedural")); //$NON-NLS-1$ //$NON-NLS-2$
		propertySet.add(mpSubstantive);

		MessageProperty mpMajorityType = new MessageProperty(
				Messages.getString("MotionConfigurationPanel.PropertyMajorityType"), //$NON-NLS-1$
				motion.getMajorityType().toString());
		propertySet.add(mpMajorityType);

		add(new PropertyPanel(propertySet, true, false), new CC().grow());

	}
}
