package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.AddMediaListener;
import local.mediamanager.listener.BorrowMediaListener;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.itemlookup.AmazonItemLookup;
import local.mediamanager.util.scan.ScanIntentIntegrator;
import local.mediamanager.util.scan.ScanResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//TODO test> horizontales drehen nicht mehr moeglich oder static variable wegen..
//drehen..das scanner nur EINMAL aufgerufen wird

//TODO toasts als dialog

/**
 * GUI zum Scannen eines Mediums. Ist der Barcode Scanner nicht installiert so
 * wird der Benutzer danach gefragt ob der MediaManager den Barcode Scanner
 * installieren soll.
 * 
 * @author Jörg Langner
 */
public class ScanMedia extends Activity {

	private static final int MENU_ABOUT = 0;
	private static final int MENU_BACK = 1;
	private final String MEDIA_NOT_FOUND = "Das Medium konnte von Amazon nicht gefunden"
			+ " werden.";
	private final String MEDIA_SCAN_ABORTED = "Scan Vorgang abgebrochen.";
	private final String BARCODE_NOT_SCANNED = "Barcode konnte nicht gescannt werden.";
	// Typ ist entweder Scannen fuer "Medium Scannen" oder Scannen fuer
	// "Medium entleihen"
	private String type; // Typ kommt von Intent Extra
	public static String TYPE_ADD_MEDIA_SCAN = "ADD_MEDIA_SCAN";
	public static String TYPE_BORROW_MEDIA_SCAN = "BORROW_MEDIA_SCAN";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent in = this.getIntent();
		type = in.getStringExtra("type");
		ScanIntentIntegrator.initiateScan(this);
	}

	/**
	 * Diese Methode wird aufgerufen sobald die Scan Activity, d.h. der
	 * Scanvorgang, beendet ist. Das Ergebenis des Scannens(erfolgreich oder
	 * nicht) wird dem Benutzer dargestellt/mitgeteilt.
	 * 
	 * @param requestCode
	 *            Anfragecode des Intents (hier
	 *            ScanIntentIntegrator.REQUEST_CODE)
	 * @param resultCode
	 *            Ergebniscode der Activity (erfolgreicht, abgebrochen etc.)
	 * @param intent
	 *            Der Intent mit dem Barcode und dem Scanformat
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Progress Dialog starten, da die Anfrage an Amazon und deren
		// Antwort (das XML Dokument) ein paar Sekunden dauern kann.
		// Zudem muss das XML Dokument noch geparst werden.
		ProgressDialog progressDialog = ProgressDialog.show(this, "",
				"Bitte warten...", true);
		// Ergebnis des Scanvorgangs eines Mediums wird geholt
		ScanResult scanResult = ScanIntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			if (scanResult.getBarcode() != null) {
				String uri = AmazonItemLookup.createRequestURL(scanResult
						.getBarcode());
				final Media media = AmazonItemLookup.fetchMedia(uri);
				if (media != null) {
					// Das ItemLoopkUp bei Amazon ist beendet und das XML
					// Dokument wurde geparst.
					// D.h. alle Informationen des Mediums stehen nun zur
					// Verfuegung und der ProgressDialog wird geschlossen
					progressDialog.dismiss();
					// je nachdem von wo der Aufruf des Scannens kam, so wird
					// das GUI entsprechende Layout gesetzt und mit den
					// Medieninformationen aus dem Scanvorgang gefuellt
					if (type.equals(TYPE_ADD_MEDIA_SCAN)) {
						// Layout AddMedia
						setContentView(R.layout.addmedia);
						// Mediendaten in die GUI eintragen
						setMediaProperties(media);
						// setzt den "Medium anlegen" Button Listener
						setAddMediaProperties();
					}
					if (type.equals(TYPE_BORROW_MEDIA_SCAN)) {
						// Layout BorrowMedia
						setContentView(R.layout.borrowmedia);
						// Mediendaten in die GUI eintragen
						setMediaProperties(media);
						// fuellt den Konakt Spinner mit Kontakten und setzt den
						// "Medien entleihen" Buttons Listener
						setBorrowMediaProperties();
					}
				} else {
					// Barcode wurde bei Amazon nicht gefunden
					Context context = getApplicationContext();
					Toast.makeText(context, MEDIA_NOT_FOUND, Toast.LENGTH_LONG)
							.show();
				}
			} else {
				// Benutzer hat Scanvorgang abgebrochen
				Context context = getApplicationContext();
				Toast.makeText(context, MEDIA_SCAN_ABORTED, Toast.LENGTH_LONG)
						.show();
			}
		} else {
			// Barcode konnte nicht gelesen werden
			Context context = getApplicationContext();
			Toast.makeText(context, BARCODE_NOT_SCANNED, Toast.LENGTH_LONG)
					.show();
			this.finish();
		}
	}

	/**
	 * Setzt den "Neues Medium anlegen"-Button Listener
	 */
	private void setAddMediaProperties() {
		// "Medium anlegen" Button
		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new AddMediaListener(this));
	}

	/**
	 * Fuellt die Kontaktliste und setzt den "Entliehene Medium anlegen"-Button
	 * Listener
	 */
	private void setBorrowMediaProperties() {
		// Spinner "Medium entleihen an" mit Kontaktliste füllen
		Spinner spLegalOwner = (Spinner) findViewById(R.id.spLegalOwner);
		Contact contactNameList = new Contact(this);
		ArrayAdapter<CharSequence> contactAdapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, contactNameList
						.getContactNameList());
		spLegalOwner.setAdapter(contactAdapter);
		// "Medium anlegen" Button
		Button btBorrowSave = (Button) findViewById(R.id.btBorrowSave);
		btBorrowSave.setOnClickListener(new BorrowMediaListener(this));
	}

	/**
	 * Setzt die Eigenschaften (Titel, Autor etc.) des uebergebene Media Objekts
	 * in die GUI Elemente
	 * 
	 * @param media
	 *            Media Objekt mit den Eigenschaften die in der GUI angezeigt
	 *            werden
	 */
	private void setMediaProperties(Media media) {
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
	}
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_ABOUT, 0, "Info").setIcon(R.drawable.about);
	    menu.add(0, MENU_BACK, 0, "Zurück").setIcon(R.drawable.end);
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_ABOUT:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setIcon(R.drawable.icon);
	    	builder.setTitle("Über MediaManager 1.0");
	    	builder.setMessage("(c) 2010 by \n- Jörg Langner \n- Andreas Wiedemann \n\n" + "http://code.google.com/p/android-mediamanager");
	    	builder.setCancelable(false);
	    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					dialog.cancel();
	    	           }
	    	       });
	    	AlertDialog alert = builder.create();
	    	alert.show();
	        return true;
	    case MENU_BACK:
	        this.finish();
	        return true;
	    }
	    return false;
	}
}
