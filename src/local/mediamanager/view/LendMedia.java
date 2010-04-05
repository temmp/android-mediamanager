package local.mediamanager.view;

import java.util.HashMap;
import java.util.List;

import local.mediamanager.R;
import local.mediamanager.listener.LendMediaListener;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.view.sharedmenues.SharedActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * GUI zum Verleihen eines Mediums.
 * 
 * @author Andreas Wiedemann
 */
public class LendMedia extends SharedActivity {

	// GUI Elemente
	private Spinner spLendto;
	private Spinner spMedia;
	private Button btBorrowmedia;
	// Adapter fuer Spinner
	private ArrayAdapter<CharSequence> mediaTypeAdapter;
	private ArrayAdapter<CharSequence> contactAdapter;

	// HashMap aus [laufender Nummer & MediumID]
	private HashMap<Integer, Integer> filteredIDList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// HashMap
		filteredIDList = new HashMap<Integer, Integer>();
		// View setzen
		setContentView(R.layout.lendmedia);
		// Spinner "Medium" mit Medienliste fuellen
		spMedia = (Spinner) findViewById(R.id.spMedia);
		mediaTypeAdapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		spMedia.setAdapter(mediaTypeAdapter);
		List<Media> mediaList = new XMLMediaFileEditor(this).getAllMedia();
		int counterMedia = 0;
		int counterLentMedia = 0;
		for (Media media : mediaList) {
			if (media.getStatus().equals(Media.STATUS.VORHANDEN.getName())) {
				mediaTypeAdapter.add(media.getTitle());
				filteredIDList.put(counterLentMedia, counterMedia);
				++counterLentMedia;
			}
			++counterMedia;
		}

		// Spinner "Medium entleihen an" mit Kontaktliste füllen
		spLendto = (Spinner) findViewById(R.id.spLendto);
		Contact contactNameList = new Contact(this);
		contactAdapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, contactNameList
						.getContactNameList());
		spLendto.setAdapter(contactAdapter);

		btBorrowmedia = (Button) findViewById(R.id.btLendmedia);
		btBorrowmedia.setOnClickListener(new LendMediaListener(this,
				filteredIDList));
	}
}
