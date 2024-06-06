URLS REST

/pendingNotifications

@GET /pendingNotifications //Obtener notificaciones pendientes de envio.

@POST /markAsNotified/{idNotificacion} //Indique en la base que la notificacion fue enviada (notified = true)

/articles

POST /articles // crear un nuevo articulo
Header: user

GET /articles // obtener colección de articulos
Header: user

GET /articles/{idArticulo}

PATCH /articles/{idArticulo}
Header: user

POST /articles/{idArticulo}/users/ // crear anotacion
Header: user

GET /articles/{idArticulo}/users // ver usuarios anotados en una publicación

PATCH /articles/{idArticulo}/close // actualizar articulo (para por ejemplo, cerrar la publicacion)
Header: user

/users

POST /users

PATCH /users/{idUsuario}

GET /users

GET /users/{idUsuario}

GET /users/login

Body para crear un usuario de prueba:
{
  "name":"juan",
  "surname":"perez",
  "email": "b@b.com",
  "pass": "123456"
}

REQUIRED ENVIROMENT VARIABLES:
CON_STRING
MONGO_DB
