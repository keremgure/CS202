DROP TABLE IF EXISTS #TempDB;
GO

SELECT W.fid,Z.city, SUM(B.qty) as sums
INTO #TempDB
FROM Website W, Buys B,Local_Market LM, LM_CC CC, Addresses A,Zips Z
WHERE W.rid = B.rid AND B.cc = CC.cc AND CC.lid = LM.lid AND LM.addr = A.addr AND A.zip = Z.zip
GROUP BY W.fid, Z.city



-- SELECT t.city, MAX(t.sums)
-- FROM #tempdb t
-- GROUP BY t.city

SELECT t2.fid,t2.city,t2.sums,f.fname,f.last_name
FROM #TempDB t2,(SELECT t.city, MAX(t.sums) AS sums FROM #tempdb t GROUP BY t.city) AS t3, FARMER f
WHERE t2.sums = t3.sums AND f.fid = t2.fid