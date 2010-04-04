package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.BorrowMediaListener;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.itemlookup.AmazonItemLookup;
import local.mediamanager.view.sharedmenues.SharedActivity;
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

/**
 * Activity zum Entleihen eines Mediums per Scannen. Mit dem eingescannten
 * Barcode wird ein ItemLookup bei Amazon gemacht. Wurde das Medium von Amazon
 * gefunden werden die ermittelten Informationen ueber das Medium in die GUI
 * eingetragen. Wird das MEdium nicht von Amazon gefunden so wird die
 * BorrowMedia Activity aufgerufen in welche die Medieninformationen selber per
 * Hand eingetragen werden muessen.
 * 
 * @author Joerg Langner
 */
public class BorrowMediaScan extends SharedActivity {

	private final static int SCAN_REQUEST_CODE = 0;
	private final String MEDIA_NOT_FOUND = "Das Medium konnte von Amazon nicht gefunden"
			+ " werden und muss daher manuell angelegt werden.";

	// der eingescannte barcode
	private String barcode;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Scanvorgang starten
		Intent in = new Intent(BorrowMediaScan.this, ScanMedia.class);
		startActivityForResult(in, SCAN_REQUEST_CODE);
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
				// Progress Dialog starten, da die Anfrage an Amazon und deren
				// Antwort (das XML Dokument) ein paar Sekunden dauern kann.
				// Zudem muss das XML Dokument noch geparst werden.
				ProgressDialog progressDialog = ProgressDialog.show(this, "",
						"Bitte warten...", true);

				barcode = data.getStringExtra("barcode");
				String uri = AmazonItemLookup.createRequestURL(barcode);
				// TODO media ueberall nicht mehr final
				final Media media = AmazonItemLookup.fetchMedia(uri);

				progressDialog.dismiss();

				if (media != null) {
					// Das ItemLoopkUp bei Amazon ist beendet und das XML
					// Dokument wurde geparst.
					// D.h. alle Informationen des Mediums stehen nun zur
					// Verfuegung und der ProgressDialog wird geschlossen
					progressDialog.dismiss();

					// Layout BorrowMedia
					setContentView(R.layout.borrowmedia);

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

					// Spinner "Medium entleihen an" mit Kontaktliste füllen
					Spinner spLegalOwner = (Spinner) findViewById(R.id.spLegalOwner);
					Contact contactNameList = new Contact(this);
					ArrayAdapter<CharSequence> contactAdapter = new ArrayAdapter<CharSequence>(
							this, android.R.layout.simple_spinner_item,
							contactNameList.getContactNameList());
					spLegalOwner.setAdapter(contactAdapter);

					// "Medium anlegen" Button
					Button btBorrowSave = (Button) findViewById(R.id.btBorrowSave);
					btBorrowSave.setOnClickListener(new BorrowMediaListener(
							this));
				} else {
					// Barcode wurde bei Amazon nicht gefunden
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(MEDIA_NOT_FOUND).setCancelable(false)
							.setNeutralButton("Weiter",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Intent in = new Intent(
													BorrowMediaScan.this,
													BorrowMedia.class);
											in.putExtra(BorrowMedia.BARCODE,
													barcode);
											startActivity(in);
											BorrowMediaScan.this.finish();
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
			break;
		default:
			break;
		}
	}
}
