package eu.fays.rockbox.jaxb.mapofmap2;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public class Dictionary {
	@XmlID
	@XmlAttribute
	private String id;

	@XmlAttribute
	private String key;

	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	private List<Item> items;

	public Dictionary() {
		id = randomUUID().toString();
	}

	public Dictionary(Entry<String, Map<String, Object>> entry) {
		this();
		setKey(entry.getKey());
		items = entry.getValue().entrySet().stream().map(Item::new).collect(toList());

	}

	public String getId() {
		if (id == null) {
			id = randomUUID().toString();
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Stream<Dictionary> dictionaryStream() {
		final List<Dictionary> result = new ArrayList<>();
		final Deque<Item> stack = new ArrayDeque<>(getItems());
		result.add(this);

		while (!stack.isEmpty()) {
			final Item item = stack.pop();
			if (item.hasDictionary()) {
				result.add(item.getDictionary());
				stack.addAll(item.getDictionary().getItems());
			}
		}

		return result.stream();
	}
}
