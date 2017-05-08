FROM websphere-liberty:latest
COPY airline_app/server.xml /config/
ADD airline_app/apps/* /config/apps/
RUN installUtility install --acceptLicense defaultServer
