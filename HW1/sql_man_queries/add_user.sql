-- add login  
CREATE LOGIN [baris]
WITH PASSWORD='TwmrLc5TgfsBBX13';  
  
-- add user 
CREATE USER [baris] 
FROM LOGIN [baris]
WITH DEFAULT_SCHEMA=dbo; 
  
-- add user to role(s) in db 
ALTER ROLE dbmanager ADD MEMBER [baris]; 
ALTER ROLE loginmanager ADD MEMBER [baris]; 