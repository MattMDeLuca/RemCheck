package remCheckProcessing;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipInputStream;

import javafx.util.Pair;

public class AlfrescoConnector {
	private Pair<String, String> authenticationInformation;
	private int exportStatusCode;
	private URL exportURL;
	
	//private int zipStatusCode;
	
 	public AlfrescoConnector () {
		
	}
	
	public void setAuthInformation(Pair<String, String> authInfo) {
		this.authenticationInformation = authInfo;
	}
	
	public int getStatusCode() {
		return exportStatusCode;
	}
	
	public URL getExportURL() {
		return exportURL;
	}
	
	public void retrieveExportCSV (String export) throws MalformedURLException {
		setAuthentication(authenticationInformation.getKey(), authenticationInformation.getValue());
		String properURL = "https://uswip.pearsoncms.com/alfresco/api/-default-/public/cmis/versions/1.1/browser/root?objectId=workspace://SpacesStore/";
		
		
		if (export.contains(properURL)) {exportURL = new URL(export);} //Testing the URL to ensure we can access the export correctly.
		
		if (export.contains("document-details")) {
			if (export.contains("site")) {
				System.out.println("site");
				System.out.println(properURL + export.split("&")[0].split("SpacesStore/")[1]);
				exportURL = new URL(properURL + export.split("&")[0].split("SpacesStore/")[1]);	
			}
			
			else {
				System.out.println("document-details");
				System.out.println(properURL + export.split("SpacesStore/")[1]);
				exportURL = new URL(properURL + export.split("SpacesStore/")[1]);
			}
		}						
	}
	
	public InputStream retrieveExportInputStream( )throws IOException {

		HttpURLConnection alfrescoConnection = (HttpURLConnection) exportURL.openConnection();
		exportStatusCode = alfrescoConnection.getResponseCode();
		return alfrescoConnection.getInputStream();
		
	}

	public ZipInputStream retrieveZip(String assessmentUrl) throws IOException {
		setAuthentication(authenticationInformation.getKey(), authenticationInformation.getValue());
		URL url = new URL(assessmentUrl);
		URLConnection alfrescoConnection = url.openConnection();
		ZipInputStream alfrescoZipInputStream = new ZipInputStream(alfrescoConnection.getInputStream());
		return alfrescoZipInputStream;

	}
	
	private void setAuthentication(String user, String pass) {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
		
	}
	


}
