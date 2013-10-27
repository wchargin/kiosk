package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.lcmmun.kiosk.Delegate;

/**
 * This and related classes adapted from <a href=
 * "http://www.java2s.com/Code/Java/Swing-Components/AutocompleteComboBox.htm"
 * >the Java2S website</a>.
 * 
 * @author Sun Microsystems
 * @author William Chargin
 * 
 */
class AutoDelegateComboBox extends JComboBox {
	
	/**
	 * The list of delegates.
	 */
	private final List<Delegate> list;
	
	class AutoTextFieldEditor extends BasicComboBoxEditor {
		private final ImageIcon NO_ICON = new ImageIcon(new BufferedImage(
				24, 24, BufferedImage.TYPE_INT_ARGB));
		private JLabel icon = new JLabel(NO_ICON);
		private JPanel pnl = new JPanel(new BorderLayout());

		AutoTextFieldEditor(List<Delegate> list) {
			editor = new AutoDelegateTextField(list,
					AutoDelegateComboBox.this);
			pnl.add(editor);
			pnl.add(icon, BorderLayout.WEST);
		}

		@Override
		public Component getEditorComponent() {
			return pnl;
		}

		public void setIcon(Delegate d) {
			icon.setIcon(d == null ? NO_ICON : d.getSmallIcon());
		}

		private AutoDelegateTextField getAutoTextFieldEditor() {
			return (AutoDelegateTextField) editor;
		}
	}

	class AutoDelegateTextField extends JTextField {
		class AutoDocument extends PlainDocument {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void insertString(int i, String s,
					AttributeSet attributeset) throws BadLocationException {
				if (s == null || s.isEmpty())
					return;
				String s1 = getText(0, i);
				String s2 = getMatch(s1 + s);
				int j = (i + s.length()) - 1;
				if (s2 == null) {
					s2 = getMatch(s1);
					j--;
				}
				if (autoComboBox != null && s2 != null) {
					String name = s2;
					for (Delegate delegate : list) {
						if (name.equalsIgnoreCase(delegate.getName())) {
							if (autoComboBox.getSelectedItem() == delegate) {
								break;
							}
							autoComboBox.setSelectedValue(delegate);
							break;
						}
					}
				}
				super.remove(0, getLength());
				super.insertString(0, s2, attributeset);
				setSelectionStart(j + 1);
				setSelectionEnd(getLength());
			}

			public void remove(int i, int j) throws BadLocationException {
				int k = getSelectionStart();
				if (k > 0)
					k--;
				String s = getMatch(getText(0, k));
				if (s == null) {
					super.remove(i, j);
				} else {
					super.remove(0, getLength());
					super.insertString(0, s, null);
				}
				if (autoComboBox != null && s != null) {
					String name = s;
					for (Delegate delegate : list) {
						if (name.equalsIgnoreCase(delegate.getName())) {
							if (autoComboBox.getSelectedItem() == delegate) {
								break;
							}
							autoComboBox.setSelectedValue(delegate);
							break;
						}
					}
				}
				try {
					setSelectionStart(k);
					setSelectionEnd(getLength());
				} catch (Exception exception) {
				}
			}

			public void replace(int i, int j, String s,
					AttributeSet attributeset) throws BadLocationException {
				super.remove(i, j);
				insertString(i, s, attributeset);
			}

		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private List<Delegate> dataList;

		private AutoDelegateComboBox autoComboBox;

		AutoDelegateTextField(List<Delegate> list, AutoDelegateComboBox b) {
			autoComboBox = null;
			if (list == null) {
				throw new IllegalArgumentException();
			} else {
				dataList = list;
				autoComboBox = b;
				init();
			}
			addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					selectAll();
				}

				@Override
				public void focusLost(FocusEvent e) {
					select(0, 0);
				}
			});
		}

		public List<Delegate> getDataList() {
			return dataList;
		}

		private String getMatch(String s) {
			for (int i = 0; i < dataList.size(); i++) {
				String s1 = dataList.get(i).toString();
				if (s1 != null) {
					if (!false
							&& s1.toLowerCase().startsWith(s.toLowerCase()))
						return s1;
				}
			}

			return null;
		}

		private void init() {
			setDocument(new AutoDocument());
			if (dataList.size() > 0)
				setText(dataList.get(0).toString());
		}

		public void replaceSelection(String s) {
			AutoDocument lb = (AutoDocument) getDocument();
			if (lb != null) {
				try {
					int i = Math.min(getCaret().getDot(), getCaret()
							.getMark());
					int j = Math.max(getCaret().getDot(), getCaret()
							.getMark());
					lb.replace(i, j - i, s, null);
				} catch (Exception exception) {
				}
			}
		}

		public void setDataList(List<Delegate> list) {
			if (list == null) {
				throw new IllegalArgumentException();
			} else {
				dataList = list;
				return;
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AutoTextFieldEditor autoTextFieldEditor;

	private boolean isFired;

	public AutoDelegateComboBox(List<Delegate> list) {
		isFired = false;
		autoTextFieldEditor = new AutoTextFieldEditor(list);
		setEditable(true);
		setRenderer(new DelegateRenderer());
		setModel(new DefaultComboBoxModel(list.toArray()) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void fireContentsChanged(Object obj, int i, int j) {
				if (!isFired)
					super.fireContentsChanged(obj, i, j);
			}

		});
		this.list = list;
		setEditor(autoTextFieldEditor);
	}

	protected void fireActionEvent() {
		if (!isFired)
			super.fireActionEvent();
	}

	public List<Delegate> getDataList() {
		return autoTextFieldEditor.getAutoTextFieldEditor().getDataList();
	}

	public void setDataList(List<Delegate> list) {
		autoTextFieldEditor.getAutoTextFieldEditor().setDataList(list);
		setModel(new DefaultComboBoxModel(list.toArray()));
	}

	void setSelectedValue(Delegate d) {
		if (isFired) {
			return;
		} else {
			isFired = true;
			setSelectedItem(d);
			fireItemStateChanged(new ItemEvent(this, 701,
					selectedItemReminder, 1));
			isFired = false;
			if (autoTextFieldEditor != null) {
				autoTextFieldEditor.setIcon(d);
			}
			return;
		}
	}

}