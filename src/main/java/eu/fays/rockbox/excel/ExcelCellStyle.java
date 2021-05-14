package eu.fays.rockbox.excel;

/**
 * Excel cell style
 *
 */
public enum ExcelCellStyle {
	/** Excel "Bad" cell style */
	BAD(new byte[] { (byte) 0x9C, (byte) 0x00, (byte) 0x06 }, new byte[] { (byte) 0xFF, (byte) 0xC7, (byte) 0xCE }),

	/** Excel "Good" cell style */
	GOOD(new byte[] { (byte) 0x00, (byte) 0x61, (byte) 0x00 }, new byte[] { (byte) 0xC6, (byte) 0xEF, (byte) 0xCE }),

	/** Excel "Neutral" cell style */
	NEUTRAL(new byte[] { (byte) 0x9C, (byte) 0x57, (byte) 0x00 }, new byte[] { (byte) 0xFF, (byte) 0xEB, (byte) 0x9C }); 
	
	/** Font's RGB color values */
	public final byte[] fontRGB;
	
	/** Foreground's RGB color values */
	public final byte[] foregroundRGB;
	
	/**
	 * Constructor 
	 * @param fontRGB font's RGB color values
	 * @param foregroundRGB foreground's RGB color values
	 */
	private ExcelCellStyle(final byte[] fontRGB, final byte[] foregroundRGB) {
		this.fontRGB = fontRGB;
		this.foregroundRGB = foregroundRGB;
	}
}
