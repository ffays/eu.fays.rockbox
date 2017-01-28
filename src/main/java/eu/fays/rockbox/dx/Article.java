package eu.fays.rockbox.dx;

import static java.text.MessageFormat.format;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

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

	@XmlElement
	public int sku;
	@XmlElement
	public boolean available;
	@XmlElement
	public BigDecimal price;
	@XmlElement
	public String description;
}
