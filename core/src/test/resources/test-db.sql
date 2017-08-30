/* Schema */

create table if not exists testdata (
	 code varchar(100) primary key 
	,description varchar(100) 
	,sequence int 
	,obsolete int not null
);

/* Data */

MERGE INTO testdata KEY(code) VALUES ('c1','test',1,0);
MERGE INTO testdata KEY(code) VALUES ('c2','test des',2,0);
MERGE INTO testdata KEY(code) VALUES ('c3','test eq',3,0);
MERGE INTO testdata KEY(code) VALUES ('c4','test',4,0);
MERGE INTO testdata KEY(code) VALUES ('c5','test',5,0);
MERGE INTO testdata KEY(code) VALUES ('c6','test desc',6,1);
MERGE INTO testdata KEY(code) VALUES ('c7','ztest',7,0);
MERGE INTO testdata KEY(code) VALUES ('c8','test',8,0);
MERGE INTO testdata KEY(code) VALUES ('c9','test',9,0);
MERGE INTO testdata KEY(code) VALUES ('c10','test',10,0);
MERGE INTO testdata KEY(code) VALUES ('c11','test data',11,0);
MERGE INTO testdata KEY(code) VALUES ('c12','test eq',12,0);
MERGE INTO testdata KEY(code) VALUES ('c13','test',13,0);
MERGE INTO testdata KEY(code) VALUES ('c14','test',14,1);
MERGE INTO testdata KEY(code) VALUES ('c15','test eq',15,1);
MERGE INTO testdata KEY(code) VALUES ('c16','test',16,0);
MERGE INTO testdata KEY(code) VALUES ('c17','test abc',17,0);
MERGE INTO testdata KEY(code) VALUES ('c18','test',18,0);
MERGE INTO testdata KEY(code) VALUES ('c19','test',19,0);
MERGE INTO testdata KEY(code) VALUES ('c20','atest',20,1);
MERGE INTO testdata KEY(code) VALUES ('c21','test x',21,0);
MERGE INTO testdata KEY(code) VALUES ('c22','test',22,0);
MERGE INTO testdata KEY(code) VALUES ('c23','test last',23,0);


commit;
