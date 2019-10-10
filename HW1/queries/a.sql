SELECT P.pid,MAX(P.qty) as maxx, F.fname
FROM Farmer F,Produces P
WHERE F.fid = P.fid AND P.qty = (SELECT MAX(P2.qty)
                                 FROM Produces P2
                                 WHERE P.pid = P2.pid)
GROUP BY P.pid,F.fname