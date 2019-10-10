DROP TABLE IF EXISTS #TempDB;

SELECT W.pid,PH.lm_name,SUM(B.qty) as sums
INTO #TempDB
FROM LM_CC CC,Local_Market LM,Buys B, Phones PH, Website W
WHERE B.rid = W.rid AND B.cc = CC.cc AND CC.lid = LM.lid AND Lm.phone_number = PH.phone_number
GROUP BY W.pid, PH.lm_name


-- SELECT T.pid, MAX(T.sums)
-- FROM #TempDB T
-- GROUP BY T.pid
-- SELECT * FROM #TempDB

/*SELECT t.pid ,MAX(t.sums) AS maxus
FROM #TempDB t
GROUP BY t.pid*/

SELECT t2.lm_name,t2.pid, t2.sums
FROM #TempDB t2,(SELECT t.pid ,MAX(t.sums) AS maxus
                 FROM #TempDB t
                 GROUP BY t.pid) as t3
WHERE t2.sums = t3.maxus