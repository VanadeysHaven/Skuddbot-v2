#Dockerfile for Database

FROM mysql/mysql-server:latest

EXPOSE 3306

COPY init-database.sql /docker-entrypoint-initdb.d

CMD ["--log_bin_trust_function_creators=1"]