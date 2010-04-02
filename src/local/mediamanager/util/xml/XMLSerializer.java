package local.mediamanager.util.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Document;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

/**
 * Diese Klasse übernimmt die Aufgabe der XML Serialization. Es beinhaltet
 * Methoden fuer das Erstellen einer XML Datei im lokalen Speicher der
 * Applikation und das Schreiben eines XML Dokumentes in diese Datei.
 * 
 * @author Jörg Langner
 */

public class XMLSerializer {

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
	public XMLSerializer(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Diese Methode wird nur einmal, beim ersten Starten des MediaManagers,
	 * aufgerufen. Es erstellt eine XML Datei in welcher die Medien(Barcode,
	 * Titel, Autor etc)gespeichert werden koennen. Hier wird der XML Prolog
	 * (Encodung = UTF-8)und das root-Element angelegt.
	 */
	public void createXMLFile() {
		/*
		 * XML Datei erstellen
		 */
		FileOutputStream fileos = null;
		try {
			fileos = activity.openFileOutput(XMLMediaFileEditor.FILE_NAME,
					Activity.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			Log.e("log", "XML Datei konnte nicht geschrieben werden");
		}

		/*
		 * root Element erstellen und schreiben
		 */
		XmlSerializer serializer = Xml.newSerializer();
		try {
			// der fileoutputstream wird als ausgabe (die ausgabe erfolgt in
			// UTF-8 Format) fuer den serializer festgelegt
			serializer.setOutput(fileos, "UTF-8");
			// XML Prolog mit Codierung wird geschrieben
			serializer.startDocument(null, Boolean.valueOf(true));
			// indentation option setzen
			serializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			// root start tag erstellen
			serializer.startTag(null, "root");
			// root end tag erstellen
			serializer.endTag(null, "root");
			serializer.endDocument();
			// xml daten werden in den fileoutputstream geschrieben
			serializer.flush();
			// fileoutputstream wird geschlossen
			fileos.flush();
			fileos.close();
		} catch (Exception e) {
			Log.e("log", "Fehler beim Erstellen der XML Datei");
		}
	}

	/**
	 * Diese Methode schreibt ein XML Dokument in den lokalen Speicher der
	 * Applikation.
	 * 
	 * @param doc
	 *            XML Dokument welches geschrieben werden wird
	 */
	public void writeDocumentToFile(Document doc) {
		try {
			XmlSerializer fileOutputSerializer = new KXmlSerializer();
			fileOutputSerializer.setOutput(activity.openFileOutput(
					XMLMediaFileEditor.FILE_NAME, Activity.MODE_PRIVATE),
					"UTF-8");
			// XML Dokument wird serialisiert
			doc.write(fileOutputSerializer);
		} catch (Exception exception) {
			Log.e("log", "XML Dokument konnte nicht geschrieben werden");
		}
	}
}
