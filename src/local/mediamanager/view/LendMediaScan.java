package local.mediamanager.view;

import java.util.HashMap;

import local.mediamanager.R;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.Date;
import local.mediamanager.util.itemlookup.AmazonItemLookup;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

public class LendMediaScan extends Activity {

	// TODO public ueberall -> fixen 
	public final static int SCAN_REQUEST = 0;
	// Beschriftungen fuer Dialogfenster-Fehlermeldung
	private final String MEDIA_NOT_AVAILABLE = "Das eingescannte Medium kann nicht"
			+ " verliehen werden da es bereits verliehen ist oder es entliehen ist.";
	// Informationstext wenn Medium angelegt wurde
	private final String MEDIA_SUCCESSFULLY_ADDED = "Medium erfolgreich verliehen.";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Scanvorgang starten
		Intent in = new Intent(LendMediaScan.this, ScanMedia.class);
		startActivityForResult(in, SCAN_REQUEST);
	}

	/*
	 * wird aufgerufen wenn eine von hier gestartete ScanMedia activity beendet
	 * wurde
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ueberpruefen von welcher Activity der callback kommt
		switch (requestCode) {
		case SCAN_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				// Spinner aus den Resourcen holen und den Adapter setzen
				Spinner spMedia = (Spinner) findViewById(R.id.spMedia);
				spMedia.setEnabled(false);
				ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
						this, android.R.layout.simple_spinner_item);
				// der ermittelte Barcode
				final String barcode = data.getStringExtra("barcode");
				// ueberpruefen ob barcode in der xml datei vorhanden ist
				Media media = new XMLMediaFileEditor(this)
						.getMediaByBarcode(barcode);
				// Medium nicht vorhanden
				if (media == null) {
					// Medium muss erst angelegt werden:
					// zunaechst werden Medieninformationen ermittelt:
					// Progress Dialog starten, da die Anfrage an Amazon und
					// deren
					// Antwort (das XML Dokument) ein paar Sekunden dauern kann.
					// Zudem muss das XML Dokument noch geparst werden.
					ProgressDialog progressDialog = ProgressDialog.show(this,
							"", "Bitte warten...", true);

					// String barcode = data.getStringExtra("barcode");
					String uri = AmazonItemLookup.createRequestURL(barcode);
					media = AmazonItemLookup.fetchMedia(uri);

					// Medium wird nun angelegt
					progressDialog.dismiss();
					Intent in = new Intent(LendMediaScan.this, AddMedia.class);
					in.putExtra(AddMedia.NAME_OF_EXTRA_DATA, AddMedia.VALUE_OF_EXTRA_DATA);
					in.putExtra(AddMedia.BARCODE, media.getBarcode());
					in.putExtra(AddMedia.TITLE, media.getTitle());
					in.putExtra(AddMedia.AUTHOR, media.getAuthor());
					in.putExtra(AddMedia.TYPE, media.getType());
					startActivity(in);

					// Spinner "Medium" mit Medientitel fuellen
					spMedia.setAdapter(adapter);
					adapter.add(media.getTitle());
				}
				// Medium vorhanden
				else {
					if (media.getStatus().equals(
							Media.STATUS.VORHANDEN.getName())) {
						// Spinner "Medium" mit Medientitel fuellen
						spMedia.setAdapter(adapter);
						adapter.add(media.getTitle());
					} else {
						// Medium ist nicht vorhanden
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
				// Layout LendMedia
				setContentView(R.layout.lendmedia);

				// Button "Medium verleihen"
				Button btBorrowmedia = (Button) findViewById(R.id.btLendmedia);
				btBorrowmedia.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Spinner spLendto = (Spinner) findViewById(R.id.spLendto);
						DatePicker dpLendtime = (DatePicker) findViewById(R.id.dpLendtime);
						Contact contacts = new Contact(LendMediaScan.this);
						HashMap<Integer, Integer> contactIDMap = contacts
								.getContactIDMap();
						Date dateObject = new Date(dpLendtime.getDayOfMonth(),
								dpLendtime.getMonth() + 1, dpLendtime.getYear());
						String date = dateObject.getDate();
						XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(
								LendMediaScan.this);
						Media media = xmlEditor.getMediaByBarcode(barcode);
						media.setOwner(contactIDMap.get(
								spLendto.getSelectedItemPosition()).toString());
						media.setDate(date);
						media.setStatus(Media.STATUS.VERLIEHEN.getName());
						xmlEditor.updateMediaByBarcode(barcode, media);
						Context context = getApplicationContext();
						Toast.makeText(context, MEDIA_SUCCESSFULLY_ADDED,
								Toast.LENGTH_SHORT).show();
						LendMediaScan.this.finish();
					}

				});

			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Barcode Scannen war nicht erfolgreich
				// Die entsprechende Fehlermeldung wird von der ScanMedia
				// Activity dem Benutzer gezeigt..daher ist hier nichts zu tun
				this.finish();
			}
			break;
		default:
			break;
		}
	}

}
