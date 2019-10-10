SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER FUNCTION [dbo].[checkBuysQty](@qty INT,@rid INT)
RETURNS VARCHAR(5)
AS
BEGIN
	IF(@qty <= (SELECT W.qty-T.sums as qty FROM Website W,(SELECT SUM(B.qty) as sums FROM Buys B WHERE B.rid = @rid) as T WHERE W.rid = @rid))
	RETURN 'TRUE'
RETURN 'FALSE'
END

GO
