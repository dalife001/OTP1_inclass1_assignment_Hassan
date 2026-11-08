package org.example;

import java.sql.Connection;
import java.util.Locale;

public class DatabaseTest {

    public static void main(String[] args) {

        try {
            var units = new TemperatureUnitDAO().getAllUnits(Locale.ENGLISH);
            var records = new TempRecordDAO().findLatest(5);
            System.out.println("Database connection successful!");
            System.out.println("Temperature units loaded: " + units.size());
            System.out.println("Recent conversions available: " + records.size());
        } catch (Exception e) {
            System.err.println("Database health check failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}