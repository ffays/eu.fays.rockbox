package eu.fays.rockbox.excel;

import static com.opencsv.ICSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.ICSVWriter.DEFAULT_LINE_END;
import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;
import static eu.fays.rockbox.excel.ExcelCellStyle.BAD;
import static eu.fays.rockbox.excel.ExcelCellStyle.GOOD;
import static eu.fays.rockbox.excel.ExcelCellStyle.NEUTRAL;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardOpenOption.READ;
import static java.text.DateFormat.SHORT;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getDateTimeInstance;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class ExcelTooling {
	/** Excel workbook */
	private final XSSFWorkbook workbook;
	/** Excel Data format */
	private XSSFDataFormat dataFormat;
	/** Excel Date cell style */
	private XSSFCellStyle dateCellStyle;
	/** Excel Timestamp cell style */
	private XSSFCellStyle timestampCellStyle;
	/** Excel creation helper */
	private XSSFCreationHelper creationHelper;
	/** Excel default indexed color map */
	private DefaultIndexedColorMap defaultIndexedColorMap;
	/** Excel bad cell style */
	private XSSFCellStyle badCellStyle;
	/** Excel good cell style */
	private XSSFCellStyle goodCellStyle;
	/** Excel neutral cell style */
	private XSSFCellStyle neutralCellStyle;

	/** Java custom Date format, used for conversions : dd MMM yyyy */
	public static final String CUSTOM_JAVA_DATE_FORMAT = "dd MMM yyyy";
	/** Java custom Time format */
	public static final String CUSTOM_JAVA_TIME_FORMAT = "hh:mm:ss";
	/** Java custom Timestamp format, used for conversions : dd MMM yyyy HH:mm:ss */
	public static final String CUSTOM_JAVA_TIMESTAMP_FORMAT = CUSTOM_JAVA_DATE_FORMAT + " " + CUSTOM_JAVA_TIME_FORMAT;

	/** Excel custom Date format : dd mmmm yyyy */
	public static final String CUSTOM_EXCEL_DATE_FORMAT = CUSTOM_JAVA_DATE_FORMAT.toLowerCase();
	/** Excel custom Time format : hh:mm:ss */
	public static final String CUSTOM_EXCEL_TIME_FORMAT = CUSTOM_JAVA_TIME_FORMAT.toLowerCase();
	/** Excel custom Timestamp format : dd mmm yyyy hh:mm:ss */
	public static final String CUSTOM_EXCEL_TIMESTAMP_FORMAT = CUSTOM_EXCEL_DATE_FORMAT + " " + CUSTOM_EXCEL_TIME_FORMAT;

	/** Custom "dd MMM yyyy"/"d MMM yyyy" Date parser */
	// @formatter:off
	public static final DateTimeFormatter CUSTOM_DATE_PARSER = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ofPattern(CUSTOM_JAVA_DATE_FORMAT))
			.appendOptional(DateTimeFormatter.ofPattern((CUSTOM_JAVA_DATE_FORMAT.substring(1))))
			.toFormatter();
	// @formatter:on

	/**
	 * Custom timestamp parser, one of
	 * <ul>
	 * <li>dd MMM yyyy HH:mm:ss
	 * <li>d MMM yyyy HH:mm:ss
	 * <li>dd MMM yyyy H:mm:ss
	 * <li>d MMM yyyy H:mm:ss
	 * </ul>
	 */
	// @formatter:off
	public static final DateTimeFormatter CUSTOM_TIMESTAMP_PARSER = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ofPattern(CUSTOM_JAVA_TIMESTAMP_FORMAT))
			.appendOptional(DateTimeFormatter.ofPattern(CUSTOM_JAVA_TIMESTAMP_FORMAT.substring(1)))
			.appendOptional(DateTimeFormatter.ofPattern(CUSTOM_JAVA_DATE_FORMAT + " " + CUSTOM_JAVA_TIME_FORMAT.substring(1)))
			.appendOptional(DateTimeFormatter.ofPattern(CUSTOM_JAVA_DATE_FORMAT.substring(1) + " " + CUSTOM_JAVA_TIME_FORMAT.substring(1)))
			.toFormatter();
	// @formatter:on

	/**
	 * Excel format accept to escape the semicolon with a backslash,<br>
	 * however in such case, Apache POI does not format the timestamp correctly, i.e. it does output the backslash
	 */
	private static final Pattern TIMESTAMP_FORMAT_WITH_COLON_ESCAPED_PATTERN = Pattern.compile("(\\d?\\d)\\\\:(\\d{2})\\\\:(\\d{2})$");

	/** Default date format used for conversions, c.f. {@link DateFormat#getDateInstance(int, Locale)} with {@link DateFormat#SHORT} and {@link Locale#getDefault()} */
	public static final DateFormat DEFAULT_DATE_DATEFORMAT = getDateInstance(SHORT, Locale.getDefault());
	/** Default time format used for conversions, c.f. {@link DateFormat#getDateTimeInstance(int, int, Locale)} with {@link DateFormat#SHORT} twice and {@link Locale#getDefault()} */
	public static final DateFormat DEFAULT_TIMESTAMP_DATEFORMAT = getDateTimeInstance(SHORT, SHORT, Locale.getDefault());

	/**
	 * Constructor
	 * 
	 * @param workbook Excel workbook
	 */
	public ExcelTooling(final XSSFWorkbook workbook) {
		//
		assert workbook != null;
		//

		this.workbook = workbook;
	}

	/**
	 * Returns Excel Data format
	 * 
	 * @return Excel Data format
	 */
	public XSSFDataFormat getDataFormat() {
		if (dataFormat == null) {
			// lazy init
			dataFormat = workbook.createDataFormat();
		}
		return dataFormat;
	}

	/**
	 * Returns Excel Date cell style
	 * 
	 * @return Excel Date cell style
	 */
	public XSSFCellStyle getDateCellStyle() {
		if (dateCellStyle == null) {
			// lazy init
			dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(getDataFormat().getFormat(CUSTOM_EXCEL_DATE_FORMAT));
		}
		return dateCellStyle;
	}

	/**
	 * Returns Excel Timestamp cell style
	 * 
	 * @return Excel Timestamp cell style
	 */
	public XSSFCellStyle getTimestampCellStyle() {
		if (timestampCellStyle == null) {
			// lazy init
			timestampCellStyle = workbook.createCellStyle();
			timestampCellStyle.setDataFormat(getDataFormat().getFormat(CUSTOM_EXCEL_TIMESTAMP_FORMAT));
		}
		return timestampCellStyle;
	}

	/**
	 * Returns the creation helper
	 * 
	 * @return the creation helper
	 */
	public XSSFCreationHelper getCreationHelper() {
		if (creationHelper == null) {
			// lazy init
			creationHelper = workbook.getCreationHelper();
		}
		return creationHelper;
	}

	/**
	 * Returns the default indexed color map
	 * 
	 * @return the default indexed color map
	 */
	private DefaultIndexedColorMap getDefaultIndexedColorMap() {
		if (defaultIndexedColorMap == null) {
			// lazy init
			defaultIndexedColorMap = new DefaultIndexedColorMap();
		}
		return defaultIndexedColorMap;
	}

	/**
	 * Returns the bad cell style
	 * 
	 * @return the bad cell style
	 */
	private XSSFCellStyle getBadCellStyle() {
		if (badCellStyle == null) {
			// lazy init
			final XSSFColor badFontColor = new XSSFColor(BAD.fontRGB, getDefaultIndexedColorMap());
			final XSSFColor badForegroundColor = new XSSFColor(BAD.foregroundRGB, getDefaultIndexedColorMap());
			final XSSFFont badCellFont = workbook.createFont();
			badCellFont.setColor(badFontColor);

			badCellStyle = workbook.createCellStyle();
			badCellStyle.setFillForegroundColor(badForegroundColor);
			badCellStyle.setFont(badCellFont);
			badCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		return badCellStyle;
	}

	/**
	 * Returns the good cell style
	 * 
	 * @return the good cell style
	 */
	private XSSFCellStyle getGoodCellStyle() {
		if (badCellStyle == null) {
			// lazy init
			final XSSFColor goodFontColor = new XSSFColor(GOOD.fontRGB, getDefaultIndexedColorMap());
			final XSSFColor goodForegroundColor = new XSSFColor(GOOD.foregroundRGB, getDefaultIndexedColorMap());
			final XSSFFont goodCellFont = workbook.createFont();
			goodCellFont.setColor(goodFontColor);

			goodCellStyle = workbook.createCellStyle();
			goodCellStyle.setFillForegroundColor(goodForegroundColor);
			goodCellStyle.setFont(goodCellFont);
			goodCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		return goodCellStyle;
	}

	/**
	 * Returns the neutral cell style
	 * 
	 * @return the neutral cell style
	 */
	private XSSFCellStyle getNeutralCellStyle() {
		if (neutralCellStyle == null) {
			// lazy init
			/** Excel neutral font color */
			final XSSFColor neutralFontColor = new XSSFColor(NEUTRAL.fontRGB, getDefaultIndexedColorMap());
			/** Excel neutral foreground color */
			final XSSFColor neutralForegroundColor = new XSSFColor(NEUTRAL.foregroundRGB, getDefaultIndexedColorMap());
			/** Excel neutral cell color */
			final XSSFFont neutralCellFont = workbook.createFont();
			neutralCellFont.setColor(neutralFontColor);
			neutralCellStyle = workbook.createCellStyle();
			neutralCellStyle.setFillForegroundColor(neutralForegroundColor);
			neutralCellStyle.setFont(neutralCellFont);
			neutralCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		return neutralCellStyle;
	}

	/**
	 * Creates a cell comment
	 * 
	 * @param cell the cell
	 * @param comment the comment
	 */
	public void createCellComment(final XSSFCell cell, final String comment) {
		//
		assert cell != null;
		assert comment != null;
		//
		final ClientAnchor clientAnchor = getCreationHelper().createClientAnchor();
		clientAnchor.setCol1(cell.getColumnIndex() + 1);
		clientAnchor.setCol2(cell.getColumnIndex() + 3);
		clientAnchor.setRow1(cell.getRowIndex() + 1);
		clientAnchor.setRow2(cell.getRowIndex() + 5);
		final XSSFDrawing drawing = cell.getSheet().createDrawingPatriarch();
		final XSSFComment cellComment = drawing.createCellComment(clientAnchor);
		cellComment.setString(comment);
		cell.setCellComment(cellComment);
	}

	/**
	 * Format the cell as one of "Bad", "Good" or "Neutral" style
	 * 
	 * @param cell the cell
	 * @param excelCellStyle the cell style
	 */
	public void setCellStyle(final XSSFCell cell, final ExcelCellStyle excelCellStyle) {
		//
		assert cell != null;
		assert excelCellStyle != null;
		//

		switch (excelCellStyle) {
		case GOOD:
			cell.setCellStyle(getGoodCellStyle());
			break;
		case BAD:
			cell.setCellStyle(getBadCellStyle());
			break;
		case NEUTRAL:
			cell.setCellStyle(getNeutralCellStyle());
			break;
		}
	}

	/**
	 * Converts the sheets of the given source Excel file into a CSV file<br>
	 * Note: if there are multiple sheets, they will be separated by a Form Feed character in the resulting file
	 * 
	 * @param source the Excel file
	 * @param target the text file, pipe-delimited
	 * @param recordEmptyRows asks to record rows that does not contain any data
	 * @param separator the separator character between fields to use when creating the CSV content.
	 * @param quotechar the quote character to use when creating the CSV content.
	 * @param maxSheets the maximum number of sheets to convert
	 * @throws IOException in case of unexpected error
	 * @throws InvalidFormatException in case of unexpected error
	 */
	public static void convertExcelToCommaSeparatedValues(final File source, final File target, final boolean recordEmptyRows, final char separator, final char quotechar, final int maxSheets)
			throws InvalidFormatException, IOException {
		//
		assert source != null;
		assert target != null;
		assert maxSheets > 0;
		assert source.exists();
		assert source.isFile();
		assert source.canRead();
		assert !source.getAbsolutePath().equals(target.getAbsolutePath());
		//

		// c.f. http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/ss/examples/ToCSV.java
		try (final InputStream is = newInputStream(source.toPath(), READ);
				final XSSFWorkbook workbook = new XSSFWorkbook(is);
				final FileWriter fw = new FileWriter(target);
				final CSVWriter writer = new CSVWriter(fw, separator, quotechar, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END)) {

			final XSSFFormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			final DataFormatter formatter = new DataFormatter();

			for (int s = 0; s < workbook.getNumberOfSheets() && s < maxSheets; s++) {
				if (s > 0) {
					fw.write('\f' /* Form Feed */ );
				}
				final XSSFSheet sheet = workbook.getSheetAt(s);
				final int rowN = sheet.getPhysicalNumberOfRows();
				final int colN = sheet.getRow(0).getLastCellNum();
				for (int r = 0; r < rowN; r++) {
					final XSSFRow row = sheet.getRow(r);
					final int colM = max(min(row.getLastCellNum(), colN), 0);
					final String[] values = new String[colM];

					boolean anyValue = false;
					for (int c = 0; c < colM; c++) {
						final XSSFCell cell = row.getCell(c);
						if (cell != null) {
							values[c] = asString(cell, formatter, evaluator);
							anyValue |= !values[c].isEmpty();
						}
					}

					if (anyValue) {
						final String[] line = colN == colM ? values : Arrays.copyOf(values, colN);
						writer.writeNext(line);
					} else if (recordEmptyRows) {
						writer.writeNext(new String[colN]);
					}
				}
			}
		}
	}

	/**
	 * Returns the value of of the cell as a string
	 * 
	 * @param cell the cell
	 * @param formatter the data formatter
	 * @param evaluator the formula evaluator
	 * @return the value
	 */
	public static String asString(final Cell cell, final DataFormatter formatter, final XSSFFormulaEvaluator evaluator) {
		//
		assert cell != null;
		assert formatter != null;
		assert evaluator != null;
		//
		String result = "";
		final CellType cellType = cell.getCellType();
		if (cellType == FORMULA) {
			try {
				result = formatter.formatCellValue(cell, evaluator);
			} catch (final NotImplementedException e) {
				// No operation
			}
		} else {
			result = formatter.formatCellValue(cell);
			if (cellType == NUMERIC) {
				final Matcher matcher = TIMESTAMP_FORMAT_WITH_COLON_ESCAPED_PATTERN.matcher(result);
				if (matcher.find()) {
					result = matcher.replaceFirst("$1:$2:$3");
				}
			}
		}

		return result;
	}

	/**
	 * Converts the given CSV file source Excel file into an Excel file
	 * 
	 * @param source the Excel file
	 * @param target the text file, pipe-delimited
	 * @param separator the delimiter to use for separating entries.
	 * @param quotechar the character to use for quoted elements.
	 * @throws IOException in case of unexpected error
	 * @throws InvalidFormatException in case of unexpected error
	 * @throws CsvValidationException in case of unexpected error
	 */
	public static void convertCommaSeparatedValuesToExcel(final File source, final File target, final char separator, final char quotechar)
			throws InvalidFormatException, IOException, CsvValidationException {
		//
		assert source != null;
		assert target != null;
		assert source.exists();
		assert source.isFile();
		assert source.canRead();
		assert !source.getAbsolutePath().equals(target.getAbsolutePath());
		//

		final CSVParserBuilder csvParserBuilder = new CSVParserBuilder();
		csvParserBuilder.withSeparator(separator);
		if (quotechar != NO_QUOTE_CHARACTER) {
			csvParserBuilder.withQuoteChar(quotechar);
		}
		final CSVParser csvParser = csvParserBuilder.build();

		try (final InputStream is = newInputStream(source.toPath(), READ);
				final InputStreamReader isr = new InputStreamReader(is);
				final CSVReader reader = new CSVReaderBuilder(isr).withCSVParser(csvParser).build();
				final XSSFWorkbook workbook = new XSSFWorkbook();
				final FileOutputStream out = new FileOutputStream(target)) {
			final XSSFSheet sheet = workbook.createSheet();
			final CreationHelper creationHelper = workbook.getCreationHelper();

			final short excelCustomDateFomat = creationHelper.createDataFormat().getFormat(CUSTOM_EXCEL_DATE_FORMAT);
			final short excelCustomTimestampFomat = creationHelper.createDataFormat().getFormat(CUSTOM_EXCEL_TIMESTAMP_FORMAT);
			final XSSFCellStyle excelCustomDateStyle = workbook.createCellStyle();
			final XSSFCellStyle excelCustomTimestampStyle = workbook.createCellStyle();
			excelCustomDateStyle.setDataFormat(excelCustomDateFomat);
			excelCustomTimestampStyle.setDataFormat(excelCustomTimestampFomat);

			String[] headers = null;
			int[] columnTextMaxLengths = null;

			int r = 0;
			String[] line = reader.readNext();
			while (line != null) {
				final Row row = sheet.createRow(r++);

				if (r == 1) {
					// Headers : column names
					headers = line;
					columnTextMaxLengths = new int[headers.length];
				}

				final int colN = max(line.length, headers.length);
				for (int c = 0; c < colN; c++) {
					final Cell cell = row.createCell(c);
					if (c >= line.length) {
						continue;
					}
					final String value = line[c];

					// Column text max length
					columnTextMaxLengths[c] = max(columnTextMaxLengths[c], value.length());

					// Guess the format
					final Double valueAsDouble = asDoubleOrNull(value);
					if (valueAsDouble != null) {
						cell.setCellValue(valueAsDouble.doubleValue());
					} else {
						final LocalDateTime valueAsDateTime = asDateTimeOrNull(value);
						if (valueAsDateTime != null) {
							cell.setCellValue(valueAsDateTime);
							cell.setCellStyle(excelCustomTimestampStyle);
						} else {
							final LocalDate valueAsDate = asDateOrNull(value);
							if (valueAsDate != null) {
								cell.setCellValue(valueAsDate);
								cell.setCellStyle(excelCustomDateStyle);
							} else {
								// Fallback
								cell.setCellValue(value);
							}
						}
					}
				}
				line = reader.readNext();
			}

			// Auto fit
			for (int c = 0; c < columnTextMaxLengths.length; c++) {
				sheet.setColumnWidth(c, Long.valueOf(round(((double) columnTextMaxLengths[c]) * 1.14388d * 256d)).intValue());
			}

			// Format as table
			formatAsTable(workbook, sheet, headers, r, "TableStyleMedium9", "Table0", 1L);

			workbook.write(out);
		}
	}

	/**
	 * Format as table
	 * 
	 * @param workbook the workbook
	 * @param sheet the sheet
	 * @param headers the headers
	 * @param rowN the total number of rows
	 * @param style the table style
	 * @param name the table name (must not contain spaces)
	 * @param id the table id
	 */
	public static void formatAsTable(final XSSFWorkbook workbook, final XSSFSheet sheet, final String[] headers, final int rowN, final String style, final String name, final long id) {
		//
		assert workbook != null;
		assert sheet != null;
		assert headers != null;
		assert rowN > 0;
		assert style != null;
		assert name != null;
		assert id > 0L;
		assert headers.length > 0;
		assert !style.isEmpty();
		assert !name.isEmpty();
		assert name.indexOf(' ') == -1 : "The Table name cannot contain spaces!";
		//

		final int colN = headers.length;
		final XSSFCreationHelper creationHelper = workbook.getCreationHelper();
		final AreaReference areaReference = creationHelper.createAreaReference(new CellReference(0, 0), new CellReference(max(rowN - 1, 1), max(colN - 1, 1)));
		// Note: the given example CreateTable.java is not working, so we have to stick to the old way.
		// link: https://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/xssf/usermodel/examples/CreateTable.java
		final XSSFTable table = sheet.createTable(null);
		final CTTable cttable = table.getCTTable();
		final CTTableStyleInfo tableStyleInfo = cttable.addNewTableStyleInfo();
		tableStyleInfo.setName(style);
		tableStyleInfo.setShowColumnStripes(false);
		tableStyleInfo.setShowRowStripes(true);
		cttable.setRef(areaReference.formatAsString());
		cttable.setDisplayName(name);
		cttable.setName(name);
		cttable.setId(id);
		final CTTableColumns columns = cttable.addNewTableColumns();
		cttable.addNewAutoFilter();
		columns.setCount((long) colN);
		for (int c = 0; c < colN; c++) {
			final CTTableColumn column = columns.addNewTableColumn();
			column.setName(headers[c]);
			column.setId(c + 1);
		}
	}

	/**
	 * Returns the given value as a Double value
	 * 
	 * @param value the value
	 * @return the value converted (maybe null in case of conversion failure)
	 */
	static Double asDoubleOrNull(final String value) {
		Double result = null;
		try {
			result = Double.parseDouble(value);
		} catch (final NumberFormatException e) {
			// No Operation
		}
		return result;
	}

	/**
	 * Returns the given value as a Date value.<br>
	 * Note: Time format, either {@link ExcelTooling#CUSTOM_TIMESTAMP_PARSER CUSTOM_TIMESTAMP_PARSER} or {@link ExcelTooling#DEFAULT_TIMESTAMP_DATEFORMAT DEFAULT_TIMESTAMP_FORMAT}
	 * 
	 * @param value the value
	 * @return the value converted (maybe null in case of conversion failure)
	 */
	static LocalDateTime asDateTimeOrNull(final String value) {
		LocalDateTime result = null;
		try {
			result = LocalDateTime.parse(value, CUSTOM_TIMESTAMP_PARSER);
		} catch (final DateTimeParseException e) {
			// No Operation
		}

		if (result == null) {
			try {
				result = DEFAULT_TIMESTAMP_DATEFORMAT.parse(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			} catch (final ParseException e) {
				// No Operation
			}
		}
		return result;
	}

	/**
	 * Returns the given value as a Date value.<br>
	 * Note: Date format, either {@link ExcelTooling#CUSTOM_DATE_PARSER CUSTOM_DATE_PARSER} or {@link ExcelTooling#DEFAULT_DATE_FOMAT DEFAULT_DATE_FOMAT}
	 * 
	 * @param value the value
	 * @return the value converted (maybe null in case of conversion failure)
	 */
	static LocalDate asDateOrNull(final String value) {
		LocalDate result = null;
		try {
			result = LocalDate.parse(value, CUSTOM_DATE_PARSER);
		} catch (final DateTimeParseException e) {
			// Do Nothing
		}

		if (result == null) {
			try {
				result = DEFAULT_DATE_DATEFORMAT.parse(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			} catch (final ParseException e) {
				// No Operation
			}
		}

		return result;
	}
}
