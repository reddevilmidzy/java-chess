use chess;

create table moving
(
    movement_id int primary key auto_increment,
    camp        varchar(5)  not null,
    start       varchar(2) not null,
    destination varchar(2) not null
);
