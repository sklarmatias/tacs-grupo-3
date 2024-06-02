package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.service.NotificationService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;


@Path("/pendingNotifications")
public class NotificationController {

    private final NotificationService notificationService = new NotificationService();

    @GET
    @Produces("application/json")
    public List<Notification.NotificationDTO> getPendingNotifications() {
        System.out.println("getting pending notifications");
        List<Notification> pendingNotifications = notificationService.getPendingNotifications();
        return pendingNotifications.stream()
                .map(Notification::convertToDTO)
                .collect(Collectors.toList());
    }


    @POST
    @Path("/markAsNotified/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response markAsNotified(@PathParam("id") String id) {
        boolean updated = notificationService.markAsNotified(id);
        if (updated) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
