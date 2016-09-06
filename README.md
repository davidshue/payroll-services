## payroll-services

This is a playground project that uses spring-boot (spring-mvc, spring-data, spring-restdocs-mockmvc) to simulate a payroll system.

This application will bootstrap a list of employees, some are hourly, some are salaried, and some are commission based.

Assume everyday a cron job will call the payroll web services and the payroll web service will output list of employees that should get
paid that day, plus the dollar amount of their pay.

Hourly employees are paid weekly on each Monday. Salaried employees are paid monthly at the 1st of each month. Commission based employes
are paid bi-weekly on every other Monday. Each employ has a start date, so if the start date for that employee is in the middle of a pay,
his pay will start from his start date instead of the normal start of the pay cycle.

No employees are paid on weekends.

## Build and Run the Project

###check out the project first

git clone https://github.com/davidshue/payroll-services.git

###build it

mvn clean package

###run it

java -jar target/*.jar

## web services endpoint
http://localhost:8080/ws/v2/employees

http://localhost:8080/ws/v2/payroll(?day=yyyy-MM-dd)

day request param is not required.

## API endpoints
http://localhost:8080/api/docs/api-guide.html

http://localhost:8080/api/docs/getting-started-guide.html