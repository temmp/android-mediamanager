package local.mediamanager.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

/**
 * Diese Klasse stellt Methoden zur Verfuegung um die im Handy eingetragenen
 * Kontakte zu ermitteln oder von einem gegebenen Kontakt dessen Telefonnummern
 * oder Mailadressen zu ermitteln.
 * 
 * @author Jörg Langner
 */

public class Contact {

	private Activity activity;
	private final String NO_NUMBER_AVAILABLE = "NA";

	public Contact(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Diese Methode ermittelt die Namen aller eingespeicherten Kontakte und
	 * speichert sie in eine Liste.
	 * 
	 * @return Liste mit den Namen aller Kontakte.
	 */
	public List<CharSequence> getContactNameList() {
		Cursor people = this.activity.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		ArrayList<CharSequence> contactList = new ArrayList<CharSequence>();
		while (people.moveToNext()) {
			int nameFieldColumnIndex = people
					.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			contactList.add(people.getString(nameFieldColumnIndex));
		}
		people.close();
		return contactList;
	}

	/**
	 * Diese Methode ermittelt die ID's aller eingespeicherten Kontakte. Eine
	 * fortlaufende Nummer wird mit dieser ID assoziert.
	 * 
	 * @return HashMap aus [laufender Nummer & ID des Kontaktes im Handy]
	 */
	public HashMap<Integer, Integer> getContactIDMap() {
		Cursor people = this.activity.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		HashMap<Integer, Integer> contactIDMap = new HashMap<Integer, Integer>();
		int contactCounter = 0;
		while (people.moveToNext()) {
			int numberFieldColumnIndex = people.getColumnIndex(PhoneLookup._ID);
			String number = people.getString(numberFieldColumnIndex);
			contactIDMap.put(contactCounter, Integer.parseInt(number));
			++contactCounter;
		}
		people.close();
		return contactIDMap;
	}

	/**
	 * Aus der KontaktID wird der Kontaktname ermittelt
	 * 
	 * @param contactID
	 *            Die ID des Kontaktes
	 * @return Name des Kontaktes
	 */
	public String getContactName(String contactID) {
		String contactName = "";
		Cursor people = this.activity.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while (people.moveToNext()) {
			int numberFieldColumnIndex = people.getColumnIndex(PhoneLookup._ID);
			String number = people.getString(numberFieldColumnIndex);
			if (number.equals(contactID)) {
				int nameFieldColumnIndex = people
						.getColumnIndex(PhoneLookup.DISPLAY_NAME);
				contactName = people.getString(nameFieldColumnIndex);
			}
		}
		people.close();
		return contactName;
	}

	/**
	 * Diese Methode ermittelt die Telefonnummern (Mobil, Hauptnummer, Andere
	 * Nummer, Arbeit Mobil, Auto, Zu Hause) des Kontaktes.
	 * 
	 * @param contactID
	 *            Die ID des Kontaktes
	 * @return HashMap bestehend aus [Art der Nummer & Nummer]
	 */
	public HashMap<String, String> getContactPhoneNumbers(String contactID) {
		// Arten von Telefonnummern
		String MOBILE = "Mobil";
		String MAIN = "Hauptnummer";
		String OTHER = "Andere Nummer";
		String WORK_MOBILE = "Arbeit Mobil";
		String CAR = "Auto";
		String HOME = "Zu Hause";

		String phoneNumber = "";
		HashMap<String, String> phoneNumbers = new HashMap<String, String>();
		phoneNumber = getPhoneNumberByType(contactID, Phone.TYPE_MOBILE);
		if (!phoneNumber.equals(NO_NUMBER_AVAILABLE))
			phoneNumbers.put(MOBILE, phoneNumber);
		phoneNumber = "";
		phoneNumber = getPhoneNumberByType(contactID, Phone.TYPE_MAIN);
		if (!phoneNumber.equals(NO_NUMBER_AVAILABLE))
			phoneNumbers.put(MAIN, phoneNumber);
		phoneNumber = "";
		phoneNumber = getPhoneNumberByType(contactID, Phone.TYPE_OTHER);
		if (!phoneNumber.equals(NO_NUMBER_AVAILABLE))
			phoneNumbers.put(OTHER, phoneNumber);
		phoneNumber = "";
		phoneNumber = getPhoneNumberByType(contactID, Phone.TYPE_WORK_MOBILE);
		if (!phoneNumber.equals(NO_NUMBER_AVAILABLE))
			phoneNumbers.put(WORK_MOBILE, phoneNumber);
		phoneNumber = "";
		phoneNumber = getPhoneNumberByType(contactID, Phone.TYPE_CAR);
		if (!phoneNumber.equals(NO_NUMBER_AVAILABLE))
			phoneNumbers.put(CAR, phoneNumber);
		phoneNumber = "";
		phoneNumber = getPhoneNumberByType(contactID, Phone.TYPE_HOME);
		if (!phoneNumber.equals(NO_NUMBER_AVAILABLE))
			phoneNumbers.put(HOME, phoneNumber);
		return phoneNumbers;
	}

	/**
	 * Aus der KontaktID und dem Typ wird die Telefonnummer ermittelt.
	 * 
	 * @param contactID
	 *            Die ID des Kontaktes
	 * @param type
	 *            Typ der Telefonnummer
	 * @return Mobilnummer des Kontaktes des entsprechenden Typs. Falls keine
	 *         Nummer ermittelt werden konnten wird "NONE" zurueckgegeben
	 */
	private String getPhoneNumberByType(String contactID, int type) {
		String phoneNumber = "";
		// Cursor fuer den Zugriff auf die Tabelle.
		// Das <?> dient als Platzhalter fuer die contactID. Geht aber auch mit
		// Escapesequenz <...Data.CONTACT_ID + "=\"" + contactID + "\"" +
		// " AND "...>
		// Der Data.MIMETYPE bestimmt die Art der Daten die die "Data" Tabelle
		// enthaelt. Die "Data" Tabelle enthaelt Felder/Spalten von "DATA1" bis
		// "DATA15". Der hier benutzte Data.MIMETYP "Phone.CONTENT_ITEM_TYPE"
		// enthaelt die Telefonnummer im "DATA1" Feld.
		// Der Phone.TYPE bestimmt die Art der Telefonnummer (z.B. Home, Work
		// etc)
		Cursor cursor = this.activity.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Phone.NUMBER },
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
						+ Phone.CONTENT_ITEM_TYPE + "' " + "AND " + Phone.TYPE
						+ "=" + type, new String[] { contactID }, null);

		// contactID ist eindeutig..daher gibt es definitiv genau eine row
		cursor.moveToNext();
		try {
			// siehe Kommentar oben. "DATA1" enthaelt die Telefonnummer
			phoneNumber = cursor.getString(cursor.getColumnIndex(Data.DATA1));
		}
		// Exception wird geworfen wenn kein Zugriff auf den Index 0
		// moeglich ist. Das bedeutet der Kontakt hat keine Telefonnummer.
		// Falls dies der Fall ist wird dies im phoneNumber-String vermerkt
		catch (CursorIndexOutOfBoundsException e) {
			phoneNumber = NO_NUMBER_AVAILABLE;
		}
		return phoneNumber;
	}

	/**
	 * Aus der KontaktID wird die E-Mail-Adresse ermittelt.
	 * 
	 * @param contactID
	 *            Die ID des Kontaktes
	 * @return E-Mail Adresse des Kontaktes
	 */
	public String getContactMailAddress(String contactID) {
		String NO_MAIL_ADDRESS_AVAILABLE = "Keine E-Mail Adresse vorhanden.";
		String mailAddress = "";
		// Cursor fuer den Zugriff auf die Tabelle.
		// Das <?> dient als Platzhalter fuer die contactID. Geht aber auch mit
		// Escapesequenz <...Data.CONTACT_ID + "=\"" + contactID + "\"" +
		// " AND "...>
		// Der Data.MIMETYPE bestimmt die Art der Daten die die "Data" Tabelle
		// enthaelt. Die "Data" Tabelle enthaelt Felder/Spalten von "DATA1" bis
		// "DATA15". Der hier benutzte Data.MIMETYP "Email.CONTENT_ITEM_TYPE"
		// enthaelt die Telefonnummer im "DATA1" Feld.
		Cursor cursor = this.activity.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Data.DATA1 },
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
						+ Email.CONTENT_ITEM_TYPE + "'",
				new String[] { contactID }, null);

		// contactID ist eindeutig..daher gibt es definitiv genau eine row
		cursor.moveToNext();
		try {
			// siehe Kommentar oben. "DATA1" enthaelt die E-Mail Adresse
			mailAddress = cursor.getString(cursor.getColumnIndex(Data.DATA1));
			Log.i("log", "Mail address = " + mailAddress);
		}
		// Exception wird geworfen wenn kein Zugriff auf den Index 0
		// moeglich ist. Das bedeutet der Kontakt hat keine E-Mail Adresse.
		// Falls dies der Fall ist wird dies im mailAddress-String vermerkt
		catch (CursorIndexOutOfBoundsException e) {
			mailAddress = NO_MAIL_ADDRESS_AVAILABLE;
		}
		return mailAddress;
	}
}
