package eu.fays.rockbox.jackson;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface IBookBasicMixin {
	@JsonProperty String getId();
	@JsonProperty String getTitle();
	@JsonIgnore String getAuthor();
	@JsonIgnore LocalDate getPublicationDate();
	@JsonIgnore Book getPreviousEdition();
	@JsonIgnore	Borrower getBorrower();
}
