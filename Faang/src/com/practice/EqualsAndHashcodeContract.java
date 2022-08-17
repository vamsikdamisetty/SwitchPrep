package com.practice;

import java.util.HashSet;
import java.util.Objects;

public class EqualsAndHashcodeContract {

	public static void main(String[] args) {

		Employee e1 = new Employee(201, 10000, "Vamsi");
		Employee e2 = new Employee(2, 20000, "Krishna");
		Employee e3 = new Employee(2, 20000, "Krishna");
		
		System.out.println(e1 == e3);
		System.out.println(e3 == e2);
		
		System.out.println(e1.equals(e2));
		System.out.println(e2.equals(e3));
		
		HashSet<Employee> hs = new HashSet<>();
		hs.add(e2);
		System.out.println(hs);
		hs.add(e2);
		hs.add(e3);
		hs.add(e3);
		System.out.println(hs);
		
		System.out.println(e2.hashCode() &  15);
		System.out.println(e3.hashCode() &  15);
		System.out.println(e1.hashCode() &  15);
	}
}

class Employee {

	private int empID;
	private int salary;
	private String eName;

	public Employee(int empID, int salary, String eName) {
		super();
		this.empID = empID;
		this.salary = salary;
		this.eName = eName;
	}

	public int getEmpID() {
		return empID;
	}

	public void setEmpID(int empID) {
		this.empID = empID;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public String geteName() {
		return eName;
	}

	public void seteName(String eName) {
		this.eName = eName;
	}

//	@Override
//	public boolean equals(Object obj) {
//		
//		if(obj == null || obj.getClass() != getClass()) {
//			return false;
//		}
//		
//		Employee e = (Employee) obj;
//		return e.getEmpID()== this.getEmpID();
//	}
//	
//	@Override
//	public int hashCode() {
//		// TODO Auto-generated method stub
//		return getEmpID();
//	}

	
	@Override
	public String toString() {
		return "Employee [empID=" + empID + ", salary=" + salary + ", eName=" + eName + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(eName, empID, salary);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		return Objects.equals(eName, other.eName) && empID == other.empID && salary == other.salary;
	}

	public char charAt(int index) {
		return eName.charAt(index);
	}

	
}
