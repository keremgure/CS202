SELECT F.fname
FROM Farmer F,Website W,Produces P
WHERE F.fid = W.fid AND w.pid = P.pid AND P.fid = F.fid AND W.qty > (SELECT SUM(p2.qty)
                                                                     FROM Produces P2
                                                                     WHERE F.fid = p2.fid AND p.pid = p2.pid)