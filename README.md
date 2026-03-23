## Team Contributions
* Margie - Employee details and login/logout system
   * Handled the login logic for both employee and payroll staff users.
   * Managed the employee menu and the display of employee details.
* Julius - Get employee info
   * Focused on extracting employee information from the Employee Details.csv file.
   * Worked on parsing employee data like name, birthday, and hourly rate.
   * Assisted in linking employee data to the attendance records.
* Gibby - Payroll calculation
   * Responsible for the logic in computing gross and net salaries, including deductions (SSS, PhilHealth, Pag-IBIG, tax).
   * Implemented the cutoff splits (1st–15th and 16th–end of month).
   * Worked on the computeHours() method and the display of payroll summaries.
* Janna - Minor adjustments
   * Made refinements to the code for better functionality and readability.
   * Helped small fixes across the system.
---
**MotorPH Payroll System (TA Update)**

**Overview**

The Payroll System is a console-based Java application designed to simulate a structured payroll processing system for MotorPH. The program supports multiple user roles and processes employee payroll based on attendance records and government-mandated deductions.

This enhanced version improves upon the original implementation by focusing on efficiency, modularity, scalability, and code readability, in line with best programming practices.

---

**User Roles**

The system supports two types of users:

**Employee User**
Can log in using employee credentials

Can view personal information:

- Employee Number  
- Full Name  
- Birthday  

**Payroll Staff User**
Can process payroll for:

- A single employee  
- All employees  
- Generates detailed payroll reports per cutoff and per month  

---

**Login Credentials**

| Roles | Username | Password|
|----------|----------|----------|
| Employee  | employee | 12345 |
| Payroll Staff  | payroll_staff | 12345  |

---

**System Features**
1. Data Handling

Employee data is read from:
```text
Employee Details.csv
```
Attendance data is read from:
```text
Attendance Record.csv
```

Data is loaded once and stored using:
```text
Map<String, EmployeeInfo>
Map<String, List<AttendanceRecord>>
```
Improves performance by avoiding repeated file access

2. Payroll Processing

The system computes payroll for the period:

June to December 2024

Each month is divided into two cutoff periods:

First Cutoff: Day 1–15
Second Cutoff: Day 16–end of month

For each employee, the system:

- Calculates total hours worked per cutoff.  
- Computes gross salary using hourly rate.  
- Combines both cutoffs to determine monthly gross salary.  
- Applies government deductions only in the second cutoff.  
- Displays a detailed payroll summary.  


3. Attendance and Work Hour Computation

The system calculates work hours based on login and logout times:

- Login before 8:10 AM is adjusted to 8:00 AM (grace period)  
- Logout after 5:00 PM is capped at 5:00 PM  
- Lunch break (1 hour) is deducted if:  
  - The employee worked at least 5 hours  

Ensures accurate and realistic work hour computation

4. Payroll Calculation

Gross Salary
```text
Gross Salary = Hours Worked × Hourly Rate
```

Government Deductions (Applied on Monthly Gross)

- SSS – Based on salary bracket  
- PhilHealth – 3% contribution (with limits)  
- Pag-IBIG – 1%–2% capped at ₱100  
- Withholding Tax – Based on TRAIN law  

Deductions are computed using total monthly salary and applied to the second cutoff only, following standard payroll practices

5. Dynamic Month Handling

Months are processed using a loop:
```text
for (int month = 6; month <= 12; month++)
```

Number of days per month is dynamically determined using:
```text
YearMonth.of(2024, month).lengthOfMonth()
```

Month names are generated dynamically using:
```text
java.time.Month
```

Eliminates hardcoded values and improves scalability

---

**Design Improvements**

Compared to the previous version, the system now includes:

- Efficient data loading (no repeated file reading)  
- Modular structure (separate methods for each responsibility)  
- Improved variable naming for clarity  
- Correct payroll deduction logic  
- Enhanced time computation logic  
- Cleaner and more maintainable codebase  
---

Project Structure
```text
group8.calculator
│
├── Calculator.java
│   ├── EmployeeInfo (class)
│   ├── AttendanceRecord (class)
│   ├── loadAllEmployees()
│   ├── loadAttendanceByEmployee()
│   ├── displayEmployeeDetails()
│   ├── displayEmployeePayroll()
│   ├── computeHours()
│   ├── computeSSS()
│   ├── computePhilHealth()
│   ├── computePagibig()
│   └── computeTax()
```
---

**How to Run the Program**

CSV files available in the resources folder:
```text
Employee Details.csv
Attendance Record.csv
```
Project Directory
```text
MO-IT101-Group-8/src/main/java
```
Steps to Run
Navigate to the source directory:
```text
cd MO-IT101-Group-8/src/main/java
```
Compile the program:
```text
javac group8/basicpayrollmotorph/BasicPayrollMotorPH.java
```
Run the program:
```text
java group8.basicpayrollmotorph.BasicPayrollMotorPH
```

**Summary**

The enhanced MotorPH Payroll System provides a more efficient and scalable solution for payroll processing. By addressing previous limitations and applying structured programming principles, the system now delivers:

Accurate payroll computation
Improved performance
Better maintainability
Compliance with required payroll rules

---

**Project Plan Link**
* https://docs.google.com/spreadsheets/d/1dBRgeTXiUNJuw5zbtHSljKCA9ZgDH7hiGJl_bFZogwI/edit?usp=sharing
<br> 

---

**Quality Assurance (QA) Testing**

QA testing was conducted to verify the correctness, reliability, and completeness of the system. Test cases include:

Valid and invalid login credentials
Employee detail lookup
Payroll computation for individual and multiple employees
Attendance parsing and hour computation
Cutoff-based payroll processing
Deduction calculations

The detailed QA testing documentation, including test cases and results, can be accessed through the link below:
QA Testing Documentation: insert link
