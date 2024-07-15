package ar.edu.utn.frba.tacs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.utn.frba.tacs.model.LoggedUser;
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
        System.out.println("Cantidad de Notificaciones pendientes: " + pendingNotifications.size());
        List<Notification.NotificationDTO> notificationsWithSessions = new ArrayList<>();
        List<LoggedUser> activeSessions ;
        for (Notification notification : pendingNotifications) {
            activeSessions = loggedUserRepository.listOpenSessions(notification.getSubscriber());
            System.out.println("Cantidad de Sesiones activas en total: " + activeSessions.size() + " - " + "UserId: " + notification.getSubscriber());
            List<String> sessions = loggedUserRepository.listOpenSessionsInBot(notification.getSubscriber());
            System.out.println("Cantidad de Sesiones activas en BOT: " + activeSessions.size() + " - " + "UserId: " + notification.getSubscriber());
            if (sessions != null) {
                System.out.println("No se encontraron sesiones activas asociadas a las notificiaciones pendientes");
            }else {
            for (String sessionId : sessions) {
                Notification.NotificationDTO dto = notification.convertToDTO();
                dto.setSubscriber(sessionId);
                notificationsWithSessions.add(dto);
            }
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
