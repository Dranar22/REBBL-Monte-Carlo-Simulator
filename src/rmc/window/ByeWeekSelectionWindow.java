package rmc.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.*;

import rmc.window.component.JCheckBoxList;

public class ByeWeekSelectionWindow extends JDialog {

	private boolean okSelected = false;
	private List<String> selectedTeams;

	public ByeWeekSelectionWindow(JFrame parent, List<String> teamNames, List<String> byeWeekTeams) {
		super(parent, "Bye Week Specification", true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

		JCheckBoxList list = JCheckBoxList.createCheckBoxList(teamNames, byeWeekTeams);
		list.setBorder(BorderFactory.createLoweredBevelBorder());

		listPanel.add(list);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		buttonPanel.add(Box.createHorizontalGlue());

		JButton okButton = new JButton("Ok");
		okButton.setAction(new AbstractAction("Ok") {

			@Override
			public void actionPerformed(ActionEvent e) {
				okSelected = true;
				selectedTeams = list.getSelectedItems();
				ByeWeekSelectionWindow.this.dispose();
			}
		});

		buttonPanel.add(okButton);

		buttonPanel.add(Box.createHorizontalStrut(50));

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setAction(new AbstractAction("Cancel") {

			@Override
			public void actionPerformed(ActionEvent e) {
				ByeWeekSelectionWindow.this.dispose();
			}
		});
		buttonPanel.add(cancelButton);

		buttonPanel.add(Box.createHorizontalGlue());

		okButton.setSize(cancelButton.getSize());

		contentPane.add(listPanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(contentPane);
		pack();

		setSize(getWidth() + 20, getHeight());

		setLocationRelativeTo(parent);
	}

	public boolean wasSaveSelected() {
		return okSelected;
	}

	public List<String> getSelectedTeams() {
		return selectedTeams;
	}
}