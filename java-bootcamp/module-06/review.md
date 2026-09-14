# Module 6 Review

## What was done

Module 6 focuses on lambdas, functional interfaces, streams, filtering, mapping, min/max, grouping, and parallel stream
awareness.

The lab project applies these ideas to employee analytics using `Employee`, `EmployeeData`, `EmployeeService`,
`ReportService`, and `Main`.

## Step-by-step notes

1. Practiced lambdas and simple functional interfaces.
2. Filtered employees by salary or department.
3. Mapped employees to names or adjusted salary values.
4. Found salary extremes with comparators.
5. Grouped employees by department.
6. Built report-style output from stream pipelines.
7. Compared ordinary streams with parallel streams as an awareness exercise.

## Code ideas to remember

```java
List<String> names = employees.stream()
        .map(Employee::name)
        .toList();
```

```java
long count = employees.stream()
        .filter(employee -> employee.salary() > 60_000)
        .count();
```

## Verification

Run the exercises and lab main class. Check that stream results match the sample employee data.
