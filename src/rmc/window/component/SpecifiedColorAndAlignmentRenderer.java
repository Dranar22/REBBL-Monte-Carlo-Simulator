package rmc.window.component;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

public class SpecifiedColorAndAlignmentRenderer extends DefaultTableCellRenderer {

	public SpecifiedColorAndAlignmentRenderer(Color bgColor, int alignment) {
		super();
		setBackground(bgColor);
		setHorizontalAlignment(alignment);
	}
}