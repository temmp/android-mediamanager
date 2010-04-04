package local.mediamanager.listener;

import local.mediamanager.R;
import local.mediamanager.model.Media;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Listener wenn ein neues Medium angelegt wird. Ermittelt die in der GUI
 * eingegebenen Medieneigenschaften und veranlasst deren Speicherung in das XML
 * Dokument.
 * 
 * @author Joerg Langner
 */
public class AddMediaListener implements OnClickListener {

	private Activity activity;
	private final String MEDIA_SUCCESSFULLY_ADDED = "Medium erfolgreich angelegt.";

	public AddMediaListener(Activity activity) {
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
		media.setStatus(Media.STATUS.VORHANDEN.getName());
		media.setDate(Media.DEFAULT_DATE);
		media.setLegalOwner(Media.DEFAULT_LEGAL_OWNER);
		media.setOwner(Media.DEFAULT_OWNER);

		// Medium wird hinzugefuegt
		XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(activity);
		xmlEditor.addMedia(media);

		// Nachricht an Benutzer das Medium erfolgreich hinzugefuegt wurde
		Context context = activity.getApplicationContext();
		Toast.makeText(context, MEDIA_SUCCESSFULLY_ADDED, Toast.LENGTH_SHORT)
				.show();
		// Resultat des Medium anlegens
		activity.setResult(Activity.RESULT_OK);
		// activity wird geschlossen
		activity.finish();
	}
}
