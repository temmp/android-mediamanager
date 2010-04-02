package local.mediamanager.util.itemlookup;

import java.net.MalformedURLException;
import java.net.URL;

import local.mediamanager.model.Media;
import local.mediamanager.util.xml.XMLUrlFileReader;

/**
 * Die Klasse stellt Methoden bereit um die Anfrage URL (Request URL) zu
 * erzeugen (mit der gewuenschten Barcodenummer) und die Anfrage an Amazon zu
 * senden und deren Ergebnis zu ermitteln (Amazon Response). Als Antwort wird
 * von Amazon eine XML geliefert.
 * 
 * @author Jörg Langner
 */

public class AmazonItemLookup {

	// der Service der von Amazon genutzt wird
	private static final String amazonService = "AWSECommerceService";
	// der Access Key des Services
	private static final String amazonAccessKey = "AKIAI2PXWQQEUT4WCQHA";
	// die Operation die ausgefuehrt wird -> es soll nach einem Amazon Artikel
	// gesucht werden
	private static final String amazonOperation = "ItemLookup";
	// Art der Antwort von Amazon -> fuer unsere Zwecke wird kein
	// "ausfuehrliche" Antwort benoetigt sondern nur das noetigste wie Autor,
	// Titel, Typ. Dies spart auch Ressourcen (da kleinere XML d.h. schneller
	// da weniger Uebertragen werden muss und einfacheres Parsen)
	private static final String amazonResponseGroup = "Small";
	// Index in welchem gesucht werden soll -> es soll in allen Kategorien nach
	// dem Barcode gesucht werden
	private static final String amazonSearchIndex = "All";
	// Typ der Anfrage (Request ID) -> in diesem Fall ein Barcode EAN
	private static final String amazonIdType = "EAN";

	/**
	 * Erstellt die Request-URL fuer ein Medium.
	 * 
	 * @param barcode
	 *            Barcode Nummer des Mediums
	 * @return Request-URL mit welcher die Anfrage an Amazon gestellt werden
	 *         kann
	 */
	public static String createRequestURL(String barcode) {
		// Bsp Request:
		// http://
		// de.free.apisigning.com/
		// onca/xml
		// ?Service=AWSECommerceService
		// &AWSAccessKeyId=AKIAI2PXWQQEUT4WCQHA
		// &Operation=ItemLookup
		// &ItemId=9783642015939
		// &ResponseGroup=Small
		// &SearchIndex=All
		// &IdType=EAN
		String requestURL = null;
		String protocol = "http://";
		String host = "de.free.apisigning.com/";
		String requestURI = "onca/xml";
		String service = "Service=";
		String accessKey = "AWSAccessKeyId=";
		String operation = "Operation=";
		String itemId = "ItemId=";
		String responseGroup = "ResponseGroup=";
		String searchIndex = "SearchIndex=";
		String idType = "IdType=";
		requestURL = protocol + host + requestURI + "?" + service
				+ amazonService + "&" + accessKey + amazonAccessKey + "&"
				+ operation + amazonOperation + "&" + itemId + barcode + "&"
				+ responseGroup + amazonResponseGroup + "&" + searchIndex
				+ amazonSearchIndex + "&" + idType + amazonIdType;
		return requestURL;
	}

	/**
	 * Sendet die Anfrage an Amazon und verarbeitet die XML Antwort in ein
	 * Medium.
	 * 
	 * @param requestURL
	 *            RequestURL die an Amazon gegeben wird
	 * @return Medium mit den Inhalten die aus der Amazon Response ermittelt
	 *         werden konnten
	 */
	public static Media fetchMedia(String requestURL) {
		URL url;
		Media media = null;
		try {
			url = new URL(requestURL);
			XMLUrlFileReader reader = new XMLUrlFileReader(url);
			media = reader.getMedia();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return media;
	}
}
