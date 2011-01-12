package resources;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.Category;
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
	public Response createCategory(Category category) {		
		em.persist(category);
		
		return Response.created(null).build();
	}
}
