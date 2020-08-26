CREATE TABLE `virtual_service_seq` (
    `virtual_service_seq` VARCHAR(255) NOT NULL,
    `next_val` INT(19),
    PRIMARY KEY (`virtual_service_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO virtual_service_seq SET virtual_service_seq='virtual_service_seq', next_val=1;
CREATE TABLE  virtual_service (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,  operationid varchar(250), priority int,
                input MEDIUMTEXT  , output MEDIUMTEXT  , resources varchar(50),requestType varchar(50),url varchar(250),  type varchar(50),
                method varchar(50), httpStatusCode varchar(50),contentType varchar2(50),excludeList varchar(250),rule varchar(2500),
                availableParamsList varchar(4000) ,headerParamsList varchar(4000),
				lastUsedDateTime TIMESTAMP, usageCount INT );
     
