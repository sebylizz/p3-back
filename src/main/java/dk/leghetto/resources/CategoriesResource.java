package dk.leghetto.resources;

import jakarta.ws.rs.core.Response;

import java.util.List;

import dk.leghetto.classes.Category;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriesResource {

    @Path("/getCategories")
    @GET
    public Response getCategories() {
        List<Category> category = Category.listAll();
        return Response.ok(category).build();
    }

    @RolesAllowed("admin")
    @Path("/addCategory")
    @POST
    @Transactional
    public Response addCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Category name cannot be null or empty.")
                    .build();
        }
        Category existingCategory = Category.find("name", category.getName()).firstResult();
        if (existingCategory != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Category with the name '" + category.getName() + "' already exists.")
                    .build();
        }
        if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
            Category parentCategory = Category.findById(category.getParentCategory().getId());
            if (parentCategory == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Parent category with ID '" + category.getParentCategory().getId()
                                + "' does not exist.")
                        .build();
            }
            category.setParentCategory(parentCategory);
        }

        category.persist();
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

}
