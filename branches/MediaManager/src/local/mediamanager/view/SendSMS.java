package local.mediamanager.view;

import java.util.HashMap;

import local.mediamanager.R;
import local.mediamanager.model.Media;
import local.mediamanager.util.Contact;
import local.mediamanager.util.xml.XMLMediaFileEditor;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SendSMS extends Activity {

	// Beschriftungen der GUI und dessen Elemente
	private final String TEXTVIEW_SELECT_TEL_NUMBER_TITLE = "Wählen sie eine Telefonnummer"
			+ " aus:";
	private final String TEXTVIEW_SMS_TEXT_TITLE = "Nachricht:";
	private final String CAPTION_BUTTON_SEND_SMS = "SMS senden";
	
	// Name der Intent Eigenschaft
	public static final String MEDIA_POS = "mediaPos";
	private static final int MENU_ABOUT = 0;
	private static final int MENU_BACK = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent in = this.getIntent();
		String mediaPos = in.getStringExtra(MEDIA_POS);

		Contact contact = new Contact(this);
		// HashMap die die Telefonnummern eines Kontaktes enthaelt
		HashMap<String, String> phoneNumbers = null;
		// das ausgewaehlte Medium holen
		Media media = new XMLMediaFileEditor(this).getMediaByPosition(Integer
				.parseInt(mediaPos));
		// es wird geprueft ob das Medium verliehen oder entliehen ist.
		// trifft eines davon zu, werden die Telefonnummern des Kontaktes
		// ermittelt.
		if (media.getStatus().equals(Media.STATUS.VERLIEHEN.getName())) {
			phoneNumbers = contact.getContactPhoneNumbers(media.getOwner());
		} else if (media.getStatus().equals(Media.STATUS.ENTLIEHEN.getName())) {
			phoneNumbers = contact
					.getContactPhoneNumbers(media.getLegalOwner());
		}
		// wenn die HashMap null ist, dann heisst das, das das Medium weder
		// verliehen nocht entliehen ist
		if (phoneNumbers != null) {
			// es wird geprueft ob fuer den Kontakt eine Telefonnummer
			// hinterlegt ist
			if (phoneNumbers.size() > 0) {
				String smsText = null;
				// der SMS Text wird generiert je nachdem ob das Medium ver-
				// oder entliehen ist
				smsText = generateSMSMessage(media);
				// dIE GUI zum Versenden einer SMS wird erzeugt und
				// angezeigt
				createSmsGUI(phoneNumbers, smsText);
			}
			// alert dialog - fuer den Kontakt ist keine Telefonnummer
			// hinterlegt
			else {
				final String NO_TEL_NUMBER = "Für diesen Kontakt ist keine "
						+ "Telefonnummer angegeben.";
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(NO_TEL_NUMBER).setCancelable(false)
						.setNeutralButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										SendSMS.this.finish();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
		// Medium ist weder verliehen noch entliehen
		else {
			final String MEDIA_IS_AVAILABLE = "Es ist nicht möglich eine SMS"
					+ " zu senden, da das Medium weder verliehen noch entliehen ist.";
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(MEDIA_IS_AVAILABLE).setCancelable(false)
					.setNeutralButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SendSMS.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	/**
	 * Erstellt die GUI zum Versenden einer SMS. Sie enthaelt ein oder mehrere
	 * RadioButtons zur Auswahl der hinterlegten Telefonnummer des Kontaktes und
	 * ein Eingabefeld fuer den SMS Text.
	 * 
	 * @param numbers
	 *            Die Auswahl an Telefonnummern an die eine SMS gesendet werden
	 *            kann.
	 * @param smsText
	 *            Der SMS Text der gesendet werden soll. Dieser kann jedoch im
	 *            EditText Feld geaendert werden.
	 */
	private void createSmsGUI(HashMap<String, String> numbers, String smsText) {
		// Hashmap mit den Telefonnummern
		final HashMap<String, String> phoneNumbers = numbers;

		// Layout
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		// Telefonnummer
		TextView tvSelectPhoneNumber = new TextView(this);
		tvSelectPhoneNumber.setText(TEXTVIEW_SELECT_TEL_NUMBER_TITLE);
		final RadioGroup radioGroup = new RadioGroup(this);
		// die counter Variable wird der RadioButton ID zugewiesen, dh z.b. der
		// zweite RadioButton hat die ID 2
		int idCounter = 0;
		// je nachdem wieviele Telefonnummern der Kontakt hat, soviele
		// RadioButtons gibt es zur Auswahl
		for (String key : phoneNumbers.keySet()) {
			RadioButton rbPhoneNumber = new RadioButton(this);
			rbPhoneNumber.setText(key);
			rbPhoneNumber.setId(idCounter);
			radioGroup.addView(rbPhoneNumber);
			++idCounter;
		}
		// die erste Tel. Nummer wird per default gecheckt
		radioGroup.check(0);
		// Nachrichtentext
		TextView tvMessage = new TextView(this);
		tvMessage.setText(TEXTVIEW_SMS_TEXT_TITLE);
		// Eingabefeld fuer die SMS Nachricht
		final EditText etMessage = new EditText(this);
		// setzt ein LayoutParams mit weight = 1 (by default haben alle GUI
		// elemente 0)...
		LinearLayout.LayoutParams etMessageWeight = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
		// ...das Eingabefeld der SMS Nachricht mit der weight = 1 nimmt die
		// gesamte zur Verfuegung stehende hoehe des Fensters ein (je nachdem ob
		// es eine oder mehrere checkboxen gibt). Somit ist immer das gesamte
		// Fenster in seiner Hoehe ausgefuellt.
		etMessage.setLayoutParams(etMessageWeight);
		// den Text oben im EditText anzeigen (Standard ist in der Mitte)
		etMessage.setGravity(Gravity.TOP);
		etMessage.setText(smsText);
		// SMS Senden Button
		Button btNext = new Button(this);
		btNext.setText(CAPTION_BUTTON_SEND_SMS);
		btNext.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// die ID des RadioButtons welcher gecheckt wurde wird ermittelt
				int checkedRadioButtonID = radioGroup.getCheckedRadioButtonId();
				// der RadioButton wird ueber die RadioGroup mittels der ID
				// geholt
				RadioButton rbChecked = (RadioButton) radioGroup
						.getChildAt(checkedRadioButtonID);
				// der Text des RadioButtons welcher auch gleichzeitig der key
				// der Telefonnummer-Hashmap ist wird ermittelt
				CharSequence key = rbChecked.getText();
				// die SMS wird an die angegebene Telefonnummer mit dem
				// angegebenen Text versendet
				local.mediamanager.util.SMS.sendSMS(phoneNumbers.get(key),
						etMessage.getText().toString(), SendSMS.this);
				// GUI wird geschlossen
				SendSMS.this.finish();
			}

		});

		layout.addView(tvSelectPhoneNumber);
		layout.addView(radioGroup);
		layout.addView(tvMessage);
		layout.addView(etMessage);
		layout.addView(btNext);
		this.setContentView(layout);
	}

	/**
	 * Je nachdem ob das Medium verliehen oder entliehen ist wird der
	 * entsprechende SMS Text erstellt.
	 * 
	 * @param media
	 *            Medium auf welchem der SMS Text basiert.
	 * @return SMS-Text
	 */
	private String generateSMSMessage(Media media) {
		// CAUTION: bei einer zu langen Message wirft der android SmsManager
		// eine exception..warum auch immer
		String message = "";
		if (media.getStatus().equals(Media.STATUS.VERLIEHEN.getName())) {
			message = "[MediaManager autogenerierte Erinnerungsnachricht] Das Medium ["
					+ media.getType()
					+ "] mit dem Titel ["
					+ media.getTitle()
					+ "] läuft am [" + media.getDate() + "] ab.";
		} else if (media.getStatus().equals(Media.STATUS.ENTLIEHEN.getName())) {
			message = "[MediaManager autogenerierte Verlängerungsnachricht] Ich möchte das Medium ["
					+ media.getType()
					+ "] mit dem Titel ["
					+ media.getAuthor()
					+ "] bis zum [DATUM EINTRAGEN] ausleihen.";
		}
		return message;
	}
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_ABOUT, 0, "Info").setIcon(R.drawable.about);
	    menu.add(0, MENU_BACK, 0, "Zurück").setIcon(R.drawable.end);
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_ABOUT:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setIcon(R.drawable.icon);
	    	builder.setTitle("Über MediaManager 1.0");
	    	builder.setMessage("(c) 2010 by \n- Jörg Langner \n- Andreas Wiedemann \n\n" + "http://code.google.com/p/android-mediamanager");
	    	builder.setCancelable(false);
	    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					dialog.cancel();
	    	           }
	    	       });
	    	AlertDialog alert = builder.create();
	    	alert.show();
	        return true;
	    case MENU_BACK:
	        this.finish();
	        return true;
	    }
	    return false;
	}
}
