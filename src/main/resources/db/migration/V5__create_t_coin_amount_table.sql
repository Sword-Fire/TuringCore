create table t_coin_amount
(
    id          INTEGER not null
        primary key autoincrement
        unique,
    player_uuid BLOB    not null,
    type_id     TEXT    not null
        constraint t_coin1_t_coin_type_id_fk
            references t_coin_type
            on update cascade on delete cascade,
    amount      INTEGER
);
