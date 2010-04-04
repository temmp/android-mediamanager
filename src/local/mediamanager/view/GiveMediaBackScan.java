package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.GiveMediaBackScanListener;
import local.mediamanager.model.Media;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.view.sharedmenues.SharedActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class GiveMediaBackScan extends SharedActivity {

	// Requestcode fuer Scannen
	private final int SCAN_REQUEST_CODE = 0;

	// Nachricht wenn Medium nicht in der xml datei gefunden wurde
	private final String MEDIA_NOT_FOUND = "Medium kann nicht zurückgemeldet"
			+ " werden da es noch nicht angelegt wurde.";
	// Nachricht wenn Medium nicht zurueckgemeldet werden kann
	private final String MEDIA_NOT_LENT_OR_BORROWED = "Medium kann nicht"
			+ " zurückgemeldet werden da es weder entliehen noch verliehen ist.";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Scanvorgang starten
		 Intent in = new Intent(GiveMediaBackScan.this, ScanMedia.class);
		 startActivityForResult(in, SCAN_REQUEST_CODE);
		// Test without scanner
//		onActivityResult(SCAN_REQUEST_CODE, Activity.RESULT_OK, new Intent()
//				.putExtra(ScanMedia.BARCODE_EXTRA, "222"));
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
				// der gescannte Barcode
				String barcode = data.getStringExtra(ScanMedia.BARCODE_EXTRA);
				// Medium mit diesem Barcode suchen
				XMLMediaFileEditor parser = new XMLMediaFileEditor(this);
				Media media = parser.getMediaByBarcode(barcode);
				// Uberpruefen ob Medium mit diesem Barcode gefunden wurde
				if (media != null) {
					// Medium in xml gefunden
					// ueberpruefen ob Medium zurueckgegeben werden kann dh ob
					// es verliehen oder entliehen ist
					if (media.getStatus().equals(
							Media.STATUS.VERLIEHEN.getName())
							|| media.getStatus().equals(
									Media.STATUS.ENTLIEHEN.getName())) {
						// Medium kann zurueckgemeldet werden
						// Layout der GUI setzen
						setContentView(R.layout.givemediaback);
						// Medieneigenschaften in GUI eintragen
						EditText tvBarcode = (EditText) findViewById(R.id.etBarcode);
						tvBarcode.setEnabled(false);
						tvBarcode.setText(media.getBarcode());

						EditText tvTitle = (EditText) findViewById(R.id.etTitle);
						tvTitle.setEnabled(false);
						tvTitle.setText(media.getTitle());

						EditText tvAuthor = (EditText) findViewById(R.id.etAuthor);
						tvAuthor.setEnabled(false);
						tvAuthor.setText(media.getAuthor());

						EditText tvMediatype = (EditText) findViewById(R.id.etMediatype);
						tvMediatype.setEnabled(false);
						tvMediatype.setText(media.getType());

						// Button "zurückmelden" listener setzen
						Button btGiveMediaBack = (Button) findViewById(R.id.btGiveMediaBack);
						btGiveMediaBack
								.setOnClickListener(new GiveMediaBackScanListener(
										this, barcode));
					}
					// Medium ist "vorhanden" und kann damit nicht
					// zurueckgemeldet werden
					else {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								this);
						builder.setMessage(MEDIA_NOT_LENT_OR_BORROWED)
								.setCancelable(false).setNeutralButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												GiveMediaBackScan.this.finish();
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
					}

				}
				// Medium nicht in xml gefunden
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(MEDIA_NOT_FOUND).setCancelable(false)
							.setNeutralButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											GiveMediaBackScan.this.finish();
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
