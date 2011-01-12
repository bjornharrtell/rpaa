package entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "subjects", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@NamedQuery(name = "findAllSubjects", query = "select s from Subject s order by s.name")
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@Column(unique = true, nullable = false)
	String name;

	@Column(unique = false, nullable = true)
	String principal;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "category", nullable=true)
	Category category;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subject")
	List<Vote> votes;

	public Subject() {
	}

	public Subject(String name, Category category, String principal) {
		this.name = name;
		this.category = category;
		this.principal = principal;
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

	public int getVotes() {
		return votes.size();
	}

	public void addVote(Vote vote) {
		vote.setSubject(this);
		votes.add(vote);
	}

	public Integer getCategory() {
		Integer id;
		
		if (category == null) {
			 id = null;
		} else {
			id = category.id;
		}
		
		return id;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getPrincipal() {
		return principal;
	}
}
