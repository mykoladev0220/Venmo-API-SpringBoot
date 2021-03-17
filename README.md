# TenmoAPICapstone

* Created Venmo-like payment app.
* Programmed a RESTful API server using the Spring Boot framework.
* Used a PostgreSQL database to store the data.
* Completed the client command line application to interact with the server.

## Server Side:

* Database included TRANSFER, ACCOUNT, and USER tables.
* Implemented DAO pattern using interfaces to declare abstract methods. (TransferDAO.java, AccountDAO.java, UserDAO.java)
* Created JDBC classes that implemented interfaces and used JdbcTemplate class to query database with SQL strings. (JdbcTransferDAO.java, JdbcAccountDAO.java, JdbcUserDAO.java)
* Programmed server API endpoints in controller classes for GET, POST, and PUT HTTP requests. (TransferController.java, AccountController.java, UserController.java)
* Required authentication by using @PreAuthorize("isAuthenticated()") annotation.

## Client Side:

* Wrote model classes for Transfer, Account, and User objects used for JSON serialization. (Transfer.java, Account.java, User.java)
* Coded service classes that create and send authenticated objects to the API server using RestTemplate class. (TransferService.java, AccountService.java, UserService.java)
* Included methods to generate authentic entities using HttpEntity class that sets bearer token into HttpHeaders included in requests.
* Pogrammed methods to display returning information to the user in the console. (ConsoleService.java)
