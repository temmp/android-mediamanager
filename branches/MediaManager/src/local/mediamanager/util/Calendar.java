package local.mediamanager.util;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Erstellt einen Eintrag in den Kalender, dass den Benutzer des MediaManagers
 * daran erinnert wenn ein Medium ablaeuft.
 * 
 * @author Andreas Wiedemann
 */
public class Calendar {
	private Activity activity;

	public Calendar(Activity activity) {
		this.activity = activity;
	}

	public void addCalendarEntry(int year, int month, int day, String title) {
		Uri calendars = Uri.parse("content://calendar/calendars");
		String[] projection = new String[] { "_id" };
		Cursor managedCursor = activity.managedQuery(calendars, projection,
				"selected=1", null, null);

		if (managedCursor.moveToFirst()) {
			String calId;
			int idColumn = managedCursor.getColumnIndex("_id");

			calId = managedCursor.getString(idColumn);
			ContentValues event = new ContentValues();
			event.put("calendar_id", calId);
			event.put("title", "MediaManager");
			event.put("description", "Das Medium mit dem Titel " + title
					+ " ist abgelaufen.");
			// year (0 is 1900), month (0 - 11), day (1 - 31), hour (0 - 23),
			// minute (0 - 59), second (0 - 59)
			year = year - 1900;
			event.put("dtstart", java.util.Date
					.UTC(year, month, day, 0, 00, 00));
			event.put("dtend", java.util.Date.UTC(year, month, day, 0, 00, 00));
			event.put("allDay", 1); // Ganztägiges Event
			event.put("eventStatus", 1); // confirmed = verfügbar
			event.put("visibility", 2); // Privat (Öffentlich wäre 1)
			event.put("transparency", 1); // transparent = Verfügbar (Keine
			// Kollision mit anderen Terminen)
			event.put("hasAlarm", 1); // Funktioniert noch nicht wirklich, aber
			// keinen anderen key gefunden

			Uri eventsUri = Uri.parse("content://calendar/events");
			@SuppressWarnings("unused")
			Uri url = activity.getContentResolver().insert(eventsUri, event);
		}
	}
}
