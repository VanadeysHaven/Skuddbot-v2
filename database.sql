drop database if exists skuddbot_v2;
create database skuddbot_v2;
use skuddbot_v2;

create table servers(
    id bigint primary key,
    server_name varchar(40)
);

create table identifier(
    id int primary key auto_increment,
    server_id bigint not null,
    user_id bigint default null,
    twitch_username varchar(40) default null,
    mixer_username varchar(40) default null,
    foreign key (server_id) references servers(id),
    unique index(server_id, user_id),
    unique index(server_id, twitch_username),
    unique index(server_id, mixer_username)
);

create table stats(
    id int primary key auto_increment,
    stat_name varchar(40) not null unique
);

create table user_has_stats(
    stat_id int,
    user_id int,
    stat_value text not null,
    primary key (stat_id, user_id),
    foreign key (stat_id) references stats(id),
    foreign key (user_id) references identifier(id)
);

create table user_settings(
    id int primary key auto_increment,
    setting_name varchar(40) not null
);

create table user_has_settings(
    setting_id int,
    user_id int,
    setting_value text not null ,
    primary key (setting_id, user_id),
    foreign key (setting_id) references user_settings(id),
    foreign key (user_id) references identifier(id)
);

create table currencies(
    id int primary key auto_increment,
    currency_name varchar(40) not null unique
);

create table user_has_currencies(
    currency_id int,
    user_id int,
    currency_value int not null,
    primary key (currency_id, user_id),
    foreign key (currency_id) references currencies(id),
    foreign key (user_id) references identifier(id)
);

create table server_settings(
    id int primary key auto_increment,
    setting_name varchar(40) not null unique
);

create table server_has_settings(
    setting_id int,
    server_id bigint,
    setting_value text not null ,
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
    metadata_name varchar(40) not null unique
);

create table command_has_metadata(
    metadata_id int,
    command_id int,
    metadata_value text not null,
    primary key (metadata_id, command_id),
    foreign key (metadata_id) references metadata(id),
    foreign key (command_id) references commands(id)
);

create table properties(
    id int primary key auto_increment,
    property_name varchar(40) not null unique
);

create table command_has_properties(
    property_id int,
    command_id int,
    property_value text not null,
    primary key (property_id, command_id),
    foreign key (property_id) references properties(id),
    foreign key (command_id) references commands(id)
);

insert into servers (id, server_name) value (123, 'Dank Meme\'s');
insert into identifier (server_id, user_id) value (123, 123);
update identifier set twitch_username='cooltimmetje', mixer_username='yo_mama' where id=1;

insert ignore into server_settings (setting_name) value ('xp_min');
select * from server_settings;

insert into server_has_settings (setting_id, server_id, setting_value) value ((select id from server_settings where setting_name='xp_min'), 224987945638035456, '5') on duplicate key update setting_value='7';

delete shs from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where shs.server_id=123 AND ss.setting_name='xp_min';

select setting_name, setting_value from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where server_id=224987945638035456;

insert into servers (id, server_name) value (123,'test') on duplicate key update server_name=?;
