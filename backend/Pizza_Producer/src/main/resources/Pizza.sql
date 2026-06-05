drop database if exists pizza_db;

create database pizza_db;

use pizza_db;

create table IF NOT EXISTS pizza(

orderId int(11) NOT NULL AUTO_INCREMENT,

pizzaName varchar(20),quantity int,

bill double,customerContactNumber varchar(10), PRIMARY KEY(orderId)

);

insert into pizza (orderId,pizzaName,quantity,bill,customerContactNumber) values

  (1001,'VegSmall',1,200,"1234567890"),

  (1002,'VegMedium',2,400,"9543214753"),

  (1003,'VegLarge',1,600,"5843919283"),

  (1004,'NonVegSmall',2,400,"3742828282");
commit;