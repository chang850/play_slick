package models

case class Resource(id: Long, resourceType: String, product: String, codeMajor: String, codeMinor: String, ResourceId: String, description: String)

//CREATE TABLE MT_RESOURCE (
//ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
//RESOURCE_TYPE VARCHAR(255) NOT NULL, -- 리소스 구분
//PRODUCT VARCHAR(255) NOT NULL, -- 제품
//CODE_MAJOR VARCHAR(255) NOT NULL, -- 대분류
//CODE_MINOR VARCHAR(255) NOT NULL, -- 소분류
//RESOURCE_ID VARCHAR(255) NOT NULL, -- 소분류
//DESCRIPTION VARCHAR(255) NOT NULL  --  비고
//);