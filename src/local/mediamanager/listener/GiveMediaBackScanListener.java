package local.mediamanager.listener;

import local.mediamanager.model.Media;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GiveMediaBackScanListener implements OnClickListener {

	private Activity activity;
	private String barcode;

	private final String MEDIA_SUCCESSFULLY_SET_BACK = "Medium erfolgreich " +
			"zurückgemeldet.";

	public GiveMediaBackScanListener(Activity activity, String barcode) {
		this.activity = activity;
		this.barcode = barcode;
	}

	@Override
	public void onClick(View arg0) {
		XMLMediaFileEditor parser = new XMLMediaFileEditor(activity);
		Media media = parser.getMediaByBarcode(barcode);
		
		// es muss unterschieden werden ob das Medium verliehen oder entliehen
		// ist
		if (media.getStatus().equals(Media.STATUS.VERLIEHEN.getName())) {
			// wenn das Medium verliehen ist muss der owner auf den
			// DEFAULT_OWNER gesetzt werden, da ja das Medium wieder in den
			// "eigenen" Besitz uebergeht
			media.setOwner(Media.DEFAULT_OWNER);
			// default Datum setzen
			media.setDate(Media.DEFAULT_DATE);
			// das Medium auf "vorhanden" setzen
			media.setStatus(Media.STATUS.VORHANDEN.getName());
			// Medium in xml updaten dh zurueckmelden
			parser.updateMediaByBarcode(barcode, media);
		} else if (media.getStatus().equals(Media.STATUS.ENTLIEHEN.getName())) {
			// wenn das Medium entliehen ist dann wird das Medium aus dem
			// MediaManager geloescht da fremde Medien, die nicht im Moment
			// entliehen sind, nicht gespeichert werden
			parser.removeMediaByBarcode(media.getBarcode());
		}
		
		// Nachricht an Benutzer das Medium erfolgreich zurückgemeldet wurde
		Context context = activity.getApplicationContext();
		Toast.makeText(context, MEDIA_SUCCESSFULLY_SET_BACK, Toast.LENGTH_SHORT)
				.show();
		// activity wird geschlossen
		activity.finish();
	}

}
