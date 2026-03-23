/**
 * This program processes employee payroll based on attendance records.
 * It reads employee and attendance data from CSV files, computes total hours worked,
 * calculates gross salary, applies government deductions, and displays payroll results.
 *
 * Key Features:
 * - Supports two user roles: Employee and Payroll Staff
 * - Computes hours worked based on official working schedule (8:00 AM – 5:00 PM)
 * - Applies grace period and lunch break deduction
 * - Calculates SSS, PhilHealth, Pag-IBIG, and tax deductions
 * - Processes payroll per cutoff (1–15 and 16–end of month)
 *
 * Notes:
 * - Attendance data is grouped per employee for efficient processing
 * - Payroll is computed monthly and split into two cutoff periods
 */

package group8.basicpayrollmotorph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class BasicPayrollMotorPH {

    // Represents a single employee's basic information loaded from the CSV file.
    // This is used for payroll computation and display.
    static class EmployeeInfo {
        String employeeNumber;
        String firstName;
        String lastName;
        String birthday;
        double basicSalary;
        double hourlyRate;

        EmployeeInfo(String employeeNumber, String firstName, String lastName,
                     String birthday, double basicSalary, double hourlyRate) {
            this.employeeNumber = employeeNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.basicSalary = basicSalary;
            this.hourlyRate = hourlyRate;
        }
    }

   // Represents a processed attendance entry.
   // Stores only the necessary fields (date and computed hours worked)
   // to simplify payroll calculations.
    static class AttendanceRecord {
        int year;
        int month;
        int day;
        double hoursWorked;

        AttendanceRecord(int year, int month, int day, double hoursWorked) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hoursWorked = hoursWorked;
        }
    }

    // Entry point of the program.
    // Handles user login and menu navigation for both Employee and Payroll Staff roles.
    
    public static void main(String[] args) {
         String empFile = "resources/Employee Details.csv";
        String attFile = "resources/Attendance Record.csv";

        Scanner sc = new Scanner(System.in);

/**
 * Reads employee data from the CSV file and stores it in a map.
 *
 * Why a Map?
 * - Allows fast lookup using employee number as the key.
 *
 * Notes:
 * - Skips invalid or incomplete rows to prevent program crashes
 * - Cleans numeric fields before parsing
 */
        Map<String, EmployeeInfo> employees = loadAllEmployees(empFile);
        Map<String, List<AttendanceRecord>> attendanceByEmployee = loadAttendanceByEmployee(attFile);

        String employeeUser = "employee";
        String payrollUser = "payroll_staff";
        String password = "12345";

        while (true) {
            String username = "";
            String pass = "";

            // LOGIN LOOP
            while (true) {
                System.out.println("MotorPH");
                System.out.print("Enter Username: ");
                username = sc.nextLine();

                System.out.print("Enter Password: ");
                pass = sc.nextLine();

                if (!pass.equals(password) ||
                        (!username.equals(employeeUser) && !username.equals(payrollUser))) {
                    System.out.println("Incorrect username and/or password.");
                    continue;
                }
                break;
            }

            // EMPLOYEE LOGIN
            if (username.equals(employeeUser)) {
                while (true) {
                    System.out.println("EMPLOYEE MENU");
                    System.out.println("1. Enter your employee number");
                    System.out.println("2. Exit program");
                    System.out.print("Choose option: ");

                    int choice;
                    try {
                        choice = Integer.parseInt(sc.nextLine());
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                        continue;
                    }

                    if (choice == 1) {
                        System.out.print("Enter your employee number: ");
                        String inputEmpNo = sc.nextLine();
                        displayEmployeeDetails(employees, inputEmpNo);
                    } else if (choice == 2) {
                        System.out.println("Logging out...");
                        break;
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
                continue;
            }

            // PAYROLL STAFF LOGIN
            else if (username.equals(payrollUser)) {
                while (true) {
                    System.out.println("PAYROLL MENU");
                    System.out.println("1. Process One Employee");
                    System.out.println("2. Process All Employees");
                    System.out.println("3. Exit Program");
                    System.out.print("Choose option: ");

                    int choice;
                    try {
                        choice = Integer.parseInt(sc.nextLine());
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                        continue;
                    }

                    if (choice == 1) {
                        System.out.print("Enter employee number: ");
                        String inputEmpNo = sc.nextLine();
                        displayEmployeePayroll(employees, attendanceByEmployee, inputEmpNo);
                    } else if (choice == 2) {
                        for (EmployeeInfo employee : employees.values()) {
                            displayEmployeePayroll(employees, attendanceByEmployee, employee.employeeNumber);
                        }
                    } else if (choice == 3) {
                        System.out.println("Logging out...");
                        break;
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
            }
        }
    }

    /**
     * Loads all employee records once and stores them in a map for faster lookup.
     */
    static Map<String, EmployeeInfo> loadAllEmployees(String empFile) {
        Map<String, EmployeeInfo> employees = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] employeeFields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (employeeFields.length < 19) continue;

                try {
                    String employeeNumber = employeeFields[0].trim();
                    String lastName = employeeFields[1].trim();
                    String firstName = employeeFields[2].trim();
                    String birthday = employeeFields[3].trim();
                    double basicSalary = Double.parseDouble(employeeFields[13].replaceAll("[\",]", "").trim());
                    double hourlyRate = Double.parseDouble(employeeFields[18].replaceAll("[\",]", "").trim());

                    employees.put(employeeNumber,
                            new EmployeeInfo(employeeNumber, firstName, lastName, birthday, basicSalary, hourlyRate));
                } catch (Exception ignored) {
                    // Skip rows with invalid numeric values instead of stopping the whole program.
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file.");
        }

        return employees;
    }

/**
 * Reads attendance records and groups them by employee number.
 *
 * Each record is:
 * - Parsed from CSV
 * - Converted into hours worked immediately
 * - Stored in a list per employee
 *
 * Why grouping?
 * - Improves performance by avoiding repeated file reading
 * - Makes payroll computation faster and cleaner
 */
    static Map<String, List<AttendanceRecord>> loadAttendanceByEmployee(String attFile) {
        Map<String, List<AttendanceRecord>> attendanceByEmployee = new LinkedHashMap<>();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(attFile))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] attendanceFields = line.split(",");
                if (attendanceFields.length < 6) continue;

                try {
                    String employeeNumber = attendanceFields[0].trim();
                    String[] dateParts = attendanceFields[3].trim().split("/");
                    if (dateParts.length != 3) continue;

                    int month = Integer.parseInt(dateParts[0].trim());
                    int day = Integer.parseInt(dateParts[1].trim());
                    int year = Integer.parseInt(dateParts[2].trim());

                    LocalTime login = LocalTime.parse(attendanceFields[4].trim(), timeFormat);
                    LocalTime logout = LocalTime.parse(attendanceFields[5].trim(), timeFormat);
                    double hoursWorked = computeHours(login, logout);

                    AttendanceRecord record = new AttendanceRecord(year, month, day, hoursWorked);
                    attendanceByEmployee.computeIfAbsent(employeeNumber, key -> new ArrayList<>()).add(record);
                } catch (Exception ignored) {
                    // Skip malformed attendance rows and continue reading the file.
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading attendance file.");
        }

        return attendanceByEmployee;
    }

/**
 * Displays basic employee information.
 *
 * Used by employees to view their personal details.
 * Validates if the employee number exists before displaying.
 */
    static void displayEmployeeDetails(Map<String, EmployeeInfo> employees, String empNoInput) {
        EmployeeInfo employee = employees.get(empNoInput.trim());

        if (employee == null) {
            System.out.println("Employee number does not exist.");
            return;
        }

        System.out.println("\nEmployee # : " + employee.employeeNumber);
        System.out.println("Employee Name : " + employee.lastName + ", " + employee.firstName);
        System.out.println("Birthday : " + employee.birthday);
    }

/**
 * Computes and displays payroll details for a specific employee.
 *
 * Process:
 * 1. Retrieve employee and attendance records
 * 2. Loop through each payroll month
 * 3. Split work hours into two cutoff periods:
 *    - First cutoff: Days 1–15
 *    - Second cutoff: Days 16–end of month
 * 4. Compute gross salary for each cutoff
 * 5. Apply government deductions based on total monthly salary
 * 6. Display detailed payroll breakdown
 *
 * Notes:
 * - Deductions are applied only in the second cutoff
 * - Uses YearMonth to correctly determine the number of days in each month
 */
    static void displayEmployeePayroll(Map<String, EmployeeInfo> employees,
                                       Map<String, List<AttendanceRecord>> attendanceByEmployee,
                                       String empNoInput) {
        EmployeeInfo employee = employees.get(empNoInput.trim());
        if (employee == null) {
            System.out.println("Employee number does not exist.");
            return;
        }

        List<AttendanceRecord> employeeAttendance = attendanceByEmployee.getOrDefault(
                employee.employeeNumber, Collections.emptyList());

        System.out.println("\nEmployee # : " + employee.employeeNumber);
        System.out.println("Employee Name: " + employee.lastName + ", " + employee.firstName);
        System.out.println("Birthday: " + employee.birthday);

        // Loop through June to December, as required by the payroll period.
        for (int month = 6; month <= 12; month++) {
            int daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

            double firstHalfHours = sumHoursForCutoff(employeeAttendance, 2024, month, 1, 15);
            double secondHalfHours = sumHoursForCutoff(employeeAttendance, 2024, month, 16, daysInMonth);

            double gross1 = computeGrossPay(firstHalfHours, employee.hourlyRate);
            double gross2 = computeGrossPay(secondHalfHours, employee.hourlyRate);
            double monthlyGross = gross1 + gross2;

            // Government deductions are based on the full monthly gross salary.
            double sss = computeSSS(monthlyGross);
            double philhealth = computePhilHealth(monthlyGross);
            double pagibig = computePagibig(monthlyGross);
            double taxableIncome = monthlyGross - sss - philhealth - pagibig;
            double tax = computeTax(taxableIncome);
            double totalDeduction = sss + philhealth + pagibig + tax;

            double net1 = gross1;
            double net2 = Math.max(0, gross2 - totalDeduction);

            String monthName = java.time.Month.of(month).name();
            monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();

            printFirstCutoff(monthName, firstHalfHours, gross1, net1);
            printSecondCutoff(monthName, daysInMonth, secondHalfHours, gross2,
                    sss, philhealth, pagibig, tax, totalDeduction, net2);
        }
    }

/**
 * Calculates total hours worked within a specific cutoff period.
 *
 * Filters records based on:
 * - Year
 * - Month
 * - Day range (cutoff period)
 */
    static double sumHoursForCutoff(List<AttendanceRecord> records,
                                    int year, int month, int startDay, int endDay) {
        double totalHours = 0;

        for (AttendanceRecord record : records) {
            if (record.year != year || record.month != month) continue;
            if (record.day < startDay || record.day > endDay) continue;
            totalHours += record.hoursWorked;
        }

        return totalHours;
    }

    // ===== HELPER METHODS FOR OUTPUT =====
// Displays payroll summary for the first cutoff period (no deductions applied).  
    static void printFirstCutoff(String monthName, double hours, double gross, double net) {
        System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
        System.out.printf("Total Hours Worked: %.2f%n", hours);
        System.out.printf("Gross Salary: %.2f%n", gross);
        System.out.printf("Net Salary: %.2f%n", net);
        System.out.println("----------------------------------");
    }
    
// Displays payroll summary for the second cutoff period,
// including all government deductions and final net salary.
    
    static void printSecondCutoff(String monthName, int endDay,
                                  double hours, double gross,
                                  double sss, double philhealth, double pagibig,
                                  double tax, double totalDeduction, double net) {
        System.out.println("\nCutoff Date: " + monthName + " 16 to " + endDay);
        System.out.printf("Total Hours Worked: %.2f%n", hours);
        System.out.printf("Gross Salary: %.2f%n", gross);

        System.out.println("Each Deduction:");
        System.out.printf("SSS: %.2f%n", sss);
        System.out.printf("PhilHealth: %.2f%n", philhealth);
        System.out.printf("Pag-IBIG: %.2f%n", pagibig);
        System.out.printf("Tax: %.2f%n", tax);

        System.out.printf("Total Deductions: %.2f%n", totalDeduction);
        System.out.printf("Net Salary: %.2f%n", net);
        System.out.println("----------------------------------");
    }

/**
 * Computes the actual hours worked based on company rules.
 *
 * Rules applied:
 * - Login before 8:10 AM → counted as 8:00 AM (grace period)
 * - Logout after 5:00 PM → capped at 5:00 PM
 * - Deduct 1-hour lunch break if work duration ≥ 5 hours
 *
 * Returns:
 * - Total worked hours in decimal format
 */
    static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime grace = LocalTime.of(8, 10);
        LocalTime end = LocalTime.of(17, 0);

        if (login.isBefore(grace)) {
            login = start;
        }
        if (logout.isAfter(end)) {
            logout = end;
        }

        long minutesWorked = Duration.between(login, logout).toMinutes();

        if (minutesWorked < 0) {
            return 0;
        }

        // Deduct lunch only if the employee worked long enough for a full workday.
        if (minutesWorked >= 300) {
            minutesWorked -= 60;
        }

        return Math.max(0, minutesWorked / 60.0);
    }

// Calculates gross salary based on total hours worked and hourly rate.
    static double computeGrossPay(double hoursWorked, double hourlyRate) {
        return hoursWorked * hourlyRate;
    }

/**
 * Computes SSS contribution based on salary brackets.
 *
 * Uses a fixed table according to contribution rules.
 */
    static double computeSSS(double monthlySalary) {
        if (monthlySalary < 3250) return 135.00;
        else if (monthlySalary < 3750) return 157.50;
        else if (monthlySalary < 4250) return 180.00;
        else if (monthlySalary < 4750) return 202.50;
        else if (monthlySalary < 5250) return 225.00;
        else if (monthlySalary < 5750) return 247.50;
        else if (monthlySalary < 6250) return 270.00;
        else if (monthlySalary < 6750) return 292.50;
        else if (monthlySalary < 7250) return 315.00;
        else if (monthlySalary < 7750) return 337.50;
        else if (monthlySalary < 8250) return 360.00;
        else if (monthlySalary < 8750) return 382.50;
        else if (monthlySalary < 9250) return 405.00;
        else if (monthlySalary < 9750) return 427.50;
        else if (monthlySalary < 10250) return 450.00;
        else if (monthlySalary < 10750) return 472.50;
        else if (monthlySalary < 11250) return 495.00;
        else if (monthlySalary < 11750) return 517.50;
        else if (monthlySalary < 12250) return 540.00;
        else if (monthlySalary < 12750) return 562.50;
        else if (monthlySalary < 13250) return 585.00;
        else if (monthlySalary < 13750) return 607.50;
        else if (monthlySalary < 14250) return 630.00;
        else if (monthlySalary < 14750) return 652.50;
        else if (monthlySalary < 15250) return 675.00;
        else if (monthlySalary < 15750) return 697.50;
        else if (monthlySalary < 16250) return 720.00;
        else if (monthlySalary < 16750) return 742.50;
        else if (monthlySalary < 17250) return 765.00;
        else if (monthlySalary < 17750) return 787.50;
        else if (monthlySalary < 18250) return 810.00;
        else if (monthlySalary < 18750) return 832.50;
        else if (monthlySalary < 19250) return 855.00;
        else if (monthlySalary < 19750) return 877.50;
        else if (monthlySalary < 20250) return 900.00;
        else if (monthlySalary < 20750) return 922.50;
        else if (monthlySalary < 21250) return 945.00;
        else if (monthlySalary < 21750) return 967.50;
        else if (monthlySalary < 22250) return 990.00;
        else if (monthlySalary < 22750) return 1012.50;
        else if (monthlySalary < 23250) return 1035.00;
        else if (monthlySalary < 23750) return 1057.50;
        else if (monthlySalary < 24250) return 1080.00;
        else if (monthlySalary < 24750) return 1102.50;
        else return 1125.00;
    }

/**
 * Computes PhilHealth contribution.
 *
 * Rule:
 * - 3% of salary, divided between employer and employee
 * - Minimum and maximum caps are applied
 */
    static double computePhilHealth(double salary) {
        double premium = salary * 0.03;
        if (premium > 1800) premium = 1800;
        if (premium < 300) premium = 300;
        return premium / 2;
    }

/**
 * Computes Pag-IBIG contribution.
 *
 * Rule:
 * - 1% or 2% depending on salary
 * - Maximum contribution capped at 100
 */
    static double computePagibig(double salary) {
        double contribution;
        if (salary <= 1500) contribution = salary * 0.01;
        else contribution = salary * 0.02;
        if (contribution > 100) contribution = 100;
        return contribution;
    }

/**
 * Computes income tax based on TRAIN law brackets.
 *
 * Uses progressive tax rates depending on taxable income.
 */
    static double computeTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0;
        else if (taxableIncome <= 33332) return (taxableIncome - 20833) * 0.20;
        else if (taxableIncome <= 66666) return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome <= 166666) return 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome <= 666666) return 40833.33 + (taxableIncome - 166667) * 0.32;
        else return 200833.33 + (taxableIncome - 666667) * 0.35;
    }
}
