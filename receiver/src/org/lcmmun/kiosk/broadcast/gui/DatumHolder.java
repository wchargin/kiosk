
package org.lcmmun.kiosk.broadcast.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

public class DatumHolder extends JPanel {

	private static final Color INVISIBLE = new Color(0, 0, 0, 0);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The panel containing the various {@code DatumView}s.
	 */
	private JPanel panel;

	/**
	 * Creates the {@code DatumHolder}.
	 */
	public DatumHolder() {
		super(new BorderLayout());
		setOpaque(false);
		setBackground(INVISIBLE);

		panel = new JPanel(new MigLayout(new LC().flowY()));
		panel.setOpaque(false);
		panel.setBackground(INVISIBLE);
		add(panel, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.setBackground(INVISIBLE);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		scrollPane.getViewport().setOpaque(false);
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(INVISIBLE);

		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Adds the specified datum view to the bottom of the list.
	 * 
	 * @param datumView
	 *            the view to add
	 */
	public void addDatumView(DatumView datumView) {
		panel.add(datumView, new CC().grow().pushX().alignX("right")); //$NON-NLS-1$
		panel.revalidate();
		panel.repaint();
	}

	/**
	 * Removes the specified datum view from the list.
	 * 
	 * @param datumView
	 *            the view to remove
	 */
	public void removeDatumView(DatumView datumView) {
		panel.remove(datumView);
		panel.revalidate();
		panel.repaint();
	}

}
