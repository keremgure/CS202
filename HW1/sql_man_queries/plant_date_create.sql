CREATE TABLE Plant_Dates(
	plant_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
	plant_date DATE NOT NULL,
	harvest_date DATE NOT NULL
)