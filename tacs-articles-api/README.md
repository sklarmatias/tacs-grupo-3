URLS REST

/articles

POST /articles // crear un nuevo articulo

GET /articles // obtener colección de articulos

GET /articles/{idArticulo}

POST /articles/{idArticulo}/users/{idUsuario} // crear anotacion

GET /articles/{idArticulo}/users // ver usuarios anotados en una publicación

PATCH /articles/{idArticulo}/close // actualizar articulo (para por ejemplo, cerrar la publicacion)

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
