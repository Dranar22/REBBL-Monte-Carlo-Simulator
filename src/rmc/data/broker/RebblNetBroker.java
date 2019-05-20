package rmc.data.broker;

import java.io.IOException;
import java.util.Comparator;
import java.util.Vector;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.common.net.UrlEscapers;

import rmc.data.Schedule;

public class RebblNetBroker {

	public static final String REBBL_NET_BASE = "https://rebbl.net/api/v2/";

	public static Vector<String> getLeagues() throws IOException {
		String urlString = REBBL_NET_BASE + "league/";

		Content content = Request.Get(urlString).execute().returnContent();

		JsonArray leagueJson = Jsoner.deserialize(content.asString(), new JsonArray());

		Vector<String> leagueNames = new Vector<String>();

		for (int x = 0; x < leagueJson.size(); x++) {
			leagueNames.add(leagueJson.getString(x));
		}

		return leagueNames;
	}

	public static Vector<String> getSeasons(String leagueName) throws IOException {

		String urlString = UrlEscapers.urlFragmentEscaper()
				.escape(REBBL_NET_BASE + "league/" + leagueName + "/seasons/");

		Content content = Request.Get(urlString).execute().returnContent();

		JsonArray seasonJson = Jsoner.deserialize(content.asString(), new JsonArray());

		Vector<String> seasonNames = new Vector<String>();

		for (int x = 0; x < seasonJson.size(); x++) {
			seasonNames.add(seasonJson.getString(x));
		}

		seasonNames.sort(new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				Integer i1 = Integer.valueOf(s1.replaceAll("\\D", ""));
				Integer i2 = Integer.valueOf(s2.replaceAll("\\D", ""));
				return -(i1.compareTo(i2));
			}
		});

		return seasonNames;
	}

	public static Vector<String> getDivisions(String leagueName, String seasonName) throws IOException {

		String urlString = UrlEscapers.urlFragmentEscaper()
				.escape(REBBL_NET_BASE + "division/" + leagueName + "/" + seasonName + "/");

		Content content = Request.Get(urlString).execute().returnContent();

		JsonArray divisionJson = Jsoner.deserialize(content.asString(), new JsonArray());

		Vector<String> divisionNames = new Vector<String>();

		for (int x = 0; x < divisionJson.size(); x++) {
			divisionNames.add(divisionJson.getString(x));
		}

		divisionNames.sort(new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				Integer i1 = Integer.valueOf(s1.replaceAll("\\D", ""));
				Integer i2 = Integer.valueOf(s2.replaceAll("\\D", ""));

				int compare = i1.compareTo(i2);

				if (compare == 0) {
					String divLetter1 = s1.substring(s1.length() - 1, s1.length());
					String divLetter2 = s2.substring(s2.length() - 1, s2.length());
					return divLetter1.compareToIgnoreCase(divLetter2);
				}
				else {
					return compare;
				}
			}
		});

		return divisionNames;
	}

	public static Schedule getSchedule(String leagueName, String seasonName, String divisionName) throws IOException {

		String urlString = UrlEscapers.urlFragmentEscaper()
				.escape(REBBL_NET_BASE + "division/" + leagueName + "/" + seasonName + "/" + divisionName + "/slim/");

		Content content = Request.Get(urlString).execute().returnContent();

		JsonArray scheduleJson = Jsoner.deserialize(content.asString(), new JsonArray());

		Schedule schedule = new Schedule(scheduleJson);

		return schedule;
	}
}