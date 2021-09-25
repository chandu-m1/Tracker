package com.tracker.services;


import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tracker.models.LocationStats;

@Service
public class CoronaDataService {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> allStats = new ArrayList<>();
	@PostConstruct
	@Scheduled(cron = "1 * * * * *")
	public void fetchVirusData() throws IOException, InterruptedException
	{
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder() 
								.uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		//System.out.println(response.body());
		StringReader reader = new StringReader(response.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
		for (CSVRecord record : records) {
			LocationStats locationStats = new LocationStats();
		    locationStats.setState(record.get("Province/State"));
		    locationStats.setCountry(record.get("Country/Region"));
		    locationStats.setLatestCases(Integer.parseInt(record.get(record.size()-1)));
		    newStats.add(locationStats);
		}
		allStats = newStats;
	}
	public List<LocationStats> getAllStats() {
		return allStats;
	}
	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}
}
