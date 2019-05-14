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
import rmc.window.component.RebblDivisionPredictionTable;

public class RebblDivisionPositionPredictor {
	private final static int DEFAULT_SIM_NUMBER = 100000;

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

		File scheduleFile = null;
		Map<String, ManualGamePrediction> predictionMap;
		RebblDivisionPredictionTable dataTable;

		JButton runButton;
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

			setSize(900, 490);

			setLocationRelativeTo(null);
		}

		private void addFileSelectorPanel() {
			JPanel fileSelectorPanel = new JPanel();

			fileSelectorPanel.setLayout(new BorderLayout(5, 5));
			fileSelectorPanel.setBorder(BorderFactory.createTitledBorder("Schedule File Selection"));

			fileNameField = new JTextField();
			fileNameField.setEditable(false);
			fileSelectorPanel.add(fileNameField, BorderLayout.CENTER);

			JButton fileSelectorButton = new JButton("Open...");
			fileSelectorButton.setAction(new AbstractAction("Open...") {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					int retValue = fc.showOpenDialog(SwingUtilities.getWindowAncestor(PredictorFrame.this));

					if (retValue == JFileChooser.APPROVE_OPTION) {
						scheduleFile = fc.getSelectedFile();
						if (scheduleFile != null) {
							fileNameField.setText(scheduleFile.getAbsolutePath());

							CSVParser fixtureParser;
							try {
								fixtureParser = CSVParser.parse(scheduleFile, Charset.defaultCharset(),
										CSVFormat.EXCEL);
								Schedule initSchedule = new Schedule(fixtureParser);
								predictionMap = initSchedule.getManualPredictionMap();
								dataTable.setInitialSchedule(initSchedule);
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

			fileSelectorPanel.add(fileSelectorButton, BorderLayout.WEST);
			fileSelectorPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));

			getContentPane().add(fileSelectorPanel);

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

			runButton = new JButton("   Run   ");
			runButton.setAction(new AbstractAction("   Run   ") {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (scheduleFile == null) {
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

					SwingWorker<Void, Void> backgroundTask = new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {
							try {
								CSVParser fixtureParser = CSVParser.parse(scheduleFile, Charset.defaultCharset(),
										CSVFormat.EXCEL);
								Schedule baseSchedule = new Schedule(fixtureParser);

								List<Schedule> schedules = new ArrayList<Schedule>();
								for (int i = 0; i < simNum; i++) {
									Schedule simSchedule = (Schedule) baseSchedule.clone();
									simSchedule.fillMissingScores(engine);
									schedules.add(simSchedule);
								}

								dataTable.setScheduleData(schedules, playoffNum, challengerNum);
							}
							catch (IOException e1) {
								JOptionPane.showMessageDialog(PredictorFrame.this, "Error parsing file!",
										"Parsing Error", JOptionPane.ERROR_MESSAGE);
								fileNameField.setText("Error Parsing File");
								scheduleFile = null;
								e1.printStackTrace();
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
												+ "  -If the team names in the table have odd characters in them, open the csv file in Notepad or Wordpad and re-save it.",
										"Too Many Teams!", JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							}

							return null;
						}
					};

					progressBar.setString("Running...");
					progressBar.setStringPainted(true);
					progressBar.setIndeterminate(true);
					runButton.setEnabled(false);
					PredictorFrame.this.repaint();
					try {
						backgroundTask.run();
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(PredictorFrame.this,
								"This shouldn't happen!\n\nTell Dranar the exception was a "
										+ e1.getClass().getSimpleName(),
								"Oops", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					finally {
						runButton.setEnabled(true);
						progressBar.setStringPainted(false);
						progressBar.setIndeterminate(false);
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