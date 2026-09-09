#!/bin/sh

set -e

DB_DATADIR=/var/lib/postgresql/

if [ ! -d "$DB_DATADIR/data" ]; then
	echo "Setting up PostgreSQL..."
	cat << EOF > /tmp/init.sql
	ALTER USER ${DB_USER} WITH PASSWORD '${DB_PASSWORD}';
	CREATE DATABASE ${DB_NAME};
	GRANT ALL PRIVILEGES ON DATABASE ${DB_NAME} TO ${DB_USER};
EOF

	echo "Executing configuration..."
	su - postgres << EOF
	mkdir $DB_DATADIR/data
	chmod 0700 $DB_DATADIR/data
	initdb -D $DB_DATADIR/data
	echo "host all all 0.0.0.0/0 md5" >> $DB_DATADIR/data/pg_hba.conf
	postgres --single -D "$DB_DATADIR/data" postgres < /tmp/init.sql
	echo "listen_addresses='*'" >> $DB_DATADIR/data/postgresql.conf
	pg_ctl start -D $DB_DATADIR/data
EOF
	rm -fr /tmp/init.sql
	echo "Postgresql configuration completed successfully..."
else
	echo "Postgresql storage directory already created."
fi

sleep infinity
