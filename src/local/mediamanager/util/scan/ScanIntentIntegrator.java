package local.mediamanager.util.scan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

/**
 * Diese Klasse startet den Scanvorgang. Falls der Barcode Scanner bereits
 * installiert ist wird dieser gestartet. Falls nicht erscheint ein Dialog in
 * dem der Benutzer geefragt wird ob er diesen installieren möchte. Zudem stellt
 * die Klasse die Methode bereit mit der das Ergenis des Scannes abgerufen
 * werden kann.
 * 
 * @author Jörg Langner
 */

public final class ScanIntentIntegrator {
	// Anfragecode des Intents. Dient der Identifikation der Anfrage.
	public static final int REQUEST_CODE = 0;

	// Beschriftungen des Dialogs falls der Barcode Scanner noch nicht
	// installiert ist
	private static final String DEFAULT_TITLE = "Barcode Scanner installieren?";
	private static final String DEFAULT_MESSAGE = "Diese Anwendungen benötigt"
			+ " den Barcode Scanner. Möchten sie diesen installieren?";
	private static final String DEFAULT_YES = "Ja";
	private static final String DEFAULT_NO = "Nein";

	// Name des Barcode Intents welcher gestartet wird
	private static final String SCAN_INTENT_URI = "com.google.zxing.client.android.SCAN";

	// Falls der Scanner nicht installiert ist wird unter folgender URI nach dem
	// Scanner im Android Market gesucht
	private static final String SCANNER_MARKET_URI = "market://search?q=pname:com.google.zxing.client.android";

	// Nachricht die dem Benutzer erscheint falls der Scanner im Android Market
	// nicht gefunden werden konnte
	private static final String SCANNER_NOT_FOUND = "Barcode Scanner Activity"
			+ " konnte im Android Market nicht gefunden werden.";

	/**
	 * Initilisiert den ScanIntentIntegrator mit den default-Werten fuer den
	 * Dialog.
	 * 
	 * @param activity
	 *            Activity, von welcher aus gescannt wird
	 */
	public static void initiateScan(Activity activity) {
		initiateScan(activity, DEFAULT_TITLE, DEFAULT_MESSAGE, DEFAULT_YES,
				DEFAULT_NO);
	}

	/**
	 * Startet den eigentlichen Scanvorgang. Falls kein Barcode Scanner
	 * installiert ist wird ein Dialog erstellt mit dessen dieser installiert
	 * werden kann. Falls dieser schon vorhanden kann wird der eigentliche
	 * ScanIntent aufgerufen.
	 * 
	 * @param stringTitle
	 *            Titel des Dialogs
	 * @param stringMessage
	 *            Text des Dialogs
	 * @param stringButtonYes
	 *            Beschriftung des Ja-Buttons
	 * @param stringButtonNo
	 *            Beschriftung des Nein-Buttons
	 * @throws InterruptedException
	 *             Fehler beim scannen (Timeout)
	 */
	public static void initiateScan(Activity activity, String stringTitle,
			String stringMessage, String stringButtonYes, String stringButtonNo) {
		Intent intentScan = new Intent(SCAN_INTENT_URI);
		intentScan.addCategory(Intent.CATEGORY_DEFAULT);
		try {
			activity.startActivityForResult(intentScan, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			showDownloadDialog(activity, stringTitle, stringMessage,
					stringButtonYes, stringButtonNo);
		}
	}

	/**
	 * Erstellt einen Dialog in welchem der Benutzer gefragt wird, ob er den
	 * Barcode Scanner installieren moechte oder nicht.
	 * 
	 * @param activity
	 *            Activity, von welcher aus gescannt wird
	 * @param stringTitle
	 *            Titel des Dialogs
	 * @param stringMessage
	 *            Text des Dialogs
	 * @param stringButtonYes
	 *            Beschriftung des Ja-Buttons
	 * @param stringButtonNo
	 *            Beschriftung des Nein-Buttons
	 */
	private static void showDownloadDialog(final Activity activity,
			String stringTitle, String stringMessage, String stringButtonYes,
			String stringButtonNo) {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
		downloadDialog.setTitle(stringTitle);
		downloadDialog.setMessage(stringMessage);
		downloadDialog.setPositiveButton(stringButtonYes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Uri uri = Uri.parse(SCANNER_MARKET_URI);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						try {
							activity.startActivity(intent);
						} catch (ActivityNotFoundException e) {
							// Scanner im android market nicht gefunden
							AlertDialog.Builder builder = new AlertDialog.Builder(activity);
							builder.setMessage(SCANNER_NOT_FOUND).setCancelable(false)
									.setNeutralButton("Ok",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,
														int id) {
													activity.finish();
												}
											});
							AlertDialog alert = builder.create();
							alert.show();
						}
					}
				});
		downloadDialog.setNegativeButton(stringButtonNo,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				});
		downloadDialog.show();
	}

	/**
	 * Über diese Methode kann das Ergenis des Scanvorgangs abgefragt werden.
	 * 
	 * @param requestCode
	 *            Anfragecode des Intents (hier
	 *            ScanIntentIntegrator.REQUEST_CODE)
	 * @param resultCode
	 *            Ergebniscode der Activity (erfolgreicht, abgebrochen etc.)
	 * @param intent
	 *            Der Intent mit dem Barcode und dem Scanformat
	 * 
	 * @return Null wenn der event der hier gehandelt wird nicht zum
	 *         ScanIntentIntegrator passt, oder ein IntentResult Object mit dem
	 *         Ergenis des Scanvorgangs. Wenn der Benutzer das Scannen
	 *         abgebrochen hat wird ein IntentResult Object mit null-Werten
	 *         zurueckgegeben.
	 */
	public static ScanResult parseActivityResult(int requestCode,
			int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				// Barcode
				String barcode = intent.getStringExtra("SCAN_RESULT");
				// Format des Barcodes
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				return new ScanResult(barcode, format);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				return new ScanResult(null, null);
			}
		}
		return null;
	}

}
