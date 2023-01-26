package com.businessobjects.samples;

import com.crystaldecisions.sdk.occa.infostore.*;
import com.crystaldecisions.sdk.plugin.desktop.common.IReportLogon;
import com.crystaldecisions.sdk.plugin.desktop.report.IReport;
import com.crystaldecisions.sdk.framework.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import java.io.File;

public class CrystalCredChanger {
	
	private String SERVER = "am-iclbod-a003";
	private String USERNAME = "Nikhil_Admin";
	private String PASSWORD = "Summer23!";
	private boolean usePort = true;
	
	public CrystalCredChanger(String server, String user, String pass) {
		SERVER = server;
		USERNAME = user;
		PASSWORD = pass;
	}
	
	public int changeReports() throws Exception {
		// Create a new session on the server
		ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();		
		IEnterpriseSession enterprise = sessionMgr.logon(USERNAME,PASSWORD, getServer(6400),"secEnterprise");
		IInfoStore iStore = (IInfoStore) enterprise.getService("InfoStore");
		// SQL Query to get all Crystal Reports that exist on the server 
		IInfoObjects infoObjects = iStore.query(
				// Query to get all crystal reports on the server that use a Staging database
//				"SELECT * FROM CI_INFOOBJECTS WHERE SI_KIND='CRYSTALREPORT' AND SI_PROCESSINFO.SI_LOGON_INFO.SI_LOGON1.SI_SERVER = 'STAGING'"
				"SELECT * FROM CI_INFOOBJECTS WHERE SI_KIND='CRYSTALREPORT' AND SI_PROCESSINFO.SI_LOGON_INFO.SI_LOGON1.SI_SERVER = 'VALIDATION'"
				);
		
		// Function to parse CSV into a dictionary with the old user name as they key and new user name and password as the values
		Map <String, List<String>> credentialsDict = new HashMap<>();
		credentialsDict = parseCSV();
		System.out.println("Here is the dictionary of credentials -");
		System.out.println(credentialsDict);		
		int counter = 0;
		Map<String, Integer> usernames = new HashMap<>();
		int x =  0;
		for(int i=0; i < infoObjects.getResultSize(); i++) {
			// Loop through all report objects
			IInfoObject infoObject = (IInfoObject) infoObjects.get(i);
			System.out.println("Title: " + infoObject.getTitle());
			System.out.println("CUID: " + infoObject.getCUID());
			IReport rpt = (IReport) infoObject;
		
			// Check to make sure report log on data is not Null
			if((rpt.getReportLogons().size() > 0)) {
				IReportLogon rptlogon = (IReportLogon) rpt.getReportLogons().get(0);
				String username = rptlogon.getUserName().toLowerCase();
				// If current report user name is in CSV dictionary, replace it with new credentials
				if (credentialsDict.containsKey(username)) {
					// Set new user name and password
					rptlogon.setUserName(credentialsDict.get(username).get(0));
					rptlogon.setPassword(credentialsDict.get(username).get(1));
					rptlogon.setCustomPassword(credentialsDict.get(username).get(1));
					System.out.println("Changed username and password for user - " + username);
					infoObject.save();
					rpt.save();
				}
				
				// These functions help get the current report credentials from the report
				System.out.println("Server: " + rptlogon.getServerName());
				System.out.println("User: " + rptlogon.getUserName());
				
				
				// TODO: Index out of bound error for nameless reports - Either handle the error or remove call
//				System.out.println("getReportFileName(): " + rpt.getReportFileName());
				System.out.println("getDatabaseName(): " + rptlogon.getDatabaseName());
				System.out.println("getDatabaseDLLName(): " + rptlogon.getDatabaseDLLName());
				System.out.println("getCustomServerName(): " + rptlogon.getCustomServerName());
				System.out.println("getCustomUserName(): " + rptlogon.getCustomUserName());
				System.out.println("getCustomDatabaseName(): " + rptlogon.getCustomDatabaseName());
			}
			System.out.println("\n\n");
			counter += 1;
		}
		System.out.println(counter);
		return 1;
	}
	
	// This function parses the uploaded CSV file and returns a HashMap
	private Map<String, List<String>> parseCSV() throws Exception {
		// Open the CSV file for reading
		String filePath = getFilePath();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        
		Map<String, List<String>> parsedCSV = new HashMap<>();
		String line;
        while ((line = reader.readLine()) != null) {
        	// Split the line into an array of values
            String[] values = line.split(",");
            // Get the key and value from the line
            String key = values[0];
            String value1 = values[1];
            String value2 = values[2];
            
            // Check if the key already exists in the dictionary and throw exception
            if (parsedCSV.containsKey(key)) {
            	throw new Exception("This username has already shown up, please remove duplicates from the CSV -" + key);
            } else {
                // If the key does not exist, create a new list of values
                List<String> list = new ArrayList<>();
                list.add(value1);
                list.add(value2);
                // Add the key and list of values to the dictionary
                parsedCSV.put(key, list);
            }
        }
        // Close the reader and return parsed HashMap
        reader.close();
		return parsedCSV;
	}
	
	// This function finds the path of the CSV file uploaded to the GUI and returns it as a string
	private static String getFilePath() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setDialogTitle("Select migration CSV");
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			return selectedFile.getAbsolutePath();
		}else {
			System.exit(0);
		}
		return "";
	}
	
	// This function returns the server string
	private String getServer(int tmpPort) {
		String portStr = (usePort) ? (":" + Integer.toString(tmpPort)) : ""; 
		String serverStr = SERVER + portStr;
		return serverStr;
	}
}
