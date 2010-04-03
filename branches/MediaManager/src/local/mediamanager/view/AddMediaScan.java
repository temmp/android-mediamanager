package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.AddMediaListener;
import local.mediamanager.model.Media;
import local.mediamanager.util.itemlookup.AmazonItemLookup;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddMediaScan extends Activity {

	public final static int SCAN_REQUEST = 0;
	private final String MEDIA_NOT_FOUND = "Das Medium konnte von Amazon nicht gefunden"
			+ " werden.";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Scanvorgang starten
		Intent in = new Intent(AddMediaScan.this, ScanMedia.class); 
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
				// Progress Dialog starten, da die Anfrage an Amazon und deren
				// Antwort (das XML Dokument) ein paar Sekunden dauern kann.
				// Zudem muss das XML Dokument noch geparst werden.
				ProgressDialog progressDialog = ProgressDialog.show(this, "",
						"Bitte warten...", true);

				String barcode = data.getStringExtra("barcode");
				String uri = AmazonItemLookup.createRequestURL(barcode);
				final Media media = AmazonItemLookup.fetchMedia(uri);

				progressDialog.dismiss();

				if (media != null) {
					// Das ItemLoopkUp bei Amazon ist beendet und das XML
					// Dokument wurde geparst.
					// D.h. alle Informationen des Mediums stehen nun zur
					// Verfuegung und der ProgressDialog wird geschlossen
					progressDialog.dismiss();

					// Layout AddMedia
					setContentView(R.layout.addmedia);

					// Medieneingenschaften in die GUI eintragen
					EditText etBarcode = (EditText) findViewById(R.id.etBarcode);
					etBarcode.setText(media.getBarcode());
					EditText etTitle = (EditText) findViewById(R.id.etTitle);
					etTitle.setText(media.getTitle());
					EditText etAuthor = (EditText) findViewById(R.id.etAuthor);
					etAuthor.setText(media.getAuthor());
					Spinner spMediatype = (Spinner) findViewById(R.id.spMediatype);
					spMediatype.setEnabled(false);
					ArrayAdapter<CharSequence> spinnerAdapter;
					spinnerAdapter = new ArrayAdapter<CharSequence>(this,
							android.R.layout.simple_spinner_item);
					spinnerAdapter.add(media.getType());
					spMediatype.setAdapter(spinnerAdapter);

					// "Medium anlegen" Button
					Button btSave = (Button) findViewById(R.id.btSave);
					btSave.setOnClickListener(new AddMediaListener(this));
				} else {
					// Barcode wurde bei Amazon nicht gefunden
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(MEDIA_NOT_FOUND).setCancelable(false)
							.setNeutralButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											setResult(Activity.RESULT_CANCELED);
											AddMediaScan.this.finish();
											// jetzt wird die onActivityResult
											// Methode
											// der aufrufenden
											// Actitvity aufgerufen
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Barcode Scannen war nicht erfolgreich
				// Die entsprechende Fehlermeldung wird von der ScanMedia
				// Activity dem Benutzer gezeigt..daher ist hier nichts zu tun
				this.finish();
			}
		default:
			break;
		}
	}	
}