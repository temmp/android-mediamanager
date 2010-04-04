package local.mediamanager.util.xml;

import java.util.LinkedList;
import java.util.List;

import local.mediamanager.model.Media;

import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import android.app.Activity;

/**
 * Diese Klasse stellt Methoden zur Verfügung mit welchen Medien in das XML
 * Dokument hinzugefuegt und geloescht werden koennen. Zudem stellt sie Methoden
 * bereit mit welchen Medien aus dem XML Dokument gelesen werden koennen.
 * 
 * @author Jörg Langner
 */

public class XMLMediaFileEditor {

	private XMLSerializer xmlSerializer;
	private XMLParser xmlParser;
	/**
	 * Name der XML Datei in welche geschrieben oder aus welcher gelesen wird.
	 */
	public static final String FILE_NAME = "media.xml";

	/**
	 * Der Konstruktor erzeugt den XML Serializer und den XML Parser.
	 * 
	 * @param activity
	 *            Activity fuer Zugriff auf den lokalen Speicher der Applikation
	 */
	public XMLMediaFileEditor(Activity activity) {
		this.xmlSerializer = new XMLSerializer(activity);
		this.xmlParser = new XMLParser(activity);
	}

	/**
	 * Die Methode fuegt ein neues Medium in die XML Datei ein.
	 * 
	 * Nach dem einfuegen eines Mediums sieht der Aufbau des XML Dokumtes
	 * beispielsweise so aus:
	 * 
	 * <root> <media type=""> <barcode></barcode> <title></title>
	 * <author></author> <status legalOwner="" owner="" date="" ></status>
	 * </media> </root>
	 * 
	 * @param media
	 *            Medium Object
	 */

	public void addMedia(Media media) {
		// xml doc holen
		Document doc = xmlParser.readDocumentFromFile();
		// root Element der xml datei holen
		Element root = doc.getRootElement();
		// neue childs erstellen (createElement(namespace, name))
		Element mediaChild = new Element().createElement(null, "media");
		Element barcodeChild = new Element().createElement(null, "barcode");
		Element titleChild = new Element().createElement(null, "title");
		Element authorChild = new Element().createElement(null, "author");
		Element statusChild = new Element().createElement(null, "status");
		// attribut des mediaChilds setzen(namespace, name, value)
		mediaChild.setAttribute(null, "type", media.getType());
		statusChild.setAttribute(null, "legalOwner", media.getLegalOwner());
		statusChild.setAttribute(null, "owner", media.getOwner());
		statusChild.setAttribute(null, "date", media.getDate());
		// values der anderen childs setzen
		barcodeChild.addChild(Node.TEXT, media.getBarcode());
		titleChild.addChild(Node.TEXT, media.getTitle());
		authorChild.addChild(Node.TEXT, media.getAuthor());
		statusChild.addChild(Node.TEXT, media.getStatus());
		// dem root element das mediaChild hinzufuegen (type=ELEMENT,
		// Node->Element)
		root.addChild(Node.ELEMENT, mediaChild);
		// dem mediaChild das barcode-,title- und authorChild hinzufuegen
		mediaChild.addChild(Node.ELEMENT, barcodeChild);
		mediaChild.addChild(Node.ELEMENT, titleChild);
		mediaChild.addChild(Node.ELEMENT, authorChild);
		mediaChild.addChild(Node.ELEMENT, statusChild);
		// das xml doc schreiben
		xmlSerializer.writeDocumentToFile(doc);
	}

	/**
	 * Diese Methode loescht ein Medium anhand des Barcodes.
	 * 
	 * @param barcode
	 *            Medium mit diesem Barcode wird geloescht
	 * @return true wenn ein Medium mit diesem Barcode geloescht wurde
	 */
	public boolean removeMediaByBarcode(String barcode) {
		// xml doc holen
		Document doc = xmlParser.readDocumentFromFile();
		// root element holen
		Element root = doc.getRootElement();
		// countervariable fuer das aktuelle medium
		int currentElement = 0;
		// anzahl childs des root elements im xml dokument
		int lastElement = root.getChildCount();
		boolean elementFound = false;
		while (elementFound == false && currentElement < lastElement) {
			// die vorhandenen medien werden nacheinander geholt
			Element elem = (Element) root.getChild(currentElement);
			// barcode des aktuellen media childs gleich der des zu loeschenden
			// barcodes?
			if (((Element) elem.getChild(0)).getText(0).equals(barcode)) {
				// element gefunden also schleife beenden
				elementFound = true;
				// element aus xml doc entfernen
				root.removeChild(currentElement);
				// xml doc schreiben
				xmlSerializer.writeDocumentToFile(doc);
				return true;
			} else {
				++currentElement;
			}
		}
		return false;
	}

	/**
	 * Diese Methode loescht ein Medium anhand der Position im XML Document.
	 * 
	 * @param pos
	 *            Position des Mediums
	 * @return true wenn ein Medium mit dieser Position geloescht wurde
	 */
	public boolean removeMediaByPosition(int pos) {
		// xml doc holen
		Document doc = xmlParser.readDocumentFromFile();
		// root element holen
		Element root = doc.getRootElement();
		if (root.getElement(pos) != null) {
			// element aus xml doc entfernen
			root.removeChild(pos);
			// xml doc schreiben
			xmlSerializer.writeDocumentToFile(doc);
			return true;
		}
		return false;
	}
	

	/**
	 * Diese Methode sucht nach einem Medium mit dem uebergebenen Barcode und
	 * gibt das Medium zurueck.
	 * 
	 * @param barcode
	 *            Nach dem Medium mit diesem barcode wird gesucht.
	 * @return Das Medium mit diesem barcode. Wenn kein Medium mit diesem
	 *         Barcode gefunden wurde ist der Rueckgabewert null.
	 */
	public Media getMediaByBarcode(String barcode) {
		// Medium welches spaeter zurueckgegeben wird
		Media media = null;
		// xml doc holen
		Document doc = xmlParser.readDocumentFromFile();
		// root element holen
		Element root = doc.getRootElement();
		// countervariable aktuelles medium
		int currentElement = 0;
		// anzahl childs des root elements im xml dokument
		int lastElement = root.getChildCount();
		boolean elementFound = false;
		while (elementFound == false && currentElement < lastElement) {
			// die vorhandenen medien werden nacheinander geholt
			Element elem = (Element) root.getChild(currentElement);
			// barcode des aktuellen media childs gleich der des zu loeschenden
			// barcodes?
			if (((Element) elem.getChild(0)).getText(0).equals(barcode)) {
				// element gefunden also schleife beenden
				elementFound = true;
				// Mediumwerte speichern
				media = new Media();
				media.setType(elem.getAttributeValue(0));
				media.setBarcode(((Element) elem.getChild(0)).getText(0));
				media.setTitle(((Element) elem.getChild(1)).getText(0));
				media.setAuthor(((Element) elem.getChild(2)).getText(0));
				media.setStatus(((Element) elem.getChild(3)).getText(0));
				Element statusChild = (Element) elem.getChild(3);
				media.setLegalOwner(statusChild.getAttributeValue(0));
				media.setOwner(statusChild.getAttributeValue(1));
				media.setDate(statusChild.getAttributeValue(2));
			} else {
				++currentElement;
			}
		}
		return media;
	}

	/**
	 * Diese Methode sucht nach einem Medium mit der uebergebenen Position und
	 * gibt das Medium zurueck.
	 * 
	 * @param pos
	 *            Nach dem Medium mit dieser Position wird gesucht.
	 * @return Das Medium mit dieser Position.
	 */
	public Media getMediaByPosition(int pos) {
		// Medium welches spaeter zurueckgegeben wird
		Media media = new Media();
		// xml doc holen
		Document doc = xmlParser.readDocumentFromFile();
		// root element holen
		Element root = doc.getRootElement();
		// das entsprechende child holen
		Element child = (Element) root.getChild(pos);
		// Mediumwerte speichern
		media.setType(child.getAttributeValue(0));
		media.setBarcode(((Element) child.getChild(0)).getText(0));
		media.setTitle(((Element) child.getChild(1)).getText(0));
		media.setAuthor(((Element) child.getChild(2)).getText(0));
		media.setStatus(((Element) child.getChild(3)).getText(0));
		Element statusChild = (Element) child.getChild(3);
		media.setLegalOwner(statusChild.getAttributeValue(0));
		media.setOwner(statusChild.getAttributeValue(1));
		media.setDate(statusChild.getAttributeValue(2));
		return media;
	}

	/**
	 * Diese Methode updated ein Medium
	 * 
	 * @param pos
	 *            Positions des Mediums
	 * @param media
	 *            Das geupdatede Medium
	 */
	public void updateMediaByPosition(int pos, Media media) {
		removeMediaByPosition(pos);
		addMedia(media);
	}
	
	/**
	 * Diese Methode updated ein Medium
	 * 
	 * @param barcode
	 *            Barcode des Mediums
	 * @param media
	 *            Das geupdatede Medium
	 */
	public void updateMediaByBarcode(String barcode, Media media) {
		// xml doc holen
		Document doc = xmlParser.readDocumentFromFile();
		// root element holen
		Element root = doc.getRootElement();
		// countervariable aktuelles medium
		int currentElement = 0;
		// anzahl childs des root elements im xml dokument
		int lastElement = root.getChildCount();
		boolean elementFound = false;
		while (elementFound == false && currentElement < lastElement) {
			// die vorhandenen medien werden nacheinander geholt
			Element elem = (Element) root.getChild(currentElement);
			// barcode des aktuellen media childs gleich der des zu loeschenden
			// barcodes?
			if (((Element) elem.getChild(0)).getText(0).equals(barcode)) {
				// element gefunden also schleife beenden
				elementFound = true;
				// Medium loeschen
				root.removeChild(currentElement);
				// das xml doc schreiben
				xmlSerializer.writeDocumentToFile(doc);
				// Medium mit neuem Status hinzufuegen		
				addMedia(media);				
			} else {
				++currentElement;
			}
		}	
	}

	/**
	 * Diese Methode ermittelt alle Medien der XML Datei und gibt sie zurueck.
	 * 
	 * @return Ein Liste mit allen Medien
	 */
	public List<Media> getAllMedia() {
		// Liste mit allen in der xml datei vorhanden medien
		List<Media> mediaList = new LinkedList<Media>();
		// xml doc holen
		Document doc = xmlParser.readDocumentFromFile();
		// root element holen
		Element root = doc.getRootElement();
		// countervariable aktuelles medium
		int currentElement = 0;
		// anzahl childs des root elements im xml dokument
		int lastElement = root.getChildCount();
		while (currentElement < lastElement) {
			// hilfsvariable um ein gefundes medium in die liste zu schreiben
			Media media = new Media();
			// die vorhandenen medien werden nacheinander geholt
			Element elem = (Element) root.getChild(currentElement);
			Element elemSt = (Element) elem.getChild(3);
			// Mediumwerte speichern
			media.setType(elem.getAttributeValue(0));
			media.setBarcode(((Element) elem.getChild(0)).getText(0));
			media.setTitle(((Element) elem.getChild(1)).getText(0));
			media.setAuthor(((Element) elem.getChild(2)).getText(0));
			media.setStatus(((Element) elem.getChild(3)).getText(0));
			media.setLegalOwner(elemSt.getAttributeValue(0));
			media.setOwner(elemSt.getAttributeValue(1));
			media.setDate(elemSt.getAttributeValue(2));
			// Das Medium der Mediumsliste hinzufuegen
			mediaList.add(media);
			++currentElement;
		}
		return mediaList;
	}
}
