package local.mediamanager.listener;

import java.util.HashMap;

import local.mediamanager.R;
import local.mediamanager.model.Media;
import local.mediamanager.util.Calendar;
import local.mediamanager.util.Contact;
import local.mediamanager.util.Date;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Listener wenn ein eingescanntes Medium verliehen wird. Ermittelt die in der
 * GUI eingegebenen Medieneigenschaften und veranlasst deren Speicherung in das
 * XML Dokument.
 * 
 * @author Joerg Langner
 */
public class LendMediaScanListener implements OnClickListener {

	// Informationstext wenn Medium angelegt wurde
	private final String MEDIA_SUCCESSFULLY_LENT = "Medium erfolgreich verliehen.";

	private Activity activity;
	private final String barcode;

	public LendMediaScanListener(Activity activity, String barcode) {
		this.activity = activity;
		this.barcode = barcode;
	}

	@Override
	public void onClick(View arg0) {
		Spinner spLendto = (Spinner) activity.findViewById(R.id.spLendto);
		DatePicker dpLendtime = (DatePicker) activity
				.findViewById(R.id.dpLendtime);
		// die ID des Kontaktes wird anhand des im Spinner ausgewaehlten
		// Kontaktes ermittelt
		Contact contacts = new Contact(activity);
		HashMap<Integer, Integer> contactIDMap = contacts.getContactIDMap();
		// Ausleihdatum ermitteln
		Date dateObject = new Date(dpLendtime.getDayOfMonth(), dpLendtime
				.getMonth() + 1, dpLendtime.getYear());
		String date = dateObject.getDate();
		XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(activity);
		Media media = xmlEditor.getMediaByBarcode(barcode);
		media.setOwner(contactIDMap.get(spLendto.getSelectedItemPosition())
				.toString());
		media.setDate(date);
		media.setStatus(Media.STATUS.VERLIEHEN.getName());
		// Medium updaten
		xmlEditor.updateMediaByBarcode(barcode, media);
		// Kalendereintrag erstellen falls die Checkbox gecheckt ist
		CheckBox calenderEntry = (CheckBox) activity
				.findViewById(R.id.cbCalEntry);
		if (calenderEntry.isChecked()) {
			Calendar calendar = new Calendar(activity);
			calendar.addCalendarEntry(dpLendtime.getYear(), dpLendtime
					.getMonth(), dpLendtime.getDayOfMonth(), media
					.getTitle());
		}
		// nachricht an Benutzer das medium erfolgreich
		// verliehen wurde
		Context context = activity.getApplicationContext();
		Toast.makeText(context, MEDIA_SUCCESSFULLY_LENT, Toast.LENGTH_SHORT)
				.show();
		activity.finish();
	}

}
