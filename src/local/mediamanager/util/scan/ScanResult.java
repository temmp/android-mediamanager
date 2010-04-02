package local.mediamanager.util.scan;

/**
 * Transfer Klasse fuer das Ergebnis eines Barcode Scans.
 * 
 * @author Jörg Langner
 */

public class ScanResult {

	private String barcode;
	private String format;

	/**
	 * Setzt den ScanResult.
	 * 
	 * @param barcode
	 *            Barcode
	 * @param format
	 *            Format des Barcodes
	 */
	public ScanResult(String barcode, String format) {
		this.setBarcode(barcode);
		this.setFormat(format);
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}
}
