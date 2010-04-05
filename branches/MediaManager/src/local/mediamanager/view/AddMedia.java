package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.AddMediaListener;
import local.mediamanager.view.sharedmenues.SharedActivity;
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

	// GUI Elemente
	private EditText etBarcode;
	private EditText etTitle;
	private EditText etAuthor;
	private Spinner spMediatype;
	private Button btSave;
	private ArrayAdapter<CharSequence> spinnerAdapter;

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
			etBarcode = (EditText) findViewById(R.id.etBarcode);
			etBarcode.setText(in.getStringExtra(BARCODE));
		}
		if (in.getStringExtra(TITLE) != null) {
			etTitle = (EditText) findViewById(R.id.etTitle);
			etTitle.setText(in.getStringExtra(TITLE));
		}
		if (in.getStringExtra(AUTHOR) != null) {
			etAuthor = (EditText) findViewById(R.id.etAuthor);
			etAuthor.setText(in.getStringExtra(AUTHOR));
		}
		if (in.getStringExtra(TYPE) != null) {
			Log.i("log", "ungleich null");
			spMediatype.setEnabled(false);
			spinnerAdapter = new ArrayAdapter<CharSequence>(this,
					android.R.layout.simple_spinner_item);
			spinnerAdapter.add(in.getStringExtra(TYPE));
			spMediatype.setAdapter(spinnerAdapter);
		}
		if(in.getStringExtra(TYPE) == null) {
			// wenn kein typ durch die aufrufende activity uebergeben wurde dann
			// wird der spinner mit allen verfuegbaren medientypen gefuellt
			spinnerAdapter = ArrayAdapter
					.createFromResource(this, R.array.mediatypes,
							android.R.layout.simple_spinner_item);
			spinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spMediatype.setAdapter(spinnerAdapter);
		}
		// Button zum Speichern des Mediums
		btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new AddMediaListener(this));
	}
}
