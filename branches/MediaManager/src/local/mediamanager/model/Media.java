package local.mediamanager.model;

/**
 * Das Model für ein Medium.
 * 
 * @author Jörg Langner
 */

public class Media {
	private String type;
	private String barcode;
	private String title;
	private String author;
	private String status;
	private String legalOwner;
	private String owner;
	private String date;

	/**
	 * Standard Datum eines Mediums
	 */
	public static final String DEFAULT_DATE = "01-01-9999";
	/**
	 * Standard Besitzer eines Mediums
	 */
	public static final String DEFAULT_OWNER = "self";

	/**
	 * Standard Eigentuemer eines Mediums
	 */
	public static final String DEFAULT_LEGAL_OWNER = "self";

	/**
	 * Moegliche Status eines Mediums.
	 * 
	 * @author Joerg Langner
	 */
	public static enum STATUS {
		VERLIEHEN("verliehen"), ENTLIEHEN("entliehen"), VORHANDEN("vorhanden");

		private String name;

		private STATUS(String name) {
			this.name = name;
		}

		/**
		 * @return Name des Status
		 */
		public String getName() {
			return this.name;
		}
	};

	/**
	 * Moegliche Typen eines Mediums.
	 * 
	 * @author Joerg Langner
	 */
	public static enum TYPE {
		BOOK("Book"), MUSIC("Music"), MOVIE("Movie"), MAGAZINES("Magazines"), VIDEO_GAMES(
				"VideoGames");

		private String name;

		private TYPE(String name) {
			this.name = name;
		}

		/**
		 * @return Name des Typs
		 */
		public String getName() {
			return this.name;
		}
	};

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setLegalOwner(String legalOwner) {
		this.legalOwner = legalOwner;
	}

	public String getLegalOwner() {
		return legalOwner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public String toString() {
		return type + "\n" + title;
	}
}
