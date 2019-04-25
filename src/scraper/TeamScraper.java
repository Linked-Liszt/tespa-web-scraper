package scraper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TeamScraper {
	
	
	private String ConnectURL;
	private Elements TeamEles;
	
	public TeamScraper(String ConnectUrlIn) {
		ConnectURL = ConnectUrlIn;
		getTeamElements();
	}
	
	
	public void getTeamElements() {
		Document doc;
		try {
			doc = Jsoup.connect(ConnectURL).get();
			TeamEles = doc.getElementsByClass("match-detail__team1");
			TeamEles.addAll(doc.getElementsByClass("match-detail__team2")); 
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public boolean hasTeam(String Target) {
		Target = Target.toLowerCase();
		
		if(TeamEles.size() > 0) {
			for(Element Ele: TeamEles) {
				if (Ele.text().toLowerCase().contains(Target)) return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<String> getTeams() {
		ArrayList<String> Teams = new ArrayList<String>();
		if(TeamEles.size() > 0) {
			for(Element Ele: TeamEles) {
				if (!Teams.contains(Ele.text())) {
					Teams.add(Ele.text());
				}
			}
		}
		return Teams;
	}
	
	public String createOutput() {
		String output = "";
		
		ArrayList<String> Teams = getTeams();
		for(String team : Teams) {
			output = output + team + "\n";
		}
		
		output = output + ConnectURL;
		return output;
		
	}
}
