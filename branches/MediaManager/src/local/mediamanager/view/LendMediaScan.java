package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.LendMediaScanListener;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.itemlookup.AmazonItemLookup;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.view.sharedmenues.SharedActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Activity zum verleihen eines Mediums per Scannen. Wenn das eingescannt Medium
 * im MediaManager bereits existiert wird im Spinner dar entsprechende
 * Mediumtitel angezeigt und der Benutzer kann das Medium verleihen (falls es
 * nicht verliehen oder entliehen ist). Falls das Medium noch nicht angelegt
 * wurde wird dem Benutzer mitgeteilt das er das Medium erst anlegen muss.
 * Danach wird automatisch ein ItemLookUp mit dem eingescannten Barcode gemacht
 * und die AddMedia Activity aufgerufen. Falls Amazon das Medium gefunden hat
 * werden alle Informationen (Barcode, Titel etc.) in die AddMedia GUI
 * eingetragen und der Benutzer braucht nur noch den "Medium anlegen" Button
 * druecken. Falls Amazon das Medium nicht gefunden hat muss der Benutzer alle
 * Medieninformationen von Hand eintragen. Danach springt das Programm zurueck
 * in die LendMediaScan GUI und der Benutzer kann das Medium nun verleihen.
 * 
 * @author Joerg Langner
 */
public class LendMediaScan extends SharedActivity {

	// Request Code fuer Scannen
	private final int SCAN_REQUEST_CODE = 0;
	// Request Code fuer AddMedia
	private static final int ADD_MEDIA_REQUEST_CODE = 1;

	// Beschriftungen fuer Dialogfenster-Fehlermeldung
	private final String MEDIA_NOT_AVAILABLE = "Das eingescannte Medium kann nicht"
			+ " verliehen werden da es bereits verliehen ist oder es entliehen ist.";
	private final String MEDIA_NOT_EXISTS = "Das Medium ist nicht vorhanden und muss zuvor angelegt werden.";

	// Barcode der gescannt wird
	private String barcode;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Scanvorgang starten
		 Intent in = new Intent(LendMediaScan.this, ScanMedia.class);
		 startActivityForResult(in, SCAN_REQUEST_CODE);
		// Test fuer Emulator
//		onActivityResult(SCAN_REQUEST_CODE, Activity.RESULT_OK, new Intent()
//				.putExtra("barcode", "4042564096545"));
	}

	/*
	 * wird aufgerufen wenn eine von hier gestartete ScanMedia activity beendet
	 * wurde
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// ueberpruefen von welcher Activity der callback kommt
		switch (requestCode) {
		case SCAN_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				// Layout LendMedia
				setContentView(R.layout.lendmedia);
				// der ermittelte Barcode
				barcode = data.getStringExtra(ScanMedia.BARCODE_EXTRA);
				// ueberpruefen ob barcode in der xml datei vorhanden ist
				Media media = new XMLMediaFileEditor(this)
						.getMediaByBarcode(barcode);
				// Medium wurde nicht in der xml gefunden
				if (media == null) {
					// Medium muss erst angelegt werden
					// es werden die Medieninformationen ermittelt:
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(MEDIA_NOT_EXISTS).setCancelable(false)
							.setNeutralButton("Weiter",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											addMedia(barcode);
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}
				// Medium wurde in der xml gefunden
				else {
					if (media.getStatus().equals(
							Media.STATUS.VORHANDEN.getName())) {
						// Spinner aus den Resourcen holen und den Adapter
						// setzen
						Spinner spMedia = (Spinner) findViewById(R.id.spMedia);
						spMedia.setEnabled(false);
						ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
								this, android.R.layout.simple_spinner_item);
						// Spinner "Medium" mit Medientitel fuellen
						spMedia.setAdapter(adapter);
						adapter.add(media.getTitle());
					} else {
						// Medium ist nicht "vorhanden" dh es ist bereits
						// verliehen oder entliehen. daher kann das medium nicht
						// verliehen werden
						AlertDialog.Builder builder = new AlertDialog.Builder(
								this);
						builder.setMessage(MEDIA_NOT_AVAILABLE).setCancelable(
								false).setNeutralButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										LendMediaScan.this.finish();
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
					}
				}

				// Spinner "Medium entleihen an" mit Kontaktliste füllen
				Spinner spLendto = (Spinner) findViewById(R.id.spLendto);
				Contact contactNameList = new Contact(this);
				ArrayAdapter<CharSequence> contactAdapter = new ArrayAdapter<CharSequence>(
						this, android.R.layout.simple_spinner_item,
						contactNameList.getContactNameList());
				spLendto.setAdapter(contactAdapter);

				// Button "Medium verleihen"
				Button btBorrowmedia = (Button) findViewById(R.id.btLendmedia);
				btBorrowmedia.setOnClickListener(new LendMediaScanListener(
						this, barcode));

			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Barcode Scannen war nicht erfolgreich
				// Die entsprechende Fehlermeldung wird von der ScanMedia
				// Activity dem Benutzer gezeigt..daher ist hier nichts zu tun
				this.finish();
			}
			break;
		case ADD_MEDIA_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				// Spinner aus den Resourcen holen und den Adapter setzen
				Spinner spMedia = (Spinner) findViewById(R.id.spMedia);
				spMedia.setEnabled(false);
				ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
						this, android.R.layout.simple_spinner_item);
				// Spinner "Medium" mit Medientitel fuellen
				spMedia.setAdapter(adapter);
				adapter.add(new XMLMediaFileEditor(this).getMediaByBarcode(
						barcode).getTitle());
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// der Benutzer hat beim Anlegen des Mediums abgebrochen. damit
				// ist das Medium nicht angelegt und kann damit auch nicht
				// verliehen werden. da der Benutzer selbst abgebrochen hat
				// braucht keine extra fehlermeldung kommen
				LendMediaScan.this.finish();
			}
			break;
		default:
			this.finish();
		}
	}

	/**
	 * Wenn das Medium welches verleiht werdern soll nicht gefunden wurde dann
	 * muss es zunaechst vom Benutzer angelegt werden.
	 * 
	 * @param barcode
	 *            Barcode fuer ItemLookup
	 */
	private void addMedia(String barcode) {
		// Progress Dialog starten, da die Anfrage an Amazon und deren Antwort
		// (das XML Dokument) ein paar Sekunden dauern kann. Zudem muss das XML
		// Dokument noch geparst werden.
		ProgressDialog progressDialog = ProgressDialog.show(LendMediaScan.this,
				"", "Bitte warten...", true);
		String uri = AmazonItemLookup.createRequestURL(barcode);
		Media media = AmazonItemLookup.fetchMedia(uri);
		// suche bei amazon beendet
		progressDialog.dismiss();
		// ueberpruefen ob amazon das medium gefunden hat
		if (media != null) {
			// die durch die ItemLoopUp Anfrage ermittelten Medieninformationen
			// werden an die AddMedia Activity weitergegeben welche die Werte
			// wie ISBN, Titel etc in die GUI eintraegt
			Intent in = new Intent(LendMediaScan.this, AddMedia.class);
			in.putExtra(AddMedia.BARCODE, media.getBarcode());
			in.putExtra(AddMedia.TITLE, media.getTitle());
			in.putExtra(AddMedia.AUTHOR, media.getAuthor());
			in.putExtra(AddMedia.TYPE, media.getType());
			startActivityForResult(in, ADD_MEDIA_REQUEST_CODE);
		}
		// medium wurde nicht von amazon gefunden daher muss das Medium manuell
		// angelegt werden d.h. die Medieninformationen wie ISBN, Titel etc
		// muessen vom Benutzer selber eingertragen werden
		else {
			Intent in = new Intent(LendMediaScan.this, AddMedia.class);
			in.putExtra(AddMedia.BARCODE, barcode);
			startActivityForResult(in, ADD_MEDIA_REQUEST_CODE);
		}
	}
}