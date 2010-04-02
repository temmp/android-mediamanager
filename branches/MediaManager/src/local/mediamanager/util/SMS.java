package local.mediamanager.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

// TODO exception bei entliehenen medien aufgrund der laenge der msg
// TODO Toasts kommen teilweise 2-3x hintereinander bei no service
/**
 * Klasse zum Versenden einer SMS. Dem Benutzer wird mitgeteilt ob das Senden
 * funktioniert hat und ob die SMS zugestellt wurde.
 * 
 * @author Joerg Langner
 */
public class SMS {

	// Konstanten fuer die Bezeichnung des SENT und DELIVERED Intent
	private static final String SENT = "SMS_SENT";
	private static final String DELIVERED = "SMS_DELIVERED";

	/**
	 * Mit den uebergebenen Paramatern wird eine SMS versendet. Zudem erfolgt
	 * eine Ueberpruefung ob der SMS versandt erfolgreich oder nicht erfolgreich
	 * war. Dieses wird dem Benutzer mitgeteilt.
	 * 
	 * @param phoneNumber
	 *            Telefonnummer des Empfaengers
	 * @param message
	 *            Inhalt der SMS Nachricht
	 */
	public static void sendSMS(String phoneNumber, String message,
			Activity activity) {

		final Activity act = activity;
		PendingIntent.getBroadcast(activity, 0, new Intent(SENT), 0);

		// Broadcast fuer den Intent SENT
		PendingIntent sentPI = PendingIntent.getBroadcast(activity, 0,
				new Intent(SENT), 0);
		// Broadcast fuer den Intent DELIVERED
		PendingIntent deliveredPI = PendingIntent.getBroadcast(activity, 0,
				new Intent(DELIVERED), 0);

		// Listener ob SMS gesendet werden konnte
		activity.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(act.getBaseContext(),
							"SMS wurde erfolgreich versendet.",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(act.getBaseContext(),
							"Ein allgemeiner Fehler ist aufgetreten.",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(act.getBaseContext(),
							"Kein Service verfuegbar.", Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(act.getBaseContext(),
							"Keine PDU mitgeliefert.", Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(act.getBaseContext(),
							"Funk ist explizit ausgeschaltet.",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// Listener ob SMS zugestellt werden konnte
		activity.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(act.getBaseContext(),
							"SMS wurde erfolgreich zugestellt.",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(act.getBaseContext(),
							"SMS konnte nicht zugestellt werden.",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		// SMS wird via Android SmsManager versendet
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}
}
