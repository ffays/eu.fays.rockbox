package eu.fays.rockbox.dx;

import static java.text.MessageFormat.format;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "sku", "available", "price", "description" })
public class Article {

	public Article() {

	}

	public Article(final int sku, final boolean available, final BigDecimal price, final String description) {
		this.sku = sku;
		this.available = available;
		this.price = price;
		this.description = description;
	}

	@Override
	public String toString() {
		return format("{0,number,0} - {1} - {2} - {3}", sku, available, price, description);
	}

	@Override
	public int hashCode() {
		return sku;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Article)) {
			return false;
		}
		return sku == ((Article) o).sku;
	}

	@XmlAttribute
	public int sku;
	@XmlAttribute
	public boolean available;
	@XmlAttribute
	public BigDecimal price;
	@XmlValue
	public String description;
}
