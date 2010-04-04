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
		Contact contacts = new Contact(activity);
		HashMap<Integer, Integer> contactIDMap = contacts.getContactIDMap();
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
		xmlEditor.updateMediaByPosition(filteredIDList.get(spMedia
				.getSelectedItemPosition()), media);
		Context context = activity.getApplicationContext();
		Toast.makeText(context, MEDIA_SUCCESSFULLY_ADDED, Toast.LENGTH_SHORT)
				.show();
		activity.finish();
	}
}
