package rmc.window.component;

import javax.swing.table.DefaultTableCellRenderer;

public class DoubleIntoPercentRenderer extends DefaultTableCellRenderer {

	public DoubleIntoPercentRenderer() {
		super();
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
			setText(String.format("%.2f%%", (doubleValue * 100)));
		}
	}
}