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
 * Listener wenn ein eingescanntes Medium verliehen wird. Ermittelt die in der GUI
 * eingegebenen Medieneigenschaften und veranlasst deren Speicherung in das XML
 * Dokument.
 * 
 * @author Joerg Langner
 */
public class LendMediaScanListener implements OnClickListener {

	// Informationstext wenn Medium angelegt wurde
	private final String MEDIA_SUCCESSFULLY_LENT = "Medium erfolgreich verliehen.";
	
	private Activity activity;
	private final String barcode;
	
	public LendMediaScanListener(Activity activity, String barcode){
		this.activity = activity;
		this.barcode = barcode;
	}
	
	@Override
	public void onClick(View arg0) {
		Spinner spLendto = (Spinner) activity.findViewById(R.id.spLendto);
		DatePicker dpLendtime = (DatePicker) activity.findViewById(R.id.dpLendtime);
		Contact contacts = new Contact(activity);
		HashMap<Integer, Integer> contactIDMap = contacts
				.getContactIDMap();
		Date dateObject = new Date(dpLendtime.getDayOfMonth(),
				dpLendtime.getMonth() + 1, dpLendtime.getYear());
		String date = dateObject.getDate();
		XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(
				activity);
		Media media = xmlEditor.getMediaByBarcode(barcode);
		media.setOwner(contactIDMap.get(
				spLendto.getSelectedItemPosition()).toString());
		media.setDate(date);
		media.setStatus(Media.STATUS.VERLIEHEN.getName());
		media.setLegalOwner(Media.DEFAULT_LEGAL_OWNER);
		// Medium updaten
		xmlEditor.updateMediaByBarcode(barcode, media);
		Context context = activity.getApplicationContext();
		// nachricht an Benutzer das medium erfolgreich
		// verliehen wurde
		Toast.makeText(context, MEDIA_SUCCESSFULLY_LENT,
				Toast.LENGTH_SHORT).show();
		activity.finish();
	}

}
