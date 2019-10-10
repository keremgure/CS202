-- DROP TABLE IF EXISTS #TempPIDs
-- SELECT DISTINCT W.pid
-- INTO #TempPIDs
-- FROM Website W


-- SELECT T.pid,F.fname
-- FROM #TempPIDs T,Farmer F, Website W
-- WHERE F.fid = W.fid AND T.pid = W.pid AND
--                      W.qty = (SELECT MAX(W2.qty)
--                                 FROM Website W2
--                                 WHERE W2.pid =T.pid
--                                 )
-- GROUP BY T.pid, F.fname


SELECT W.pid, F.fname,W.qty
FROM Website W, (SELECT W2.pid as pids, MAX(W2.qty)as maxs FROM Website W2 GROUP BY W2.pid) as T,Farmer F
WHERE W.pid = T.pids AND W.qty = T.maxs AND F.fid = w.fid