Distributed Query Optimizer

	The intention of this project is to implement a distributed query processing system, based on the theory of 'Principles of Distributed Database Systems'
	by M. Tamer Oszu and Patrick Valduriez (http://www.springer.com/computer/database+management+%26+information+retrieval/book/978-1-4419-8833-1).
	As of 12/10/2011, this project is not yet complete. So far, only the query decomposition and data localization optimization
	processes have been implemented. The global optimization and distributed execution logic is yet to be finished.

To execute this application:
	- Install and start Apache Tomcat (http://tomcat.apache.org/)
	- Update the 'appserver.home' variable in the 'build.properties' file
	- Execute the 'deploy' task in the 'build.xml' file
	- Should you make a change in the source code, 'undeploy' and 'deploy' the application using the 'build.xml' ant tasks 