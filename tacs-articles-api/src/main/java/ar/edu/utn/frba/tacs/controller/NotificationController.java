package ar.edu.utn.frba.tacs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.repository.login.LoggedUserMemoryRepository;
import ar.edu.utn.frba.tacs.repository.login.LoggedUserRepository;
import ar.edu.utn.frba.tacs.service.NotificationService;
import ar.edu.utn.frba.tacs.service.ReportService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/pendingNotifications")
@Produces("application/json")
public class NotificationController {

    private final NotificationService notificationService;
    private final LoggedUserRepository loggedUserRepository = LoggedUserMemoryRepository.getInstance();
    public NotificationController(){
        notificationService = new NotificationService(System.getenv("CON_STRING"));
    }
    public  NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }
    @GET
    public List<Notification.NotificationDTO> getPendingNotifications() {
        List<Notification> pendingNotifications = notificationService.getPendingNotifications();
        List<Notification.NotificationDTO> notificationsWithSessions = new ArrayList<>();

        for (Notification notification : pendingNotifications) {
            List<String> sessions = loggedUserRepository.listOpenSessionsInBot(notification.getSubscriber());
            for (String sessionId : sessions) {
                Notification.NotificationDTO dto = notification.convertToDTO();
                dto.setSubscriber(sessionId);
                notificationsWithSessions.add(dto);
            }
        }

        return notificationsWithSessions;
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
