

# FOR PRODUCTION: RUN UNTIL THIS LINE

insert into servers (id, server_name) value (123, 'Dank Meme\'s');
insert into identifier (server_id, discord_id) value (123, 123);
update identifier set twitch_username='VanadeysHaven', mixer_username='VanadeysHaven' where id=1;
insert ignore into server_settings (setting_name) value ('xp_min');
insert ignore into user_settings (setting_name) value ('lvl_up_notify');
insert ignore into stats (stat_name) value ('xp');
insert ignore into currencies (currency_name) value ('skuddbux');
insert into server_has_settings (setting_id, server_id, setting_value) value ((select get_server_setting_id('xp_min')), 123, '5') on duplicate key update setting_value='7';
insert into user_has_settings (setting_id, user_id, setting_value) values ((select get_user_setting_id('lvl_up_notify')), 1, 'MESSAGE') on duplicate key update setting_value='DM';
insert into user_has_stats(stat_id, user_id, stat_value) value ((select get_stat_id('xp')), 1, '5') on duplicate key update stat_value='7';
insert into donators(id) value (76593288865394688);
insert into admin_users(discord_id) value (76593288865394688);

# FOR TESTING: RUN UNTIL THIS LINE

select * from server_settings;
delete shs from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where shs.server_id=123 AND ss.setting_name='xp_min';
select setting_name, setting_value from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where server_id=224987945638035456;

insert into servers (id, server_name) value (123,'test') on duplicate key update server_name=?;
select setting_name from user_settings;
delete ush from user_has_settings ush join user_settings us on ush.setting_id = us.id where ush.user_id=1 and us.setting_name='lvl_up_notify';
select us.setting_name, uhs.setting_value from user_has_settings uhs join user_settings us on uhs.setting_id = us.id where uhs.user_id=1;

select stat_name from stats;
select s.stat_name, uhs.stat_value from user_has_stats uhs join stats s on uhs.stat_id = s.id where uhs.user_id=2;
delete uhs from user_has_stats uhs join stats s on uhs.stat_id = s.id where uhs.user_id=1 and s.stat_name='xp';
select id.discord_id, id.twitch_username, id.mixer_username, uhs.stat_value from user_has_stats uhs join identifier id on uhs.user_id = id.id join stats s on uhs.stat_id = s.id where id.server_id=224987945638035456 and s.stat_name='xp';

select * from donators;
select data_name, discord_id, data_value from donator_has_data dhd join donator_data dd on dhd.data_id = dd.id;
insert into donators(id, ping_message) value (3742, null) on duplicate key update ping_message='hi';
delete from donator_has_data where discord_id=76593288865394688;
delete from donators where id=76593288865394688;
insert into donator_has_data(data_id, discord_id, data_value) value (get_donator_type_id('no_u'), 69, 'new number who dis');

insert into admin_users(discord_id) value (76593288865394688);
delete from admin_users where discord_id=76593288865394688;
select * from admin_users;

select server_id, discord_id, twitch_username, mixer_username from identifier where id=1;

select * from global_settings;
insert into global_settings(setting, value) value ('commit', 'def456') on duplicate key update value='bye';
delete from global_settings where setting='hi';

insert ignore into currencies (currency_name) value (?);
select currency_name from currencies;
select c.currency_name, uhc.currency_value from user_has_currencies uhc join currencies c on uhc.currency_id = c.id where uhc.user_id=?;
delete uhc from user_has_currencies uhc join currencies c on uhc.currency_id = c.id where uhc.user_id=? and c.currency_name=?;
insert into user_has_currencies(currency_id, user_id, currency_value) value ((select get_currency_id(?)),?,?) on duplicate key update currency_value=?;
select id.discord_id, id.twitch_username, id.mixer_username, uhc.currency_value from user_has_currencies uhc join identifier id on uhc.user_id = id.id join currencies c on uhc.currency_id = c.id where id.server_id=? and c.currency_name=?;

# 2.2.1 update script
delete from user_has_stats where stat_id=(select get_stat_id('daily_last_claim'));

drop table if exists temp_stats;
create table temp_stats (
    user_id int,
    stat_value int
);

insert into temp_stats
select user_id, stat_value
from user_has_stats
where stat_id=(select get_stat_id('daily_longest_streak'));

update user_has_stats
set stat_value = (select stat_value from temp_stats ts where user_has_stats.user_id = ts.user_id)
where stat_id=(select get_stat_id('daily_current_streak'));

drop table temp_stats;