package rmc.window.component;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

public class ColorShiftingRenderer extends DefaultTableCellRenderer {

	private static Color GREEN = new Color(175, 255, 175);
	private static Color RED = new Color(255, 220, 220);
	private static Color YELLOW = new Color(255, 255, 210);

	public ColorShiftingRenderer() {
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

			int red, green, blue;
			if (doubleValue < 0.4) {

				double ratio = doubleValue * 2.5;

				red = (int) Math.abs((ratio * YELLOW.getRed()) + ((1 - ratio) * RED.getRed()));
				green = (int) Math.abs((ratio * YELLOW.getGreen()) + ((1 - ratio) * RED.getGreen()));
				blue = (int) Math.abs((ratio * YELLOW.getBlue()) + ((1 - ratio) * RED.getBlue()));
			}
			else if (doubleValue > 0.6) {
				double ratio = (doubleValue - 0.6) * 2.5;

				red = (int) Math.abs((ratio * GREEN.getRed()) + ((1 - ratio) * YELLOW.getRed()));
				green = (int) Math.abs((ratio * GREEN.getGreen()) + ((1 - ratio) * YELLOW.getGreen()));
				blue = (int) Math.abs((ratio * GREEN.getBlue()) + ((1 - ratio) * YELLOW.getBlue()));
			}
			else {
				red = YELLOW.getRed();
				green = YELLOW.getGreen();
				blue = YELLOW.getBlue();
			}

			setBackground(new Color(red, green, blue));
			setText(String.format("%.1f%%", (doubleValue * 100)));
		}
	}
}