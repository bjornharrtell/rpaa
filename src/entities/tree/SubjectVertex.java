package entities.tree;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "subjectvertices")
public class SubjectVertex extends Vertex {
	String title;
	int votes;

	public SubjectVertex() {
		super();
	}

	public SubjectVertex(String title) {
		super();
		this.title = title;
	}

	public SubjectVertex(String title, int votes) {
		this.title = title;
		this.votes = votes;
	}

	public String getTitle() {
		return title;
	}
}
