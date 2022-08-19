package eu.fays.rockbox.jpa;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Adapter to convert a list of {@link Tag} into a single String where all list elements have been concatenated with pipe "|" sign.
 */
@Converter
public class ListOfTagAdapter implements AttributeConverter<List<Tag>, String> {

	/**
	 * @see jakarta.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
	 */
	@Override
	public String convertToDatabaseColumn(final List<Tag> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		final StringBuilder result = new StringBuilder();
		boolean flag = false;

		for (final Tag value : list) {
			if (flag) {
				result.append('|');
			} else {
				flag = true;
			}
			result.append(value.name());
		}
		return result.toString();

	}

	/**
	 * @see jakarta.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
	 */
	@Override
	public List<Tag> convertToEntityAttribute(final String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		final List<Tag> result = new ArrayList<>();
		for (final String name : value.split("|")) {
			result.add(Tag.valueOf(name));
		}
		return result;
	}

}
