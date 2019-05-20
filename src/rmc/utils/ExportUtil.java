package rmc.utils;

import java.io.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import rmc.data.MatchResult;
import rmc.data.Schedule;

public class ExportUtil {

	public static void writeScheduleFile(Schedule schedule, File fileToWrite) throws IOException {
		try (FileWriter outputFile = new FileWriter(fileToWrite)) {
			try (CSVPrinter printer = new CSVPrinter(outputFile, CSVFormat.EXCEL)) {
				for (MatchResult match : schedule.getAllMatches()) {
					String teamOne = match.getTeamOne();
					String teamTwo = match.getTeamTwo();
					int scoreOne = match.getScoreOne();
					int scoreTwo = match.getScoreTwo();
					printer.printRecord(teamOne, scoreOne == -1 ? "" : scoreOne, scoreTwo == -1 ? "" : scoreTwo,
							teamTwo);
				}
				printer.close();
			}
		}
	}
}