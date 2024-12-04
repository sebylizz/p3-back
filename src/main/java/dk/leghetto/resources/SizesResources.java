package dk.leghetto.resources;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dk.leghetto.classes.Sizes;

@Path("/sizes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SizesResources {

    @Path("/getSizes")
    @GET
    public Response getSizes() {
        List<Sizes> sizes = Sizes.listAll(); 
        return Response.ok(sizes).build();
    }
    
    @RolesAllowed("admin")
    @Path("/addSizes")
    @POST
    @Transactional
    public Response addSize(Sizes size) {
        Sizes existingSize = Sizes.find("name", size.getName()).firstResult();
        if (existingSize != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Size with the name '" + size.getName() + "' already exists.")
                    .build();
        }
        size.persist();
        return Response.status(Response.Status.CREATED).entity(size).build();
    }
}
