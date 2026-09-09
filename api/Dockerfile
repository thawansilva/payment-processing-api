FROM alpine:3.23

WORKDIR /app

RUN apk update && apk add --no-cache openjdk21 curl tini bash maven

COPY setup/setup_java.sh /usr/local/bin/
COPY --exclude=setup --exclude=Dockerfile . .

RUN chmod +x /usr/local/bin/setup_java.sh

ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/setup_java.sh"]
