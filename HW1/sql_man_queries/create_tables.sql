CREATE TABLE Zips
(
  zip INT NOT NULL,
  city VARCHAR(20) NOT NULL,
  PRIMARY KEY (zip)
);

CREATE TABLE Phones
(
  phone_number INT NOT NULL,
  lm_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (phone_number)
);

CREATE TABLE PlantDates
(
  plant_date DATE NOT NULL,
  harvest_date DATE NOT NULL,
  PRIMARY KEY (plant_date)
);

CREATE TABLE Altitudes
(
  altitude INT NOT NULL,
  min_temp INT NOT NULL,
  PRIMARY KEY (altitude)
);

CREATE TABLE Product
(
  pid INT NOT NULL,
  pname VARCHAR(20) NOT NULL,
  hardness_level INT NOT NULL,
  plant_date DATE NOT NULL,
  altitude INT NOT NULL,
  PRIMARY KEY (pid),
  FOREIGN KEY (plant_date) REFERENCES PlantDates(plant_date),
  FOREIGN KEY (altitude) REFERENCES Altitudes(altitude)
);

CREATE TABLE Addresses
(
  addr VARCHAR(160) NOT NULL,
  zip INT NOT NULL,
  PRIMARY KEY (addr),
  FOREIGN KEY (zip) REFERENCES Zips(zip)
);

CREATE TABLE Local_Market
(
  lid INT NOT NULL,
  budget INT NOT NULL,
  addr VARCHAR(160) NOT NULL,
  phone_number INT NOT NULL,
  PRIMARY KEY (lid),
  FOREIGN KEY (addr) REFERENCES Addresses(addr),
  FOREIGN KEY (phone_number) REFERENCES Phones(phone_number)
  -- CONSTRAINT budgetCheck
  -- CHECK(budget < (SELECT SUM(B.price)
  --                 FROM Buys B
  --                 WHERE B.lid = lid
  -- ))
);

CREATE TABLE Farmer
(
  fid INT NOT NULL,
  fname VARCHAR(20) NOT NULL,
  last_name VARCHAR(20) NOT NULL,
  addr VARCHAR(160) NOT NULL,
  PRIMARY KEY (fid),
  FOREIGN KEY (addr) REFERENCES Addresses(addr)
);

CREATE TABLE FarmerEmail
(
  eid INT NOT NULL,
  email VARCHAR(32) NOT NULL,
  fid INT NOT NULL,
  PRIMARY KEY (eid),
  FOREIGN KEY (fid) REFERENCES Farmer(fid)
);

CREATE TABLE FarmerPhones
(
  ph_id INT NOT NULL,
  phone INT NOT NULL,
  fid INT NOT NULL,
  PRIMARY KEY (ph_id),
  FOREIGN KEY (fid) REFERENCES Farmer(fid)
);

CREATE TABLE Website
(
  rid INT NOT NULL,
  qty INT NOT NULL,
  price INT NOT NULL,
  pid INT NOT NULL,
  fid INT NOT NULL,
  PRIMARY KEY (rid),
  FOREIGN KEY (pid) REFERENCES Product(pid),
  FOREIGN KEY (fid) REFERENCES Farmer(fid)
);

CREATE TABLE Produces
(
  qty INT NOT NULL,
  fid INT NOT NULL,
  pid INT NOT NULL,
  PRIMARY KEY (fid, pid),
  FOREIGN KEY (fid) REFERENCES Farmer(fid),
  FOREIGN KEY (pid) REFERENCES Product(pid)
);

CREATE TABLE Deposits
(
  amount INT NOT NULL,
  fid INT NOT NULL,
  rid INT NOT NULL,
  PRIMARY KEY (fid, rid),
  FOREIGN KEY (fid) REFERENCES Farmer(fid),
  FOREIGN KEY (rid) REFERENCES Website(rid)
);

CREATE TABLE Registers
(
  IBAN CHAR(34) NOT NULL,
  fid INT NOT NULL,
  rid INT NOT NULL,
  PRIMARY KEY (fid, rid),
  FOREIGN KEY (fid) REFERENCES Farmer(fid),
  FOREIGN KEY (rid) REFERENCES Website(rid)
);

CREATE TABLE LM_CC
(
  cc INT NOT NULL,
  lid INT NOT NULL,
  PRIMARY KEY (cc),
  FOREIGN KEY (lid) REFERENCES Local_Market(lid)
);

CREATE TABLE Buys
(
  qty INT NOT NULL,
  price INT NOT NULL,
  lid INT NOT NULL,
  rid INT NOT NULL,
  cc INT NOT NULL,
  PRIMARY KEY (lid, rid),
  FOREIGN KEY (lid) REFERENCES Local_Market(lid),
  FOREIGN KEY (rid) REFERENCES Website(rid),
  FOREIGN KEY (cc) REFERENCES LM_CC(cc)
);
