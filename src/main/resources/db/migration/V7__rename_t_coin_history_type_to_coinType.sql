create table t_coin_history_dg_tmp
(
    id           INTEGER not null
        primary key autoincrement
        unique,
    player_uuid  BLOB    not null,
    coin_type_id TEXT    not null
        constraint t_coin1_t_coin_type_id_fk
            references t_coin_type
            on update cascade on delete cascade,
    amount       INTEGER not null,
    time         INTEGER not null,
    reason       TEXT    not null
);

-- noinspection SqlResolve
insert into t_coin_history_dg_tmp(id, player_uuid, coin_type_id, amount, time, reason)
select id, player_uuid, type_id, amount, time, reason
from t_coin_history;

drop table t_coin_history;

alter table t_coin_history_dg_tmp
    rename to t_coin_history;

