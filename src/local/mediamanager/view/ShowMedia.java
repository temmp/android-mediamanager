package local.mediamanager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import local.mediamanager.R;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import local.mediamanager.view.sharedmenues.SharedListActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * GUI für die Anzeige der Medien. Es besteht aus einem Spinner (durch den die
 * Medienliste gefiltert wird) und der eigentliche Liste (die die Medien
 * anzeigt). Mit dem Filter können entweder alle, nur geliehene oder nur
 * verliehene Medien angezeigt werden. Durch einen kurzen Klick auf eines dieser
 * Medien werden dessen Medieninformationen angezeigt. Bei einem laengeren Klick
 * oeffnet sich das Kontextmenue in welchem man ein Medium zurueckmelden,
 * loeschen oder Erinnerungs-/Verlaengerungs-SMS versenden kann.
 * 
 * @author Jörg Langner
 */
public class ShowMedia extends SharedListActivity {

	// GUI Elemente
	private ListView listView;
	private Spinner filterSpinner;
	private LinearLayout linearLayout;
	private TextView textView;
	private ArrayAdapter<CharSequence> spinnerAdapter;

	// Bezeichnungen fuer das Kontext Menue
	private final String SMS_ENTRY = "SMS";
	private final String DELETE_ENTRY = "Löschen";
	private final String BACK_ENTRY = "Zurückmelden";
	private final String REMINDER_SMS_ENTRY = "Erinnerungs-SMS";
	private final String LENGTHENING_SMS_ENTRY = "Verlängerungs-SMS";

	// Nachrichten an Benutzer
	final String MEDIA_DELETED = "Medium wurde gelöscht.";
	final String MEDIA_IS_BACK = "Medium wurde zurückgemeldet.";
	final String MEDIA_COULD_NOT_SET_BACK = "Medium kann nicht zurückgemeldet"
			+ " werden da es werder entliehen nocht verliehen ist.";

	// Konstanten fuer die ContextMenu Optionen
	private final int BACK = 0;
	private final int SMS = 1;
	private final int DELETE = 2;

	// HashMap aus [laufender Nummer & Medium]
	private HashMap<Integer, Integer> filteredIDList;

	// enum fuer die Filterart
	private enum FILTER {
		SHOW_ALL_MEDIA(0, "Alle Medien anzeigen"), SHOW_LENT_MEDIA(1,
				"Verliehene Medien anzeigen"), SHOW_BORROWED_MEDIA(2,
				"Entliehene Medien anzeigen");

		private int code;
		private String text;

		private FILTER(int code, String text) {
			this.code = code;
			this.text = text;
		}

		public int getCode() {
			return this.code;
		}

		public String getText() {
			return this.text;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// HashMap
		filteredIDList = new HashMap<Integer, Integer>();
		// Textfeld ueber dem Spinner
		textView = new TextView(this);
		textView.setText("Filter auswählen:");
		// Spinner mit dem der Filter bestimmt werden kann
		filterSpinner = new Spinner(this);
		// Layout des "Spinners"
		spinnerAdapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		// Layout der "Spinnerauswahl"
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdapter.add(FILTER.SHOW_ALL_MEDIA.getText());
		spinnerAdapter.add(FILTER.SHOW_LENT_MEDIA.getText());
		spinnerAdapter.add(FILTER.SHOW_BORROWED_MEDIA.getText());
		filterSpinner.setAdapter(spinnerAdapter);
		// Ueberschrift bei der Auswahl der Filter
		filterSpinner.setPromptId(R.string.spFilter);
		filterSpinner.setSelection(0);
		filterSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						setFilteredList(pos);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// hier ist nichts zu tun
					}

				});

		// HeaderView fuer die ListView
		linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(textView);
		linearLayout.addView(filterSpinner);
		// ListView mit dem Header (Textfeld und Spinner) und der Liste der
		// Medien
		listView = getListView();
		listView.addHeaderView(linearLayout);
		listView.setTextFilterEnabled(true);
		listView.setAdapter(new ArrayAdapter<Media>(this,
				android.R.layout.simple_list_item_1, new XMLMediaFileEditor(
						ShowMedia.this).getAllMedia()));
		// Context Menu fuer die Items der Liste
		registerForContextMenu(getListView());
	}

	/**
	 * Hier wird die gefilterte Liste erstellt und angezeigt. Gefiltert wird auf
	 * Grundlage des Parameters(alle Medien anzeigen, verliehene anzeigen,
	 * entliehene anzeigen):
	 * 
	 * @param filterID
	 *            Im enum "FILTER" sind die möglichen Filterungsarten
	 *            herauszulesen
	 */
	private void setFilteredList(int filterID) {
		// bei der Anzeige aller Medien wird die HashMap 1zu1 zur XML umgesetzt
		if (filterID == FILTER.SHOW_ALL_MEDIA.getCode()) {
			// if (filterID == FILTER.SHOW_ALL_MEDIA.getCode()) {
			// listView.setAdapter(new ArrayAdapter<Media>(ShowMedia.this,
			// android.R.layout.simple_list_item_1, new XMLEditor(
			// ShowMedia.this).getAllMedia()));
			List<Media> mediaList = new XMLMediaFileEditor(ShowMedia.this)
					.getAllMedia();
			List<Media> filteredMediaList = new ArrayList<Media>();
			int counterMedia = 0;
			for (Media media : mediaList) {
				filteredMediaList.add(media);
				filteredIDList.put(counterMedia, counterMedia);
				++counterMedia;
			}
			listView.setAdapter(new ArrayAdapter<Media>(ShowMedia.this,
					android.R.layout.simple_list_item_1, filteredMediaList));
		} else if (filterID == FILTER.SHOW_LENT_MEDIA.getCode()) {
			List<Media> mediaList = new XMLMediaFileEditor(ShowMedia.this)
					.getAllMedia();
			List<Media> filteredMediaList = new ArrayList<Media>();
			int counterMedia = 0;
			int counterLentMedia = 0;
			for (Media media : mediaList) {
				if (media.getStatus().equals(Media.STATUS.VERLIEHEN.getName())) {
					filteredMediaList.add(media);
					filteredIDList.put(counterLentMedia, counterMedia);
					++counterLentMedia;
				}
				++counterMedia;
			}
			listView.setAdapter(new ArrayAdapter<Media>(ShowMedia.this,
					android.R.layout.simple_list_item_1, filteredMediaList));
		} else if (filterID == FILTER.SHOW_BORROWED_MEDIA.getCode()) {
			List<Media> mediaList = new XMLMediaFileEditor(ShowMedia.this)
					.getAllMedia();
			List<Media> filteredMediaList = new ArrayList<Media>();
			int counterMedia = 0;
			int counterLentMedia = 0;
			for (Media media : mediaList) {
				if (media.getStatus().equals(Media.STATUS.ENTLIEHEN.getName())) {
					filteredMediaList.add(media);
					filteredIDList.put(counterLentMedia, counterMedia);
					++counterLentMedia;
				}
				++counterMedia;
			}
			listView.setAdapter(new ArrayAdapter<Media>(ShowMedia.this,
					android.R.layout.simple_list_item_1, filteredMediaList));
		}
	}

	/**
	 * Wird aufgerufen sobald auf ein Item/Medium aus der Liste gedrueckt wird.
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Tricky: Die ListView items fangen bei dem Index/Position 0 an. Jedoch
		// ist das erste Element das Headerelement. Dh der Index startet
		// praktisch immer erst bei 1, denn Index 0 ist immer der Header.
		position = position - 1;
		// value der HashMap
		int value = filteredIDList.get(position);
		Media media = new XMLMediaFileEditor(this).getMediaByPosition(value);
		// ItemClick Dialog anzeigen
		showItemClickDialog(media);
	}

	/**
	 * Die Methode erstellt einen Dialog der die entsprechenden
	 * Medieninformationen anzeigt, je nach dem welchen Status das Medium hat.
	 * 
	 * @param media
	 *            Medium zu welchem die Informationen angezeigt werden sollen
	 */
	private void showItemClickDialog(Media media) {
		final String AUTHOR = "Autor: ";
		final String BARCODE = "Barcode: ";
		final String STATUS = "Status: ";
		final String LENT_TO = "An: ";
		final String BORROWED_FROM = "Von: ";
		final String LENT_OR_BORROWED_TILL = "Bis: ";

		Dialog dialog;
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.showmedia_dialog);
		dialog.setTitle(media.getTitle());
		TextView text = (TextView) dialog.findViewById(R.id.text);
		// wenn das Medium verliehen ist, dann wird u.a. angezeigt an wen und
		// bis wann es verliehen ist
		if (media.getStatus().equals(Media.STATUS.VERLIEHEN.getName())) {
			String contactName = new Contact(this).getContactName(media
					.getOwner());
			text.setText(AUTHOR + media.getAuthor() + "\n" + BARCODE
					+ media.getBarcode() + "\n" + STATUS + media.getStatus()
					+ "\n" + LENT_TO + contactName + "\n"
					+ LENT_OR_BORROWED_TILL + media.getDate());
		}
		// wenn das Medium entliehen ist, dann wird u.a. angezeigt von wem und
		// bis wann es entliehen ist
		else if (media.getStatus().equals(Media.STATUS.ENTLIEHEN.getName())) {
			String contactName = new Contact(this).getContactName(media
					.getLegalOwner());
			text.setText(AUTHOR + media.getAuthor() + "\n" + BARCODE
					+ media.getBarcode() + "\n" + STATUS + media.getStatus()
					+ "\n" + BORROWED_FROM + contactName + "\n"
					+ LENT_OR_BORROWED_TILL + media.getDate());
		}
		// das Medium ist vorhanden
		else {
			text.setText(AUTHOR + media.getAuthor() + "\n" + BARCODE
					+ media.getBarcode() + "\n" + STATUS + media.getStatus());
		}
		// das zum Medium passende Bild anzeigen
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		if (media.getType().equals(Media.TYPE.BOOK.getName())) {
			image.setImageResource(R.drawable.media_book);
		} else if (media.getType().equals(Media.TYPE.MUSIC.getName())) {
			image.setImageResource(R.drawable.media_music);
		} else if (media.getType().equals(Media.TYPE.MOVIE.getName())) {
			image.setImageResource(R.drawable.media_movie);
		} else if (media.getType().equals(Media.TYPE.VIDEO_GAMES.getName())) {
			image.setImageResource(R.drawable.media_game);
		} else if (media.getType().equals(Media.TYPE.MAGAZINES.getName())) {
			image.setImageResource(R.drawable.media_magazine);
		}

		dialog.setOwnerActivity(this);
		dialog.show();
	}

	/**
	 * Bei einem Kontextmenue Klick auf ein Medium aus der Liste wird diese
	 * Methode aufgerufen. Je nachdem welchen Status das Medium hat werden die
	 * Kontextmenueoptionen angezeigt.
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, BACK, 0, BACK_ENTRY);
		// Je nachdem welcher Filter gerade genutzt wird, so werden die
		// entsprechenden KontextMenu Optionen angezeigt.
		if ((int) filterSpinner.getSelectedItemId() == FILTER.SHOW_ALL_MEDIA
				.getCode()) {
			menu.add(0, SMS, 0, SMS_ENTRY);
		}
		// fuer die verliehenen Medien gibt es die KontextMenu Option
		// "Erinnerungs-SMS"
		else if ((int) filterSpinner.getSelectedItemId() == FILTER.SHOW_LENT_MEDIA
				.getCode()) {
			menu.add(0, SMS, 0, REMINDER_SMS_ENTRY);
		}
		// fuer die entliehenen Medien gibt es die KontextMenu Option
		// "Verlaengerungs-SMS"
		else if ((int) filterSpinner.getSelectedItemId() == FILTER.SHOW_BORROWED_MEDIA
				.getCode()) {
			menu.add(0, SMS, 0, LENGTHENING_SMS_ENTRY);
		}
		menu.add(0, DELETE, 0, DELETE_ENTRY);
	}

	/**
	 * Wird aufgerufen wenn eine Kontextmenuoption ausgewaehlt wurde.
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// aus der info.id wird die tatsaechliche Position des Mediums in der
		// XML ermittelt
		int value = filteredIDList.get((int) info.id);
		// je nachdem welcher Filter gesetzt ist wird die Medienliste spaeter
		// updated
		int filterID = (int) filterSpinner.getSelectedItemId();
		switch (item.getItemId()) {
		case BACK:
			setMediaBack(value);
			// die updated Medienliste anzeigen
			setFilteredList(filterID);
			return true;
		case SMS:
			// SendSMS GUI oeffnen mit dem entsprechenden Medium als
			// Intent(damit daraus der Kontakt und aus dem Kontakt die
			// Telefonnummer/n ermittelt werden kann)
			Intent mediaPos = new Intent(this, SendSMS.class);
			mediaPos.putExtra(SendSMS.MEDIA_POS, String.valueOf(value));
			startActivity(mediaPos);
			return true;
		case DELETE:
			new XMLMediaFileEditor(this).removeMediaByPosition(value);
			Toast.makeText(this, MEDIA_DELETED, Toast.LENGTH_LONG).show();
			// die updated Medienliste anzeigen
			setFilteredList(filterID);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * Die Methode meldet ein Medium zurueck. D.h. wenn ein Medium verliehen ist
	 * wird es auf vorhanden gesetzt. Wenn es entliehen ist wird es aus der
	 * Liste geloescht. Wenn es keines von beiden ist, kann das Medium nicht
	 * zurueck gemeldet werden.
	 * 
	 * @param selectedMedia
	 *            Das Medium welches zurueckgemeldet werden soll.
	 */
	private void setMediaBack(int selectedMedia) {
		XMLMediaFileEditor xmlEditor = new XMLMediaFileEditor(ShowMedia.this);
		Media mediaBack = xmlEditor.getMediaByPosition((int) selectedMedia);
		if (mediaBack.getStatus().equals(Media.STATUS.VORHANDEN.getName())) {
			Toast.makeText(this, MEDIA_COULD_NOT_SET_BACK, Toast.LENGTH_LONG)
					.show();
		} else if (mediaBack.getStatus().equals(
				Media.STATUS.VERLIEHEN.getName())) {
			mediaBack.setStatus(Media.STATUS.VORHANDEN.getName());
			xmlEditor.updateMediaByPosition(selectedMedia, mediaBack);
			Toast.makeText(this, MEDIA_IS_BACK, Toast.LENGTH_LONG).show();
		} else if (mediaBack.getStatus().equals(
				Media.STATUS.ENTLIEHEN.getName())) {
			xmlEditor.removeMediaByPosition(selectedMedia);
			Toast.makeText(this, MEDIA_IS_BACK, Toast.LENGTH_LONG).show();
		}
	}
}