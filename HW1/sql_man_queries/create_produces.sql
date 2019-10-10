CREATE TABLE Produces(
	pro_id int NOT NULL,
	fid int NOT NULL,
	pid int NOT NULL,
	qty int NOT NULL,
	PRIMARY KEY (pro_id),
	FOREIGN KEY (fid) REFERENCES Farmer(fid),
	FOREIGN KEY (pid) REFERENCES Product(pid)
)