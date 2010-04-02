package local.mediamanager.util;

/**
 * Diese Klasse formatiert die im Konstruktor übergebenen Paramater zu einem
 * Datum.
 * 
 * @author Andreas Wiedemann
 */

public class Date {

	private String date;

	public Date(int dayOfMonth, int month, int year) {
		if (dayOfMonth < 10) {
			this.date = "0" + dayOfMonth;
		} else {
			this.date = "" + dayOfMonth;
		}

		if (month < 10) {
			this.date = this.date + "-" + "0" + month;
		} else {
			this.date = this.date + "-" + month;
		}
		this.date = this.date + "-" + year;
	}

	/**
	 * Gibt ein formatiertes Datum zurueck.
	 * 
	 * @return Das formatierte Datum.
	 */
	public String getDate() {
		return date;
	}
}
