package rmc.window.component;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class JCheckBoxList extends JList<JCheckBox> {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public JCheckBoxList() {
		setCellRenderer(new CellRenderer());
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index != -1) {
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public JCheckBoxList(ListModel<JCheckBox> model) {
		this();
		setModel(model);
	}

	protected class CellRenderer implements ListCellRenderer<JCheckBox> {
		public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index,
				boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = value;

			// Drawing checkbox, change the appearance here
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}

	public List<String> getSelectedItems() {
		List<String> selectedItems = new ArrayList<String>();

		for (int x = 0; x < getModel().getSize(); x++) {
			JCheckBox element = getModel().getElementAt(x);
			if (element.isSelected()) {
				selectedItems.add(element.getText());
			}
		}

		return selectedItems;
	}

	public static JCheckBoxList createCheckBoxList(List<String> elements, List<String> selectedElements) {
		DefaultListModel<JCheckBox> model = new DefaultListModel<JCheckBox>();
		for (String element : elements) {
			JCheckBox checkbox = new JCheckBox(element, selectedElements.contains(element));
			model.addElement(checkbox);
		}

		return new JCheckBoxList(model);
	}
}