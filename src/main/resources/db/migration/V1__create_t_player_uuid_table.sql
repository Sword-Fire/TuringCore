create table t_player_uuid
(
    player_uuid TEXT not null
        primary key
        unique,
    player_name TEXT not null
        unique
);

