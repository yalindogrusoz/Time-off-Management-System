import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.time.*;

public final class DataFiles {
    private static final String USERS = "users.csv";
    private static final String EMPLOYEES = "employees.csv";
    private static final String MANAGERS = "managers.csv";
    private static final String ADMINS = "administrators.csv";
    private static ArrayList<User> users;
    private static ArrayList<Employee> employees;
    private static ArrayList<Manager> managers;
    private static ArrayList<Admin> admins;
    private static final String TIMEOFF_REQUESTS = "timeoff_requests.csv";
    private static final String WITHDRAWAL_REQUESTS = "withdrawal_requests.csv";
    private static final String BLOCKED_DATES   = "blocked_dates.csv";    // teamName,date
    private static final String MAX_CONCURRENT  = "max_concurrent.csv";   // teamName,max

    public static void bootstrapAdmin(String user, String pass, String first, String last) {
        upsertUser(user, pass, first, last);
        upsertRow(ADMINS, new String[]{user});
    }

    // users.csv: username,password,firstName,lastName
    public static void upsertUser(String username, String password, String first, String last) {
        upsertRow(USERS, new String[]{ username, password, first, last });
    }

    // employees.csv: username,currBalance,maxBalance,managerUsername
    public static void upsertEmployee(String u, int max, int cur, String mgr) {
        // write as: username, currBalance, maxBalance, managerUsername
        upsertRow(EMPLOYEES, new String[]{ u, String.valueOf(cur), String.valueOf(max), mgr });
    }

    public static Employee findEmployee(String username) {
        for (Employee e : employees) {
            if (e.getUsername().equalsIgnoreCase(username)) return e;
        }
        return null;
    }

    // managers.csv: username,teamName
    public static void upsertManager(String u, String team) {
        upsertRow(MANAGERS, new String[]{ u, team });
    }

    public static Manager findManager(String username) {
        for (Manager m : managers) {
            if (m.getUsername().equalsIgnoreCase(username)) return m;
        }
        return null;
    }

    // administrators.csv: username
    public static void upsertAdmin(String u) {
        upsertRow(ADMINS, new String[]{ unnullify(u) });
    }

    public static Admin findAdmin(String username) {
        if (admins == null) return null;
        for (Admin a : admins) {
            if (a.getUsername().equalsIgnoreCase(username)) return a;
        }
        return null;
    }

    public static User findUser(String username) {
        int idx = binarySearch(users, username); // users is sorted, so binary search and find i
        if (idx < 0) {
            return null;
        }
        return users.get(idx);
    }

    // insert in the correct position (in an ordered manner alphabetically for username) the newRow row in the path csv file
    private static void upsertRow(String path, String[] newRow) {
        List<String[]> rows = readRows(path);
        int idx = -1;
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            String u = "";
            if (r != null && r.length > 0 && r[0] != null)
            {
                u = r[0].trim();
            }
            if (u.equalsIgnoreCase(unnullify(newRow[0]))) { idx = i; break; }
        }
        if (idx >= 0) {
            rows.set(idx, newRow);          // update existing row, since the username already exists
        } else { // if the username doesn't already exist and has to be added newly:
            String username = newRow[0];
            if (username == null){
                username = "";
            }
            username = username.trim();

            int insertAt = rows.size();
            for (int i = 0; i < rows.size(); i++) { //forall rows
                String[] row = rows.get(i); //row = ith row in rows
                String u = ""; // a variable for the userame (trimmed) in the ith row
                if (row != null && row.length > 0 && row[0] != null) {
                    u = row[0].trim();
                }
                if (u.compareToIgnoreCase(username) > 0) { // if u is alphabetically larger than the username, the username must be inserted between the previous line and that line. hence set insertAt to i and break
                    insertAt = i;
                    break;
                }
            }
            rows.add(insertAt, newRow);     // keep file ordered by username by adding the newRow at the found insertAt value and shift the subsequent rows using the List function add()
        }
        writeRows(path, rows);
    }



    // performs binary search on an ArrayList<User> that is alphabetically sorted by username
    // polymorphic list works
    private static int binarySearch(List<User> users, String username) {
        String key = unnullify(username); // key = username.trim() if non-empty, and "" if null
        key = key.toLowerCase();
        int lowBound = 0;
        int highBound = users.size() - 1;
        while (lowBound <= highBound) {
            int mid = (lowBound + highBound) >>> 1;
            User row = users.get(mid);
            String midUser = "";
            if (row != null && row.getUsername() != null) { // avoid null or empty rows
                midUser = row.getUsername().trim().toLowerCase(); // trim and lower for compareTo() consistency
            }
            int cmp = midUser.compareTo(key);
            if (cmp == 0) return mid; // if equal, mid is the index of the user
            if (cmp < 0) lowBound = mid + 1;  // the username being searched is in the upper half
            else highBound = mid - 1; // the username being searched is in the lower half
        }
        return -1; // if exited without return, username doesn't exist so return a negative integer
    }

    private static List<String[]> readRows(String path) {
        File f = new File(path);
        List<String[]> out = new ArrayList<>();
        if (!f.exists()) return out;
        try (Scanner sc = new Scanner(f, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // skip comments and empty lines
                out.add(line.split(",", -1));
            }
        } catch (IOException ignored) {}
        return out;
    }

    private static void writeRows(String path, List<String[]> rows) {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) { // open file using path, convert special characters, and create a printwriter
            for (String[] r : rows) pw.println(String.join(",", r));
        } catch (IOException e) {
            throw new RuntimeException(e); // in case file doesnt exist or no permission given etc., avoid rıuntime error
        }
    }

    private static String unnullify(String s)
    {
        if (s == null) return "";
        return s.trim();
    }




    // reads employee.csv to obtain values for the fields of employees, and then create employees and an Array
    public static ArrayList<Employee> createEmployees() {
        ArrayList<Employee> list = new ArrayList<>();
        File f = new File("employees.csv");
        if (!f.exists()) return list; // avoid runtime error

        try (java.util.Scanner sc = new java.util.Scanner(f, java.nio.charset.StandardCharsets.UTF_8)) { // try creating a reader for file f with special charset
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // skip empty lines and comments

                // employees.csv: username,currBalance,maxBalance,managerUsername
                String[] p = line.split(",", -1); // keep empty fields
                if (p.length != 4) continue; // skip any wrongly formatted lines

                String uname = p[0].trim();
                int currBalance = DataFiles.parseIntOrZero(p[1]);
                int maxBalance = DataFiles.parseIntOrZero(p[2]);

                String managerusername = p[3].trim();

                // pull base fields from users.csv
                String[] urow = null; // username,password,first,last
                List<String[]> userRows = readRows(USERS);
                for (String[] r : userRows) {
                    if (r != null && r.length >= 4 && r[0] != null && r[0].trim().equals(uname)) {
                        urow = r;
                        break;
                    }
                }
                if (urow == null || urow.length != 4) continue; // confirm valid format

                String pw = urow[1].trim(); //password
                String fn = urow[2].trim();
                String ln = urow[3].trim();

                //String username, String password, String firstName, String lastName, int currbalance, int maxbalance, String managername
                Employee e = new Employee(uname, pw, fn, ln, currBalance, maxBalance, managerusername);
                list.add(e);
            }
        } catch (java.io.IOException ignored) {}
        employees = list;
        return list;
    }

    public static ArrayList<Manager> createManagers() {
        ArrayList<Manager> list = new ArrayList<>();
        File f = new File("managers.csv");
        if (!f.exists()) return list; // avoid runtime error

        try (java.util.Scanner sc = new java.util.Scanner(f, java.nio.charset.StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // skip empty lines and comments

                // managers.csv: username,teamName
                String[] p = line.split(",", -1); // keep empty fields
                if (p.length != 2) continue; // skip any wrongly formatted lines

                String uname    = p[0].trim();
                String teamname = p[1].trim();

                // pull base fields from users.csv
                String[] urow = null; // username,password,first,last
                java.util.List<String[]> userRows = readRows(USERS);
                for (String[] r : userRows) {
                    if (r != null && r.length >= 4 && r[0] != null && r[0].trim().equals(uname)) {
                        urow = r;
                        break;
                    }
                }
                if (urow == null || urow.length != 4) continue;

                String password  = urow[1].trim();
                String firstname = urow[2].trim();
                String lastname  = urow[3].trim();

                // Manager now only needs teamName (no balances / no managerUsername)
                Manager m = new Manager(uname, password, firstname, lastname, teamname);
                list.add(m);
            }
        } catch (java.io.IOException ignored) {}
        managers = list;
        return list;
    }

    // ===== ID helpers: derive next IDs from the last row's first column (no meta.csv) =====
    public static int readLastIdFromCsv(String fileName) {
        File f = new File(fileName);
        if (!f.exists() || f.length() == 0) return 0;

        String lastNonEmpty = null;
        try (java.util.Scanner sc = new java.util.Scanner(f, "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lastNonEmpty = line;
                }
            }
        } catch (IOException ignored) {}

        if (lastNonEmpty == null) return 0;

        // first column before the first comma
        String[] parts = lastNonEmpty.split(",", -1);
        if (parts.length == 0) return 0;

        String firstCol = parts[0].trim();
        // if IDs were numeric like 1,2,3... this works directly
        // if you had prefixes (e.g., TOR-12), try to strip non-digits:
        try {
            return Integer.parseInt(firstCol);
        } catch (NumberFormatException ex) {
            // fallback: strip non-digits and try again
            String digits = firstCol.replaceAll("\\D+", "");
            if (digits.isEmpty()) return 0;
            try {
                return Integer.parseInt(digits);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
    }

    public static int nextTimeOffId() {
        int last = readLastIdFromCsv("timeoff_requests.csv");
        return last + 1;
    }

    public static int nextWithdrawalId() {
        int last = readLastIdFromCsv("withdrawal_requests.csv");
        return last + 1;
    }

    // ---------- Append helpers (ensure first column is ID) ----------
    public static synchronized void appendTimeOffRequest(TimeOffRequest tor) {
        // CSV: id,username,startDate,endDate,reason,status
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream("timeoff_requests.csv", true),
                java.nio.charset.StandardCharsets.UTF_8))) {
            String line = String.join(",",
                    tor.getID(),
                    tor.getEmployee().getUsername(),
                    tor.getStartDate().toString(),
                    tor.getEndDate().toString(),
                    escapeCsv(tor.getReason()),
                    tor.getStatus().name()
            );
            pw.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void appendWithdrawalRequest(WithdrawalRequest wr) {
        // CSV: id,username,torId,reason,status
        // (username = the employee requesting withdrawal; torId = the time-off being withdrawn)
        String torId = (wr.getTimeOffRequest() == null ? "" : wr.getTimeOffRequest().getID());
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream("withdrawal_requests.csv", true),
                java.nio.charset.StandardCharsets.UTF_8))) {
            String line = String.join(",",
                    wr.getID(),
                    wr.getEmployee().getUsername(),
                    torId,
                    escapeCsv(wr.getReason()),
                    wr.getStatus().name()
            );
            pw.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Small helper so commas/newlines in reasons don't break CSV
    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needsQuotes = s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"");
        String out = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + out + "\"" : out;
    }

    // reads administrators.csv to obtain values for the fields of admin, and then create Admin objects and an Array
    public static ArrayList<Admin> createAdmin() {
        ArrayList<Admin> list = new ArrayList<>();
        File f = new File("administrators.csv");
        if (!f.exists()) return list; // avoid runtime error

        try (java.util.Scanner sc = new java.util.Scanner(f, java.nio.charset.StandardCharsets.UTF_8)) { // try creating a reader for file f with special charset
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // skip empty lines and comments

                // managers.csv: teamname
                String[] p = line.split(","); // keep empty fields
                if (p.length != 1) continue; // skip any wrongly formatted lines
                String uname = p[0].trim();

                // pull base fields from users.csv
                String[] urow = null; // username,password,first,last
                List<String[]> userRows = readRows(USERS);
                for (String[] r : userRows) {
                    if (r != null && r.length >= 4 && r[0] != null && r[0].trim().equals(uname)) {
                        urow = r;
                        break;
                    }
                }
                if (urow == null || urow.length != 4) continue; // confirm valid format

                String password = urow[1].trim(); //password
                String firstname = urow[2].trim();
                String lastname = urow[3].trim();

                Admin m = new Admin(uname, password, firstname, lastname);

                list.add(m);
            }
        } catch (java.io.IOException ignored) {}
        admins = list;
        return list;
    }

    // Build the polymorphic users list by inserting each object in alphabetical order by username.
// Assumes there are NO duplicates across employees/managers/admins.
    public static ArrayList<User> createUsers() {
        if (users == null) users = new ArrayList<>(); // initialize the polymorphic array
        users.clear();

        // insert EMPLOYEES

        for (Employee employeeobj : employees) {
            String name = employeeobj.getUsername();
            int insertAt = 0; // the index that holds where the object should be inserted at to keep the alphabetic sorted-ness within the ArrayList with respect to usernames
            while (insertAt < users.size()) {
                String cu = users.get(insertAt).getUsername();
                if (cu.compareToIgnoreCase(name) > 0) break;
                insertAt++;
            }
            users.add(insertAt, employeeobj);
        }


        // insert MANAGERS

        for (Manager managerobj : managers) {
            String name = managerobj.getUsername();
            int insertAt = 0; // the index that holds where the object should be inserted at to keep the alphabetic sorted-ness within the ArrayList with respect to usernames
            while (insertAt < users.size()) {
                String cu = users.get(insertAt).getUsername();
                if (cu.compareToIgnoreCase(name) > 0) break;
                insertAt++;
            }
            users.add(insertAt, managerobj);
        }


        // insert the admins
        for (Admin adminobj : admins) {
            String name = adminobj.getUsername();
            int insertAt = 0; // the index that holds where the object should be inserted at to keep the alphabetic sorted-ness within the ArrayList with respect to usernames
            while (insertAt < users.size()) {
                String cu = users.get(insertAt).getUsername();
                if (cu.compareToIgnoreCase(name) > 0) break;
                insertAt++;
            }
            users.add(insertAt, adminobj);
        }

        return users;
    }

    // helper
    private static int parseIntOrZero(String s) {
        try
        {
            return Integer.parseInt(s.trim());
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    // find a User object by username from the in-memory, alphabetically-sorted `users` list
    private static User findUserObj(String username) {
        int idx = binarySearch(users, username); // uses your object binarySearch(List<User>, String)
        if(idx == -1) return null;
        return users.get(idx);
    }

    // takes a username and finds the corresponding password
    public static String findPassword(String username) {
        User u = findUserObj(username); // object-only, uses your binarySearch over `users`
        return u.getPassword();
    }


    // Update a single field in a CSV row identified by username.
    // path: e.g., "employees.csv"
    // username: row key in column 0 (case-insensitive match)
    // index: which column to update (0-based; 0 is the username itself)
    // newValue: the new string to put into that column
    public static boolean updateCsvField(String path, String username, int index, String newValue) {
        List<String[]> rows = readRows(path);

        // find the row by username (col 0)
        int pos = -1; // sentinal value, stays -1 if there is no username that is present in the path we are searching in
        String key = username.trim();
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            String usern = "";
            if (r != null && r.length > 0 && r[0] != null) usern = r[0].trim();
            if (usern.equalsIgnoreCase(key)) { pos = i; break; }
        }
        if (pos < 0) return false; // not found

        // ensure index exists
        String[] row = rows.get(pos);
        if (index < 0) return false;
        if (index >= row.length) { // if the index is greater than the row length (if there are less comma seperated values in a line than what you wanted to change) that's not possible
            return false;
        }

        // set value
        row[index] = newValue.trim();
        rows.set(pos, row);

        // if index = 0 meaning that username changed, keep file sorted by username
        if (index == 0) {
            String newUser = (row[0] == null) ? "" : row[0].trim();

            // take the updated row out of the list temporarily
            rows.remove(pos);

            // find the new alphabetical position for this username
            int insertAt = 0; // the index where the row should be inserted to keep alphabetical order
            while (insertAt < rows.size()) {
                String[] r2 = rows.get(insertAt);
                String u2 = (r2 != null && r2.length > 0 && r2[0] != null) ? r2[0].trim() : "";
                if (u2.compareToIgnoreCase(newUser) > 0) break; // found a larger username
                insertAt++;
            }

            // insert the row back at its correct position
            rows.add(insertAt, row);
        }

        writeRows(path, rows);
        return true;
    }

    // delete one row by username (col 0) from a csv
    public static boolean deleteRow(String path, String username) {
        java.util.List<String[]> rows = readRows(path);
        String key = username.trim();
        int idx = -1; // sentinal value = -1 if username doesn't exist. Otherwise, holds the index of the row with the username we seek
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            String u = "";
            if (r != null && r[0] != "" && r[0] != null)
            {
                u = r[0].trim();
            }
            if (u.equalsIgnoreCase(key))
            {
                idx = i;
                break;
            }
        }
        if (idx < 0) return false; // no row with username found
        rows.remove(idx);
        writeRows(path, rows);
        return true;
    }

    // Save a new TimeOffRequest row to timeoff_requests.csv
    public static void upsertTimeOffRequest(TimeOffRequest req) {
        List<String[]> rows = readRows(TIMEOFF_REQUESTS);

        // look for an existing row with same ID
        int idx = -1;
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r != null && r.length > 0 && r[0].trim().equalsIgnoreCase(req.getID())) {
                idx = i;
                break;
            }
        }

        String[] newRow = new String[] {
                req.getID(),
                req.getEmployee().getUsername(),
                req.getStartDate().toString(),
                req.getEndDate().toString(),
                req.getReason(),
                req.getStatus().toString()
        };

        if (idx >= 0) rows.set(idx, newRow); // update
        else rows.add(newRow); // insert new

        writeRows(TIMEOFF_REQUESTS, rows);
    }

    // Save a new WithdrawalRequest row to withdrawal_requests.csv
    public static void upsertWithdrawalRequest(WithdrawalRequest req) {
        List<String[]> rows = readRows(WITHDRAWAL_REQUESTS);

        int idx = -1;
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r != null && r.length > 0 && r[0].trim().equalsIgnoreCase(req.getID())) {
                idx = i;
                break;
            }
        }

        String[] newRow = new String[] {
                req.getID(),
                req.getEmployee().getUsername(),
                req.getTimeOffRequest().getID(),
                req.getReason(),
                req.getStatus().toString()
        };

        if (idx >= 0) rows.set(idx, newRow);
        else rows.add(newRow);

        writeRows(WITHDRAWAL_REQUESTS, rows);
    }

    public static ArrayList<BlockedDateRestriction> createBlockedDateRestrictions() {
        ArrayList<BlockedDateRestriction> list = new ArrayList<>();
        File f = new File(BLOCKED_DATES);
        if (!f.exists()) return list;

        try (Scanner sc = new Scanner(f, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // teamName,date
                String[] p = line.split(",", -1);
                if (p.length < 2) continue;

                String teamName = "";
                if (p[0] != null) teamName = p[0].trim();

                String dateStr = "";
                if (p[1] != null) dateStr = p[1].trim();

                java.time.LocalDate date;
                try {
                    date = java.time.LocalDate.parse(dateStr);
                } catch (Exception ex) {
                    continue;
                }

                list.add(new BlockedDateRestriction(teamName, date));
            }
        } catch (IOException ignored) {}

        return list;
    }

    public static ArrayList<MaxConcurrentRestriction> createMaxConcurrentRestrictions() {
        ArrayList<MaxConcurrentRestriction> list = new ArrayList<>();
        File f = new File(MAX_CONCURRENT);
        if (!f.exists()) return list;

        try (Scanner sc = new Scanner(f, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // teamName,max
                String[] p = line.split(",", -1);
                if (p.length < 2) continue;

                String teamName = "";
                if (p[0] != null) teamName = p[0].trim();

                int max;
                try {
                    String maxStr = "";
                    if (p[1] != null) maxStr = p[1].trim();
                    max = Integer.parseInt(maxStr);
                } catch (Exception ex) {
                    continue;
                }

                list.add(new MaxConcurrentRestriction(teamName, max));
            }
        } catch (IOException ignored) {}

        return list;
    }

    public static int countApprovedForTeamOn(String teamName, java.time.LocalDate date) {
        int count = 0;
        File f = new File("timeoff_requests.csv");
        if (!f.exists()) return 0;

        if (employees == null) createEmployees();
        if (managers == null) createManagers();

        try (Scanner sc = new Scanner(f, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // id,username,startDate,endDate,reason,status
                String[] p = line.split(",", -1);
                if (p.length != 6) continue;

                String status = "";
                if (p[5] != null) status = p[5].trim();
                if (!"APPROVED".equalsIgnoreCase(status)) continue;

                String uname = "";
                if (p[1] != null) uname = p[1].trim();
                Employee e = findEmployee(uname);
                if (e == null) continue;

                Manager m = findManager(e.getManagerUsername());
                String empTeam = "";
                if (m != null && m.getTeamName() != null) empTeam = m.getTeamName().trim();
                if (!empTeam.equalsIgnoreCase(teamName)) continue;

                java.time.LocalDate start;
                java.time.LocalDate end;
                try {
                    String s = "";
                    String t = "";
                    if (p[2] != null) s = p[2].trim();
                    if (p[3] != null) t = p[3].trim();
                    start = java.time.LocalDate.parse(s);
                    end   = java.time.LocalDate.parse(t);
                } catch (Exception ex) {
                    continue;
                }

                if (!date.isBefore(start) && !date.isAfter(end)) {
                    count++;
                }
            }
        } catch (IOException ignored) {}

        return count;
    }

    // timeoff_requests.csv: id,username,startDate,endDate,reason,status
    public static ArrayList<TimeOffRequest> createTimeOffRequests() {
        ArrayList<TimeOffRequest> list = new ArrayList<TimeOffRequest>();
        File f = new File("timeoff_requests.csv");
        if (!f.exists()) return list;

        try (java.util.Scanner sc = new java.util.Scanner(f, java.nio.charset.StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(",", -1);
                if (p.length < 6) continue;

                String id       = p[0].trim();
                String username = p[1].trim();
                String startStr = p[2].trim();
                String endStr   = p[3].trim();
                String reason   = p[4]; // keep as-is (may be empty)
                String statusS  = p[5].trim();

                User u = findUser(username);
                if (!(u instanceof Employee)) continue;
                Employee emp = (Employee) u;

                java.time.LocalDate start = java.time.LocalDate.parse(startStr);
                java.time.LocalDate end   = java.time.LocalDate.parse(endStr);
                Status st;
                try { st = Status.valueOf(statusS); } catch (Exception e) { st = Status.PENDING; }

                // constructor with id + status (you added this earlier)
                TimeOffRequest tor = new TimeOffRequest(id, emp, start, end, reason, st);
                list.add(tor);
            }
        } catch (Exception ignored) {}
        return list;
    }

    // withdrawal_requests.csv: id,username,timeOffRequestId,reason,status
    public static ArrayList<WithdrawalRequest> createWithdrawalRequests() {
        ArrayList<WithdrawalRequest> list = new ArrayList<WithdrawalRequest>();
        File f = new File("withdrawal_requests.csv");
        if (!f.exists()) return list;

        try (java.util.Scanner sc = new java.util.Scanner(f, java.nio.charset.StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(",", -1);
                if (p.length < 5) continue;

                String id       = p[0].trim();
                String username = p[1].trim();
                String torId    = p[2].trim();
                String reason   = p[3];
                String statusS  = p[4].trim();

                User u = findUser(username);
                if (!(u instanceof Employee)) continue;
                Employee emp = (Employee) u;

                // find the referenced TOR
                TimeOffRequest tor = findTimeOffRequestById(torId);

                Status st;
                try { st = Status.valueOf(statusS); } catch (Exception e) { st = Status.PENDING; }

                WithdrawalRequest wr = new WithdrawalRequest(id, emp, tor, reason, st);
                list.add(wr);
            }
        } catch (Exception ignored) {}
        return list;
    }

    // Helper Method: find a TOR by id by scanning the file (used for WR loading)
    public static TimeOffRequest findTimeOffRequestById(String id) {
        ArrayList<TimeOffRequest> all = createTimeOffRequests();
        int i = 0;
        while (i < all.size()) {
            TimeOffRequest t = all.get(i);
            if (t.getID().equalsIgnoreCase(id)) return t;
            i++;
        }
        return null;
    }

    // teamName,date   (append or update; here "update" only matters if exact duplicate exists)
    public static void upsertBlockedDateRestriction(String teamName, LocalDate date) {
        List<String[]> rows = readRows(BLOCKED_DATES);

        // avoid duplicates of same team+date
        int idx = -1;
        int i = 0;
        while (i < rows.size()) {
            String[] r = rows.get(i);
            String t = "";
            String d = "";
            if (r != null && r.length >= 2) {
                if (r[0] != null) t = r[0].trim();
                if (r[1] != null) d = r[1].trim();
            }
            if (t.equalsIgnoreCase(teamName) && d.equals(date.toString())) {
                idx = i;
                break;
            }
            i++;
        }

        String[] newRow = new String[]{ teamName == null ? "" : teamName.trim(), date.toString() };
        if (idx >= 0) rows.set(idx, newRow); else rows.add(newRow);
        writeRows(BLOCKED_DATES, rows);
    }

    public static void deleteBlockedDateRestriction(String teamName, LocalDate date) {
        List<String[]> rows = readRows(BLOCKED_DATES);
        int i = 0;
        while (i < rows.size()) {
            String[] r = rows.get(i);
            String t = "";
            String d = "";
            if (r != null && r.length >= 2) {
                if (r[0] != null) t = r[0].trim();
                if (r[1] != null) d = r[1].trim();
            }
            if (t.equalsIgnoreCase(teamName) && d.equals(date.toString())) {
                rows.remove(i);
                break;
            }
            i++;
        }
        writeRows(BLOCKED_DATES, rows);
    }

    // teamName,max  (one row per team; upsert replaces existing)
    public static void upsertMaxConcurrentRestriction(String teamName, int max) {
        List<String[]> rows = readRows(MAX_CONCURRENT);

        int idx = -1;
        int i = 0;
        while (i < rows.size()) {
            String[] r = rows.get(i);
            String t = "";
            if (r != null && r.length >= 1 && r[0] != null) t = r[0].trim();
            if (t.equalsIgnoreCase(teamName)) { idx = i; break; }
            i++;
        }

        String[] newRow = new String[]{ teamName == null ? "" : teamName.trim(), String.valueOf(max) };
        if (idx >= 0) rows.set(idx, newRow); else rows.add(newRow);
        writeRows(MAX_CONCURRENT, rows);
    }

    public static void deleteMaxConcurrentRestriction(String teamName) {
        List<String[]> rows = readRows(MAX_CONCURRENT);
        int i = 0;
        while (i < rows.size()) {
            String[] r = rows.get(i);
            String t = "";
            if (r != null && r.length >= 1 && r[0] != null) t = r[0].trim();
            if (t.equalsIgnoreCase(teamName)) {
                rows.remove(i);
                break;
            }
            i++;
        }
        writeRows(MAX_CONCURRENT, rows);
    }
}