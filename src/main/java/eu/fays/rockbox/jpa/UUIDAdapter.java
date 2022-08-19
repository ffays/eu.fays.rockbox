package eu.fays.rockbox.jpa;

import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Adapter to store UUID (identity operation).
 */
@Converter
public class UUIDAdapter implements AttributeConverter<UUID, UUID> {

	/**
	 * @see jakarta.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
	 */
	@Override
	public UUID convertToDatabaseColumn(final UUID uuid) {
		return uuid;
	}

	/**
	 * @see jakarta.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
	 */
	@Override
	public UUID convertToEntityAttribute(final UUID uuid) {
		return uuid;
	}
}
