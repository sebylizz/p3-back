package dk.leghetto.resources;
import jakarta.ws.rs.core.Response;

import java.util.List;

import dk.leghetto.classes.Collection;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/collections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CollectionsResource {

    @Path("/getCollections")
    @GET
    public Response getCollection() {
        List<Collection> collcetion = Collection.listAll(); 
        return Response.ok(collcetion).build();
    }

    @RolesAllowed("admin")
    @Path("/addCollection")
    @POST
    @Transactional
    public Response addCollection(Collection collection) {
        Collection existingCollection = Collection.find("name", collection.getName()).firstResult();
        if (existingCollection != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Color with the name '" + collection.getName() + "' already exists.")
                    .build();
        }
        collection.persist();
        return Response.status(Response.Status.CREATED).entity(collection).build();
    }
}
