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

import entities.User;
import entities.Vote;
import entities.Vote_;

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
	public User getCurrentUser() {
		String principal = securityContext.getUserPrincipal().getName();

		int votesLeft = 3;

		// TODO: make this a named query
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Vote> criteriaQuery = criteriaBuilder.createQuery(Vote.class);
		Root<Vote> p = criteriaQuery.from(Vote.class);
		Predicate condition = criteriaBuilder.equal(p.get(Vote_.principal), principal);
		criteriaQuery.where(condition);
		TypedQuery<Vote> query = em.createQuery(criteriaQuery);
		List<Vote> votes = query.getResultList();

		votesLeft = votesLeft - votes.size();

		return new User(principal, votesLeft);
	}
}
