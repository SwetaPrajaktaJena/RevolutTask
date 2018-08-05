package com.revolut.task;

import com.revolut.task.service.AccountService;
import com.revolut.task.service.ServiceExceptionMapper;
import com.revolut.task.service.TransactionService;
import com.revolut.task.service.UserService;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Main Class (Starting point) 
 */
public class Application {

	private static Logger log = Logger.getLogger(Application.class);

	
	public static Server StartServer() {

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
				UserService.class.getCanonicalName() + "," + AccountService.class.getCanonicalName() + ","
						+ ServiceExceptionMapper.class.getCanonicalName() + ","
						+ TransactionService.class.getCanonicalName());

		try {
			jettyServer.start();
			jettyServer.join();
		} catch (Exception e) {
			e.printStackTrace();
	        jettyServer.destroy();
		} 
		return jettyServer;
	}

	public static void main(String[] args) throws Exception {
		StartServer();
	}

}
