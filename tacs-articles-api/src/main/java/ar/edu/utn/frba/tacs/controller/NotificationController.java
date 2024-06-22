package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.service.NotificationService;
import ar.edu.utn.frba.tacs.service.ReportService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/pendingNotifications")
@Produces("application/json")
public class NotificationController {

    private final NotificationService notificationService;
    public NotificationController(){
        notificationService = new NotificationService(System.getenv("CON_STRING"));
    }
    public  NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }
    @GET
    public List<Notification.NotificationDTO> getPendingNotifications() {
        List<Notification> pendingNotifications = notificationService.getPendingNotifications();
        return pendingNotifications.stream()
                .map(Notification::convertToDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/markAsNotified/{id}")
    @Consumes("application/json")
    public Response markAsNotified(@PathParam("id") String id) {
        int updated = notificationService.markAsNotified(id);
        if (updated == 0) {
            return Response.ok().build();
        } else if (updated == 1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
