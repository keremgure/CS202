SELECT DISTINCT PH.lm_name
FROM Buys B,Local_Market LM,Phones PH,LM_CC CC
WHERE B.cc = CC.cc AND CC.lid = LM.lid AND LM.phone_number = PH.phone_number AND LM.budget < B.qty * (SELECT W.price
                                                                                              FROM Website W
                                                                                              WHERE B.rid = W.rid)