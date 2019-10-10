SELECT TOP 1 F.fname, SUM(B.qty*W.price) as money
FROM Farmer F,Buys B, Website W
WHERE f.fid = W.fid AND B.rid = W.rid
GROUP by F.fname
ORDER BY money DESC