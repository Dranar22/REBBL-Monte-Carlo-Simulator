package rmc.window.component;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

public class WholeDoubleIntoPercentRenderer extends DefaultTableCellRenderer {

	public WholeDoubleIntoPercentRenderer(Color bgColor) {
		super();
		setBackground(bgColor);
	}

	public void setValue(Object value) {

		if (value == null) {
			setText("");
		}
		else {
			Double doubleValue;

			try {
				doubleValue = Double.valueOf(value.toString());
			}
			catch (Exception e) {
				doubleValue = 0.0;
			}
			setText(String.format("%.2f%%", doubleValue));
		}
	}
}