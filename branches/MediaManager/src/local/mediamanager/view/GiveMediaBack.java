package local.mediamanager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import local.mediamanager.model.Media;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.view.sharedmenues.SharedListActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * GUI fuer das manuelle Zurückmelden eines Mediums. Zeigt eine Liste an mit
 * allen verliehenen und entliehenen Medien. Bei der Auswahl eines Mediums wird
 * ein Dialog angezeigt ob der Benutzer dieses Medium zurueckmelden moechte.
 * 
 * @author Joerg Langner
 */
public class GiveMediaBack extends SharedListActivity {

	// GUI Elemente
	private ListView listView;
	private TextView headerText;

	// Beschriftungen
	private final String HEADER_TEXT = "<b>Wählen Sie das Medium aus welches"
			+ " Sie zurückmelden möchten<b>";

	// Nachricht an Benutzer wenn medium zurueckgemeldet wurde
	final String MEDIA_IS_BACK = "Medium wurde zurückgemeldet.";

	// Beschriftung fuer Dialog ob er das ausgewaehlte Medium zurueckmelden
	// moechte
	final String GIVE_MEDIA_BACK = "Möchten Sie diesen Medium zurückmelden?";
	// Buttonbeschriftungen des Dialogs
	final String POSITIV_BUTTON = "Ja";
	final String NEGATIVE_BUTTON = "Nein";

	// HashMap aus [laufender Nummer & Medium]
	private HashMap<Integer, Integer> filteredIDList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		headerText = new TextView(this);
		headerText.setText(Html.fromHtml(HEADER_TEXT));
		headerText.setGravity(Gravity.CENTER);
		// ListView mit dem Header (Textfeld und Spinner) und der Liste der
		// Medien
		listView = getListView();
		listView.addHeaderView(headerText);
		listView.setTextFilterEnabled(true);
		// listView mit der gefilterten Medienliste fuellen (nur verliehene und
		// entliehene Medien)
		filteredIDList = new HashMap<Integer, Integer>();
		setFilteredList();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// Tricky: Die ListView items fangen bei dem Index/Position 0
				// an. Jedoch ist das erste Element das Headerelement. Dh der
				// Index startet praktisch immer erst bei 1, denn Index 0 ist
				// immer der Header.
				final int position = pos - 1;
				// Benutzerabfrage ob er zurueckmelden moechte oder nicht
				AlertDialog.Builder builder = new AlertDialog.Builder(
						GiveMediaBack.this);
				builder.setMessage(GIVE_MEDIA_BACK).setCancelable(false)
						.setPositiveButton(POSITIV_BUTTON,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										// value der HashMap
										int value = filteredIDList
												.get(position);
										setMediaBack(value);
										GiveMediaBack.this.finish();
									}
								}).setNegativeButton(NEGATIVE_BUTTON,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	/**
	 * Hier wird die gefilterte Medienliste erstellt und angezeigt.
	 */
	private void setFilteredList() {
		List<Media> mediaList = new XMLMediaFileEditor(this).getAllMedia();
		List<Media> filteredMediaList = new ArrayList<Media>();
		int counterMedia = 0;
		int counterLentMedia = 0;
		for (Media media : mediaList) {
			if (media.getStatus().equals(Media.STATUS.VERLIEHEN.getName())
					|| media.getStatus().equals(
							Media.STATUS.ENTLIEHEN.getName())) {
				filteredMediaList.add(media);
				filteredIDList.put(counterLentMedia, counterMedia);
				++counterLentMedia;
			}
			++counterMedia;
		}
		listView.setAdapter(new ArrayAdapter<Media>(this,
				android.R.layout.simple_list_item_1, filteredMediaList));
	}

	/**
	 * Die Methode meldet ein Medium zurueck. D.h. wenn ein Medium verliehen ist
	 * wird es auf vorhanden gesetzt. Wenn es entliehen ist wird es aus der
	 * Liste geloescht.
	 * 
	 * @param selectedMedia
	 *            Das Medium welches zurueckgemeldet werden soll.
	 */
	private void setMediaBack(int selectedMedia) {
		XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(this);
		Media mediaBack = xmlEditor.getMediaByPosition((int) selectedMedia);
		// wenn das Medium verliehen ist wird es auf vorhanden gesetzt
		if (mediaBack.getStatus().equals(Media.STATUS.VERLIEHEN.getName())) {
			mediaBack.setStatus(Media.STATUS.VORHANDEN.getName());
			mediaBack.setDate(Media.DEFAULT_DATE);
			xmlEditor.updateMediaByPosition(selectedMedia, mediaBack);
			Toast.makeText(this, MEDIA_IS_BACK, Toast.LENGTH_LONG).show();
			// wenn das Medium entliehen ist wird es geloescht da fremde Medien
			// nicht gespeichert werden
		} else if (mediaBack.getStatus().equals(
				Media.STATUS.ENTLIEHEN.getName())) {
			xmlEditor.removeMediaByPosition(selectedMedia);
			Toast.makeText(this, MEDIA_IS_BACK, Toast.LENGTH_LONG).show();
		}
	}
}
