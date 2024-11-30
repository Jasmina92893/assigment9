import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Employee {
    private int id;
    private String name;
    private String position;
    private double salary;
    private Date hireDate;

    public Employee() {}

    public Employee(String name, String position, double salary, Date hireDate) {
        this.name = name;
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public Employee(int id, String name, String position, double salary, Date hireDate) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                '}';
    }
}

class EmployeeData {
    private static final String URL = "jdbc:mysql://localhost:3306/employee_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void createEmployee(Employee employee) {
        String query = "INSERT INTO employee (name, position, salary, hire_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getPosition());
            preparedStatement.setDouble(3, employee.getSalary());
            preparedStatement.setDate(4, new java.sql.Date(employee.getHireDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Employee getEmployeeById(int id) {
        String query = "SELECT * FROM employee WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("position"),
                        resultSet.getDouble("salary"),
                        resultSet.getDate("hire_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employee";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                employees.add(new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("position"),
                        resultSet.getDouble("salary"),
                        resultSet.getDate("hire_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public void updateEmployee(Employee employee) {
        String query = "UPDATE employee SET name = ?, position = ?, salary = ?, hire_date = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getPosition());
            preparedStatement.setDouble(3, employee.getSalary());
            preparedStatement.setDate(4, new java.sql.Date(employee.getHireDate().getTime()));
            preparedStatement.setInt(5, employee.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int id) {
        String query = "DELETE FROM employee WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class EmployeeManagementApp {
    public static void main(String[] args) {
        EmployeeData employeeData = new EmployeeData();

        Employee newEmployee = new Employee("John Doe", "Developer", 75000.00, new Date(System.currentTimeMillis()));
        employeeData.createEmployee(newEmployee);

        Employee retrievedEmployee = employeeData.getEmployeeById(1);
        System.out.println("Retrieved Employee: " + retrievedEmployee);

        List<Employee> employees = employeeData.getAllEmployees();
        System.out.println("All Employees: " + employees);

        if (retrievedEmployee != null) {
            retrievedEmployee.setPosition("Senior Developer");
            retrievedEmployee.setSalary(85000.00);
            employeeData.updateEmployee(retrievedEmployee);
            System.out.println("Updated Employee: " + employeeData.getEmployeeById(1));
        }

        employeeData.deleteEmployee(1);
        System.out.println("Employee deleted.");
    }
}
