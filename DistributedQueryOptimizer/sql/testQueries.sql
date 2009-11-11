SELECT * FROM tabla;
SELECT nombre FROM tabla;
SELECT tabla.* FROM tabla;
SELECT tabla.nombre, tabla.nombre2 FROM tabla;
SELECT tabla.*, nombre FROM tabla;
SELECT MAX(tabla.atributo) from tabla;
SELECT tabla.*, nombre FROM tabla, tabla2;
SELECT * FROM tabla, tabla2 where tabla.atributo = tabla2.atributo and tabla.atributo = "1";
SELECT tabla.*, nombre FROM (SELECT * FROM tabla);
SELECT * FROM tabla WHERE tabla.atributo=1;
SELECT * FROM tabla WHERE tabla.atributo=1 and tabla.atributo2=2;
SELECT * FROM tabla WHERE tabla.atributo=1 or tabla.atributo2=2;
select * from tabla1, tabla2 where tabla1.atributo = tabla2.atributo;
select tabla.nombre, tabla.apellido from tabla where tabla.nombre = "juan";
select tabla.nombre, tabla.apellido from tabla where tabla.nombre = "juan" and tabla.atributo=1 and tabla.atributo2=2;
select tabla.nombre, tabla.apellido from tabla,tabla2 where tabla.nombre = "juan" and tabla.atributo=1 and tabla.atributo2=2;
select tabla.atributo from tabla;
select tabla.atributo from tabla1, tabla2 where tabla1.atributo = tabla2.atributo;
select tabla1.atributo from tabla1, tabla2 where tabla1.atributo = (select max(atributo) from tabla3);
select R.nombre from R,S where R.enum = S.enum and (S.duracion=30 or R.edad=55);
select emp.ename from proj, asg, emp where asg.eno = emp.eno and asg.pno = proj.pno and emp.ename != "J Doe" and proj.pname = "cad cam" and (asg.dur = 12 or asg.dur=24);