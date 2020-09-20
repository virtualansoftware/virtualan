CREATE SEQUENCE virtual_service_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE  virtual_service (id NUMBER DEFAULT virtual_service_SEQ.NEXTVAL PRIMARY KEY,  operationid varchar2(250),  type varchar2(50),priority int,rule clob,
                input clob, output clob, resources varchar2(50),url varchar2(250),requestType varchar2(50),contentType varchar2(50), method varchar2(50), httpStatusCode varchar2(50),excludeList varchar2(250), availableParamsList varchar(4000) ,headerParamsList varchar(4000),
                lastUsedDateTime TIMESTAMP, usageCount INT);
                
                
