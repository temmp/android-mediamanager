package local.mediamanager.view;

import local.mediamanager.R;
import local.mediamanager.listener.AddMediaListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


/**
 * GUI zum manuellen Hinzufuegen eines Mediums.
 * 
 * @author Andreas Wiedemann
 */
public class AddMedia extends Activity {

	private static final int MENU_ABOUT = 0;
	private static final int MENU_BACK = 1;
	private Spinner spMediatype;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addmedia);

		spMediatype = (Spinner) findViewById(R.id.spMediatype);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.mediatypes, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMediatype.setAdapter(adapter);

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
