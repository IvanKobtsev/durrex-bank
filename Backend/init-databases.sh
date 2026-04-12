#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE durrex_users;
    CREATE DATABASE durrex_auth;
    CREATE DATABASE durrex_core;
    CREATE DATABASE durrex_credit;
    CREATE DATABASE durrex_web_app_settings;
    CREATE DATABASE durrex_mobile_app_settings;
    CREATE DATABASE durrex_monitoring;
EOSQL

