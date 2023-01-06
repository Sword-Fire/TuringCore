create table t_player_uuid
(
    player_name TEXT not null
        primary key
        unique,
    player_uuid TEXT not null
        unique
);

