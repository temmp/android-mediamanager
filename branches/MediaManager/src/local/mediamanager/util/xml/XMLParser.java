package local.mediamanager.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.util.Log;

/**
 * Diese Klasse übernimmt die Aufgaben des XML Parsings. Es stellt Methoden
 * zur Verfügung mit der ein XML Dokument aus einer XML Datei geparst wird.
 * 
 * @author Jörg Langner
 */

public class XMLParser {

	/**
	 * Activity ist von Context abgeleitet. Context enthält die openFileInput
	 * und openFileOutput Methoden, mit denen ein Stream-Input/Output auf den
	 * lokalen Speicher des Android Handys aufgebaut werden kann
	 */
	private Activity activity;

	/**
	 * Konstruktor der die Activity setzt.
	 * 
	 * @param activity
	 *            Activity fuer Zugriff auf den lokalen Speicher der Applikation
	 */
	public XMLParser(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Diese Methode liest eine XML Datei aus und gibt ein XML Dokument zurueck.
	 * 
	 * @return XML Dokument welches aus der Datei gelesen wurde
	 */
	public Document readDocumentFromFile() {
		Document doc = null;
		XmlPullParser fileInputParser = null;
		try {
			fileInputParser = new KXmlParser();
			fileInputParser.setInput(activity
					.openFileInput(XMLMediaFileEditor.FILE_NAME), "UTF-8");
			doc = new Document();
			// XML Dokument wird geparst
			doc.parse(fileInputParser);
		} catch (Exception exception) {
			Log.e("log", "XML Dokument konnte nicht gelesen werden");
		}
		return doc;
	}

	/**
	 * Die Methode stellt eine GET Request an die uebergebene URL. Die Respsonse
	 * ist ein XML Dokument welches geparst wird.
	 * 
	 * @param url
	 *            Anfrage URL
	 * @return XML Dokument
	 */
	public static Document readDocumentFromURL(URL url) {
		Document doc = null;
		XmlPullParser urlInputParser = null;
		URLConnection connection = null;
		HttpURLConnection httpConnection = null;
		try {
			connection = url.openConnection();
			httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();
				urlInputParser = new KXmlParser();
				urlInputParser.setInput(in, "UTF-8");
				doc = new Document();
				doc.parse(urlInputParser);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return doc;
	}
}
