# Lab 19: Integration Testing and UI Test Automation — macOS

**OS:** macOS  
**Primary IDE:** IntelliJ IDEA Community Edition  
**Optional IDE:** VS Code  
**Shell:** macOS Terminal (zsh)  
**Stack hint:** JDK 21 · Maven · Angular · PostgreSQL test strategy · Chrome · GitHub Actions  
**Full lab steps:** [LAB-19-GUIDE.md](LAB-19-GUIDE.md)  
**Other
OS:** [Windows guide](LAB-19-WINDOWS.md) · [IDE conventions](../../../Week%201%20-%20Java%20and%20JVM%20Foundations/_IDE-CONVENTIONS.md)

## Prerequisites (macOS)

- [Lab 0 (macOS)](../../../Week%201%20-%20Java%20and%20JVM%20Foundations/module-00/lab0/LAB-0-MACOS.md) complete
- IntelliJ **Project SDK 21**; Node LTS; Chrome/Chromium
- Docker optional (Testcontainers PostgreSQL)

## Paths (macOS)

| Item             | macOS                                      |
|------------------|--------------------------------------------|
| Workspace        | `~/java-bootcamp`                          |
| This lab project | `~/java-bootcamp/examples/lab19-crm`       |
| Evidence         | `~/java-bootcamp/notes/screenshots/lab-19` |

```bash
cd ~/java-bootcamp
mkdir -p notes/screenshots/lab-19
cd examples/lab19-crm
```

### Commands this lab typically uses

```bash
cd ~/java-bootcamp/examples/lab19-crm
mvn -B test -Dtest=CustomerApiIT
# UI (separate terminals): ng serve --port 4200
mvn -B test -Dtest=CustomerUiSeleniumIT
```

## Do the lab

Complete **[LAB-19-GUIDE.md](LAB-19-GUIDE.md)**.

## Evidence / screenshots

Save under `~/java-bootcamp/notes/screenshots/lab-19`. Redact DB URLs/passwords.

## Pass criteria

| # | Confirm                                              | Your notes  |
|---|------------------------------------------------------|-------------|
| 1 | Workspace open with SDK **21**                       | Pass / Fail |
| 2 | Project under `examples/lab19-crm`                   | Pass / Fail |
| 3 | GUIDE checkpoints complete                           | Pass / Fail |
| 4 | API IT and/or Selenium commands succeed as specified | Pass / Fail |
| 5 | Screenshots under `notes/screenshots/lab-19/`        | Pass / Fail |
