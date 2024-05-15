package ar.edu.utn.frba.tacs.controller;

import ar.edu.utn.frba.tacs.service.ReportService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/reports")
public class ReportsController {
	private final ReportService reportService = new ReportService();

    @GET
    @Path("/users")
    public int getUsersCount() {
        return reportService.getUsersCount();
    }
    
    @GET
    @Path("/articles")
    public int countArticles() {
        return reportService.countArticles();
    }
    
    @GET
    @Path("/articles/success")
    public int countSuccessfulArticles() {
        return reportService.countSuccessfulArticles();
    }
    
    @GET
    @Path("/articles/failed")
    public int countFailedArticles() {
        return reportService.countFailedArticles();
    }

    @GET
    @Path("/engaged_users")
    public int getEngagedUsers() {
        return reportService.getEngagedUsers();
    }
}
