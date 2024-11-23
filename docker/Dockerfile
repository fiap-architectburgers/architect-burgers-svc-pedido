##
# This version of the Dockerfile is to be integrated with the Pipeline and it depends on
# the application being pre-packaged in the ./temp_libs directory at the repository
#
# (for reference, the commands below run at the repo root will prepare the dependencies)
# mvn install dependency:copy-dependencies
# mkdir ./docker/temp_libs && cp ./modules/web-app/target/architect-burgers-webapi-*.jar ./docker/temp_libs/ && cp ./modules/web-app/target/lib/* ./docker/temp_libs/
#
#
FROM eclipse-temurin:21-jdk-alpine

RUN adduser apprunner -D

RUN mkdir /app

COPY ./temp_libs/* /app/
COPY ./entrypoint.sh /app/

RUN chown -R apprunner /app
RUN chmod a+x /app/entrypoint.sh

USER apprunner

ENV archburgers.datasource.dbUrl=jdbc:postgresql://localhost/archburgers?autoReconnect=true
ENV archburgers.datasource.dbUser=myDbUser
ENV archburgers.datasource.dbPass=muDbPass

EXPOSE 8091

CMD ["/app/entrypoint.sh"]
