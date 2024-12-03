package dk.leghetto.resources;
import jakarta.ws.rs.core.Response;

import java.util.List;

import dk.leghetto.classes.Colors;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/colors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ColorsResource {

    @Path("/getColors")
    @GET
    public Response getColors() {
        List<Colors> colors = Colors.listAll(); 
        return Response.ok(colors).build();
    }

    @RolesAllowed("admin")
    @Path("/addColor")
    @POST
    @Transactional
    public Response addColor(Colors color) {
        Colors existingColor = Colors.find("name", color.getName()).firstResult();
        if (existingColor != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Color with the name '" + color.getName() + "' already exists.")
                    .build();
        }
        color.persist();
        return Response.status(Response.Status.CREATED).entity(color).build();
    }



}
