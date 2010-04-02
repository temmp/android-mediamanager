package local.mediamanager.view;

import local.mediamanager.util.scan.ScanIntentIntegrator;
import local.mediamanager.util.scan.ScanResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ScanMediaNew extends Activity {

	// Konstanten fuer die Fehlermeldungen
	private final String MEDIA_SCAN_ABORTED = "Scan Vorgang abgebrochen.";
	private final String BARCODE_NOT_SCANNED = "Barcode konnte nicht gescannt werden.";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Barcode Scanner starten
		ScanIntentIntegrator.initiateScan(this);
	}

	/**
	 * Diese Methode wird aufgerufen sobald die Scan Activity, d.h. der
	 * Scanvorgang, beendet ist. Falls das Ergebnis des Scannens nicht
	 * erfolgreich war, wird dieses dem Benutzer mitgeteilt.
	 * 
	 * @param requestCode
	 *            Anfragecode des Intents (hier
	 *            ScanIntentIntegrator.REQUEST_CODE)
	 * @param resultCode
	 *            Ergebniscode der Activity (erfolgreich, abgebrochen)
	 * @param intent
	 *            Der Intent mit dem Barcode und dem Scanformat
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Ergebnis des Scanvorgangs eines Mediums wird geholt
		ScanResult scanResult = ScanIntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			if (scanResult.getBarcode() != null) {
				Intent setText = new Intent();
				setText.putExtra("barcode", scanResult.getBarcode());
				setResult(Activity.RESULT_OK, setText);
				ScanMediaNew.this.finish();
				// jetzt wird die onActivityResult Methode der aufrufenden
				// Actitvity aufgerufen
			} else {
				// Benutzer hat Scanvorgang abgebrochen
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(MEDIA_SCAN_ABORTED).setCancelable(false)
						.setNeutralButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										setResult(Activity.RESULT_CANCELED);
										ScanMediaNew.this.finish();
										// jetzt wird die onActivityResult
										// Methode der aufrufenden
										// Actitvity aufgerufen
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		} else {
			// Barcode konnte nicht gelesen werden
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(BARCODE_NOT_SCANNED).setCancelable(false)
					.setNeutralButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									setResult(Activity.RESULT_CANCELED);
									ScanMediaNew.this.finish();
									// jetzt wird die onActivityResult Methode
									// der aufrufenden
									// Actitvity aufgerufen
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}