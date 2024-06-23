FROM ubuntu:24.04

RUN apt-get update && apt-get -y install cron && apt-get -y install curl

COPY deadline /etc/cron.d/hello-cron

CMD ["cron", "-f"]

# Give execution rights on the cron job
RUN chmod 0644 /etc/cron.d/hello-cron

# Give execution rights on the cron job, Apply cron job, Create the log file to be able to run tail
RUN chmod 0644 /etc/cron.d/hello-cron && crontab /etc/cron.d/hello-cron && touch /var/log/cron.log

# Run the command on container startup
CMD cron && tail -f /var/log/cron.log