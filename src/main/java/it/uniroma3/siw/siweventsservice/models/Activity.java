package it.uniroma3.siw.siweventsservice.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	private Float duration;

	@NotEmpty
	@ManyToMany
	private List<Tool> toolList;

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Activity activity = (Activity) o;
		return name.equals(activity.name);
	}

	@Override
	public int hashCode () {
		return Objects.hash(name);
	}
}
