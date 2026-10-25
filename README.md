# Temperature Converter Assignment

## 1. Assignment Description

A JavaFX desktop application that converts Celsius, Fahrenheit, and Kelvin values. It validates input, identifies extreme Celsius values below -40 or above 50, stores units and conversion records in PostgreSQL, and includes automated tests and build support.

## 2. Technologies and Tools Used

- Java 21 and JavaFX 21
- Maven and PostgreSQL JDBC 42.7.2
- PostgreSQL
- JUnit Jupiter 5.11.0
- JaCoCo, Docker, and Jenkins

## 3. Design Approach and Implementation Method

`Main` provides the JavaFX form with an input field, source/target unit selectors, conversion button, and result messages. `TemperatureConverter` contains the conversion logic; `TemperatureUnit` and `TempRecord` hold data.

`TemperatureUnitDAO` creates and loads the default units, while `TempRecordDAO` saves conversions in PostgreSQL. Database settings use `TEMPERATURE_DB_URL`, `TEMPERATURE_DB_USER`, and `TEMPERATURE_DB_PASSWORD`. If the database is unavailable, the application uses default units and reports that results are not saved.

## 4. Testing and Quality Assurance Steps

Run automated tests with:

```bash
mvn test
```

Result: **8 tests passed, 0 failures, 0 errors.**

| Test class | Coverage |
| --- | --- |
| `TemperatureConverterTest` | Unit conversions and extreme-temperature checks |
| `TemperatureUnitTest` | Unit values and null-name validation |
| `TempRecordTest` | Conversion values and record metadata |

Manual checks include valid conversions such as `32 F = 0 C`, same-unit conversion, blank/non-numeric input, database record saving, and the no-database fallback. JaCoCo output is generated at `target/site/jacoco/index.html`.

## 5. How to Run

Prerequisites: JDK 21+, Maven 3.9+, and a graphical display. PostgreSQL is required only for persistence.

For database access, set:

```bash
export TEMPERATURE_DB_URL="jdbc:postgresql://<host>:<port>/<database>?sslmode=require"
export TEMPERATURE_DB_USER="<database-user>"
export TEMPERATURE_DB_PASSWORD="<database-password>"
```

Run tests, the GUI, or a package with:

```bash
mvn test
mvn javafx:run
mvn clean package
```

The application can start without database credentials, but conversions will not be persisted. Docker support is provided by the included `Dockerfile`.