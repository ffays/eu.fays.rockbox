package eu.fays.rockbox.jpa;

import javax.persistence.JoinColumn;
import static javax.persistence.CascadeType.ALL;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.Converter;

@Entity
@Table(schema = "kanban", name = "story")
public class Story {

	@Id
	@Column(name = "story_name")
	public String name;

	@Column(name = "description")
	public String description;

	@Column
	@Enumerated(EnumType.STRING)
	public Board board = Board.BACKLOG;

	@Column
	public boolean archived = false;

	@Column
	public LocalDateTime created = LocalDateTime.now();

	@OneToMany(mappedBy = "story", cascade = ALL, orphanRemoval = true)
	@OrderColumn(name="index")
	@CascadeOnDelete
	public List<Task> tasks = new ArrayList<>();

	@ElementCollection
	@CollectionTable(schema = "kanban", name = "story__tag", joinColumns = { @JoinColumn(name = "story_name") })
	@Column(name = "tag_name")
	@Enumerated(EnumType.STRING)
	public List<Tag> tags = new ArrayList<>();
	
	
	@Column(name = "tag_name2")
	@Enumerated(EnumType.STRING)
	@Convert(converter = ListOfTagAdapter.class)
	public List<Tag> tags2 = new ArrayList<>();

	public Story() {

	}

	public Story(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Story)) {
			return false;
		}

		return name.equals(((Story) o).name);
	}

	@Override
	public String toString() {
		return name + " \u21d2 " + tasks.toString();
	}

}
