package entities.grid;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Entity
@Table(name = "subjects",uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@NamedQuery(name = "findAllSubjects", query = "select s from Subject s order by s.name")
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(unique=true, nullable=false)
	private String name;

	@Column(unique=false, nullable=true)
	private String principal;

	@OneToOne(optional=true)
	@JoinColumn(name = "category")
	private Category category;

	// NOTE: this is for custom ordering support
	// @OneToOne(optional = true, cascade = CascadeType.ALL)
	// private Subject link;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subject")
	private List<Vote> votes;

	public Subject() {
	}

	public Subject(String name, Category category, String principal/* , Subject link */) {
		this.name = name;
		this.category = category;
		this.principal = principal;

		// NOTE: this is for custom ordering support
		// this.link = link;
	}

	public int getId() {
		return id;
	}

	// NOTE: this is for custom ordering support
	/*
	 * public void setLink(Subject link) { this.link = link; }
	 * 
	 * public Subject getLink() { return link; }
	 */

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getVotesSum() {
		return votes.size();
	}

	public void addVote(Vote vote) {
		vote.setSubject(this);
		votes.add(vote);
	}

	public boolean haveVoted(String ip) {
		for (Vote vote : votes) {
			if (vote.getPrincipal().equals(ip))
				return true;
		}
		return false;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getPrincipal() {
		return principal;
	}
	
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("id", this.getId());
		jsonObject.put("name", this.getName());
		jsonObject.put("principal", this.getPrincipal());
		jsonObject.put("category", this.category != null ? this.category.getName() : "Okänd");
		jsonObject.put("votes", this.getVotesSum());
	
		return jsonObject;
	}
}
