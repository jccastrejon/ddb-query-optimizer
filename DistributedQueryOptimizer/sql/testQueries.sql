SELECT * FROM tabla;
SELECT nombre FROM tabla;
SELECT tabla.* FROM tabla;
SELECT nombre, nombre2 FROM tabla;
SELECT tabla.*, nombre FROM tabla;
SELECT tabla.*, nombre FROM tabla, tabla2;
SELECT tabla.*, nombre FROM (SELECT * FROM tabla);
SELECT * FROM tabla WHERE atributo=1;
SELECT * FROM tabla WHERE atributo=1 and atributo2=2;
SELECT * FROM tabla WHERE atributo2 in (1,2);
SELECT * FROM tabla WHERE atributo2 in (select * from tabla);
select * from tabla1, tabla2 where tabla1.atributo = tabla2.atributo;
select tabla.atributo from tabla;
select tabla.atributo from tabla, tabla2 where tabla1.atributo = tabla2.atributo;