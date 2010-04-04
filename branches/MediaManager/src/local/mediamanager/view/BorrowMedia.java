package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.BorrowMediaListener;
import local.mediamanager.util.Contact;
import local.mediamanager.view.sharedmenues.SharedActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * GUI zum manuellen Entleihen eines Mediums.
 * 
 * @author Andreas Wiedemann
 */
public class BorrowMedia extends SharedActivity {

	// Spinner
	private Spinner spMediatype;
	private Spinner spLegalOwner;

	// Intent extra namen
	public static final String BARCODE = "barcode";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.borrowmedia);

		// es wird ueberprueft ob die aufrufende Activity dieser Activity einen
		// Barcode mitgegeben hat. Falls ja wird dieser in die GUI eingetragen.
		Intent in = getIntent();
		if (in.getStringExtra(BARCODE) != null) {
			EditText etBarcode = (EditText) findViewById(R.id.etBarcode);
			etBarcode.setText(in.getStringExtra(BARCODE));
		}

		// Spinner "Medientyp" mit den verfuegbaren Medientypen fuellen
		spMediatype = (Spinner) findViewById(R.id.spMediatype);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.mediatypes, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMediatype.setAdapter(adapter);

		// Spinner "Medium entleihen an" mit Kontaktliste füllen
		spLegalOwner = (Spinner) findViewById(R.id.spLegalOwner);
		Contact contactNameList = new Contact(this);
		ArrayAdapter<CharSequence> contactAdapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, contactNameList
						.getContactNameList());
		spLegalOwner.setAdapter(contactAdapter);

		// Button zum speichern des Mediums
		Button btBorrowSave = (Button) findViewById(R.id.btBorrowSave);
		btBorrowSave.setOnClickListener(new BorrowMediaListener(this));
	}
}
