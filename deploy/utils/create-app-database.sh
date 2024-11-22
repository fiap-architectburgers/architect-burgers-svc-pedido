#!/bin/bash

#############################################################
# Creates a database and database user for the application
# This allows a single server (RDS cluster) to be shared by multiple services
# operating in a completely independent way.
###############################################################################

if [ $# -ne 7 ]
then
  echo "Missing parameters"
  exit 1
fi

db_host="$1"
default_db="$2"
adm_user="$3"
adm_pass="$4"

app_db="$5"
app_user="$6"
app_pass="$7"

####

user_exists=$(PGPASSWORD="$adm_pass" psql -h "$db_host" -U "$adm_user" -d "$default_db" -tAc "SELECT 1 FROM pg_roles WHERE rolname='$app_user'")

if [ $? -ne 0 ]; then
  echo "Error connecting to database"
  exit 1
fi

if [ -z "$user_exists" ]; then
  echo "User $app_user does not exist. Creating it..."

  PGPASSWORD="$adm_pass" psql -h "$db_host" -U "$adm_user" -d "$default_db" -c "CREATE USER $app_user WITH PASSWORD '$app_pass'"
  if [ $? -eq 0 ]; then
    echo "User $app_user created successfully."
  else
    echo "Failed to create user $app_user."
    exit 1
  fi

else
  echo "User $app_user already exists."
fi

###
db_exists=$(PGPASSWORD="$adm_pass" psql -h "$db_host" -U "$adm_user" -d "$default_db" -tAc "SELECT 1 FROM pg_database WHERE datname='$app_db'")

if [ -z "$db_exists" ]; then
  echo "Database $app_db does not exist. Creating it..."

  PGPASSWORD="$adm_pass" createdb -h "$db_host" -U "$adm_user" -O "$app_user" "$app_db"
  if [ $? -eq 0 ]; then
    echo "Database $app_db created successfully."
  else
    echo "Failed to create database $app_db."
    exit 1
  fi

else
  echo "Database $app_db already exists."
fi
