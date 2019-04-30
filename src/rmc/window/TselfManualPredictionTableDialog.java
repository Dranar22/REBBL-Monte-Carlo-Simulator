package rmc.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.table.TableColumn;

import rmc.data.ManualGamePrediction;
import rmc.window.component.TselfManualPredictionTable;

public class TselfManualPredictionTableDialog extends JDialog {

	List<ManualGamePrediction> gPredictions;

	public TselfManualPredictionTableDialog(JFrame parent, List<ManualGamePrediction> predictions) {
		super(parent, "Manual Predictions", true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		gPredictions = predictions;

		JPanel contentPane = new JPanel(new BorderLayout());

		TselfManualPredictionTable predictionTable = new TselfManualPredictionTable(predictions);

		for (int x = 0; x < predictionTable.getColumnCount(); x++) {
			TableColumn column = predictionTable.getColumnModel().getColumn(x);
			if (x == 0 || x == 4) {
				column.setPreferredWidth(220);
			}
			else {
				column.setPreferredWidth(95);
			}
		}

		JScrollPane centerPane = new JScrollPane();
		centerPane.setViewportView(predictionTable);
		contentPane.add(centerPane, BorderLayout.CENTER);

		JButton closeButton = new JButton(new AbstractAction("Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				TselfManualPredictionTableDialog.this.dispose();
			}
		});

		contentPane.add(closeButton, BorderLayout.SOUTH);

		setContentPane(contentPane);
		pack();

		setSize(getWidth() + 100, getHeight());

		setLocationRelativeTo(parent);
	}

}