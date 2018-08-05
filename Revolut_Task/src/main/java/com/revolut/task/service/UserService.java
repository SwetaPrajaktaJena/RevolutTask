package com.revolut.task.service;

import com.revolut.task.dao.DAOFactory;
import com.revolut.task.dao.impl.ImplDAOFactory;
import com.revolut.task.exception.CustomException;
import com.revolut.task.model.User;

import org.apache.log4j.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
 
	private static final DAOFactory factoryOBJ=new ImplDAOFactory();
    
	private static Logger log = Logger.getLogger(UserService.class);

	/**
	 * Find by userName
	 *
	 */
    @GET
    @Path("/{userName}")
    public User getUserByName(@PathParam("userName") String userName) throws CustomException {
        if (log.isDebugEnabled())
            log.debug("Request Received for get User by Name " + userName);
        final User user = factoryOBJ.getUserDAO().getUserByName(userName);
        if (user == null) {
            throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
        }
		return user;
    }
    
    /**
	 * Find by all
	 * 
	 */
    @GET
    @Path("/all")
    public List<User> getAllUsers() throws CustomException {
        return factoryOBJ.getUserDAO().getAllUsers();
    }
    
    /**
     * Create User
     * 
     */
    @Path("/create")
	@POST
    public User createUser(@FormParam(value = "userName") String userName,
    		@FormParam(value = "email") String email,
    		@FormParam(value = "userId") int userId) throws CustomException {
        User user = new User(userId, userName,email);
        final long uId = factoryOBJ.getUserDAO().insertUser(user);
        if(uId==1)return user;
        else return null;
    }
    
    /**
     * Find by User Id
     * 
     */
    
    @PUT
    @Path("/update/{userId}/{userName}/{email}")
    public User updateUser(@PathParam("userName") String userName,
    		@PathParam("email") String email,
    		@PathParam("userId") int userId) throws CustomException {
        User user = new User(userId,userName,email);
        final long uId = factoryOBJ.getUserDAO().insertUser(user);
        return user;
    }
    
    
    /**
     * Delete by User Id
     * 
     */
    
    @Path("/{userId}")
    @DELETE
    public Response deleteUser(@PathParam("userId") long userId) throws CustomException {
        int deleteCount = factoryOBJ.getUserDAO().deleteUser(userId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
