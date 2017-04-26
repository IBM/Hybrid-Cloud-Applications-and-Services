FROM nginx

# Copy custom configuration file from the current directory
COPY nginx.conf /etc/nginx/nginx.conf
COPY nginx.crt /etc/nginx/nginx.crt
COPY nginx.key /etc/nginx/nginx.key
COPY ./entry.sh /
RUN chmod 755 /entry.sh
ENTRYPOINT ["/entry.sh"]