package local.mediamanager.util.xml;

import java.net.URL;

import local.mediamanager.model.Media;

import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;

/**
 * Die Klasse ermittelt aus der Antwort XML von Amazon (Response) die
 * Eigenschaften (Autor, Titel, Typ etc.) des eingescannten Mediums.
 * 
 * @author Jörg Langner
 */

public class XMLUrlFileReader {

	private Document doc;

	/**
	 * Aus der uebergebenen URL wird das XML Document geparst.
	 * 
	 * @param url
	 *            URL welches das XML Document angibt welches geparst werden
	 *            soll
	 */
	public XMLUrlFileReader(URL url) {
		doc = XMLParser.readDocumentFromURL(url);
	}

	/**
	 * Das XML Dokument wird geparst und die Informationen uber das Medium in
	 * ein Media Object gespeichert welches dann zurueckgegeben wird.
	 * 
	 * @return Media Objekt mit den ermittelten Informationen
	 */
	public Media getMedia() {
		// Elemente des Amazon XML Dokuments
		final String ITEM_LOOKUP_RESPONSE = "ItemLookupResponse";
		final String ITEMS = "Items";
		final String REQUEST = "Request";
		final String ITEM = "Item";
		final String ITEM_ATTRIBUTES = "ItemAttributes";
		final String AUTHOR = "Author";
		final String TITLE = "Title";
		final String PRODUCT_GROUP = "ProductGroup";
		final String ITEM_LOOKUP_REQUEST = "ItemLookupRequest";
		final String ITEM_ID = "ItemId";

		// Default Werte fuer das Media Object falls das Titel, Autor oder Typ
		// Element nicht vorhanden ist
		final String AUTHOR_NOT_AVAILABLE = "Autor nicht vorhanden";
		final String TITLE_NOT_AVAILABLE = "Titel nicht vorhanden";
		final String MEDIA_TYPE_NOT_AVAILABLE = "Medientyp nicht vorhanden";

		Media media = new Media();
		Element itemLookupResponse = doc.getElement(null, ITEM_LOOKUP_RESPONSE);
		Element items = itemLookupResponse.getElement(null, ITEMS);
		Element request = items.getElement(null, REQUEST);
		try {
			Element item = items.getElement(null, ITEM);
			Element itemAttributes = item.getElement(null, ITEM_ATTRIBUTES);
			// Aufgrund von Unvollstaendigkeiten seitens Amazon kann es sein das
			// nicht alle Medien diese Elemente haben (z.B. ist der Autor
			// (Regisseur) einer DVD nicht immer gegeben).
			// Daher wird fuer jedes Element einzeln geprueft ob es vorhanden
			// ist oder nicht. Wenn nicht wird ein String mit einem Default-Text
			// wie "Autor nicht vorhanden" gespeichert
			try {
				Element author = itemAttributes.getElement(null, AUTHOR);
				// itemAttributes.
				media.setAuthor(author.getText(0));
			} catch (RuntimeException e) {
				// Element nicht vorhanden oder mehrmals vorhanden:
				// Falls das Element mehrmals vorhanden ist wird hier der String
				// mit allen Autoren des Mediums zusammengesetzt
				String authors = "";
				int childCount = itemAttributes.getChildCount();
				for (int i = 0; i < childCount; i++) {
					Element tmp = itemAttributes.getElement(i);
					if (tmp.getName().equals(AUTHOR)) {
						authors = authors + tmp.getText(0) + "; ";
					}
				}
				// wenn der String weiterhin leer ist dann ist kein Autor
				// gegeben
				if (authors.equals("")) {
					media.setAuthor(AUTHOR_NOT_AVAILABLE);
				} else {
					media.setAuthor(authors);
				}
			}
			try {
				Element title = itemAttributes.getElement(null, TITLE);
				media.setTitle(title.getText(0));
			} catch (RuntimeException e) {
				// element nicht vorhanden
				media.setTitle(TITLE_NOT_AVAILABLE);
			}
			try {
				Element type = itemAttributes.getElement(null, PRODUCT_GROUP);
				// Amazon hat als Medientyp "DVD" welches im MediaManager als
				// "Movie" bezeichnet wird
				if (type.getText(0).equals("DVD")) {
					media.setType(Media.TYPE.MOVIE.getName());
				} else {
					media.setType(type.getText(0));
				}
			} catch (RuntimeException e) {
				// element nicht vorhanden
				media.setType(MEDIA_TYPE_NOT_AVAILABLE);
			}
			// barcode
			Element itemLookupRequest = request.getElement(null,
					ITEM_LOOKUP_REQUEST);
			Element itemId = itemLookupRequest.getElement(null, ITEM_ID);
			media.setBarcode(itemId.getText(0));
		} catch (RuntimeException e) {
			// barcode nummer nicht gefunden
			media = null;
		}
		return media;
	}
}
