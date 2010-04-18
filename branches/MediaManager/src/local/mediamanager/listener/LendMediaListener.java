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
 * Listener wenn ein Medium verliehen wird. Ermittelt die in der GUI
 * eingegebenen Medieneigenschaften und veranlasst deren Speicherung in das XML
 * Dokument.
 * 
 * @author Joerg Langner
 */
public class LendMediaListener implements OnClickListener {

	// Dialog Beschriftung wenn Medium erfolgreichv erliehen wurde
	private final String MEDIA_SUCCESSFULLY_ADDED = "Medium erfolgreich verliehen.";

	// HashMap aus [laufender Nummer & MediumID]
	private HashMap<Integer, Integer> filteredIDList;
	// Activity/GUI aus der die Informationen geholt werden
	private Activity activity;

	public LendMediaListener(Activity activity,
			HashMap<Integer, Integer> filteredIDList) {
		this.activity = activity;
		this.filteredIDList = filteredIDList;
	}

	@Override
	public void onClick(View v) {
		Spinner spMedia = (Spinner) activity.findViewById(R.id.spMedia);
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
		Media media = xmlEditor.getMediaByPosition(filteredIDList.get(spMedia
				.getSelectedItemPosition()));
		media.setOwner(contactIDMap.get(spLendto.getSelectedItemPosition())
				.toString());
		media.setDate(date);
		media.setStatus(Media.STATUS.VERLIEHEN.getName());
		// Medium wird hinzugefuegt
		xmlEditor.updateMediaByPosition(filteredIDList.get(spMedia
				.getSelectedItemPosition()), media);
		// Kalendereintrag erstellen falls die Checkbox gecheckt ist
		CheckBox calenderEntry = (CheckBox) activity
				.findViewById(R.id.cbCalEntry);
		if (calenderEntry.isChecked()) {
			Calendar calendar = new Calendar(activity);
			calendar.addCalendarEntry(dpLendtime.getYear(), dpLendtime
					.getMonth(), dpLendtime.getDayOfMonth(), media
					.getTitle());
		}
		// Nachricht des erfolgreichen Anlegens des Mediums an Benutzer
		Context context = activity.getApplicationContext();
		Toast.makeText(context, MEDIA_SUCCESSFULLY_ADDED, Toast.LENGTH_SHORT)
				.show();
		// activity wird beendet
		activity.finish();
	}
}
