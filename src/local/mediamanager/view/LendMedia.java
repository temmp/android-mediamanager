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

	// HashMap aus [laufender Nummer & MediumID]
	private HashMap<Integer, Integer> filteredIDList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// HashMap
		filteredIDList = new HashMap<Integer, Integer>();
		// View setzen
		setContentView(R.layout.lendmedia);
		// Spinner "Medium" mit Medienliste fuellen
		Spinner spMedia = (Spinner) findViewById(R.id.spMedia);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		spMedia.setAdapter(adapter);
		List<Media> mediaList = new XMLMediaFileEditor(this).getAllMedia();
		int counterMedia = 0;
		int counterLentMedia = 0;
		for (Media media : mediaList) {
			if (media.getStatus().equals(Media.STATUS.VORHANDEN.getName())) {
				adapter.add(media.getTitle());
				filteredIDList.put(counterLentMedia, counterMedia);
				++counterLentMedia;
			}
			++counterMedia;
		}

		// Spinner "Medium entleihen an" mit Kontaktliste füllen
		Spinner spLendto = (Spinner) findViewById(R.id.spLendto);
		Contact contactNameList = new Contact(this);
		ArrayAdapter<CharSequence> contactAdapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, contactNameList
						.getContactNameList());
		spLendto.setAdapter(contactAdapter);

		Button btBorrowmedia = (Button) findViewById(R.id.btLendmedia);
		btBorrowmedia.setOnClickListener(new LendMediaListener(this,
				filteredIDList));
	}
}
