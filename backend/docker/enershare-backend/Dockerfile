FROM amazoncorretto:17-alpine

EXPOSE 15111

ARG JAR_FILE=enershare-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar

ARG DB_IP
ARG DB_PORT
ARG DB_NAME
ARG DB_USERNAME
ARG DB_PASSWORD

ENV DB_IP_ENV=${DB_IP}
ENV DB_PORT_ENV=${DB_PORT}
ENV DB_NAME_ENV=${DB_NAME}
ENV DB_USERNAME_ENV=${DB_USERNAME}
ENV DB_PASSWORD_ENV=${DB_PASSWORD}

RUN touch ./entrypoint.sh
RUN echo "java -jar ./app.jar --enershare.db.ip=${DB_IP_ENV} --enershare.db.port=${DB_PORT_ENV} --enershare.db.name=${DB_NAME_ENV} --enershare.db.username=${DB_USERNAME_ENV} --enershare.db.password=${DB_PASSWORD_ENV}" > ./entrypoint.sh
RUN chmod +x ./entrypoint.sh
CMD ["sh", "./entrypoint.sh"]
