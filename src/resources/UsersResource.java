package resources;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import entities.grid.Vote;
import entities.grid.Vote_;
import entities.tree.Edge;
import entities.tree.Edge_;
import entities.tree.Vertex;

@Stateless
@Path("/users")
public class UsersResource {
	@PersistenceContext
	private EntityManager em;

	@Context
	private SecurityContext securityContext;

	@GET
	@Path("/current")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getCurrentUser() throws JSONException {
		String principal = securityContext.getUserPrincipal().getName();

		JSONObject user = new JSONObject();

		int votesLeft = 3;

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Vote> criteriaQuery = criteriaBuilder.createQuery(Vote.class);
		Root<Vote> p = criteriaQuery.from(Vote.class);
		Predicate condition = criteriaBuilder.equal(p.get(Vote_.principal), principal);
		criteriaQuery.where(condition);
		TypedQuery<Vote> query = em.createQuery(criteriaQuery);
		List<Vote> votes = query.getResultList();

		votesLeft = votesLeft - votes.size();

		user.put("principal", principal);
		user.put("votesLeft", votesLeft);

		return user;
	}
}
