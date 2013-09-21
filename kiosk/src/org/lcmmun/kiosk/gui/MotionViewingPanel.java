package org.lcmmun.kiosk.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
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
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.MotionResult;
import org.lcmmun.kiosk.motions.Motion;

import tools.customizable.MessageProperty;
import tools.customizable.MultipleChoiceProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;

/**
 * A panel for viewing a motion and selecting whether it passes or not.
 * 
 * @author William Chargin
 * 
 */
public class MotionViewingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the viewing panel.
	 * 
	 * @param motion
	 *            the motion to configure
	 */
	public MotionViewingPanel(final Motion motion, Committee committee) {
		super(new MigLayout(new LC().flowY()));

		JLabel lblMotion = new JLabel(Messages.getString("MotionViewingPanel.MotionSuperTitle")); //$NON-NLS-1$
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

		final MultipleChoiceProperty<Delegate> mcpProposingDelegate = new MultipleChoiceProperty<Delegate>(
				Messages.getString("MotionViewingPanel.PropertyProposingDelegate"), committee.getPresentDelegates(), //$NON-NLS-1$
				motion.getProposingDelegate());
		propertySet.add(mcpProposingDelegate);
		mcpProposingDelegate.setRenderer(new DelegateRenderer());
		mcpProposingDelegate.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				motion.setProposingDelegate(mcpProposingDelegate.getValue());
			}
		});

		propertySet.addAll(motion.getPropertySet());

		propertySet.add(null); // separator

		MessageProperty mpSubstantive = new MessageProperty(Messages.getString("MotionViewingPanel.PropertyMotionType"), //$NON-NLS-1$
				motion.isSubstantive() ? Messages.getString("MotionViewingPanel.PropertyValueSubstantive") : Messages.getString("MotionViewingPanel.PropertyValueProcedural")); //$NON-NLS-1$ //$NON-NLS-2$
		propertySet.add(mpSubstantive);

		MessageProperty mpMajorityType = new MessageProperty(Messages.getString("MotionViewingPanel.PropertyMajorityType"), //$NON-NLS-1$
				motion.getMajorityType().toString());
		propertySet.add(mpMajorityType);

		add(new PropertyPanel(propertySet, false, false), new CC().grow());

		final JComboBox cmbxResult = new JComboBox(MotionResult.values());
		add(cmbxResult, new CC().growX());
		cmbxResult.setSelectedItem(MotionResult.PASSED);
		cmbxResult.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				motion.setResult((MotionResult) cmbxResult.getSelectedItem());
			}
		});

	}
}
