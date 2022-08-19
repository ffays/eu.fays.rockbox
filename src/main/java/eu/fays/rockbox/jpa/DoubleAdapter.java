package eu.fays.rockbox.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Adapter to convert {@link Double#NaN} (Java) to NULL (Database) back and forth.
 */
@Converter
public class DoubleAdapter implements AttributeConverter<Double, Double> {

	/**
	 * @see jakarta.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
	 */
	@Override
	public Double convertToDatabaseColumn(final Double d) {
		if (Double.isNaN(d)) {
			return null;
		}
		return d;
	}

	/**
	 * @see jakarta.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
	 */
	@Override
	public Double convertToEntityAttribute(final Double d) {
		if (d == null) {
			return Double.NaN;
		}
		return d;
	}
}
