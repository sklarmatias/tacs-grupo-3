# tacs-grupo-3
Git repository of the group 3 TACS project.
## Documentation

It consists of 3 components:

* A REST API that handles the core business logic and communication with the database.
* A database that persists all entities.
* A Telegram bot that serves as an interface between the end user and the API, allowing the end user to make use of all the required use cases.

### Tech Stack

For the database, we chose MongoDB.

For the API, we chose a Tomcat Java server.

For the Telegram bot, we are using the Telegram API through Java, connecting the instance using the long polling strategy.
## Enviroment variables

To run this proyect, you'll have to configure your .env file with the following enviroment variables:

### Database related:

`MONGO_INITDB_ROOT_USERNAME`

`MONGO_INITDB_ROOT_PASSWORD`

`MONGO_DB`

### Telegram Bot related:

`BOT_TOKEN`

`BOT_USERNAME`

`NOTIFICATION_REFRESHING_TIME` time in seconds

`RESOURCE_URL` this one should be the API root URL, without any final "/"

## Deployment

To deploy this project, configure all necessary enviroment variables and then run

```bash
  docker compose up --build
```
