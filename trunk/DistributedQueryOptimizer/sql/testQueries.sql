SELECT * FROM tabla;
SELECT nombre FROM tabla;
SELECT tabla.* FROM tabla;
SELECT nombre, nombre2 FROM tabla;
SELECT tabla.*, nombre FROM tabla;
SELECT tabla.*, nombre FROM tabla, tabla2;
SELECT tabla.*, nombre FROM (SELECT * FROM tabla);