package com.java.streams;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.Comparator;

/**
 * ============================================================
 *  JAVA STREAMS CHEAT SHEET - CODING SIGNAL PREP
 * ============================================================
 *  Covers:
 *   1. Basics – filter, map, forEach, collect
 *   2. Sorting (single & multi-field, natural & custom)
 *   3. Filtering on user-defined objects
 *   4. Mapping / Transforming
 *   5. Reduce & Aggregate (sum, avg, min, max, count)
 *   6. Grouping & Partitioning (Collectors.groupingBy / partitioningBy)
 *   7. FlatMap (nested collections)
 *   8. Collecting to Map (toMap, handling duplicates)
 *   9. Distinct, Limit, Skip, TakeWhile, DropWhile
 *  10. Finding & Matching (findFirst, findAny, anyMatch, allMatch, noneMatch)
 *  11. String joining with streams
 *  12. Chaining complex pipelines (real interview-style problems)
 * ============================================================
 */
public class StreamsCheatSheet {

    // ─────────────────────────────────────────────
    //  USER-DEFINED CLASSES
    // ─────────────────────────────────────────────

    static class Employee {
        String name;
        String department;
        double salary;
        int age;
        List<String> skills;

        Employee(String name, String department, double salary, int age, List<String> skills) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.age = age;
            this.skills = skills;
        }

        public String getName()       { return name; }
        public String getDepartment() { return department; }
        public double getSalary()     { return salary; }
        public int getAge()           { return age; }
        public List<String> getSkills() { return skills; }

        @Override
        public String toString() {
            return name + " [" + department + ", $" + salary + ", age=" + age + "]";
        }
    }

    static class Order {
        String orderId;
        String customerId;
        double amount;
        String status; // "PLACED", "SHIPPED", "DELIVERED", "CANCELLED"

        Order(String orderId, String customerId, double amount, String status) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.amount = amount;
            this.status = status;
        }

        public String getOrderId()    { return orderId; }
        public String getCustomerId() { return customerId; }
        public double getAmount()     { return amount; }
        public String getStatus()     { return status; }

        @Override
        public String toString() {
            return orderId + " [cust=" + customerId + ", $" + amount + ", " + status + "]";
        }
    }

    // ─────────────────────────────────────────────
    //  SAMPLE DATA
    // ─────────────────────────────────────────────

    public static List<Employee> employees() {
        return List.of(
            new Employee("Alice",   "Engineering", 120000, 30, List.of("Java", "Spring", "AWS")),
            new Employee("Bob",     "Engineering", 110000, 28, List.of("Python", "Django", "AWS")),
            new Employee("Charlie", "Marketing",    85000, 35, List.of("SEO", "Analytics")),
            new Employee("Diana",   "Engineering",  95000, 26, List.of("Java", "Kafka")),
            new Employee("Eve",     "Marketing",    90000, 32, List.of("Content", "SEO")),
            new Employee("Frank",   "HR",           75000, 40, List.of("Recruiting", "Compliance")),
            new Employee("Grace",   "HR",           78000, 29, List.of("Onboarding", "Compliance")),
            new Employee("Hank",    "Engineering", 130000, 34, List.of("Java", "Spring", "Kafka", "AWS"))
        );
    }

    static List<Order> orders() {
        return List.of(
            new Order("O1", "C1", 250.0,  "DELIVERED"),
            new Order("O2", "C2", 120.0,  "SHIPPED"),
            new Order("O3", "C1", 450.0,  "DELIVERED"),
            new Order("O4", "C3", 89.99,  "CANCELLED"),
            new Order("O5", "C2", 310.0,  "PLACED"),
            new Order("O6", "C1", 175.0,  "SHIPPED"),
            new Order("O7", "C3", 520.0,  "DELIVERED"),
            new Order("O8", "C4", 60.0,   "PLACED")
        );
    }

    // ═══════════════════════════════════════════════
    //  MAIN – Run all examples
    // ═══════════════════════════════════════════════

    public static void main(String[] args) {
        section1_FilterMapCollect();
        section2_Sorting();
        section3_ReduceAndAggregate();
        section4_GroupingAndPartitioning();
        section5_FlatMap();
        section6_CollectToMap();
        section7_DistinctLimitSkip();
        section8_FindAndMatch();
        section9_StringJoining();
        section10_ComplexPipelines();
        section11_MapEntryStreams();
    }

    // ─────────────────────────────────────────────
    //  1. FILTER, MAP, COLLECT BASICS
    // ─────────────────────────────────────────────
    static void section1_FilterMapCollect() {
        printHeader("1. FILTER, MAP, COLLECT");

        List<Employee> emps = employees();

        // Filter: employees in Engineering
        List<Employee> engineers = emps.stream()
                .filter(e -> e.getDepartment().equals("Engineering"))
                .collect(Collectors.toList());
        System.out.println("Engineers: " + engineers);

        // Filter + Map: names of employees earning > 100k
        List<String> highEarnerNames = emps.stream()
                .filter(e -> e.getSalary() > 100000)
                .map(Employee::getName)        // method reference
                .collect(Collectors.toList());
        System.out.println("High earner names: " + highEarnerNames);

        // Map to different type: get all salaries as list of doubles
        List<Double> salaries = emps.stream()
                .map(Employee::getSalary)
                .collect(Collectors.toList());
        System.out.println("All salaries: " + salaries);

        emps.stream().map(Employee::getName).forEach(System.out::println);

        // forEach (terminal operation, returns void)
        System.out.print("Names via forEach: ");
        emps.stream()
                .map(Employee::getName)
                .forEach(name -> System.out.print(name + " "));
        System.out.println();

        // toSet instead of toList
        Set<String> departments = emps.stream()
                .map(Employee::getDepartment)
                .collect(Collectors.toSet());
        System.out.println("Unique departments: " + departments);

        // toUnmodifiableList (Java 10+)
        List<String> immutableNames = emps.stream()
                .map(Employee::getName)
                .collect(Collectors.toUnmodifiableList());
        System.out.println("Immutable names: " + immutableNames);
    }

    // ─────────────────────────────────────────────
    //  2. SORTING
    // ─────────────────────────────────────────────
    static void section2_Sorting() {
        printHeader("2. SORTING");

        List<Employee> emps = employees();

        // Sort by salary ascending
        List<Employee> bySalaryAsc = emps.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary))
                .collect(Collectors.toList());
        System.out.println("By salary ASC: " + bySalaryAsc);

        // Sort by salary descending
        List<Employee> bySalaryDesc = emps.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .collect(Collectors.toList());
        System.out.println("By salary DESC: " + bySalaryDesc);

        // Sort by name alphabetically
        List<Employee> byName = emps.stream()
                .sorted(Comparator.comparing(Employee::getName))
                .collect(Collectors.toList());
        System.out.println("By name: " + byName);

        // Multi-field sort: department ASC, then salary DESC
        List<Employee> multiSort = emps.stream()
                .sorted(Comparator.comparing(Employee::getDepartment)
                        .thenComparing(Comparator.comparingDouble(Employee::getSalary).reversed()))
                .collect(Collectors.toList());
        System.out.println("By dept ASC, salary DESC: " + multiSort);

        // Sort by name length
        List<Employee> byNameLength = emps.stream()
                .sorted(Comparator.comparingInt(e -> e.getName().length()))
                .collect(Collectors.toList());
        System.out.println("By name length: " + byNameLength);

        // *** SORTING PLAIN LISTS of Strings/Integers ***
        List<String> words = List.of("banana", "apple", "cherry", "date");

        // Natural order
        List<String> sortedWords = words.stream().sorted().collect(Collectors.toList());
        System.out.println("Sorted words: " + sortedWords);

        // Reverse
        List<String> reversedWords = words.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        System.out.println("Reversed words: " + reversedWords);

        // Case-insensitive sort
        List<String> mixed = List.of("banana", "Apple", "cherry", "date");
        List<String> caseInsensitive = mixed.stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        System.out.println("Case-insensitive: " + caseInsensitive);
    }

    // ─────────────────────────────────────────────
    //  3. REDUCE & AGGREGATE
    // ─────────────────────────────────────────────
    static void section3_ReduceAndAggregate() {
        printHeader("3. REDUCE & AGGREGATE");

        List<Employee> emps = employees();

        // Sum of all salaries using reduce
        double totalSalary = emps.stream()
                .map(Employee::getSalary)
                .reduce(0.0, Double::sum);
        System.out.println("Total salary (reduce): " + totalSalary);

        // Sum using mapToDouble (preferred – avoids boxing)
        double totalSalary2 = emps.stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        System.out.println("Total salary (mapToDouble): " + totalSalary2);

        // Average salary
        OptionalDouble avgSalary = emps.stream()
                .mapToDouble(Employee::getSalary)
                .average();
        System.out.println("Average salary: " + avgSalary.orElse(0.0));

        // Min salary employee
        Optional<Employee> minSalEmp = emps.stream()
                .min(Comparator.comparingDouble(Employee::getSalary));
        System.out.println("Min salary employee: " + minSalEmp.orElse(null));

        // Max salary employee
        Optional<Employee> maxSalEmp = emps.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
        System.out.println("Max salary employee: " + maxSalEmp.orElse(null));

        // Count engineers
        long engineerCount = emps.stream()
                .filter(e -> e.getDepartment().equals("Engineering"))
                .count();
        System.out.println("Engineer count: " + engineerCount);

        // Summary statistics (min, max, sum, avg, count in one pass!)
        DoubleSummaryStatistics stats = emps.stream()
                .mapToDouble(Employee::getSalary)
                .summaryStatistics();
        System.out.println("Stats: " + stats);

        // Reduce to concatenate names
        String allNames = emps.stream()
                .map(Employee::getName)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
        System.out.println("All names: " + allNames);

        // Custom reduce: find the employee with most skills
        Optional<Employee> mostSkilled = emps.stream()
                .reduce((e1, e2) -> e1.getSkills().size() >= e2.getSkills().size() ? e1 : e2);
        System.out.println("Most skilled: " + mostSkilled.orElse(null));
    }

    // ─────────────────────────────────────────────
    //  4. GROUPING & PARTITIONING
    // ─────────────────────────────────────────────
    static void section4_GroupingAndPartitioning() {
        printHeader("4. GROUPING & PARTITIONING");

        List<Employee> emps = employees();

        // Group by department
        Map<String, List<Employee>> byDept = emps.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        byDept.forEach((dept, list) -> System.out.println("  " + dept + " -> " + list));

        // Group by department, count per department
        Map<String, Long> countByDept = emps.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        System.out.println("Count by dept: " + countByDept);

        // Group by department, average salary per department
        Map<String, Double> avgSalByDept = emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)
                ));
        System.out.println("Avg salary by dept: " + avgSalByDept);

        // Group by department, get just names per department
        Map<String, List<String>> namesByDept = emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.mapping(Employee::getName, Collectors.toList())
                ));
        System.out.println("Names by dept: " + namesByDept);

        // Group by department, max salary employee per department
        Map<String, Optional<Employee>> topEarnerByDept = emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
                ));
        topEarnerByDept.forEach((dept, emp) ->
                System.out.println("  Top earner in " + dept + ": " + emp.orElse(null)));

        // Group by department, sum of salaries per department
        Map<String, Double> totalSalByDept = emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.summingDouble(Employee::getSalary)
                ));
        System.out.println("Total salary by dept: " + totalSalByDept);

        // Partitioning: split into two groups (true/false) based on predicate
        Map<Boolean, List<Employee>> highVsLow = emps.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() > 100000));
        System.out.println("High earners (>100k): " + highVsLow.get(true));
        System.out.println("Others (<=100k): " + highVsLow.get(false));

        // Group by salary range (custom buckets)
        Map<String, List<Employee>> salaryBuckets = emps.stream()
                .collect(Collectors.groupingBy(e -> {
                    if (e.getSalary() >= 120000) return "HIGH";
                    else if (e.getSalary() >= 90000) return "MID";
                    else return "LOW";
                }));
        salaryBuckets.forEach((bucket, list) ->
                System.out.println("  " + bucket + " -> " + list));
    }

    // ─────────────────────────────────────────────
    //  5. FLATMAP (Nested Collections)
    // ─────────────────────────────────────────────
    static void section5_FlatMap() {
        printHeader("5. FLATMAP");

        List<Employee> emps = employees();

        // Get all unique skills across all employees
        List<String> allSkills = emps.stream()
                .flatMap(e -> e.getSkills().stream())   // flatten List<List<String>> -> Stream<String>
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("All unique skills: " + allSkills);

        // Count occurrences of each skill
        Map<String, Long> skillCount = emps.stream()
                .flatMap(e -> e.getSkills().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("Skill frequency: " + skillCount);

        // Find employees who know "Java"
        List<String> javaDevs = emps.stream()
                .filter(e -> e.getSkills().contains("Java"))
                .map(Employee::getName)
                .collect(Collectors.toList());
        System.out.println("Java developers: " + javaDevs);

        // FlatMap with arrays
        String[][] arrays = {{"a", "b"}, {"c", "d"}, {"e", "f"}};
        List<String> flat = Arrays.stream(arrays)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        System.out.println("Flattened array: " + flat);
    }

    // ─────────────────────────────────────────────
    //  6. COLLECTING TO MAP
    // ─────────────────────────────────────────────
    static void section6_CollectToMap() {
        printHeader("6. COLLECTING TO MAP");

        List<Employee> emps = employees();

        // Name -> Employee map (assumes unique names)
        Map<String, Employee> nameToEmp = emps.stream()
                .collect(Collectors.toMap(Employee::getName, Function.identity()));
        System.out.println("Name->Employee: " + nameToEmp.get("Alice"));

        // Name -> Salary map
        Map<String, Double> nameToSalary = emps.stream()
                .collect(Collectors.toMap(Employee::getName, Employee::getSalary));
        System.out.println("Name->Salary: " + nameToSalary);

        // Department -> Comma-separated names (handling duplicate keys with merge function)
        Map<String, String> deptToNames = emps.stream()
                .collect(Collectors.toMap(
                        Employee::getDepartment,
                        Employee::getName,
                        (existing, newVal) -> existing + ", " + newVal  // merge function for duplicate keys
                ));
        System.out.println("Dept->Names: " + deptToNames);

        // Collecting to a TreeMap (sorted keys)
        TreeMap<String, Double> sortedNameToSalary = emps.stream()
                .collect(Collectors.toMap(
                        Employee::getName,
                        Employee::getSalary,
                        (a, b) -> a,          // merge (won't happen here, names are unique)
                        TreeMap::new           // map factory
                ));
        System.out.println("Sorted Name->Salary: " + sortedNameToSalary);

        // LinkedHashMap to preserve insertion order
        LinkedHashMap<String, Double> orderedMap = emps.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .collect(Collectors.toMap(
                        Employee::getName,
                        Employee::getSalary,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        System.out.println("Ordered by salary desc: " + orderedMap);
    }

    // ─────────────────────────────────────────────
    //  7. DISTINCT, LIMIT, SKIP, TAKEWHILE, DROPWHILE
    // ─────────────────────────────────────────────
    static void section7_DistinctLimitSkip() {
        printHeader("7. DISTINCT, LIMIT, SKIP");

        List<Integer> nums = List.of(1, 2, 2, 3, 3, 3, 4, 5, 5, 6);

        // Distinct
        System.out.println("Distinct: " + nums.stream().distinct().collect(Collectors.toList()));

        // Limit (first N elements)
        System.out.println("First 3: " + nums.stream().limit(3).collect(Collectors.toList()));

        // Skip (skip first N elements)
        System.out.println("Skip 5: " + nums.stream().skip(5).collect(Collectors.toList()));

        // Pagination pattern: page 2, page size 3
        int page = 2, pageSize = 3;
        List<Integer> page2 = nums.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
        System.out.println("Page 2 (size 3): " + page2);

        // takeWhile (Java 9+) – take elements while condition is true, stop at first false
        List<Integer> sorted = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> taken = sorted.stream()
                .takeWhile(n -> n < 5)
                .collect(Collectors.toList());
        System.out.println("takeWhile < 5: " + taken);

        // dropWhile (Java 9+) – drop elements while condition is true, keep rest
        List<Integer> dropped = sorted.stream()
                .dropWhile(n -> n < 5)
                .collect(Collectors.toList());
        System.out.println("dropWhile < 5: " + dropped);

        // Top 3 highest paid employees
        List<Employee> top3 = employees().stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("Top 3 earners: " + top3);
    }

    // ─────────────────────────────────────────────
    //  8. FIND & MATCH
    // ─────────────────────────────────────────────
    static void section8_FindAndMatch() {
        printHeader("8. FIND & MATCH");

        List<Employee> emps = employees();

        // findFirst – returns Optional
        Optional<Employee> firstEngineer = emps.stream()
                .filter(e -> e.getDepartment().equals("Engineering"))
                .findFirst();
        System.out.println("First engineer: " + firstEngineer.orElse(null));

        // findAny – useful in parallel streams
        Optional<Employee> anyHR = emps.stream()
                .filter(e -> e.getDepartment().equals("HR"))
                .findAny();
        System.out.println("Any HR: " + anyHR.orElse(null));

        // anyMatch – does any employee earn > 125k?
        boolean anyHighEarner = emps.stream()
                .anyMatch(e -> e.getSalary() > 125000);
        System.out.println("Any earner > 125k? " + anyHighEarner);

        // allMatch – do all employees earn > 50k?
        boolean allAbove50k = emps.stream()
                .allMatch(e -> e.getSalary() > 50000);
        System.out.println("All earn > 50k? " + allAbove50k);

        // noneMatch – no employee under 20?
        boolean noneUnder20 = emps.stream()
                .noneMatch(e -> e.getAge() < 20);
        System.out.println("None under 20? " + noneUnder20);

        // Find employee with highest salary using max
        emps.stream()
                .max(Comparator.comparingDouble(Employee::getSalary))
                .ifPresent(e -> System.out.println("Highest paid: " + e));
    }

    // ─────────────────────────────────────────────
    //  9. STRING JOINING
    // ─────────────────────────────────────────────
    static void section9_StringJoining() {
        printHeader("9. STRING JOINING");

        List<Employee> emps = employees();

        // Join names with comma
        String names = emps.stream()
                .map(Employee::getName)
                .collect(Collectors.joining(", "));
        System.out.println("Names: " + names);

        // Join with prefix and suffix
        String nameList = emps.stream()
                .map(Employee::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Name list: " + nameList);

        // Join department names (unique, sorted)
        String depts = emps.stream()
                .map(Employee::getDepartment)
                .distinct()
                .sorted()
                .collect(Collectors.joining(" | "));
        System.out.println("Departments: " + depts);
    }

    // ─────────────────────────────────────────────
    //  10. COMPLEX PIPELINES (INTERVIEW-STYLE)
    // ─────────────────────────────────────────────
    static void section10_ComplexPipelines() {
        printHeader("10. COMPLEX PIPELINES");

        List<Employee> emps = employees();
        List<Order> ords = orders();

        // Q1: Second highest salary
        Optional<Double> secondHighest = emps.stream()
                .map(Employee::getSalary)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst();
        System.out.println("Second highest salary: " + secondHighest.orElse(0.0));

        // Q2: Department with highest average salary
        Optional<Map.Entry<String, Double>> topDept = emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());
        System.out.println("Top avg salary dept: " + topDept.map(Map.Entry::getKey).orElse("N/A"));

        // Q3: For each customer, total order amount of DELIVERED orders only
        Map<String, Double> deliveredTotalByCustomer = ords.stream()
                .filter(o -> o.getStatus().equals("DELIVERED"))
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.summingDouble(Order::getAmount)
                ));
        System.out.println("Delivered total by customer: " + deliveredTotalByCustomer);

        // Q4: Employee names sorted by number of skills (desc), then by name (asc)
        List<String> sortedBySkillCount = emps.stream()
                .sorted(Comparator.<Employee, Integer>comparing(e -> e.getSkills().size()).reversed()
                        .thenComparing(Employee::getName))
                .map(e -> e.getName() + "(" + e.getSkills().size() + ")")
                .collect(Collectors.toList());
        System.out.println("By skill count desc: " + sortedBySkillCount);

        // Q5: Frequency map of order statuses
        Map<String, Long> statusFrequency = ords.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        System.out.println("Order status frequency: " + statusFrequency);

        // Q6: Customer who spent the most overall
        Optional<Map.Entry<String, Double>> topSpender = ords.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.summingDouble(Order::getAmount)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());
        System.out.println("Top spender: " + topSpender.orElse(null));

        // Q7: All engineers sorted by salary, get comma-separated names
        String engineerNames = emps.stream()
                .filter(e -> e.getDepartment().equals("Engineering"))
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .map(Employee::getName)
                .collect(Collectors.joining(", "));
        System.out.println("Engineers by salary: " + engineerNames);

        // Q8: Find if any department has all employees above age 30
        Map<String, Boolean> allAbove30ByDept = emps.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().allMatch(e -> e.getAge() > 30)
                ));
        System.out.println("All above 30 by dept: " + allAbove30ByDept);

        // Q9: Get the Nth highest salary (generalized)
        int n = 3;
        Optional<Double> nthHighest = emps.stream()
                .map(Employee::getSalary)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(n - 1)
                .findFirst();
        System.out.println(n + "rd highest salary: " + nthHighest.orElse(0.0));

        // Q10: Transform list of employees to Map<Department, List<String>> (names sorted)
        Map<String, List<String>> deptToSortedNames = emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.mapping(Employee::getName, Collectors.toList()),
                                names -> { names.sort(Comparator.naturalOrder()); return names; }
                        )
                ));
        System.out.println("Dept -> sorted names: " + deptToSortedNames);
    }

    // ─────────────────────────────────────────────
    //  11. STREAMS ON MAPS
    // ─────────────────────────────────────────────
    static void section11_MapEntryStreams() {
        printHeader("11. STREAMS ON MAPS");

        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 92);
        scores.put("Bob", 85);
        scores.put("Charlie", 78);
        scores.put("Diana", 95);
        scores.put("Eve", 88);

        // Filter map entries by value > 85
        Map<String, Integer> highScores = scores.entrySet().stream()
                .filter(e -> e.getValue() > 85)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println("Scores > 85: " + highScores);

        // Sort map by value descending
        LinkedHashMap<String, Integer> sortedByScore = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        System.out.println("Sorted by score desc: " + sortedByScore);

        // Sort map by key
        TreeMap<String, Integer> sortedByName = new TreeMap<>(scores);
        System.out.println("Sorted by name: " + sortedByName);

        // Transform values: add 5 bonus points to everyone
        Map<String, Integer> boosted = scores.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() + 5
                ));
        System.out.println("Boosted scores: " + boosted);

        // Find max entry
        scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> System.out.println("Top scorer: " + e.getKey() + " = " + e.getValue()));

        // Get keys where value matches condition
        List<String> passedStudents = scores.entrySet().stream()
                .filter(e -> e.getValue() >= 85)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Passed (>=85): " + passedStudents);

        // Swap keys and values (assumes values are unique)
        Map<Integer, String> reversed = scores.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        System.out.println("Reversed map: " + reversed);

        // Merge two maps
        Map<String, Integer> extraScores = Map.of("Frank", 91, "Alice", 97);
        Map<String, Integer> merged = new HashMap<>(scores);
        extraScores.forEach((k, v) -> merged.merge(k, v, Integer::max)); // keep the higher score
        System.out.println("Merged (max): " + merged);

        // Word frequency counter (very common interview pattern)
        String text = "the quick brown fox jumps over the lazy fox";
        Map<String, Long> wordFreq = Arrays.stream(text.split("\\s+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("Word frequency: " + wordFreq);

        // Character frequency
        String word = "mississippi";
        Map<Character, Long> charFreq = word.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("Char frequency of '" + word + "': " + charFreq);
    }

    // ─────────────────────────────────────────────
    //  UTILITY
    // ─────────────────────────────────────────────
    static void printHeader(String title) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + title);
        System.out.println("═".repeat(60));
    }
}

