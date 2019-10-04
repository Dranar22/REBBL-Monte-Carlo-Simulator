package rmc.window;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.TableColumn;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import rmc.data.ManualGamePrediction;
import rmc.data.Schedule;
import rmc.engines.*;
import rmc.exception.TooManyTeamsException;
import rmc.utils.ExportUtil;
import rmc.window.component.RebblDivisionPredictionTable;

public class RebblDivisionPositionPredictor {
	private final static int DEFAULT_SIM_NUMBER = 10000;

	private final static String ALL_WORLDS = "All";
	private final static String DRANAR_PREDICTION = "Dranar";
	private final static String TSELF_MANUAL = "tself";

	public static void main(String[] args) throws Exception {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	static void createAndShowGUI() throws Exception {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		PredictorFrame window = new PredictorFrame();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.setVisible(true);
	}

	public static class PredictorFrame extends JFrame {

		JTextField fileNameField;
		ButtonGroup engineSelection;
		JTextField simNumberField;
		JTextField playoffSpotsField;
		JTextField challengerCupField;

		JTextField rebblNetSelection;

		File scheduleFile = null;
		Schedule initSchedule = null;
		Map<String, ManualGamePrediction> predictionMap;
		List<String> byeWeeks = new ArrayList<String>();
		RebblDivisionPredictionTable dataTable;

		JButton runButton;
		JButton byeWeekButton;
		JProgressBar progressBar;

		public PredictorFrame() {
			super("ReBBL Division Predictor");

			URL iconURL = getClass().getResource("/R_Logo.png");
			// iconURL is null when not found
			ImageIcon icon = new ImageIcon(iconURL);
			setIconImage(icon.getImage());

			getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

			getContentPane().add(Box.createVerticalStrut(5));
			addFileSelectorPanel();
			getContentPane().add(Box.createVerticalStrut(5));
			addEngineSelectorPanel();
			getContentPane().add(Box.createVerticalStrut(5));
			addOptionsAndRunButtonPanel();
			addDisplayTable();
			addProgressBar();

			pack();

			setSize(900, 520);

			setLocationRelativeTo(null);
		}

		private void addFileSelectorPanel() {
			JPanel fileSelectorPanel = new JPanel();

			fileSelectorPanel.setLayout(new BoxLayout(fileSelectorPanel, BoxLayout.Y_AXIS));
			fileSelectorPanel.setBorder(BorderFactory.createTitledBorder("Schedule File Selection"));

			JPanel localSelectorPanel = new JPanel();
			localSelectorPanel.setLayout(new BorderLayout(5, 5));
			fileNameField = new JTextField();
			fileNameField.setEditable(false);
			localSelectorPanel.add(fileNameField, BorderLayout.CENTER);

			JButton fileSelectorButton = new JButton("Local...");
			fileSelectorButton.setAction(new AbstractAction("Local...") {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					int retValue = fc.showOpenDialog(SwingUtilities.getWindowAncestor(PredictorFrame.this));

					if (retValue == JFileChooser.APPROVE_OPTION) {
						scheduleFile = fc.getSelectedFile();
						if (scheduleFile != null) {
							fileNameField.setText(scheduleFile.getAbsolutePath());
							rebblNetSelection.setText("");

							CSVParser fixtureParser;
							try {
								fixtureParser = CSVParser.parse(scheduleFile, Charset.defaultCharset(),
										CSVFormat.EXCEL);
								initSchedule = new Schedule(fixtureParser);
								loadInitialSchedule();
							}
							catch (IOException e1) {
								JOptionPane.showMessageDialog(PredictorFrame.this, "Error parsing file!",
										"Parsing Error", JOptionPane.ERROR_MESSAGE);
								fileNameField.setText("Error Parsing File");
								scheduleFile = null;
								e1.printStackTrace();
							}
						}

					}

				}
			});

			localSelectorPanel.add(fileSelectorButton, BorderLayout.WEST);
			localSelectorPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));

			JPanel onlineSelectorPanel = new JPanel();
			onlineSelectorPanel.setLayout(new BorderLayout(5, 5));

			JButton onlineSelectorButton = new JButton("Online...");
			onlineSelectorButton.setAction(new AbstractAction("Online...") {

				@Override
				public void actionPerformed(ActionEvent e) {
					RebblNetSelectionDialog dialog = new RebblNetSelectionDialog(PredictorFrame.this);
					dialog.setVisible(true);

					if (dialog.wasSaveSelected()) {
						fileNameField.setText("");
						rebblNetSelection.setText(dialog.getDivisionName());
						initSchedule = dialog.getDownloadedSchedule();
						loadInitialSchedule();
					}
				}
			});

			JButton exportButton = new JButton("Export...");
			exportButton.setAction(new AbstractAction("Export...") {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (rebblNetSelection.getText() == null || rebblNetSelection.getText().isEmpty()) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"You must download a file from rebbl.net first.", "No File Downloaded",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File(rebblNetSelection.getText() + ".csv"));
					int retValue = fc.showSaveDialog(SwingUtilities.getWindowAncestor(PredictorFrame.this));

					if (retValue == JFileChooser.APPROVE_OPTION) {
						File saveFileLocation = fc.getSelectedFile();
						try {
							ExportUtil.writeScheduleFile(initSchedule, saveFileLocation);
						}
						catch (IOException e1) {
							JOptionPane.showMessageDialog(PredictorFrame.this,
									"Error exporting file!\n\n" + e1.getLocalizedMessage(), "Exporting Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}

				}
			});

			rebblNetSelection = new JTextField();
			rebblNetSelection.setEditable(false);

			onlineSelectorPanel.add(onlineSelectorButton, BorderLayout.WEST);
			onlineSelectorPanel.add(rebblNetSelection, BorderLayout.CENTER);
			onlineSelectorPanel.add(exportButton, BorderLayout.EAST);

			fileSelectorPanel.add(localSelectorPanel);
			fileSelectorPanel.add(Box.createVerticalStrut(5));
			fileSelectorPanel.add(onlineSelectorPanel);
			getContentPane().add(fileSelectorPanel);

		}

		protected void loadInitialSchedule() {
			predictionMap = initSchedule.getManualPredictionMap();
			dataTable.setInitialSchedule(initSchedule);

			List<String> teamNames = initSchedule.getTeamNames();
			for (String name : teamNames) {
				if (name.toLowerCase().startsWith("[admin]")) {
					byeWeeks.add(name);
				}
			}
		}

		private void addEngineSelectorPanel() {
			JPanel engineSelectorPanel = new JPanel();

			engineSelectorPanel.setLayout(new FlowLayout());
			engineSelectorPanel.setBorder(BorderFactory.createTitledBorder("Prediction Method"));

			engineSelection = new ButtonGroup();

			JRadioButton allWorlds = new JRadioButton("All Possible Worlds", true);
			allWorlds.setActionCommand(ALL_WORLDS);

			JRadioButton dranarPrediction = new JRadioButton("Dranar's Prediction Method");
			dranarPrediction.setActionCommand(DRANAR_PREDICTION);

			JRadioButton tselfManual = new JRadioButton("tself's Manual Probabilities");
			tselfManual.setActionCommand(TSELF_MANUAL);

			engineSelection.add(allWorlds);
			engineSelection.add(dranarPrediction);
			engineSelection.add(tselfManual);

			JButton specifyButton = new JButton("Specify...");
			specifyButton.setAction(new AbstractAction("Specify...") {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (scheduleFile == null || predictionMap == null) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"A schedule file must be loaded before making predictions!", "No file set",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					tselfManual.setSelected(true);

					TselfManualPredictionTableDialog dialog = new TselfManualPredictionTableDialog(PredictorFrame.this,
							new ArrayList<ManualGamePrediction>(predictionMap.values()));

					dialog.setVisible(true);
				}

			});

			engineSelectorPanel.add(allWorlds);
			engineSelectorPanel.add(dranarPrediction);
			engineSelectorPanel.add(tselfManual);
			engineSelectorPanel.add(specifyButton);

			engineSelectorPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));

			getContentPane().add(engineSelectorPanel);
		}

		private void addOptionsAndRunButtonPanel() {
			JPanel optionsAndRunPanel = new JPanel();

			optionsAndRunPanel.setLayout(new FlowLayout());
			optionsAndRunPanel.setBorder(BorderFactory.createTitledBorder("Running Options"));

			JLabel simNumberLabel = new JLabel("Number of Sims to Perform:");
			simNumberField = new JTextField(String.valueOf(DEFAULT_SIM_NUMBER));
			simNumberField.setHorizontalAlignment(JTextField.CENTER);
			simNumberLabel.setLabelFor(playoffSpotsField);

			JLabel playoffSpots = new JLabel("Number of Playoff Spots:");
			playoffSpotsField = new JTextField(2);
			playoffSpotsField.setText("0");
			playoffSpotsField.setHorizontalAlignment(JTextField.CENTER);
			playoffSpots.setLabelFor(playoffSpotsField);

			JLabel challengerCupSpots = new JLabel("Number of Challenger Cup Spots:");
			challengerCupField = new JTextField(2);
			challengerCupField.setText("0");
			challengerCupField.setHorizontalAlignment(JTextField.CENTER);
			challengerCupSpots.setLabelFor(challengerCupField);

			byeWeekButton = new JButton("Bye Weeks...");
			byeWeekButton.setAction(new AbstractAction("Bye Weeks...") {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (initSchedule == null) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"Please specify a schedule file before setting bye weeks!", "No file set",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					ByeWeekSelectionWindow dialog = new ByeWeekSelectionWindow(PredictorFrame.this,
							initSchedule.getTeamNames(), byeWeeks);
					dialog.setVisible(true);

					if (dialog.wasSaveSelected()) {
						byeWeeks.clear();
						byeWeeks.addAll(dialog.getSelectedTeams());
					}

				}

			});

			runButton = new JButton("   Run   ");
			runButton.setAction(new AbstractAction("   Run   ") {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (initSchedule == null) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"Please specify a schedule file before running!", "No file set",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					int simNum;
					try {
						String simNumberText = simNumberField.getText();
						if (simNumberText == null) {
							JOptionPane.showMessageDialog(PredictorFrame.this,
									"Please specify a number of Playoff spots!", "Settings error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						simNum = Integer.valueOf(simNumberText);
					}
					catch (NumberFormatException ne) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"Your value for Playoff Spots is not a number!", "Settings error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					int playoffNum;
					try {
						String playoffText = playoffSpotsField.getText();
						if (playoffText == null) {
							JOptionPane.showMessageDialog(PredictorFrame.this,
									"Please specify a number of Playoff spots!", "Settings error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						playoffNum = Integer.valueOf(playoffText);
					}
					catch (NumberFormatException ne) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"Your value for Playoff Spots is not a number!", "Settings error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					int challengerNum;
					try {
						String challengerText = challengerCupField.getText();
						if (challengerText == null) {
							JOptionPane.showMessageDialog(PredictorFrame.this,
									"Please specify a number of Challenger Cup spots!", "Settings error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						challengerNum = Integer.valueOf(challengerText);
					}
					catch (NumberFormatException ne) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"Your value for Challenger Cup Spots is not a number!", "Settings error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					AbstractMonteCarloEngine engine;

					switch (engineSelection.getSelection().getActionCommand()) {
						case ALL_WORLDS:
							engine = new AllRandomEngine();
							break;
						case DRANAR_PREDICTION:
							engine = new DranarPredictiveEngine();
							break;
						case TSELF_MANUAL:
							engine = new TselfManualPredictionEngine(predictionMap);
							break;
						default:
							engine = new AllRandomEngine();
							break;
					}

					engine.setByeWeeks(byeWeeks);

					SwingWorker<Void, Void> backgroundTask = new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {
							try {
								List<Schedule> schedules = new ArrayList<Schedule>();
								for (int i = 0; i < simNum; i++) {
									Schedule simSchedule = (Schedule) initSchedule.clone();
									simSchedule.fillMissingScores(engine);
									schedules.add(simSchedule);
								}

								dataTable.setScheduleData(schedules, playoffNum, challengerNum);
							}
							catch (CloneNotSupportedException e1) {
								JOptionPane.showMessageDialog(PredictorFrame.this,
										"Clone Not Supported\n\nTell Dranar about this!", "Oops",
										JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							}
							catch (TooManyTeamsException e) {
								JOptionPane.showMessageDialog(PredictorFrame.this,
										"Your schedule file has too many teams in it!\n"
												+ "This may be due to drops or name corruption when creating the schedule file.\n\n"
												+ "  -If your division had drops, the names of the dropped teams should be replaced with the new teams name.\n"
												+ "  -If the team names in the table have odd characters in them, open the csv file in Notepad or Wordpad and re-save it.\n\n"
												+ " For schedules that were downloaded from rebbl.net, you must export the file and do these changes manually.",
										"Too Many Teams!", JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							}

							return null;
						}
					};

					PredictorFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					progressBar.setString("Running...");
					progressBar.setStringPainted(true);
					progressBar.setIndeterminate(true);
					runButton.setEnabled(false);
					PredictorFrame.this.repaint();
					try {
						backgroundTask.execute();
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"This shouldn't happen!\n\nTell Dranar the exception was a "
										+ e1.getClass().getSimpleName() + "\n\nThis may be caused by too many sims.",
								"Oops", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					finally {
						runButton.setEnabled(true);
						progressBar.setStringPainted(false);
						progressBar.setIndeterminate(false);
						PredictorFrame.this.setCursor(Cursor.getDefaultCursor());
					}
				}
			});

			optionsAndRunPanel.add(simNumberLabel);
			optionsAndRunPanel.add(simNumberField);
			optionsAndRunPanel.add(Box.createHorizontalStrut(24));
			optionsAndRunPanel.add(playoffSpots);
			optionsAndRunPanel.add(playoffSpotsField);
			optionsAndRunPanel.add(Box.createHorizontalStrut(24));
			optionsAndRunPanel.add(challengerCupSpots);
			optionsAndRunPanel.add(challengerCupField);
			optionsAndRunPanel.add(Box.createHorizontalStrut(24));
			optionsAndRunPanel.add(byeWeekButton);
			optionsAndRunPanel.add(Box.createHorizontalStrut(24));
			optionsAndRunPanel.add(runButton);

			optionsAndRunPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
			getContentPane().add(optionsAndRunPanel);
		}

		private void addDisplayTable() {
			JScrollPane tablePane = new JScrollPane();

			dataTable = new RebblDivisionPredictionTable();

			for (int x = 0; x < dataTable.getColumnCount(); x++) {
				TableColumn column = dataTable.getColumnModel().getColumn(x);
				if (x == 0) {
					column.setPreferredWidth(220);
				}
				else if (x < 3) {
					column.setPreferredWidth(50);
				}
				else {
					column.setPreferredWidth(24);
				}
			}

			dataTable.setAutoCreateRowSorter(true);

			tablePane.setViewportView(dataTable);
			tablePane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
					BorderFactory.createLoweredBevelBorder()));

			getContentPane().add(tablePane);

		}

		private void addProgressBar() {
			progressBar = new JProgressBar();
			getContentPane().add(progressBar);

		}

	}
}