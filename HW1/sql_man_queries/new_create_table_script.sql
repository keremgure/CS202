create table Altitudes
(
    alt_id   INTEGER not null
        primary key autoincrement,
    altitude INT     not null,
    min_temp INT     not null
);

create table LM_Phones
(
    phone_number INT         not null
        primary key,
    lm_name      VARCHAR(20) not null
);

create table Plant_Dates
(
    plant_id     INTEGER not null
        primary key autoincrement,
    plant_date   INTEGER not null,
    harvest_date INTEGER not null
);

create table Product
(
    pid            INTEGER     not null
        primary key autoincrement,
    name           VARCHAR(20) not null,
    hardness_level INT         not null,
    plant_id       INT         not null
        references Plant_Dates,
    alt_id         INT         not null
        references Altitudes
);

create table Zips
(
    zip  INT         not null
        primary key,
    city VARCHAR(20) not null
);

create table Addresses
(
    address VARCHAR(160) not null
        primary key,
    zip     INT          not null
        references Zips
);

create table Farmer
(
    fid       INTEGER      not null
        primary key autoincrement,
    name      VARCHAR(20)  not null,
    last_name VARCHAR(20)  not null,
    address   VARCHAR(160) not null
        references Addresses,
    unique (name, last_name)
);

create table Farmer_Emails
(
    eid   INTEGER     not null
        primary key autoincrement,
    email VARCHAR(32) not null,
    fid   INT         not null
        references Farmer
);

create table Farmer_Phones
(
    ph_id INTEGER  not null
        primary key autoincrement,
    phone CHAR(11) not null,
    fid   INT      not null
        references Farmer
);

create table Local_Market
(
    lid          INTEGER      not null
        primary key autoincrement,
    budget       INT          not null,
    address      VARCHAR(160) not null
        references Addresses,
    phone_number CHAR(11)     not null
        references LM_Phones
);

create table LM_CreditCards
(
    cc  INT not null
        primary key,
    lid INT not null
        references Local_Market
);

create table Produces
(
    pro_id INTEGER not null
        primary key autoincrement,
    qty    INT     not null,
    fid    INT     not null
        references Farmer,
    pid    INT     not null
        references Product,
    year   INTEGER not null
);

create table Website
(
    rid    INTEGER     not null
        primary key autoincrement,
    qty    INT         not null,
    price  INT         not null,
    origin VARCHAR(20) not null,
    pid    INT         not null
        references Product,
    fid    INT         not null
        references Farmer
);

create table Buys
(
    qty   REAL    not null,
    bid   INTEGER not null
        primary key autoincrement,
    rid   INT     not null
        references Website,
    cc    INT     not null
        references LM_CreditCards,
    price REAL    not null
);

create table Deposits
(
    amount     INT not null,
    deposit_id INT not null
        primary key,
    fid        INT not null
        references Farmer,
    rid        INT not null
        references Website
);

create table Registers
(
    IBAN CHAR(34),
    fid  INT not null
        references Farmer,
    rid  INT not null
        references Website,
    primary key (fid, rid)
);

