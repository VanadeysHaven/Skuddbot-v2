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
    discord_id bigint default null,
    twitch_username varchar(40) default null,
    mixer_username varchar(40) default null,
    foreign key (server_id) references servers(id),
    unique index(server_id, discord_id),
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
    setting_name varchar(40) not null unique
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
    currency_value text not null,
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
    setting_value text not null,
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

create table donators(
    id bigint primary key,
    ping_message text
);

create table donator_data(
    id int primary key auto_increment,
    data_name varchar(40) unique
);

create table donator_has_data(
    id int primary key auto_increment,
    data_id int,
    discord_id bigint,
    data_value text not null,
    foreign key (data_id) references donator_data(id),
    foreign key (discord_id) references donators(id)
);

create table admin_users(
    discord_id bigint primary key
);

create table global_settings(
    setting varchar(40) primary key,
    value text
);

delimiter //

create function get_server_setting_id(settingReference text)
returns int
reads sql data
begin
return (select id from server_settings where setting_name=settingReference);
end //

create function get_donator_type_id(typeReference text)
returns int
reads sql data
begin
return (select id from donator_data where data_name=typeReference);
end //

create function get_user_setting_id(settingReference text)
returns int
reads sql data
begin
return (select id from user_settings where setting_name=settingReference);
end //

create function get_stat_id(statReference text)
returns int
reads sql data
begin
return (select id from stats where stat_name=statReference);
end //

create function get_currency_id(currencyReference text)
returns int
reads sql data
begin
return (select id from currencies where currency_name=currencyReference);
end //

delimiter ;

insert into donators (id) value (123);
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (1, 'ai_name');
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (2, 'bacon');
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (3, 'cake');
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (4, 'kitty');
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (5, 'playing');
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (6, 'playing_christmas');
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (7, 'playing_new_year');
INSERT INTO skuddbot_v2.donator_data (id, data_name) VALUES (8, 'puppy');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (1, 1, 123, 'Dave');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (2, 1, 123, 'Ray');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (3, 1, 123, 'Max');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (4, 1, 123, 'Bob');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (5, 1, 123, 'Dan');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (6, 1, 123, 'Shane');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (7, 1, 123, 'Liz');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (8, 1, 123, 'Tim');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (9, 1, 123, 'Mark');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (10, 1, 123, 'Elon');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (32, 2, 123, 'https://tenor.com/view/bacon-gif-4287744');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (33, 2, 123, 'https://tenor.com/view/ron-swanson-bacon-gif-6141620');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (34, 2, 123, 'https://tenor.com/view/bacon-lisa-simpson-gif-11233467');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (35, 2, 123, 'https://tenor.com/view/bacon-delicious-tasty-gif-5630262');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (36, 2, 123, 'https://tenor.com/view/bacon-love-gif-5054228');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (37, 2, 123, 'https://tenor.com/view/bacon-bae-gif-4875886');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (38, 2, 123, 'https://tenor.com/view/bacon-sizzle-cooking-great-morning-gif-12198270');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (39, 2, 123, 'https://tenor.com/view/breakfast-bacon-fry-cook-food-gif-4287633');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (40, 2, 123, 'https://tenor.com/view/bacon-sizzle-cook-crisp-food-gif-8595393');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (41, 2, 123, 'https://tenor.com/view/delish-food-porn-bacon-strips-cooking-gif-8675116');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (47, 3, 123, 'https://tenor.com/view/cake-fat-slice-gif-4931308');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (48, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/Tres-Leches-Thumbnail-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (49, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB260-German-Chocolate-Cake4-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (50, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/1C5A0056-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (51, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/Chocolate-and-Peanut-butter-cake1-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (52, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB28-Funfetti-Birthday-Cake-Thumbnail-v.2-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (53, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/NakedCakeThumb-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (54, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB198-Cinnamon_Roll_Cake-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (56, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/1C5A0658-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (110, 4, 123, 'https://i.imgur.com/YIfvVcE.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (111, 4, 123, 'https://i.imgur.com/kk6rn9F.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (112, 4, 123, 'https://i.imgur.com/JdM8Ozk.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (113, 4, 123, 'https://i.imgur.com/coK8L3v.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (114, 4, 123, 'https://i.imgur.com/AWYqRTV.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (115, 4, 123, 'https://i.imgur.com/OM1jAhs.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (116, 4, 123, 'https://i.imgur.com/W0JfyHW.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (117, 4, 123, 'https://i.imgur.com/jajWPT0.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (118, 4, 123, 'https://i.imgur.com/lVlPvCB.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (119, 4, 123, 'http://en.bcdn.biz/Images/2018/6/12/d8472a04-0d67-4dfe-ba33-faae2ef90ffd.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (141, 5, 123, 'CHEESE, FOR EVERYONE');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (142, 5, 123, 'SkyNet simulator 2017');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (143, 5, 123, 'with fire');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (144, 5, 123, 'est. 1986');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (145, 5, 123, '#BlameLam');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (146, 5, 123, 'ResidentSleeper');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (147, 5, 123, 'Arsehole exploring.');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (148, 5, 123, '^.^');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (149, 5, 123, '$version');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (150, 5, 123, 'Half Life 3');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (204, 6, 123, 'Merry Christmas!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (205, 6, 123, 'Ho ho ho!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (206, 6, 123, 'Christmas with cheese?!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (207, 6, 123, 'Christmas tree simulator 2018');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (208, 6, 123, '*burns down christmas tree*');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (209, 6, 123, 'All I want for christmas is yooouuuuu.....');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (210, 6, 123, 'All I want for christmas is MEMES!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (211, 7, 123, 'HAPPY NEW YEAR!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (212, 7, 123, '*shoots fireworks*');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (214, 8, 123, 'https://i.imgur.com/yWdsKTY.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (215, 8, 123, 'https://i.imgur.com/AXcyili.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (216, 8, 123, 'https://i.imgur.com/OC0bwph.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (217, 8, 123, 'https://i.imgur.com/YcDszjN.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (218, 8, 123, 'https://i.imgur.com/6Zkax5q.png?1');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (219, 8, 123, 'https://i.imgur.com/ePwonn0.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (220, 8, 123, 'https://i.imgur.com/Ovxcwxv.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (221, 8, 123, 'https://i.imgur.com/OFf3Iuy.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (222, 8, 123, 'https://i.imgur.com/djeivlK.mp4');

