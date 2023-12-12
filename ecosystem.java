import java.util.ArrayList;

// Employee class
public class Employee {
    private static int employeeCount = 0;
    private int employeeID;
    private String name;
    private double baseSalary;
    private Employee manager;

    public Employee(String name, double baseSalary) {
        this.name = name;
        this.baseSalary = baseSalary;
        this.employeeID = ++employeeCount;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public String getName() {
        return name;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public boolean equals(Employee other) {
        return this.getEmployeeID() == other.getEmployeeID();
    }

    public String toString() {
        return employeeID + " " + name;
    }

    public String employeeStatus() {
        return "Employee status";
    }
}

// TechnicalEmployee class
public class TechnicalEmployee extends Employee {
    private int checkIns;

    public TechnicalEmployee(String name) {
        super(name, 75000);
        checkIns = 0;
    }

    public int getCheckIns() {
        return checkIns;
    }

    public void addCheckIn() {
        checkIns++;
    }

    @Override
    public String employeeStatus() {
        return super.toString() + " has " + checkIns + " successful check ins";
    }
}

// BusinessEmployee class
public class BusinessEmployee extends Employee {
    private double bonusBudget;

    public BusinessEmployee(String name) {
        super(name, 50000);
        bonusBudget = 0;
    }

    public double getBonusBudget() {
        return bonusBudget;
    }

    @Override
    public String employeeStatus() {
        return super.toString() + " with a budget of " + bonusBudget;
    }
}

// SoftwareEngineer class
public class SoftwareEngineer extends TechnicalEmployee {
    private boolean codeAccess;

    public SoftwareEngineer(String name) {
        super(name);
        codeAccess = false;
    }

    public boolean getCodeAccess() {
        return codeAccess;
    }

    public void setCodeAccess(boolean access) {
        codeAccess = access;
    }

    @Override
    public String employeeStatus() {
        return super.employeeStatus() + " and has code access: " + codeAccess;
    }
}

// Accountant class
public class Accountant extends BusinessEmployee {
    private TechnicalLead teamSupported;

    public Accountant(String name) {
        super(name);
    }

    public TechnicalLead getTeamSupported() {
        return teamSupported;
    }

    public void supportTeam(TechnicalLead lead) {
        teamSupported = lead;
        // Update bonus budget based on SoftwareEngineers' salaries
        double totalSalary = 0;
        for (SoftwareEngineer se : lead.getDirectReports()) {
            totalSalary += se.getBaseSalary();
        }
        bonusBudget = totalSalary * 1.1;
    }

    @Override
    public String employeeStatus() {
        return super.employeeStatus() + " is supporting " + teamSupported.getName();
    }
}

// TechnicalLead class
public class TechnicalLead extends TechnicalEmployee {
    private static final int DEFAULT_HEAD_COUNT = 4;
    private int headCount;
    private ArrayList<SoftwareEngineer> directReports;

    public TechnicalLead(String name) {
        super(name);
        this.headCount = DEFAULT_HEAD_COUNT;
        this.directReports = new ArrayList<>();
    }

    public boolean hasHeadCount() {
        return directReports.size() < headCount;
    }

    public boolean addReport(SoftwareEngineer e) {
        if (hasHeadCount()) {
            directReports.add(e);
            e.setManager(this);
            return true;
        }
        return false;
    }

    public boolean approveCheckIn(SoftwareEngineer e) {
        return e.getManager() == this && e.getCodeAccess();
    }

    public boolean requestBonus(Employee e, double bonus) {
        if (getManager() instanceof BusinessLead) {
            BusinessLead businessLead = (BusinessLead) getManager();
            return businessLead.approveBonus(e, bonus);
        }
        return false;
    }

    public String getTeamStatus() {
        StringBuilder teamStatus = new StringBuilder(super.employeeStatus() + " and is managing:\n");
        if (directReports.isEmpty()) {
            teamStatus.append(" and no direct reports yet");
        } else {
            for (SoftwareEngineer se : directReports) {
                teamStatus.append(se.employeeStatus()).append("\n");
            }
        }
        return teamStatus.toString();
    }
}

// BusinessLead class
public class BusinessLead extends BusinessEmployee {
    private static final int DEFAULT_HEAD_COUNT = 10;
    private int headCount;
    private ArrayList<Accountant> directReports;

    public BusinessLead(String name) {
        super(name);
        this.headCount = DEFAULT_HEAD_COUNT;
        this.directReports = new ArrayList<>();
    }

    public boolean hasHeadCount() {
        return directReports.size() < headCount;
    }

    public boolean addReport(Accountant e, TechnicalLead supportTeam) {
        if (hasHeadCount()) {
            directReports.add(e);
            e.setManager(this);
            e.supportTeam(supportTeam);
            // Increase bonus budget
            bonusBudget += e.getBaseSalary() * 1.1;
            return true;
        }
        return false;
    }

    @Override
    public boolean requestBonus(Employee e, double bonus) {
        if (bonus <= bonusBudget) {
            bonusBudget -= bonus;
            e.getManager().approveBonus(e, bonus);
            return true;
        }
        return false;
    }

    @Override
    public boolean approveBonus(Employee e, double bonus) {
        for (Accountant accountant : directReports) {
            if (accountant.getTeamSupported() == e.getManager()) {
                if (accountant.approveBonus(bonus)) {
                    return true;
                }
            }
        }
        return false;
    }
}

// CompanyStructure class for testing
public class CompanyStructure {
    public static void main(String[] args) {
        TechnicalLead CTO = new TechnicalLead("Satya Nadella");
        SoftwareEngineer seA = new SoftwareEngineer("Kasey");
        SoftwareEngineer seB = new SoftwareEngineer("Breana");
        SoftwareEngineer seC = new SoftwareEngineer("Eric");
        CTO.addReport(seA);
        CTO.addReport(seB);
        CTO.addReport(seC);
        System.out.println(CTO.getTeamStatus());

        TechnicalLead VPofENG = new TechnicalLead("Bill Gates");
        SoftwareEngineer seD = new SoftwareEngineer("Winter");
        SoftwareEngineer seE = new SoftwareEngineer("Libby");
        SoftwareEngineer seF = new SoftwareEngineer("Gizan");
        SoftwareEngineer seG = new SoftwareEngineer("Zaynah");
        VPofENG.addReport(seD);
        VPofENG.addReport(seE);
        VPofENG.addReport(seF);
        VPofENG.addReport(seG);
        System.out.println(VPofENG.getTeamStatus());

        BusinessLead CFO = new BusinessLead("Amy Hood");
        Accountant actA = new Accountant("Niky");
        Accountant actB = new Accountant("Andrew");
        CFO.addReport(actA, CTO);
        CFO.addReport(actB, VPofENG);
        System.out.println(CFO.getTeamStatus());
    }
}
