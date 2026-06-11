package com.yourname.myapp.payroll.service;

import com.yourname.myapp.payroll.entity.Payroll;
import com.yourname.myapp.payroll.repository.PayrollRepository;
import com.yourname.myapp.payroll.repository.PayrollRepositoryImpl;
import com.yourname.myapp.payroll.util.SalaryConfigSingleton;
import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.service.EmployeeService;

import com.yourname.myapp.payroll.exception.EmployeeNotFoundException;
import com.yourname.myapp.payroll.exception.PayrollAlreadyExistsException;
import com.yourname.myapp.payroll.exception.PayrollNotFoundException;

import com.yourname.myapp.payroll.exception.PayrollAlreadyExistsException;
import com.yourname.myapp.payroll.exception.PayrollNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepo = new PayrollRepositoryImpl();
    private final EmployeeService employeeService = new EmployeeService();
    private final SalaryConfigSingleton config = SalaryConfigSingleton.getInstance();

    @Override
    public Payroll generatePayroll(String employeeId) {

        // 🔥 FETCH EMPLOYEE FROM DB
        Employee emp = employeeService.getEmployeeById(employeeId);

        if (emp == null) {
            throw new EmployeeNotFoundException(employeeId);
        }

        String role = emp.getJobRole();

        String currentMonth = java.time.Month.from(java.time.LocalDate.now()).toString();
        int currentYear = java.time.LocalDate.now().getYear();

        Payroll payroll = new Payroll(emp, currentMonth, currentYear);

        BigDecimal salary = config.getSalary(role);
        BigDecimal deduction = config.getDeduction(role);

        payroll.setGrossSalary(salary);
        payroll.setDeductions(deduction);

        BigDecimal net = salary.subtract(deduction);

        payroll.setNetPay(net);
        payroll.setCurrentMonthTotal(net);
        payroll.setSalaryTransferRecord("Pending");

        return payroll;
    }

    @Override
    public void savePayroll(Payroll payroll) {
        payrollRepo.save(payroll);
    }

    @Override
    public void updatePayroll(Payroll payroll) {
        payrollRepo.update(payroll);
    }

    @Override
    public void generatePayrollForMonth(String month, int year) {

        List<Employee> employees = employeeService.getAllEmployees();

        for (Employee emp : employees) {

            Payroll existing = payrollRepo.findByEmployeeAndMonth(
                emp.getEmployeeId(),
                month,
                year
            );

            if (existing != null) {
                throw new PayrollAlreadyExistsException(
                    emp.getEmployeeId(), month, year
                );
            } 

            // ✅ create ONLY if not exists
            Payroll payroll = new Payroll(emp, month, year);

            String role = emp.getJobRole();

            BigDecimal salary = config.getSalary(role);
            BigDecimal deduction = config.getDeduction(role);

            payroll.setGrossSalary(salary);
            payroll.setDeductions(deduction);

            BigDecimal net = salary.subtract(deduction);

            payroll.setNetPay(net);
            payroll.setCurrentMonthTotal(net);
            payroll.setSalaryTransferRecord("Pending");

            payrollRepo.save(payroll);

        }
    }

    @Override
    public Payroll getPayrollByEmployeeId(String employeeId) {
        Payroll payroll = payrollRepo.findByEmployeeId(employeeId);

        if (payroll == null) {
            throw new PayrollNotFoundException(employeeId);
        }
        return payroll;
    }

    @Override
    public List<Payroll> getAllPayrolls() {
        String currentMonth = java.time.Month.from(java.time.LocalDate.now()).toString();
        int currentYear = java.time.LocalDate.now().getYear();
        List<Employee> employees = employeeService.getAllEmployees();

        for (Employee emp : employees) {

            
            System.out.println("Checking employee: " + emp.getEmployeeId());

            Payroll existing = payrollRepo.findByEmployeeAndMonth(emp.getEmployeeId(), currentMonth, currentYear);

            System.out.println("Found payroll: " + existing);

            if (existing == null) {

                Payroll payroll = new Payroll(emp, currentMonth, currentYear);

                String role = emp.getJobRole();

                BigDecimal salary = config.getSalary(role);
                BigDecimal deduction = config.getDeduction(role);

                payroll.setGrossSalary(salary);
                payroll.setDeductions(deduction);

                BigDecimal net = salary.subtract(deduction);

                payroll.setNetPay(net);
                payroll.setCurrentMonthTotal(net);
                payroll.setSalaryTransferRecord("Pending");

                System.out.println("Saving payroll for: " + emp.getEmployeeId());

                payrollRepo.save(payroll); // 🔥 REAL SAVE
            }
        }

        return payrollRepo.findAll();
    }

    @Override
    public List<Payroll> generatePayrollForAllEmployees() {

        List<Employee> employees = employeeService.getAllEmployees();

        List<Payroll> payrollList = new ArrayList<>();

        for (Employee emp : employees) {

            String currentMonth = java.time.Month.from(java.time.LocalDate.now()).toString();
            int currentYear = java.time.LocalDate.now().getYear();

            Payroll payroll = new Payroll(emp, currentMonth, currentYear);

            String role = emp.getJobRole();

            BigDecimal salary = config.getSalary(role);
            BigDecimal deduction = config.getDeduction(role);

            payroll.setGrossSalary(salary);
            payroll.setDeductions(deduction);

            BigDecimal net = salary.subtract(deduction);

            payroll.setNetPay(net);
            payroll.setCurrentMonthTotal(net);
            payroll.setSalaryTransferRecord("Pending");
            payrollRepo.save(payroll); 
            payrollList.add(payroll);
        }
        return payrollList;
    }

    @Override
    public Payroll getPayrollByEmployeeAndMonth(String employeeId, String month, int year) {
        return payrollRepo.findByEmployeeAndMonth(employeeId, month, year);
    }
}