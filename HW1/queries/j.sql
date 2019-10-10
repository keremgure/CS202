SELECT TOP 3 P.pid, SUM(B.qty * W.price) as sums
FROM Buys B, Website W,Product P
WHERE b.rid = W.rid AND W.pid = P.pid
GROUP BY P.pid
ORDER BY sums DESC
