package local.mediamanager.view.sharedmenues;

import local.mediamanager.R;
import android.app.Dialog;
import android.app.ListActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SharedListActivity extends ListActivity {

	// Menu Optionen
	private static final int MENU_ABOUT = 0;
	private static final int MENU_BACK = 1;

	// Beschriftungen der Menuoptionen
	private static final String MENU_OPTION_INFO = "Info";
	private static final String MENU_OPTION_CLOSE = "Beenden";

	// Beschriftungen Info Dialog
	private static final String INFO_DIALOG_TITLE = "Über MediaManager 1.0";
	private static final String INFO_DIALOG_CONTENT = "(c) 2010 by \n- Jörg Langner \n- Andreas Wiedemann \n";
	private static final String INFO_DIALOG_URL = "<a href=\"http://code.google.com/p/android-mediamanager\";>MediaManager Projekt Homepage</a><br>";
	private static final String BUTTON_LABEL = "OK";

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
			// Dialog
			final Dialog dialog = new Dialog(this);
			dialog.setTitle(INFO_DIALOG_TITLE);
			// Dialog Layout
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			// Textinhalt des Dialogs
			TextView content = new TextView(this);
			content.setText(INFO_DIALOG_CONTENT);
			content.setGravity(Gravity.CENTER);
			// klickbarer Link im Dialog
			TextView link = new TextView(this);
			link.setText(Html.fromHtml(INFO_DIALOG_URL));
			link.setMovementMethod(LinkMovementMethod.getInstance());
			// OK Button
			Button btOK = new Button(this);
			btOK.setText(BUTTON_LABEL);
			btOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// der Dialog wird geschlossen
					dialog.dismiss();
				}
			});
			layout.addView(content);
			layout.addView(link);			
			layout.addView(btOK);
			dialog.setContentView(layout);
			dialog.setOwnerActivity(this);
			dialog.show();
			return true;
		case MENU_BACK:
			this.finish();
			return true;
		}
		return false;
	}
}
