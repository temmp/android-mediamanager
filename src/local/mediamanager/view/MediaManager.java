package local.mediamanager.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import local.mediamanager.R;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.util.xml.XMLSerializer;
import local.mediamanager.view.shareddialogs.MediaManagerSharedDialog;
import local.mediamanager.view.sharedmenues.SharedActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//TODO Test> progress beim connecten und parsen
//TODO Calender einbauen..was ist mit Calendereintrag loeschen bei...
//zurueckmelden? soll der eintrag automatisch erstellt werden?
//Zudem ist im Calender code eine for schleife die von 0 bis 1 laeuft?
//TODO zurueckmelden auch ueber scanner ->searchbybarcode vorhanden
//TODO Test> medium verleiehen ueber scanner->searchbybarcode vorhanden
// TODO final ueberall weg -> var zu instanzvar machen
//TODO deklaration ausserhalb von onCreate (wo eh immer gebraucht wird)
// damit auch diese final scheisse weg kommt
//TODO gemeinsamer dialog manuell/scannen + button bilder
//TODO gemeinsamer dialog fuer amazon fehler und sonstiges
//TODO die laenge der strings vom itemlookup soll nicht die GUI zerschiessen
//TODO was ist contact_entry.xml?
//TODO icons in spinner fuer medientyp

/**
 * Dies ist die Einstiegsactivity des MediaManagers. Von hier aus werden alle
 * anderen Activities wie Medium anlegen, verleihen, entleihen ,zurueckmelden
 * und Medien anzeigen gestartet.
 * 
 * @author Andreas Wiedemann
 * @author Joerg Langner
 */
public class MediaManager extends SharedActivity {

	// Textinhalt des Dialog
	private final String DIALOG_TEXT_ADD_MEDIA = "Wie möchten Sie das Medium anlegen?";
	private final String DIALOG_TEXT_LEND_MEDIA = "Möchten Sie das Medium, welches verliehen"
			+ " werden soll, manuell Auswählen oder durch scannen?";
	private final String DIALOG_TEXT_BORROW_MEDIA = "Möchten Sie das Medium, welches entliehen"
			+ " werden soll, manuell Auswählen oder durch scannen?";
	// Beschriftungen der Titel des Dialogs
	private final String DIALOG_TITLE_ADD_MEDIA = "Medium anlegen";
	private final String DIALOG_TITLE_LEND_MEDIA = "Medium verleihen";
	private final String DIALOG_TITLE_BORROW_MEDIA = "Medium entleihen";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			FileInputStream fIn = openFileInput(XMLMediaFileEditor.FILE_NAME);
			fIn.close();
		} catch (FileNotFoundException e1) {
			XMLSerializer xmlSerializer = new XMLSerializer(this);
			xmlSerializer.createXMLFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Butten zum hinzufuegen eines Mediums
		Button btAddMedia = (Button) findViewById(R.id.add_media);
		btAddMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Intent fuer manuelles anlegen eines Mediums
				Intent manualIntent = new Intent(MediaManager.this,
						AddMedia.class);
				// Intent fuer das Anlegen eines Mediums per scannen
				Intent scanIntent = new Intent(MediaManager.this,
						AddMediaScan.class);
				// Dialog zur Auswahl von "Manuell" oder "Scannen" anzeigen
				new MediaManagerSharedDialog(MediaManager.this, manualIntent,
						scanIntent, DIALOG_TEXT_ADD_MEDIA, DIALOG_TITLE_ADD_MEDIA).show();
			}

		});

		// Butten zum Anzeigen der Medien
		Button btShowMedia = (Button) findViewById(R.id.show_media);
		btShowMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// ShowMedia ist die activity die gestartet wird
				Intent in = new Intent(MediaManager.this, ShowMedia.class);
				startActivity(in);
			}

		});

		// Butten verleihen eines Mediums
		Button btLendMedia = (Button) findViewById(R.id.lend_media);
		btLendMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Intent fuer manuelles verleihen eines Mediums
				Intent manualIntent = new Intent(MediaManager.this,
						LendMedia.class);
				// Intent fuer das verleihen eines Mediums per scannen
				Intent scanIntent = new Intent(MediaManager.this,
						LendMediaScan.class);
				// Dialog zur Auswahl von "Manuell" oder "Scannen" anzeigen
				new MediaManagerSharedDialog(MediaManager.this, manualIntent,
						scanIntent, DIALOG_TEXT_LEND_MEDIA, DIALOG_TITLE_LEND_MEDIA).show();
			}

		});

		// Butten zum entleihen eines Mediums
		Button btBorrowMedia = (Button) findViewById(R.id.borrow_media);
		btBorrowMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Intent fuer manuelles entleihen eines Mediums
				Intent manualIntent = new Intent(MediaManager.this,
						BorrowMedia.class);
				// Intent fuer das Entleihen eines Mediums per scannen
				Intent scanIntent = new Intent(MediaManager.this,
						BorrowMediaScan.class);
				// Dialog zur Auswahl von "Manuell" oder "Scannen" anzeigen
				new MediaManagerSharedDialog(MediaManager.this, manualIntent,
						scanIntent, DIALOG_TEXT_BORROW_MEDIA, DIALOG_TITLE_BORROW_MEDIA).show();
			}
		});
	}
}