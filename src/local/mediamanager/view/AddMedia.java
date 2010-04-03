package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.AddMediaListener;
import local.mediamanager.view.menuhelper.SharedActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

//TODO medium kann doppelt angelegt werden
/**
 * GUI zum manuellen Hinzufuegen eines Mediums.
 * 
 * @author Andreas Wiedemann 
 */
public class AddMedia extends SharedActivity {

	// Spinner
	private Spinner spMediatype;

	// Intent extra namen
	public static final String NAME_OF_EXTRA_DATA = "scanned";
	public static final String VALUE_OF_EXTRA_DATA = "true";
	public static final String BARCODE = "barcode";
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";
	public static final String TYPE = "type";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addmedia);

		spMediatype = (Spinner) findViewById(R.id.spMediatype);

		Intent in = getIntent();
		String text = in.getStringExtra(NAME_OF_EXTRA_DATA);
		// es wurde ein Intent mit den Medieneigenschaften durch die aufrufende
		// Activity mitgegeben
		if (text != null && text.equals(VALUE_OF_EXTRA_DATA)) {
			// Medieneingenschaften in die GUI eintragen
			EditText etBarcode = (EditText) findViewById(R.id.etBarcode);
			etBarcode.setText(in.getStringExtra(in
					.getStringExtra(BARCODE)));
			EditText etTitle = (EditText) findViewById(R.id.etTitle);
			etTitle.setText(in
					.getStringExtra(in.getStringExtra(TITLE)));
			EditText etAuthor = (EditText) findViewById(R.id.etAuthor);
			etAuthor.setText(in.getStringExtra(in
					.getStringExtra(AUTHOR)));
			spMediatype.setEnabled(false);
			ArrayAdapter<CharSequence> spinnerAdapter;
			spinnerAdapter = new ArrayAdapter<CharSequence>(this,
					android.R.layout.simple_spinner_item);
			spinnerAdapter.add(in.getStringExtra(TYPE));
			spMediatype.setAdapter(spinnerAdapter);
		}
		// es wurden keine Medienionformation durch die aufrufende Activity
		// mitgegeben
		else {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.mediatypes,
							android.R.layout.simple_spinner_item);
			adapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spMediatype.setAdapter(adapter);
		}
		// Button zum Speichern des Mediums
		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new AddMediaListener(this));
	}
}
