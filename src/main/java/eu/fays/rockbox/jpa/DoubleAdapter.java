package eu.fays.rockbox.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Adapter to convert {@link Double#NaN} (Java) to NULL (Database) back and forth.
 */
@Converter
public class DoubleAdapter implements AttributeConverter<Double, Double> {

	/**
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
	 */
	@Override
	public Double convertToDatabaseColumn(final Double d) {
		if (Double.isNaN(d)) {
			return null;
		}
		return d;
	}

	/**
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
	 */
	@Override
	public Double convertToEntityAttribute(final Double d) {
		if (d == null) {
			return Double.NaN;
		}
		return d;
	}
}
