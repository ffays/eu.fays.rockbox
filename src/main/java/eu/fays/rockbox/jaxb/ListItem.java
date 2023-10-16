package eu.fays.rockbox.jaxb;

import java.lang.reflect.InvocationTargetException;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * List Item
 * @param <T> the type of the item
 * @author Frederic Fays
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ListItem<T> {

	/**
	 * Constructor
	 */
	public ListItem() {
		try {
			li = getParameterizedType().getDeclaredConstructor().newInstance();
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructor
	 * @param li value of the list item
	 */
	public ListItem(final T li) {
		this.li = li;
	}

	/**
	 * Returns the value
	 * @return the value
	 */
	public T getLi() {
		return li;
	}

	/**
	 * Sets the value
	 * @param li the value
	 */
	public void setLi(final T li) {
		this.li = li;
	}

	/**
	 * Returns the effective class of the parameterized type
	 * @return the class
	 */
	@XmlTransient
	public abstract Class<T> getParameterizedType();

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getLi().hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(getClass().isInstance(obj))) {
			return false;
		}

		@SuppressWarnings("unchecked")
		final ListItem<T> o = (ListItem<T>) obj;
		return getLi().equals(o.getLi());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getLi().toString();
	}

	/** list item's value */
	@XmlValue
	private T li;
}
