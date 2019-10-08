package eu.fays.rockbox.jpa;

import static java.util.Arrays.asList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Adapter to convert a list of String into a single String where all list elements have been concatenated with pipe "|" sign.
 */
@Converter
public class ListOfStringAdapter implements AttributeConverter<List<String>, String> {

	/**
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
	 */
	@Override
	public String convertToDatabaseColumn(final List<String> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		final StringBuilder result = new StringBuilder();
		boolean flag = false;

		for (final String value : list) {
			if (flag) {
				result.append('|');
			} else {
				flag = true;
			}
			result.append(value);
		}
		return result.toString();
	}

	/**
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
	 */
	@Override
	public List<String> convertToEntityAttribute(final String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		final List<String> result = asList(value.split("|"));
		return result;
	}

}
