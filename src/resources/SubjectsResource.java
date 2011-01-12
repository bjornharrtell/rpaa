package resources;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import entities.Category;
import entities.Category_;
import entities.Subject;
import entities.Vote;
import extjs.JsonReaderData;

@Path("/subjects")
@Stateless
public class SubjectsResource {
	@PersistenceContext
	private EntityManager em;

	@Context
	private SecurityContext securityContext;

	@GET
	@Path("/{subjectid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Subject getSubject(@PathParam("subjectid") String id) {
		return em.find(Subject.class, Integer.parseInt(id));
	}

	@PUT
	@Path("/{subjectid}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateSubject(@PathParam("subjectid") String id, JSONObject rows) throws JSONException {
		// TODO: make client PUT deserializable data		
		JSONObject subjectData = rows.getJSONObject("rows");

		Subject subject = em.find(Subject.class, Integer.parseInt(id));

		if (subjectData.has("name"))
			subject.setName(subjectData.getString("name"));
		if (subjectData.has("principal"))
			subject.setPrincipal(subjectData.getString("principal"));
	}

	@DELETE
	@Path("/{subjectid}")
	public void deleteSubject(@PathParam("subjectid") String id) throws JSONException {
		em.remove(em.find(Subject.class, Integer.parseInt(id)));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonReaderData getSubjects() throws JSONException {
		return new JsonReaderData(em.createNamedQuery("findAllSubjects").getResultList());
	}

	/*@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createSubject(final MultivaluedMap<String, String> formParameters) throws JSONException {
		String name = formParameters.get("name").get(0);
		String categoryName = formParameters.get("category").get(0);
		String principal = securityContext.getUserPrincipal().getName();

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
		Root<Category> p = criteriaQuery.from(Category.class);
		Predicate condition = criteriaBuilder.equal(p.get(Category_.name), categoryName);
		criteriaQuery.where(condition);
		TypedQuery<Category> query = em.createQuery(criteriaQuery);

		Category category = null;
		try {
			category = query.getSingleResult();
		} catch (javax.persistence.NoResultException e) {

		}

		Subject subject = new Subject(name, category, principal);
		em.persist(subject);
		em.flush();

		JSONObject jsonData = new JSONObject();
		jsonData.put("success", true);
		jsonData.put("id", subject.getId());
		
		return jsonData;
	}*/
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSubject(Subject subject) throws JSONException {
		subject.setPrincipal(securityContext.getUserPrincipal().getName());

		em.persist(subject);
		
		return Response.created(null).build();
	}

	@POST
	@Path("/{subjectid}/votes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createVote(@PathParam("subjectid") String subjectId) throws Exception {
		Subject subject = em.find(Subject.class, Integer.parseInt(subjectId));

		String principal = securityContext.getUserPrincipal().getName();

		Vote vote = new Vote();
		vote.setPrincipal(principal);
		em.persist(vote);
		subject.addVote(vote);
		
		return Response.created(null).build();
	}
}
