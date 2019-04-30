package rmc.window.component;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import rmc.data.ManualGamePrediction;

public class TselfManualPredictionTable extends JTable {

	private final static String[] COLUMN_NAMES = { "Home Team", "Home Win %", "Tie %", "Away Win %", "Away Team" };

	public TselfManualPredictionTable(List<ManualGamePrediction> predictions) {
		super(new TselfManualPredictionTableModel(predictions));

		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(true);

		getTableHeader().setOpaque(true);
		getTableHeader().setBackground(getTableHeader().getBackground().darker().darker().darker());
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		switch (column) {
			case 0:
				return new SpecifiedColorAndAlignmentRenderer(Color.LIGHT_GRAY,
						SpecifiedColorAndAlignmentRenderer.RIGHT);
			case 1:
				return new WholeDoubleIntoPercentRenderer(UIManager.getColor("Table.background"));
			case 2:
				return new WholeDoubleIntoPercentRenderer(Color.LIGHT_GRAY);
			case 3:
				return new WholeDoubleIntoPercentRenderer(UIManager.getColor("Table.background"));
			case 4:
				return new SpecifiedColorAndAlignmentRenderer(Color.LIGHT_GRAY,
						SpecifiedColorAndAlignmentRenderer.LEFT);
			default:
				return super.getCellRenderer(row, column);
		}

	}

	public static class TselfManualPredictionTableModel extends AbstractTableModel {

		int gPlayoffSpots = 0;
		int gChallengerSpots = 0;

		private List<ManualGamePrediction> gData;

		public TselfManualPredictionTableModel(List<ManualGamePrediction> predictions) {
			gData = predictions;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 1 || columnIndex == 3) {
				return true;
			}
			else {
				return false;
			}
		}

		@Override
		public int getRowCount() {
			return gData.size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0 || columnIndex == 4) {
				return String.class;
			}
			else {
				return Double.class;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			ManualGamePrediction prediction = gData.get(rowIndex);
			switch (columnIndex) {
				case 1:
					prediction.setChanceOne((double) aValue);
					break;
				case 3:
					prediction.setChanceTwo((double) aValue);
					break;
			}

			fireTableCellUpdated(rowIndex, 2);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ManualGamePrediction prediction = gData.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return prediction.getTeamOne();
				case 1:
					return prediction.getChanceOne();
				case 2:
					return 100.0 - prediction.getChanceOne() - prediction.getChanceTwo();
				case 3:
					return prediction.getChanceTwo();
				case 4:
					return prediction.getTeamTwo();
				default:
					return null;
			}
		}

	}
}