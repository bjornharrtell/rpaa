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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import entities.tree.Edge;
import entities.tree.Edge_;
import entities.tree.SubjectVertex;
import entities.tree.Vertex;



@Path("/treeloader")
@Stateless
public class TreeLoaderResource {

	@PersistenceContext
	private EntityManager em;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray getTreeNodes(@QueryParam("node") String nodeFilter) throws JSONException {
		// query for root or childnodes dep. on querystring
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Edge> criteriaQuery = criteriaBuilder.createQuery(Edge.class);
		Root<Edge> p = criteriaQuery.from(Edge.class);
		int id = Integer.valueOf(nodeFilter);
		Vertex parent = em.find(Vertex.class, id);
		Predicate condition = criteriaBuilder.equal(p.get(Edge_.parent), parent);
		criteriaQuery.where(condition);
		TypedQuery<Edge> query = em.createQuery(criteriaQuery);
		List<Edge> result = query.getResultList();

		// custom JSON output
		JSONArray array = new JSONArray();
		for (Edge edge : result) {
			Vertex child = edge.getChild();
			SubjectVertex subject = (SubjectVertex) child;
			String text = subject.getTitle();
			boolean leaf = subject.isLeaf();
			JSONObject object = new JSONObject();
			object.put("id", subject.getId());
			object.put("text", text);
			if (leaf)
				object.put("leaf", leaf);
			array.put(object);
		}

		return array;
	}
}
