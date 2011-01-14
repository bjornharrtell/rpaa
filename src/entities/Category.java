package entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@NamedQuery(name = "findAllCategories", query = "select c from Category c")
public class Category {
	//@Ref("categories/{id}")
	//URI uri;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	
	@Column(unique=true, nullable=false)
	String name;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
	List<Subject> subjects;

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name == null ? "Okänd" : name;
	}
	
	public void addSubject(Subject subject) {
		subjects.add(subject);
		subject.category = this;
	}
}
