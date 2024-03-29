package eu.fays.rockbox.jaxb;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * An adapter to both marshal and unmarshal a GregorianCalendar to an XML date<br>
 * <br>
 * Article: <a href="http://stackoverflow.com/questions/13568543/how-do-you-specify-the-date-format-used-when-jaxb-marshals-xsddatetime">How do you specify the date format used when JAXB marshals xsd:dateTime?</a><br>
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class XMLDateAdapter extends XmlAdapter<String, GregorianCalendar> {

	/** UTC time zone */
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	/** ISO8601 date format */
	private static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	static {
		ISO8601_DATE_FORMAT.setTimeZone(UTC);
	}

	/**
	 * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(final GregorianCalendar v) throws Exception {
		return ISO8601_DATE_FORMAT.format(v.getTime());
	}

	/**
	 * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public GregorianCalendar unmarshal(final String v) throws Exception {
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeZone(UTC);
		result.setTime(ISO8601_DATE_FORMAT.parse(v));
		return result;
	}

	/**
	 * Another way to convert a GregorianCalendar into an XML date
	 * @param v the input date
	 * @return the output date
	 */
	public static XMLGregorianCalendar toXMLDate(final GregorianCalendar v) {
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(ISO8601_DATE_FORMAT.format(v.getTime()));
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
