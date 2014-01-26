package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.WorkingPaper;
import org.lcmmun.kiosk.gui.events.EditingFinishedEvent;
import org.lcmmun.kiosk.gui.events.EditingFinishedListener;
import org.lcmmun.kiosk.resources.ImageFetcher;
import org.lcmmun.kiosk.resources.ImageType;

/**
 * An editor for multiple {@code WorkingPaper}s (that is, working papers and/or
 * draft resolutions).
 * 
 * @author William Chargin
 * 
 */
public class MultiWPREditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list model.
	 */
	private final DefaultListModel listModel = new DefaultListModel();

	/**
	 * The list allowing for selection of working papers.
	 */
	private final JList list = new JList(listModel);

	/**
	 * The card layout.
	 */
	private final CardLayout cardLayout = new CardLayout();

	/**
	 * The panel containing the various cards.
	 */
	private final JPanel deck = new JPanel(cardLayout);

	/**
	 * Creates the editor with the given list of papers.
	 * 
	 * @param papers
	 *            the list of papers
	 */
	public MultiWPREditor(final Collection<WorkingPaper> papers,
			Committee committee) {
		super(new BorderLayout());

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);
		splitPane.setEnabled(false);

		for (WorkingPaper paper : papers) {
			listModel.addElement(paper);
		}

		list.setCellRenderer(new WorkingPaperRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(5);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!SwingUtilities.isRightMouseButton(e)) {
					return;
				}
				final int index = list.locationToIndex(e.getPoint());
				if (index == -1) {
					return;
				}
				Object obj = listModel.getElementAt(index);
				if (!(obj instanceof WorkingPaper)) {
					return;
				}
				final WorkingPaper wp = (WorkingPaper) obj;
				JPopupMenu pop = new JPopupMenu();
				JMenuItem miDelete = new JMenuItem(
						Messages.getString("MultiWPREditor.PmiDeletePaper"), ImageFetcher //$NON-NLS-1$
								.fetchImageIcon(ImageType.DELETE));
				miDelete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (JOptionPane.showConfirmDialog(
								SwingUtilities.getWindowAncestor(list),
								String.format(
										Messages.getString("MultiWPREditor.DeletePaperPrompt"), //$NON-NLS-1$
										wp.getIdentifier()),
								Messages.getString("MultiWPREditor.DeletePaperTitle"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { //$NON-NLS-1$
							listModel.removeElement(wp);
							papers.remove(wp);
							list.clearSelection();
						}
					}
				});
				pop.add(miDelete);
				pop.show(list, e.getX(), e.getY());
			}
		});

		splitPane.setLeftComponent(new JScrollPane(list));

		splitPane.setRightComponent(deck);

		deck.add(new JPanel(), Integer.toString(-1));
		{
			final ArrayList<WorkingPaper> papersList = new ArrayList<WorkingPaper>(
					papers);
			for (int i = 0; i < papersList.size(); i++) {
				WorkingPaper paper = papersList.get(i);
				final WorkingPaperEditor wpe = new WorkingPaperEditor(paper,
						committee);
				wpe.addEditingFinishedListener(new EditingFinishedListener() {
					@Override
					public void editingFinished(EditingFinishedEvent efe) {
						WorkingPaper wp = wpe.getWorkingPaper();
						if (wp != null) {
							list.repaint();
						}
						list.clearSelection();
						list.setEnabled(true);
					}
				});
				deck.add(wpe, Integer.toString(i));
			}
		}
		cardLayout.first(deck);

		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				if (lse.getValueIsAdjusting()) {
					return;
				}
				final int selectedIndex = list.getSelectedIndex();
				if (!list.isSelectionEmpty()) {
					Component selected = null;
					for (Component component : deck.getComponents()) {
						if (component.isVisible()) {
							selected = component;
						}
					}
					if (selected instanceof WorkingPaperEditor) {
						// We're switching *from* an editor.
						WorkingPaperEditor wpe = (WorkingPaperEditor) selected;
						if (wpe.isModified()) {
							int ret = JOptionPane.showConfirmDialog(
									MultiWPREditor.this,
									Messages.getString("MultiWPREditor.SaveChangesPrompt"), //$NON-NLS-1$
									Messages.getString("MultiWPREditor.SaveChangesTitle"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
									JOptionPane.QUESTION_MESSAGE);
							switch (ret) {
							case JOptionPane.YES_OPTION:
								wpe.saveChanges();
								break;
							case JOptionPane.NO_OPTION:
							default:
								wpe.discardChanges();
								break;
							}
							list.setSelectedIndex(selectedIndex);
							// Reason: wpe.[save/discard]Changes() will cause
							// selection of
							// list to be cleared.
						}
					}
				}
				cardLayout.show(deck, Integer.toString(selectedIndex));
				list.repaint();
			}
		});
	}
}
