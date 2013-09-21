package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.lcmmun.kiosk.Delegate;

public class DelegateView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The icon used when no delegate is available.
	 */
	private static final ImageIcon BLANK_ICON = new ImageIcon(
			new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR));

	/**
	 * The label showing the delegate's icon.
	 */
	private JLabel lblIcon;

	/**
	 * The label showing the delegate's name.
	 */
	private JLabel lblName;

	/**
	 * Creates the view for no delegate in particular.
	 */
	public DelegateView() {
		super(new CardLayout());

		add(new JPanel(), Boolean.toString(false));

		JPanel pnlSpeaking = new JPanel(new BorderLayout());
		add(pnlSpeaking, Boolean.toString(true));

		lblIcon = new JLabel(BLANK_ICON);
		pnlSpeaking.add(lblIcon, BorderLayout.CENTER);
		lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

		lblName = new JLabel(Character.toString(' '));
		pnlSpeaking.add(lblName, BorderLayout.SOUTH);
		lblName.setFont(lblName.getFont()
				.deriveFont((float) (lblName.getFont().getSize() * 2))
				.deriveFont(Font.BOLD));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

	}

	/**
	 * Creates the view for the given delegate.
	 * 
	 * @param delegate
	 *            the delegate
	 */
	public DelegateView(Delegate delegate) {
		this();

		setDelegate(delegate);

	}

	/**
	 * Updates the view to match the new delegate's information.
	 * 
	 * @param delegate
	 *            the new delegate
	 */
	public void setDelegate(Delegate delegate) {
		if (delegate != null) {
			lblIcon.setIcon(delegate.getIcon());
			lblName.setText(delegate.getName());
		}
		((CardLayout) getLayout()).show(this,
				Boolean.toString(delegate != null));
	}

}
