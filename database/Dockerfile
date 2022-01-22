#Dockerfile for Database

FROM mysql/mysql-server:latest

ENV MYSQL_USER=skuddbot
ENV MYSQL_PASSWORD=password
ENV MYSQL_HOST=localhost
ENV MYSQL_DATABASE=skuddbot_v2

EXPOSE 3306

COPY init-database.sql /docker-entrypoint-initdb.d

CMD ["--log_bin_trust_function_creators=1"]