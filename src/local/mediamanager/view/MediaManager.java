package local.mediamanager.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import local.mediamanager.R;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.util.xml.XMLSerializer;
import local.mediamanager.view.menuhelper.SharedActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
//TODO medium verleiehen ueber scanner->searchbybarcode vorhanden

/**
 * Dies ist die Einstiegsactivity des MediaManagers. Von hier aus werden alle
 * anderen Activities wie Medium scannen, manuell anlegen, verleihen, entleihen
 * und Medien anzeigen gestartet.
 * 
 * @author Andreas Wiedemann
 * @author Joerg Langner
 */
public class MediaManager extends SharedActivity {

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
		Button btScanMedia = (Button) findViewById(R.id.scan_media);
		btScanMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// ScanMedia ist die activity die gestartet wird
				Intent in = new Intent(MediaManager.this, AddMediaScan.class);
				startActivity(in);
				// Local ItemLookup Test by JL:
				// String uri =
				// AmazonItemLookup.createBookRequestURL("9783642015939");
				// Log.i("log", uri);
				// Media media = AmazonItemLookup.fetchMedia(uri);
			}
		});

		Button btAddMedia = (Button) findViewById(R.id.add_media);
		btAddMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// AddMedia ist die activity die gestartet wird
				Intent in = new Intent(MediaManager.this, AddMedia.class);
				startActivity(in);
			}

		});

		Button btShowMedia = (Button) findViewById(R.id.show_media);
		btShowMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// ShowMedia ist die activity die gestartet wird
				Intent in = new Intent(MediaManager.this, ShowMedia.class);
				startActivity(in);
			}

		});

		Button btLendMedia = (Button) findViewById(R.id.lend_media);
		btLendMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final String BORROW_MEDIA_TYPE = "Möchten Sie das Medium" +
						" manuell auswählen oder durch scannen?";
				final String BORROW_MEDIA_MANUALLY = "Manuell";
				final String BORROW_MEDIA_BY_SCANINNG = "Scannen";
				// Dialog zur Auswahl von "Manuell" oder "Scannen" anzeigen
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MediaManager.this);
				builder.setMessage(BORROW_MEDIA_TYPE).setCancelable(false)
						.setPositiveButton(BORROW_MEDIA_MANUALLY,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// BorrowMedia ist die activity die
										// gestartet wird
										Intent in = new Intent(
												MediaManager.this,
												LendMedia.class);
										startActivity(in);
									}
								}).setNegativeButton(BORROW_MEDIA_BY_SCANINNG,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// BorrowMediaScan ist die activity die
										// gestartet wird
										Intent in = new Intent(
												MediaManager.this,
												LendMediaScan.class);
										startActivity(in);
									}
								});
				builder.show();

			}

		});

		Button btBorrowMedia = (Button) findViewById(R.id.borrow_media);
		btBorrowMedia.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final String BORROW_MEDIA_TYPE = "Wie wollen Sie das entliehene"
						+ " Medium anlegen?";
				final String BORROW_MEDIA_MANUALLY = "Manuell";
				final String BORROW_MEDIA_BY_SCANINNG = "Scannen";
				// Dialog zur Auswahl von "Manuell" oder "Scannen" anzeigen
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MediaManager.this);
				builder.setMessage(BORROW_MEDIA_TYPE).setCancelable(false)
						.setPositiveButton(BORROW_MEDIA_MANUALLY,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// BorrowMedia ist die activity die
										// gestartet wird
										Intent in = new Intent(
												MediaManager.this,
												BorrowMedia.class);
										startActivity(in);
									}
								}).setNegativeButton(BORROW_MEDIA_BY_SCANINNG,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// BorrowMediaScan ist die activity die
										// gestartet wird
										Intent in = new Intent(
												MediaManager.this,
												BorrowMediaScan.class);
										startActivity(in);
									}
								});
				builder.show();
			}
		});
	}
}