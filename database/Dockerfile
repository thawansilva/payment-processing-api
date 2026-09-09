FROM alpine:3.23

RUN apk update && apk add --no-cache postgresql17 tini gettext-envsubst

RUN mkdir /run/postgresql && chown postgres:postgres /run/postgresql

COPY setup/init_db.sh /usr/local/bin/

RUN chmod +x /usr/local/bin/init_db.sh

ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/init_db.sh"]
