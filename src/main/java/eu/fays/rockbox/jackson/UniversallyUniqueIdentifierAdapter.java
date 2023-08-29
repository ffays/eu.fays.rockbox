package eu.fays.rockbox.jackson;

import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Adapter to store UUID (identity operation).
 */
@Converter
public class UniversallyUniqueIdentifierAdapter implements AttributeConverter<UUID, Object> {

	/**
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
	 */
	@Override
	public Object convertToDatabaseColumn(final UUID uuid) {
		return uuid;
	}

	/**
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
	 */
	@Override
	public UUID convertToEntityAttribute(final Object uuid) {
		if (uuid instanceof String) {
			return UUID.fromString((String) uuid);
		} else if (uuid instanceof UUID) {
			return (UUID) uuid;
		}

		return null;
	}
}
