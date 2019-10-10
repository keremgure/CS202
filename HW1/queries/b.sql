SELECT Z.city,MAX(P.qty) as maxx, F.fname,P.pid
FROM Addresses A,Zips Z,Farmer F,Produces P
WHERE A.zip = Z.zip AND A.addr = F.addr AND F.fid = P.fid AND P.qty = (SELECT MAX(P2.qty)
                                 FROM Produces P2
                                 WHERE P.pid = P2.pid)
GROUP BY Z.city,F.fname,P.pid