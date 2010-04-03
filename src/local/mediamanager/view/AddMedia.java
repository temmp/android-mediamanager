package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.AddMediaListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
public class AddMedia extends Activity {

	private static final int MENU_ABOUT = 0;
	private static final int MENU_BACK = 1;
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
			builder
					.setMessage("(c) 2010 by \n- Jörg Langner \n- Andreas Wiedemann \n\n"
							+ "http://code.google.com/p/android-mediamanager");
			builder.setCancelable(false);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
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
