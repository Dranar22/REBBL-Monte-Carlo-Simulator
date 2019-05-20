package rmc.window;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

import rmc.data.Schedule;
import rmc.data.broker.RebblNetBroker;

public class RebblNetSelectionDialog extends JDialog {

	private static final Vector<String> LOADING = new Vector<String>(Arrays.asList("Loading..."));
	private static final String SELECT_HEADER = "Select...";

	private JComboBox<String> leagueBox;
	private JComboBox<String> seasonBox;
	private JComboBox<String> divisionBox;

	private JButton downloadButton;

	private Map<String, Vector<String>> leagueMap = new HashMap<String, Vector<String>>();
	private Map<String, Vector<String>> leagueSeasonMap = new HashMap<String, Vector<String>>();

	private Schedule downloadedSchedule;

	private boolean saveSelected = false;
	private String divisionName = null;

	public RebblNetSelectionDialog(JFrame parent) {
		super(parent, "rebbl.net Selector", true);

		Vector<String> leagues;
		try {
			leagues = RebblNetBroker.getLeagues();
		}
		catch (IOException e2) {
			JOptionPane.showMessageDialog(null, "Error connecting to rebbl.net!\n\n" + e2.getMessage(),
					"Connection Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		leagues.add(0, SELECT_HEADER);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));

		JLabel leagueLabel = new JLabel("League:");
		Box leagueLabelBox = Box.createHorizontalBox();
		leagueLabelBox.add(Box.createHorizontalStrut(5));
		leagueLabelBox.add(leagueLabel);
		leagueLabelBox.add(Box.createHorizontalGlue());
		selectionPanel.add(leagueLabelBox);
		leagueBox = new JComboBox<String>(leagues);
		leagueBox.setSelectedIndex(0);
		leagueBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				downloadButton.setEnabled(false);

				if (leagueBox.getSelectedIndex() == 0) {
					seasonBox.setModel(new DefaultComboBoxModel<>());
					divisionBox.setModel(new DefaultComboBoxModel<>());
					return;
				}

				String selectedLeague = leagueBox.getSelectedItem().toString();

				if (!leagueMap.containsKey(selectedLeague)) {
					RebblNetSelectionDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					seasonBox.setModel(new DefaultComboBoxModel<String>(LOADING));
					RebblNetSelectionDialog.this.repaint();

					Vector<String> seasons;
					try {
						seasons = RebblNetBroker.getSeasons(selectedLeague);
						seasons.add(0, SELECT_HEADER);

						leagueMap.put(selectedLeague, seasons);
					}
					catch (IOException e1) {
						JOptionPane.showMessageDialog(RebblNetSelectionDialog.this,
								"Error connecting to rebbl.net!\n\n" + e1.getMessage(), "Connection Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					finally {
						RebblNetSelectionDialog.this.setCursor(Cursor.getDefaultCursor());
					}

				}

				seasonBox.setModel(new DefaultComboBoxModel<String>(leagueMap.get(selectedLeague)));
				seasonBox.setSelectedIndex(0);

				divisionBox.setModel(new DefaultComboBoxModel<>());
			}
		});

		selectionPanel.add(leagueBox);
		selectionPanel.add(Box.createVerticalStrut(10));

		JLabel seasonLabel = new JLabel("Season:");
		Box seasonLabelBox = Box.createHorizontalBox();
		seasonLabelBox.add(Box.createHorizontalStrut(5));
		seasonLabelBox.add(seasonLabel);
		seasonLabelBox.add(Box.createHorizontalGlue());
		selectionPanel.add(seasonLabelBox);
		seasonBox = new JComboBox<String>();
		seasonBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				downloadButton.setEnabled(false);

				if (seasonBox.getSelectedIndex() == 0) {
					divisionBox.setModel(new DefaultComboBoxModel<>());
					return;
				}

				String selectedLeague = leagueBox.getSelectedItem().toString();
				String selectedSeason = seasonBox.getSelectedItem().toString();

				String leagueSeasonKey = selectedLeague + selectedSeason;

				if (!leagueSeasonMap.containsKey(leagueSeasonKey)) {
					RebblNetSelectionDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					divisionBox.setModel(new DefaultComboBoxModel<String>(LOADING));
					RebblNetSelectionDialog.this.repaint();

					Vector<String> divisions;
					try {
						divisions = RebblNetBroker.getDivisions(selectedLeague, selectedSeason);
						divisions.add(0, SELECT_HEADER);

						leagueSeasonMap.put(leagueSeasonKey, divisions);
					}
					catch (IOException e1) {
						JOptionPane.showMessageDialog(RebblNetSelectionDialog.this,
								"Error connecting to rebbl.net!\n\n" + e1.getMessage(), "Connection Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					finally {
						RebblNetSelectionDialog.this.setCursor(Cursor.getDefaultCursor());
					}

				}

				divisionBox.setModel(new DefaultComboBoxModel<String>(leagueSeasonMap.get(leagueSeasonKey)));
				divisionBox.setSelectedIndex(0);
			}
		});

		selectionPanel.add(seasonBox);
		selectionPanel.add(Box.createVerticalStrut(10));

		JLabel divisionLabel = new JLabel("Division:");
		Box divisionLabelBox = Box.createHorizontalBox();
		divisionLabelBox.add(Box.createHorizontalStrut(5));
		divisionLabelBox.add(divisionLabel);
		divisionLabelBox.add(Box.createHorizontalGlue());
		selectionPanel.add(divisionLabelBox);
		divisionBox = new JComboBox<String>();
		divisionBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (divisionBox.getSelectedIndex() == 0) {
					downloadButton.setEnabled(false);
				}
				else {
					downloadButton.setEnabled(true);
				}

			}
		});

		selectionPanel.add(divisionBox);
		selectionPanel.add(Box.createVerticalStrut(10));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		buttonPanel.add(Box.createHorizontalGlue());

		downloadButton = new JButton("Download");
		downloadButton.setAction(new AbstractAction("Download") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (divisionBox.getSelectedIndex() == 0) {
					JOptionPane.showMessageDialog(RebblNetSelectionDialog.this,
							"You must select a league, season, and division\nbefore downloading a schedule!",
							"Unfinished Selections", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String selectedLeague = leagueBox.getSelectedItem().toString();
				String selectedSeason = seasonBox.getSelectedItem().toString();
				String selectedDivision = divisionBox.getSelectedItem().toString();

				try {
					downloadedSchedule = RebblNetBroker.getSchedule(selectedLeague, selectedSeason, selectedDivision);
				}
				catch (IOException e1) {
					JOptionPane.showMessageDialog(RebblNetSelectionDialog.this,
							"Error connecting to rebbl.net!\n\n" + e1.getMessage(), "Connection Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				divisionName = selectedDivision;
				saveSelected = true;

				RebblNetSelectionDialog.this.dispose();
			}
		});
		downloadButton.setEnabled(false);
		buttonPanel.add(downloadButton);

		buttonPanel.add(Box.createHorizontalStrut(50));

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setAction(new AbstractAction("Cancel") {

			@Override
			public void actionPerformed(ActionEvent e) {
				RebblNetSelectionDialog.this.dispose();
			}
		});
		buttonPanel.add(cancelButton);

		buttonPanel.add(Box.createHorizontalGlue());

		contentPane.add(selectionPanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(contentPane);
		pack();

		setSize(getWidth() + 20, getHeight());

		setLocationRelativeTo(parent);
	}

	public Schedule getDownloadedSchedule() {
		return downloadedSchedule;
	}

	public boolean wasSaveSelected() {
		return saveSelected;
	}

	public String getDivisionName() {
		return divisionName;
	}
}