SELECT * FROM emp;
SELECT emp.nombre FROM emp;
SELECT emp.eno, emp.ename FROM emp;
SELECT emp.eno, asg.eno FROM emp, asg;
SELECT MAX(emp.eno) from emp;
SELECT asg.pno FROM emp, asg where emp.eno = asg.eno and emp.ename = "Juan";
SELECT emp.ename FROM (SELECT * FROM emp);
SELECT emp.ename FROM emp WHERE emp.eno = 1;
SELECT emp.ename FROM emp WHERE emp.eno > 1 and emp.title="Engineer";
SELECT emp.ename FROM emp WHERE emp.title = "Engineer" or emp.eno > 2;
SELECT emp.ename, asg.resp FROM emp, asg where emp.eno = asg.eno;
SELECT * FROM emp, asg where emp.eno = asg.eno and emp.ename = "Juan";
select emp.eno, emp.title from emp where emp.ename = "Juan";
select emp.eno, emp.title from emp where emp.ename = "Juan" and emp.eno=1 and emp.title = "Engineer";
select emp.ename, emp.title from emp, asg where emp.ename = "Juan" and emp.eno = 1 and emp.title != "Engineer";
select emp.ename from emp where emp.eno = (select max(asg.resp) from asg);
select R.nombre from R,S where R.enum = S.enum and (S.duracion=30 or R.edad=55);
select emp.ename from proj, asg, emp where asg.eno = emp.eno and asg.pno = proj.pno and emp.ename != "J Doe" and proj.pname = "cad cam" and (asg.dur = 12 or asg.dur=24);
select emp.ename from emp where emp.eno > 7;
select emp.ename from emp where emp.eno > 7 and emp.eno < 9;
select * from emp, asg where emp.eno = asg.eno;
select emp.ename from emp, asg where emp.eno = asg.eno and emp.eno > 7;
select emp.ename from emp, asg where emp.eno = asg.eno and emp.eno > 7 and asg.pno = 1;
SELECT emp.title, asg.pno FROM emp, asg where emp.eno = asg.eno and emp.eno = 1;
select emp.eno from emp, asg where emp.eno = asg.eno;