package entities.tree;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "edges", uniqueConstraints = @UniqueConstraint(columnNames = { "child", "parent" }))
public class Edge {
	@Id
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "child", nullable = false)
	Vertex child;

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "parent", nullable = false)
	Vertex parent;
	
	public Edge() {

	}

	public Edge(Vertex vertex) {
		this(vertex, null);
	}

	public Edge(Vertex vertex, Vertex parent) {
		this.child = vertex;
		this.parent = parent;
	}

	public Vertex getChild() {
		return child;
	}

	public Vertex getParent() {
		return parent;
	}
}
