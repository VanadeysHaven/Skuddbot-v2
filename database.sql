drop database if exists skuddbot_v2;
create database skuddbot_v2;
use skuddbot_v2;

create table servers(
    id bigint primary key,
    name varchar(40)
);

create table identifier(
    id int primary key auto_increment,
    server_id bigint not null,
    user_id bigint default null,
    twitch_username varchar(40) default null,
    foreign key (server_id) references servers(id),
    unique index(server_id, user_id),
    unique index(server_id, twitch_username)
);

create table stats(
    id int primary key auto_increment,
    name varchar(40) not null
);

create table user_has_stats(
    stat_id int,
    user_id int,
    value text not null,
    primary key (stat_id, user_id),
    foreign key (stat_id) references stats(id),
    foreign key (user_id) references identifier(id)
);

create table user_settings(
    id int primary key auto_increment,
    name varchar(40) not null
);

create table user_has_settings(
    setting_id int,
    user_id int,
    value text not null ,
    primary key (setting_id, user_id),
    foreign key (setting_id) references user_settings(id),
    foreign key (user_id) references identifier(id)
);

create table currencies(
    id int primary key auto_increment,
    name varchar(40) not null
);

create table user_has_currencies(
    currency_id int,
    user_id int,
    value int not null,
    primary key (currency_id, user_id),
    foreign key (currency_id) references currencies(id),
    foreign key (user_id) references identifier(id)
);

create table server_settings(
    id int primary key auto_increment,
    name varchar(40) not null
);

create table server_has_settings(
    setting_id int,
    server_id bigint,
    value text not null ,
    primary key (setting_id, server_id),
    foreign key (setting_id) references server_settings(id),
    foreign key (server_id) references servers(id)
);

create table commands(
    id int primary key auto_increment,
    invoker varchar(40) not null,
    output text not null,
    server_id bigint not null,
    unique index(invoker, server_id),
    foreign key (server_id) references servers(id)
);

create table metadata(
    id int primary key auto_increment,
    name varchar(40) not null
);

create table command_has_metadata(
    metadata_id int,
    command_id int,
    value text not null,
    primary key (metadata_id, command_id),
    foreign key (metadata_id) references metadata(id),
    foreign key (command_id) references commands(id)
);

create table properties(
    id int primary key auto_increment,
    name varchar(40) not null
);

create table command_has_properties(
    property_id int,
    command_id int,
    value text not null,
    primary key (property_id, command_id),
    foreign key (property_id) references properties(id),
    foreign key (command_id) references commands(id)
);

create table transactions(
    id int primary key auto_increment,
    user_id int,
    stat_id int,
    mutation int,
    foreign key (user_id) references identifier(id),
    foreign key (stat_id) references stats(id)
);

insert into servers (id, name) value (123, 'Dank Meme\'s');
insert into identifier (server_id, user_id) value (123, 123);
update identifier set twitch_username='cooltimmetje' where id=1;
insert into stats(name) value ('XP');