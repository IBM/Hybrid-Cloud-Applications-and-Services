FROM websphere-liberty:latest
COPY server.xml /config/
ADD apps/* /config/apps/
RUN installUtility install --acceptLicense defaultServer
