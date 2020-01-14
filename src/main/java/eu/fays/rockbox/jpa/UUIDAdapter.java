package eu.fays.rockbox.jpa;

import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Adapter to store UUID (identity operation).
 */
@Converter
public class UUIDAdapter implements AttributeConverter<UUID, UUID> {

	/**
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
	 */
	@Override
	public UUID convertToDatabaseColumn(final UUID uuid) {
		return uuid;
	}

	/**
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
	 */
	@Override
	public UUID convertToEntityAttribute(final UUID uuid) {
		return uuid;
	}
}
