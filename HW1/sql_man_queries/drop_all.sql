while(exists(select 1 from INFORMATION_SCHEMA.TABLE_CONSTRAINTS where CONSTRAINT_TYPE='FOREIGN KEY'))
begin
	declare @sql1 nvarchar(2000)
	SELECT TOP 1 @sql1=('ALTER TABLE ' + TABLE_SCHEMA + '.[' + TABLE_NAME
	+ '] DROP CONSTRAINT [' + CONSTRAINT_NAME + ']')
	FROM information_schema.table_constraints
	WHERE CONSTRAINT_TYPE = 'FOREIGN KEY'
	exec (@sql1)
	PRINT @sql1
end

GO
while(exists(select 1 from INFORMATION_SCHEMA.TABLES where TABLE_NAME != '__MigrationHistory' and TABLE_SCHEMA = 'dbo'))
begin
 declare @sql nvarchar(2000)
 SELECT TOP 1 @sql=('DROP TABLE ' + TABLE_SCHEMA + '.[' + TABLE_NAME
 + ']')
 FROM INFORMATION_SCHEMA.TABLES
 WHERE TABLE_NAME != '__MigrationHistory'
     AND
 TABLE_SCHEMA = 'dbo'
exec (@sql)
 PRINT @sql
end
GO