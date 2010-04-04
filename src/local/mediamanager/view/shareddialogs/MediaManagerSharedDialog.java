package local.mediamanager.view.shareddialogs;

import local.mediamanager.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MediaManagerSharedDialog {

	private Dialog dialog;

	private Activity activity;
	private Intent manualIntent;
	private Intent scanIntent;
	private String dialogContentText;
	private String dialogTitle;

	/**
	 * Erzeugt den Dialog ob ein Medium manuell oder per scannen angelegt,
	 * verliehen, entliehen oder zurueckgemeldet werden soll.
	 * 
	 * @param activity
	 *            Aufrufende Activity
	 * @param manuallyIntent
	 *            Intent der gestartet wird wenn das Medium manuell angelegt
	 *            oder verliehen oder entliehen oder zurueckgemeldet wird
	 * @param scanIntent
	 *            Intent der gestartet wird wenn das Medium gescannt werden soll
	 * @param dialogText
	 *            Text der im Dialog angezeigt wird
	 * @param dialogTitel
	 *            Der Titel der im Dialog angezeigt werden soll
	 */
	public MediaManagerSharedDialog(Activity activity, Intent manualIntent,
			Intent scanIntent, String dialogContentText, String dialogTitle) {
		this.activity = activity;
		this.manualIntent = manualIntent;
		this.scanIntent = scanIntent;
		this.dialogContentText = dialogContentText;
		this.dialogTitle = dialogTitle;
	}

	/**
	 * Zeigt den Dialog an.
	 */
	public void show() {
		// Dialog initialisieren
		dialog = new Dialog(activity);
		// Layout des Dialogs setzen
		dialog.setContentView(R.layout.mediamanager_shared_dialog);
		// Dialog Titel setzen
		dialog.setTitle(dialogTitle);
		// Text des Dialogs setzen
		TextView text = (TextView) dialog.findViewById(R.id.tvDialog);
		text.setText(dialogContentText);
		// ButtonListener fuer "manuell" Button
		Button manuallyButton = (Button) dialog
				.findViewById(R.id.btDialogManually);
		manuallyButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Dialog beenden...
				dialog.dismiss();
				// .. und activity starten
				activity.startActivity(manualIntent);
			}

		});
		// ButtonListener fuer "scannen" Button
		Button scanButton = (Button) dialog.findViewById(R.id.btDialogScan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Dialog beenden...
				dialog.dismiss();
				// .. und activity starten
				activity.startActivity(scanIntent);
			}

		});
		// Dialog anzeigen
		dialog.setOwnerActivity(activity);
		dialog.show();
	}
}
