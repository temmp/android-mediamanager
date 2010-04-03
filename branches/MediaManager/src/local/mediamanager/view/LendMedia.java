package local.mediamanager.view;

import java.util.HashMap;
import java.util.List;

import local.mediamanager.R;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.Date;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.view.menuhelper.SharedActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * GUI zum Verleihen eines Mediums.
 * 
 * @author Andreas Wiedemann
 */
public class LendMedia extends SharedActivity { 

	// Dialog Beschriftung wenn Medium erfolgreichv erliehen wurde
	private final String MEDIA_SUCCESSFULLY_ADDED = "Medium erfolgreich verliehen.";

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
		btBorrowmedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Spinner spMedia = (Spinner) findViewById(R.id.spMedia);
				Spinner spLendto = (Spinner) findViewById(R.id.spLendto);
				DatePicker dpLendtime = (DatePicker) findViewById(R.id.dpLendtime);
				Contact contacts = new Contact(LendMedia.this);
				HashMap<Integer, Integer> contactIDMap = contacts
						.getContactIDMap();
				Date dateObject = new Date(dpLendtime.getDayOfMonth(),
						dpLendtime.getMonth() + 1, dpLendtime.getYear());
				String date = dateObject.getDate();
				XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(
						LendMedia.this);
				Media media = xmlEditor.getMediaByPosition(filteredIDList
						.get(spMedia.getSelectedItemPosition()));
				media.setOwner(contactIDMap.get(
						spLendto.getSelectedItemPosition()).toString());
				media.setDate(date);
				media.setStatus(Media.STATUS.VERLIEHEN.getName());
				xmlEditor.updateMediaByPosition(filteredIDList.get(spMedia
						.getSelectedItemPosition()), media);
				Context context = getApplicationContext();
				Toast.makeText(context, MEDIA_SUCCESSFULLY_ADDED,
						Toast.LENGTH_SHORT).show();
				LendMedia.this.finish();
			}

		});
	}
}
