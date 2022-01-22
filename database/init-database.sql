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
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (11, 1, 123, 'Adam');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (12, 1, 123, 'Nina');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (13, 1, 123, 'Anthoine');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (14, 1, 123, 'Charlie');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (15, 1, 123, 'Niki');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (16, 1, 123, 'Onmo');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (17, 1, 123, 'Dennis');
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
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (42, 2, 123, 'https://tenor.com/view/bacon-egg-breakfast-delicious-yummy-gif-12756518');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (43, 2, 123, 'https://tenor.com/view/bacon-cooking-cook-frying-delicious-gif-12756457');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (44, 2, 123, 'https://tenor.com/view/bacon-gif-9624995');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (45, 2, 123, 'https://tenor.com/view/bacon-thick-cut-breakfast-sizzle-grill-gif-12096379');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (46, 2, 123, 'https://tenor.com/view/food-bacon-cooking-cook-pan-gif-12269972');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (47, 3, 123, 'https://tenor.com/view/cake-fat-slice-gif-4931308');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (48, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/Tres-Leches-Thumbnail-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (49, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB260-German-Chocolate-Cake4-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (50, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/1C5A0056-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (51, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/Chocolate-and-Peanut-butter-cake1-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (52, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB28-Funfetti-Birthday-Cake-Thumbnail-v.2-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (53, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/NakedCakeThumb-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (54, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB198-Cinnamon_Roll_Cake-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (56, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/1C5A0658-square.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (57, 3, 123, 'https://keyassets-p2.timeincuk.net/wp/prod/wp-content/uploads/sites/53/2019/07/pick-and-mix-choc-cake-920x605.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (58, 3, 123, 'https://blog.williams-sonoma.com/wp-content/uploads/2018/04/apr-26-Neapolitan-Ice-Cream-Cake.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (59, 3, 123, 'https://2.bp.blogspot.com/-zQMiGWCsJXg/VxgAq6ksEsI/AAAAAAACKAI/mcDNAzhELw4W_QgJ0herjSq9t_wq8CMMwCLcB/s1600/Birthday%2BCake.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (60, 3, 123, 'https://cdn-image.myrecipes.com/sites/default/files/styles/4_3_horizontal_-_1200x900/public/zodiac_cake.jpg?itok=hYRTssNS');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (61, 3, 123, 'https://i.imgur.com/jJMSKTc.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (62, 3, 123, 'https://www.bakewithstork.com/assets/Uploads/_resampled/croppedimage733456-white-chocolate-cake-strawberries-Header-732x455.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (63, 3, 123, 'https://preppykitchen.com/wp-content/uploads/2016/06/peony-cake-feature.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (64, 3, 123, 'https://cdn.igp.com/f_auto,q_auto,t_prodm/products/p-chocolate-gems-cake-1-kg--33890-m.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (65, 3, 123, 'https://www.biggerbolderbaking.com/wp-content/uploads/2018/02/wtkqnuzb1ri.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (66, 3, 123, 'https://smittenkitchendotcom.files.wordpress.com/2019/04/toasted-pecan-cake.jpg?w=1200');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (67, 3, 123, 'https://i.imgur.com/W8yPG1F.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (68, 3, 123, 'https://i.imgur.com/8S7zypN.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (69, 3, 123, 'https://www.sugarhero.com/wp-content/uploads/2018/04/circus-animal-layer-cake-1a-500x500.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (70, 3, 123, 'https://bakeplaysmile.com/wp-content/uploads/2017/03/Cheats-Chocolate-Easter-Cake-1.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (71, 3, 123, 'https://i.imgur.com/rn0YjRB.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (72, 3, 123, 'https://i.imgur.com/mLeYehH.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (73, 3, 123, 'https://i.imgur.com/YNIchsz.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (74, 3, 123, 'https://i.imgur.com/wC8zEOH.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (75, 3, 123, 'https://i.imgur.com/rZZMOdo.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (76, 3, 123, 'https://i.imgur.com/HBIlxQ9.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (77, 3, 123, 'https://i.imgur.com/yRi3iJQ.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (78, 3, 123, 'https://i.imgur.com/k4qmCvd.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (79, 3, 123, 'https://i.imgur.com/IYySM1O.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (80, 3, 123, 'https://i.imgur.com/TKJwXxj.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (81, 3, 123, 'https://i.imgur.com/QueWEYD.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (82, 3, 123, 'https://i.imgur.com/HY4Cw9q.jpg');
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
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (120, 4, 123, 'https://i.imgur.com/Ejn0Yvi.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (121, 4, 123, 'https://i.imgur.com/sECJcKA.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (122, 4, 123, 'https://i.imgur.com/Ak4G7bU.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (123, 4, 123, 'https://i.imgur.com/7kIvac3.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (124, 4, 123, 'https://i.kym-cdn.com/entries/icons/mobile/000/011/365/GRUMPYCAT.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (125, 4, 123, 'https://i.imgur.com/nkKvuBy.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (126, 4, 123, 'https://i.imgur.com/5M529kP.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (127, 4, 123, 'https://i.imgur.com/EaPTgKQ.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (128, 4, 123, 'https://i.imgur.com/b4eA1mV.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (129, 4, 123, 'https://i.imgur.com/j7GXWPF.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (130, 4, 123, 'https://i.imgur.com/GTaG70H.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (131, 4, 123, 'https://media.giphy.com/media/LZaA5gGjbUJOw/giphy.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (132, 4, 123, 'https://data.whicdn.com/images/220171863/original.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (133, 4, 123, 'https://media.tenor.com/images/633f92444dcb8bf339eb3ddf5041564d/tenor.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (134, 4, 123, 'https://i.imgur.com/c0LUCVG.gifv');
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
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (151, 5, 123, 'THARGOIDS!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (152, 5, 123, 'NOOT! NOOT!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (153, 5, 123, 'DEPLOY THE NUKES!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (154, 5, 123, 'systemctl');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (155, 5, 123, 'Organizing Memes');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (156, 5, 123, 'Stashing Memes');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (157, 5, 123, 'with your feelings');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (158, 5, 123, 'DEPLOY THE MEMES');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (159, 5, 123, '#TeamDockingComputer');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (160, 5, 123, 'Auto Dock in Progress...');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (161, 5, 123, 'Slow Down for Auto Dock');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (162, 5, 123, 'Bwoah!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (163, 5, 123, '6FDWX-XRDEV-NFA26');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (164, 5, 123, 'C9HLJ-RH38A-FPXG9');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (165, 5, 123, 'FHAXW-9T5W7-6IBVR');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (166, 5, 123, 'Generating cheese...');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (167, 5, 123, 'skuddbot.shop');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (168, 5, 123, 'Powered by PCMasterRace');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (169, 5, 123, 'Contains Cheese.');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (170, 5, 123, 'Loading cheese...');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (171, 5, 123, 'I love cheese');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (172, 5, 123, 'ALL THE CHEESE!');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (173, 5, 123, 'CheeseNet Simulator 2018');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (174, 5, 123, 'Ensuring dankest memes since 1986');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (175, 5, 123, 'Battle Royale Edition');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (176, 5, 123, 'yes');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (177, 5, 123, 'on DigitalOcean');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (178, 5, 123, 'CAJOASTED');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (179, 5, 123, 'Half Life 4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (180, 5, 123, 'with cute puppers');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (181, 5, 123, 'https://i.imgur.com/Dx2k4gf.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (182, 5, 123, 'no u');
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
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (223, 8, 123, 'https://i.imgur.com/2gs17FS.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (224, 8, 123, 'https://i.imgur.com/d4lv7MY.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (225, 8, 123, 'https://i.imgur.com/tvcY5fZ.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (226, 8, 123, 'https://i.imgur.com/AbReh2y.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (227, 8, 123, 'https://i.imgur.com/ivMozIw.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (228, 8, 123, 'https://i.imgur.com/oRcIXiM.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (229, 8, 123, 'https://i.imgur.com/7eGFo3I.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (230, 8, 123, 'https://i.imgur.com/26fVJAE.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (231, 8, 123, 'https://i.imgur.com/fSgnUKW.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (232, 8, 123, 'https://i.imgur.com/5uwTWuS.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (233, 8, 123, 'https://i.imgur.com/pvtFz21.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (234, 8, 123, 'https://i.imgur.com/V0yo8xr.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (235, 8, 123, 'https://i.imgur.com/BY0E958.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (236, 8, 123, 'https://i.imgur.com/Tey2RPc.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (237, 8, 123, 'https://i.imgur.com/u478u8t.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (238, 8, 123, 'https://i.imgur.com/uvK2JzX.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (239, 8, 123, 'https://i.imgur.com/rtCUJKp.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (240, 8, 123, 'https://i.imgur.com/izQ3ido.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (241, 8, 123, 'https://i.imgur.com/NQqZQUe.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (242, 8, 123, 'https://i.imgur.com/axybGMA.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (243, 8, 123, 'https://i.imgur.com/OpBSToj.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (244, 8, 123, 'https://i.imgur.com/AcL2Obz.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (245, 8, 123, 'https://i.imgur.com/E2MakGw.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (246, 8, 123, 'https://i.imgur.com/mAtPOPU.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (247, 8, 123, 'https://i.imgur.com/DIvMvQF.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (248, 8, 123, 'https://i.imgur.com/s1Qto6a.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (249, 8, 123, 'https://i.imgur.com/4FYhlMU.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (250, 8, 123, 'https://i.imgur.com/CBaZIL7.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (251, 8, 123, 'https://i.imgur.com/z3C6i2O.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (252, 8, 123, 'https://i.imgur.com/JKdf0BX.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (253, 8, 123, 'https://i.imgur.com/unQot96.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (254, 8, 123, 'https://i.imgur.com/OV9tgVy.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (255, 8, 123, 'https://i.imgur.com/21Edz6f.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (256, 8, 123, 'https://i.imgur.com/lzc8ii2.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (257, 8, 123, 'https://i.imgur.com/GBtOgf2.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (258, 8, 123, 'https://i.imgur.com/6BtRE8p.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (259, 8, 123, 'https://i.imgur.com/IgULGEk.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (260, 8, 123, 'https://i.imgur.com/0oleqTF.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (261, 8, 123, 'https://i.imgur.com/eNZcD6H.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (262, 8, 123, 'https://i.imgur.com/NpaA9vy.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (263, 8, 123, 'https://i.imgur.com/EdYMbQs.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (264, 8, 123, 'https://i.imgur.com/Dx2k4gf.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (265, 8, 123, 'https://i.imgur.com/SxiuAYG.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (266, 8, 123, 'https://i.imgur.com/HXrMydv.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (267, 8, 123, 'https://i.imgur.com/QIegMMv.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (268, 8, 123, 'https://i.imgur.com/IFL0e4h.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (269, 8, 123, 'https://i.imgur.com/4gCVZMv.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (270, 8, 123, 'https://i.imgur.com/eWyxqfG.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (271, 8, 123, 'https://i.imgur.com/58ps4H7.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (272, 8, 123, 'https://i.imgur.com/KmUPn0y.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (273, 8, 123, 'https://i.imgur.com/wsOpqqp.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (274, 8, 123, 'https://i.imgur.com/8z0W6r8.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (275, 8, 123, 'https://i.imgur.com/bPGz7Wo.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (276, 8, 123, 'https://i.imgur.com/OqAYTKz.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (277, 8, 123, 'https://i.imgur.com/YBEnK8P.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (278, 8, 123, 'https://i.imgur.com/uKiIvLE.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (279, 8, 123, 'https://i.imgur.com/bJH6jTU.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (280, 8, 123, 'https://i.imgur.com/HJUUSJh.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (281, 8, 123, 'https://i.imgur.com/b3zlcdU.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (282, 8, 123, 'https://i.imgur.com/AYSM9aj.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (283, 8, 123, 'https://i.imgur.com/8b414Sa.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (284, 8, 123, 'https://i.imgur.com/FON4fSh.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (285, 8, 123, 'https://i.imgur.com/mXeu16G.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (286, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510506539254153228/BBK9PuU.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (287, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510506826723360768/BBK9NQO.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (288, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510507139127836681/BBK9FP1.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (289, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510507415150657557/BBK9Ut2.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (290, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510507610081067018/BBK9I2K.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (291, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510507717878611978/BBK9Kwe.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (292, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510507882400448513/BBK9PuD.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (293, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508020225277952/BBK9Uts.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (294, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508133584732174/BBK9UtA.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (295, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508223523192842/BBK9Pv0.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (296, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508393362882561/BBK9KGp.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (297, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508499491487754/BBK9SQB.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (298, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508604915449870/BBKbEg9.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (299, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508827251048448/BBK9Qth.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (300, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510508969177907210/BBK9QtA.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (301, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510509161138749441/BBK9J0v.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (302, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510509282995863552/BBK9Wsk.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (303, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510509404576153602/BBK9YZj.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (304, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510512393126936599/BBK9SRq.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (305, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510514930793840650/BBKabZp.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (306, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510515110029033485/BBKa4AS.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (307, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510515421443784755/BBKa2ac.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (308, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510515599039004672/BBK9XnL.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (309, 8, 123, 'https://cdn.discordapp.com/attachments/406213817027264542/510515732992491550/BBKa9B3.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (310, 8, 123, 'https://www.eblogx.com/media/bilder/291018-gifdump-452/31.gif');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (311, 8, 123, 'https://i.imgur.com/CaF7fDh.gifv');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (312, 8, 123, 'https://cdn.discordapp.com/attachments/226439091095470081/510801508007411712/Siberian-Husky-Puppy-siberian-huskies-39031689-1600-1200.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (313, 8, 123, 'https://imgur.com/fZZZyAe');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (314, 8, 123, 'https://i.imgur.com/MDjGy9N.gifv');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (315, 8, 123, 'https://cdn.discordapp.com/attachments/490858712861376513/511873041710579732/15421104263551349338573.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (316, 8, 123, 'https://i.imgur.com/Qxa93FD.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (317, 8, 123, 'https://cdn.discordapp.com/attachments/447959476767817758/513380471820779522/1390662578992.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (318, 8, 123, 'https://i.imgur.com/iy3hnED.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (319, 8, 123, 'https://i.imgur.com/pCvIw09.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (320, 8, 123, 'https://i.imgur.com/1JQ79xq.gifv');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (321, 8, 123, 'https://cdn.discordapp.com/attachments/198483566026424321/515308851751550976/15429295607915536440226329097026.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (322, 8, 123, 'https://cdn.discordapp.com/attachments/371515104941965324/520513825120845824/C3rG6NcWAAA3b1c.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (323, 8, 123, 'https://cdn.discordapp.com/attachments/198483566026424321/521648418720841738/15444409987078539116257164817393.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (324, 8, 123, 'https://cdn.discordapp.com/attachments/198483566026424321/529098450151669761/image0.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (325, 8, 123, 'https://i.imgur.com/LFyHWrn.gifv');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (326, 8, 123, 'https://imgur.com/gallery/VWSUrv3');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (327, 8, 123, 'https://imgur.com/XAwJB4A');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (328, 8, 123, 'https://i.imgur.com/n47hbT8.gifv');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (329, 8, 123, 'https://i.imgur.com/Lz0h6SW.gifv');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (330, 8, 123, 'https://cdn.discordapp.com/attachments/404241095090896896/539867026324324362/image1.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (331, 8, 123, 'https://cdn.discordapp.com/attachments/404241095090896896/540908321448656897/image1.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (332, 8, 123, 'https://cdn.discordapp.com/attachments/198483566026424321/545159975006371841/image0.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (333, 8, 123, 'https://cdn.discordapp.com/attachments/209780182746267659/570216519666237450/image0.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (334, 8, 123, 'https://cdn.discordapp.com/attachments/198483566026424321/602908842250207233/image0.jpg');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (335, 8, 123, 'https://i.imgur.com/brcDvn9.mp4');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (336, 8, 123, 'https://cdn.discordapp.com/attachments/285882951831388161/662634621824925706/ENWx7BHX0AAY_sF.png');
INSERT INTO skuddbot_v2.donator_has_data (id, data_id, discord_id, data_value) VALUES (337, 8, 123, 'https://cdn.discordapp.com/attachments/285882951831388161/662639926030106634/DSC01608.JPG');
