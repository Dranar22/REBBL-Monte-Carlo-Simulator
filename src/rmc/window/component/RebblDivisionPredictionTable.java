package rmc.window.component;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import rmc.data.Schedule;
import rmc.exception.TooManyTeamsException;

public class RebblDivisionPredictionTable extends JTable {

	private final static String[] COLUMN_NAMES = { "Team Name", "PO%", "CC%", "1st", "2nd", "3rd", "4th", "5th", "6th",
			"7th", "8th", "9th", "10th", "11th", "12th", "13th", "14th" };

	public RebblDivisionPredictionTable() {
		super(new PredictionTableModel());

		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(true);

		getTableHeader().setOpaque(true);
		getTableHeader().setBackground(getTableHeader().getBackground().darker().darker().darker());
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 0) {
			return super.getCellRenderer(row, column);
		}
		else if (column < 3) {
			return new DoubleIntoPercentRenderer();
		}
		else {
			return new ColorShiftingRenderer();
		}
	}

	public void setInitialSchedule(Schedule initSchedule) {
		((PredictionTableModel) getModel()).setInitialSchedule(initSchedule);
	}

	public void setScheduleData(List<Schedule> scheduleData, int playoffNum, int challengerNum)
			throws TooManyTeamsException {
		((PredictionTableModel) getModel()).setScheduleData(scheduleData, playoffNum, challengerNum);
	}

	public static class PredictionTableModel extends AbstractTableModel {

		int gPlayoffSpots = 0;
		int gChallengerSpots = 0;

		private Object[][] gData = new Object[14][17];

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		public void setInitialSchedule(Schedule initSchedule) {
			List<String> teamNames = initSchedule.getTeamNames();
			for (int x = 0; x < teamNames.size() && x < 14; x++) {
				gData[x][0] = teamNames.get(x);
			}

			fireTableDataChanged();
		}

		public void setScheduleData(List<Schedule> scheduleData, int playoffNum, int challengerNum)
				throws TooManyTeamsException {
			clearPredictionData();
			int simNumber = scheduleData.size();

			for (Schedule schedule : scheduleData) {
				List<String> teamNames = schedule.getTeamNames();

				if (teamNames.size() > 14) {
					throw new TooManyTeamsException();
				}

				List<String> ordering = schedule.getTopTeams(teamNames.size());

				for (int x = 0; x < teamNames.size(); x++) {
					int y = 3 + ordering.indexOf(teamNames.get(x));
					if (gData[x][y] == null) {
						gData[x][y] = 0;
					}
					String positionCount = gData[x][y].toString();
					gData[x][y] = Double.valueOf(positionCount) + 1;
				}
			}

			for (int x = 0; x < getRowCount(); x++) {
				for (int y = 3; y < getColumnCount(); y++) {
					if (gData[x][y] == null) {
						gData[x][y] = 0.0;
					}
					else {
						gData[x][y] = (double) gData[x][y] / simNumber;
					}
				}

				double playoffChanceSum = 0.0;

				for (int y = 3; y < 3 + playoffNum; y++) {
					playoffChanceSum += Double.valueOf(gData[x][y].toString());
				}
				gData[x][1] = playoffChanceSum;

				double challengerChanceSum = 0.0;

				for (int y = 3 + playoffNum; y < 3 + challengerNum + playoffNum; y++) {
					challengerChanceSum += Double.valueOf(gData[x][y].toString());
				}
				gData[x][2] = challengerChanceSum;
			}

			fireTableDataChanged();
		}

		private void clearPredictionData() {
			for (int x = 0; x < getRowCount(); x++) {
				for (int y = 1; y < getColumnCount(); y++) {
					gData[x][y] = 0.0;
				}
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public int getRowCount() {
			return 14;
		}

		@Override
		public int getColumnCount() {
			return 17;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return String.class;
			}
			else {
				return Double.class;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return gData[rowIndex][columnIndex];
		}

	}
}