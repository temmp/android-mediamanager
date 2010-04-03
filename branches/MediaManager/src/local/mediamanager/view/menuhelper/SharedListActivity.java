package local.mediamanager.view.menuhelper;

import local.mediamanager.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

public class SharedListActivity extends ListActivity {

	// Menu Optionen
	private static final int MENU_ABOUT = 0;
	private static final int MENU_BACK = 1;

	// Beschriftungen der Menuoptionen
	private static final String MENU_OPTION_INFO = "Info";
	private static final String MENU_OPTION_CLOSE = "Beenden";

	// Beschriftungen Info Dialog
	private static final String INFO_DIALOG_TITLE = "Über MediaManager 1.0";
	private static final String INFO_DIALOG_CONTENT = "(c) 2010 by \n- Jörg"
			+ " Langner \n- Andreas Wiedemann \n\n"
			+ "http://code.google.com/p/android-mediamanager";

	/* Menu Items werden erstellt */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ABOUT, 0, MENU_OPTION_INFO).setIcon(R.drawable.about);
		menu.add(0, MENU_BACK, 0, MENU_OPTION_CLOSE).setIcon(R.drawable.end);
		return true;
	}

	/* Wird aufgerufen wenn ein Menuitem ausgewaehlt wird */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ABOUT:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.icon);
			builder.setTitle(INFO_DIALOG_TITLE);
			builder.setMessage(INFO_DIALOG_CONTENT);
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
