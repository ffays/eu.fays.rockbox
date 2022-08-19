package eu.fays.rockbox.jpa;

import static jakarta.persistence.CascadeType.ALL;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.CascadeOnDelete;

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
