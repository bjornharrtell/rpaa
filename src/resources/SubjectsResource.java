package resources;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import javax.ws.rs.core.SecurityContext;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Subject getSubject(@PathParam("id") String id) {
		return em.find(Subject.class, Integer.parseInt(id));
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSubjectDescription(@PathParam("id") String id) {
		Subject subject = em.find(Subject.class, Integer.parseInt(id));
		
		return subject.getDescription();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	public void updateSubjectDescription(@PathParam("id") String id, String html) throws JSONException {
		Subject subject = em.find(Subject.class, Integer.parseInt(id));
		
		subject.setDescription(html);
		
		em.flush();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateSubject(@PathParam("id") String id, JSONObject subjectData) throws JSONException {
		// TODO: make client PUT deserializable data		

		Subject subject = em.find(Subject.class, Integer.parseInt(id));

		if (subjectData.has("name"))
			subject.setName(subjectData.getString("name"));
		if (subjectData.has("principal"))
			subject.setPrincipal(subjectData.getString("principal"));
		
		em.flush();
	}

	@DELETE
	@Path("/{id}")
	public void deleteSubject(@PathParam("id") String id) throws JSONException {
		em.remove(em.find(Subject.class, Integer.parseInt(id)));
		em.flush();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonReaderData getSubjects() throws JSONException {
		return new JsonReaderData(em.createNamedQuery("findAllSubjects").getResultList());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String createSubject(Subject subject) throws JSONException {
		String principal = securityContext.getUserPrincipal().getName();
		
		subject.setPrincipal(principal);
		
		em.persist(subject);
		em.flush();
		
		return Integer.toString(subject.getId());
	}

	@POST
	@Path("/{id}/votes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String createVote(@PathParam("id") String id) throws Exception {
		Subject subject = em.find(Subject.class, Integer.parseInt(id));

		String principal = securityContext.getUserPrincipal().getName();

		Vote vote = new Vote();
		vote.setPrincipal(principal);
		
		em.persist(vote);
		subject.addVote(vote);
		em.flush();
		
		return Integer.toString(vote.getId());
	}
}
