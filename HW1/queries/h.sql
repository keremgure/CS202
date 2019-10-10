SELECT Z.city, P.lm_name
FROM Zips Z,Addresses A,Local_Market LM, Phones P
WHERE Z.zip = A.zip AND a.addr = LM.addr AND LM.phone_number = P.phone_number AND LM.budget = (SELECT MAX(LM2.budget)
                                                                                               FROM Addresses A2,Local_Market LM2
                                                                                               WHERE Z.zip = A2.zip AND A2.addr = LM2.addr)
GROUP BY Z.city, P.lm_name