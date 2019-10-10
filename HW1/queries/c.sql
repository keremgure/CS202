SELECT P1.pname as Product1, P2.pname as Product2
FROM Product P1,Product P2, Plant_Dates PD1,Plant_Dates PD2
WHERE P1.plant_id = PD1.plant_id AND P2.plant_id = PD2.plant_id AND PD2.plant_date >= PD1.harvest_date AND P1.pname <> P2.pname