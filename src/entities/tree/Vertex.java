package entities.tree;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "vertices")
@Inheritance(strategy = InheritanceType.JOINED)
public class Vertex {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true, targetEntity = Edge.class)
	List<Vertex> children;

	public Vertex() {

	}

	public int getId() {
		return id;
	}

	public boolean isLeaf() {
		return children.size() == 0 ? true : false;
	}
}
