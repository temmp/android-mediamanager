package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.AddMediaListener;
import local.mediamanager.view.menuhelper.SharedActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

	// Intent request code
	public static final int ADD_MEDIA_REQUEST_CODE = 0;

	// Intent extra namen
	public static final String BARCODE = "barcode";
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";
	public static final String TYPE = "type";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addmedia);
		spMediatype = (Spinner) findViewById(R.id.spMediatype);

		Intent in = getIntent();
		if (in.getStringExtra(BARCODE) != null) {
			EditText etBarcode = (EditText) findViewById(R.id.etBarcode);
			etBarcode.setText(in.getStringExtra(BARCODE));
		}
		if (in.getStringExtra(TITLE) != null) {
			EditText etTitle = (EditText) findViewById(R.id.etTitle);
			etTitle.setText(in.getStringExtra(TITLE));
		}
		if (in.getStringExtra(AUTHOR) != null) {
			EditText etAuthor = (EditText) findViewById(R.id.etAuthor);
			etAuthor.setText(in.getStringExtra(AUTHOR));
		}
		if (in.getStringExtra(TYPE) != null) {
			Log.i("log", "ungleich null");
			spMediatype.setEnabled(false);
			ArrayAdapter<CharSequence> spinnerAdapter;
			spinnerAdapter = new ArrayAdapter<CharSequence>(this,
					android.R.layout.simple_spinner_item);
			spinnerAdapter.add(in.getStringExtra(TYPE));
			spMediatype.setAdapter(spinnerAdapter);
		}
		if(in.getStringExtra(TYPE) == null) {
			// wenn kein typ durch die aufrufende activity uebergeben wurde dann
			// wird der spinner mit allen verfuegbaren medientypen gefuellt
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
