package local.mediamanager.listener;

import java.util.HashMap;

import local.mediamanager.R;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.Date;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Listener wenn ein entliehenes Medium angelegt wird. Ermittelt die in der GUI
 * eingegebenen Medieneigenschaften und veranlasst deren Speicherung in das XML
 * Dokument.
 * 
 * @author Joerg Langner
 */
public class BorrowMediaListener implements OnClickListener {

	private Activity activity;
	private final String MEDIA_SUCCESSFULLY_ADDED = "Medium erfolgreich angelegt.";

	public BorrowMediaListener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		Media media = new Media();
		EditText etBarcode = (EditText) activity.findViewById(R.id.etBarcode);
		media.setBarcode(etBarcode.getText().toString());
		EditText etTitle = (EditText) activity.findViewById(R.id.etTitle);
		media.setTitle(etTitle.getText().toString());
		EditText etAuthor = (EditText) activity.findViewById(R.id.etAuthor);
		media.setAuthor(etAuthor.getText().toString());
		Spinner spMediatype = (Spinner) activity.findViewById(R.id.spMediatype);
		media.setType(spMediatype.getSelectedItem().toString());
		media.setStatus(Media.STATUS.ENTLIEHEN.getName());
		// die ID des Kontaktes wird anhand des im Spinner ausgewaehlten
		// Kontaktes ermittelt
		Contact contacts = new Contact(activity);
		HashMap<Integer, Integer> contactIDMap = contacts.getContactIDMap();
		Spinner spLegalOwner = (Spinner) activity
				.findViewById(R.id.spLegalOwner);
		media.setLegalOwner(contactIDMap.get(
				spLegalOwner.getSelectedItemPosition()).toString());
		media.setOwner(Media.DEFAULT_OWNER);
		// Ausleihdatum ermitteln
		DatePicker dpBorrowtime = (DatePicker) activity
				.findViewById(R.id.dpBorrowtime);
		Date dateObject = new Date(dpBorrowtime.getDayOfMonth(), dpBorrowtime
				.getMonth() + 1, dpBorrowtime.getYear());
		String date = dateObject.getDate();
		media.setDate(date);
		// Medium wird hinzugefuegt
		XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(activity);
		xmlEditor.addMedia(media);
		// Nachricht des erfolgreichen Anlegens des Mediums an Benutzer
		Context context = activity.getApplicationContext();
		Toast.makeText(context, MEDIA_SUCCESSFULLY_ADDED, Toast.LENGTH_SHORT)
				.show();
		// activity wird beendet
		activity.finish();
	}

}
