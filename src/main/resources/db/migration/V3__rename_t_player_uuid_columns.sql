create table t_player_uuid_dg_tmp
(
    uuid BLOB not null
        primary key
        unique,
    name TEXT not null
        unique
);

-- noinspection SqlResolve
insert into t_player_uuid_dg_tmp(uuid, name)
select player_uuid, player_name
from t_player_uuid;

drop table t_player_uuid;

alter table t_player_uuid_dg_tmp
    rename to t_player_uuid;