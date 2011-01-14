package resources;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;

import entities.Category;
import entities.Subject;
import extjs.JsonReaderData;

@Path("/categories")
@Stateless
public class CategoriesResource {
	@PersistenceContext
	private EntityManager em;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonReaderData getCategories() {
		return new JsonReaderData(em.createNamedQuery("findAllCategories").getResultList());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String createCategory(Category category) {		
		em.persist(category);
		em.flush();
		
		return Integer.toString(category.getId());
	}
	
	@POST
	@Path("/{id}/subjects")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addSubject(@PathParam("id") String id, JSONObject subjectData) throws Exception {
		int subjectId = subjectData.getInt("id");
		
		Category category = em.find(Category.class, Integer.parseInt(id));
		Subject subject = em.find(Subject.class, subjectId);
		
		category.addSubject(subject);
		em.flush();
	}
}
