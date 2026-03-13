package group8.basicpayrollmotorph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BasicPayrollMotorPH {

    public static void main(String[] args) {
        String empFile = "resources/Employee Details.csv";
        String attFile = "resources/Attendance Record.csv";

        Scanner sc = new Scanner(System.in);

        String employeeUser = "employee";
        String payrollUser = "payroll_staff";
        String password = "12345";

        while (true) {   // MAIN PROGRAM LOOP
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
                    System.out.println("\nEMPLOYEE MENU");
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
                        displayEmployeeDetails(empFile, inputEmpNo);
                    } else if (choice == 2) {
                        System.out.println("Logging out...");
                        break; // back to login
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
                continue; // return to login
            }

            // PAYROLL STAFF LOGIN
            else if (username.equals(payrollUser)) {
                while (true) {
                    System.out.println("\nPAYROLL MENU");
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
                        displayEmployeePayroll(empFile, attFile, inputEmpNo);
                    } else if (choice == 2) {
                        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
                            br.readLine(); // skip header
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.trim().isEmpty()) continue;
                                String[] data = line.split(",");
                                String empNo = data[0].trim();
                                displayEmployeePayroll(empFile, attFile, empNo);
                            }
                        } catch (Exception e) {
                            System.out.println("Error reading employee file.");
                        }
                    } else if (choice == 3) {
                        System.out.println("Logging out...");
                        break; // back to login
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
            }
        }
    }

    // Display personal info
    static void displayEmployeeDetails(String empFile, String empNoInput) {
        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
            br.readLine(); // skip header
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length < 4) continue;
                if (data[0].trim().equals(empNoInput)) {
                    System.out.println("\nEmployee # : " + data[0].trim());
                    System.out.println("Employee Name : " + data[1].trim() + ", " + data[2].trim());
                    System.out.println("Birthday : " + data[3].trim());
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Employee number does not exist.");
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file.");
        }
    }

    // Payroll computation gibby
    static void displayEmployeePayroll(String empFile, String attFile, String empNoInput) {
        String empNo = "";
        String firstName = "";
        String lastName = "";
        String birthday = "";
        double hourlyRate = 0;
        boolean found = false;

        // GET EMPLOYEE INFO Julius
        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data[0].trim().equals(empNoInput)) {
                    empNo = data[0].trim();
                    lastName = data[1].trim();
                    firstName = data[2].trim();
                    birthday = data[3].trim();
                    hourlyRate = Double.parseDouble(data[18]);
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file.");
            return;
        }

        if (!found) {
            System.out.println("Employee number does not exist.");
            return;
        }

        System.out.println("\nEmployee # : " + empNo);
        System.out.println("Employee Name: " + lastName + ", " + firstName);
        System.out.println("Birthday: " + birthday);

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        double totalHoursWorked = 0;
        double totalGrossSalary = 0;
        double totalNetSalary = 0;

        for (int month = 6; month <= 12; month++) {
            double firstHalf = 0;
            double secondHalf = 0;
            int daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

            try (BufferedReader br = new BufferedReader(new FileReader(attFile))) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (!data[0].trim().equals(empNo)) continue;

                    String[] dateParts = data[3].split("/");
                    int recordMonth = Integer.parseInt(dateParts[0]);
                    int day = Integer.parseInt(dateParts[1]);
                    int year = Integer.parseInt(dateParts[2]);
                    if (year != 2024 || recordMonth != month) continue;

                    LocalTime login = LocalTime.parse(data[4].trim(), timeFormat);
                    LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);
                    double hours = computeHours(login, logout);
                    if (day <= 15) firstHalf += hours;
                    else secondHalf += hours;
                }
            } catch (Exception e) {
                System.out.println("Error reading attendance file.");
            }

            String monthName = switch (month) {
                case 6 -> "June";
                case 7 -> "July";
                case 8 -> "August";
                case 9 -> "September";
                case 10 -> "October";
                case 11 -> "November";
                case 12 -> "December";
                default -> "";
            }; // julius

            // FIRST PAYOUT - gibby
            double gross1 = firstHalf * hourlyRate;
            double net1 = gross1;
            System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
            System.out.println("Total Hours Worked: " + firstHalf);
            System.out.println("Gross Salary: " + gross1);
            System.out.println("Net Salary: " + net1);
            System.out.println("----------------------------------");

            totalHoursWorked += firstHalf;
            totalGrossSalary += gross1;
            totalNetSalary += net1;

            // SECOND PAYOUT WITH DEDUCTIONS
            double gross2 = secondHalf * hourlyRate;
            double sss = gross2 * 0.05;
            double philhealth = gross2 * 0.03;
            double pagibig = 100;
            double tax = gross2 * 0.10;
            double totalDeduction = sss + philhealth + pagibig + tax;
            double net2 = gross2 - totalDeduction;

            System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
            System.out.println("Total Hours Worked: " + secondHalf);
            System.out.println("Gross Salary: " + gross2);
            System.out.println("Each Deduction:");
            System.out.println("SSS: " + sss);
            System.out.println("PhilHealth: " + philhealth);
            System.out.println("Pag-IBIG: " + pagibig);
            System.out.println("Tax: " + tax);
            System.out.println("Total Deductions: " + totalDeduction);
            System.out.println("Net Salary: " + net2);
            System.out.println("----------------------------------");

            totalHoursWorked += secondHalf;
            totalGrossSalary += gross2;
            totalNetSalary += net2;
        }
    }

    static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime graceTime = LocalTime.of(8, 10);
        LocalTime cutoffTime = LocalTime.of(17, 0);

        if (logout.isAfter(cutoffTime)) logout = cutoffTime;

        long minutesWorked = Duration.between(login, logout).toMinutes();
        if (minutesWorked > 60) minutesWorked -= 60;
        else minutesWorked = 0;

        double hours = minutesWorked / 60.0;
// gibby
        if (!login.isAfter(graceTime)) return 8.0;
        return Math.min(hours, 8.0); 
    }
}
