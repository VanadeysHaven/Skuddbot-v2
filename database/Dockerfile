#Dockerfile for Database

FROM mysql/mysql-server:latest

EXPOSE 3306

COPY a_init-database.sql /docker-entrypoint-initdb.d
ADD import_data /docker-entrypoint-initdb.d

CMD ["--log_bin_trust_function_creators=1"]