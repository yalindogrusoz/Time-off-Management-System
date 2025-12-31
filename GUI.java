import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.ListCellRenderer;

public class GUI {
    public GUI() {
        JFrame frame = new JFrame("Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screen.width, screen.height);
        frame.setLocation(0, 0);
        frame.setLayout(null);

        //"username" text
        JPanel userlabel = new JPanel();
        userlabel.setBounds(screen.width/2-150,200,200,50);
        userlabel.setLayout(null);
        JLabel username = new JLabel("Username");
        username.setFont(new Font("Arial", Font.PLAIN, 36));
        username.setBounds(0,0,200,50);
        userlabel.add(username);
        frame.add(userlabel);

        //"username" textfield
        JPanel p = new JPanel();
        p.setBounds(screen.width/2-150,250,300,70);
        p.setLayout(null);
        JTextField t = new JTextField();
        t.setBounds(0,0,300,70);
        t.setFont(new Font("Arial", Font.PLAIN, 24));
        p.add(t);
        frame.add(p);

        // "password" text
        JPanel passwordlabel = new JPanel();
        passwordlabel.setBounds(screen.width/2-150,500,200,50);
        passwordlabel.setLayout(null);
        JLabel password = new JLabel("Password");
        password.setFont(new Font("Arial", Font.PLAIN, 36));
        password.setBounds(0,0,200,50);
        passwordlabel.add(password);
        frame.add(passwordlabel);

        //"password" textfield
        JPanel p1 = new JPanel();
        p1.setBounds(screen.width/2-150,550,300,70);
        p1.setLayout(null);
        JPasswordField passwordtextfield = new JPasswordField();
        passwordtextfield.setBounds(0,0,300,70);
        passwordtextfield.setFont(new Font("Arial", Font.PLAIN, 24));
        p1.add(passwordtextfield);
        frame.add(p1);

        //jpanel for button
        JPanel buttonpanel = new JPanel();
        buttonpanel.setBounds(screen.width/2-100,700,200,100);
        buttonpanel.setLayout(null);

        //jbutton
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 28));
        loginBtn.setBounds(0, 0, 200, 100);
        loginBtn.setBackground(new Color(100, 255, 100)); // lightgreen
        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        buttonpanel.add(loginBtn);

        frame.add(buttonpanel);

        loginBtn.addActionListener(e -> {
            String user = t.getText().trim();
            char[] pw = passwordtextfield.getPassword();
            if (user.isEmpty() || pw.length == 0) {
                JOptionPane.showMessageDialog(frame, "Please enter both username and password.");
                return;
            }
            String pass = new String(pw);
            java.util.Arrays.fill(pw, '\0');
            if (validateLogin(user, pass)) {
                GUI.showHomePage(frame, user);  // now shows the new landing screen
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        });

        frame.setVisible(true);
    }

    // takes a username and password and compares the password to the actual password of the user, returning a boolean value that allows or disallows login
    private static boolean validateLogin(String user, String pass) {
        User u = DataFiles.findUser(user);  // uses in-memory users list
        return u != null && pass.equals(u.getPassword());
    }

    // this creates the employees,managers,administrators and users.csv files if not already existant. and adds a admin object with
    // username: admin
    // password: adminpw
    // firstName: Ava
    // lastName: Admin
    // to begin with. this admin can log in and add all subsequent users themselves, giving a "seeding" or "bootstrapping" to the application
    private static void seedCsvIfEmpty() {
        try {
            File users = new File("users.csv");
            if (!users.exists() || users.length() == 0) {
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(users), java.nio.charset.StandardCharsets.UTF_8))) {
                    pw.println("admin,adminpw,Ava,Admin");
                }
            }

            File admins = new File("administrators.csv");
            if (!admins.exists() || admins.length() == 0) {
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(admins), java.nio.charset.StandardCharsets.UTF_8))) {
                    pw.println("admin");
                }
            }

            // create empty files so they exist
            new File("employees.csv").createNewFile();
            new File("managers.csv").createNewFile();
            new File("timeoff_requests.csv").createNewFile();
            new File("withdrawal_requests.csv").createNewFile();
            new File("blocked_dates.csv").createNewFile();
            new File("max_concurrent.csv").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void goToLogin(JFrame frame) {
        frame.dispose();
        javax.swing.SwingUtilities.invokeLater(GUI::new);
    }

    public static void showHomePage(JFrame frame, String username) {
        JPanel root = new JPanel(null);
        Dimension size = frame.getSize();

        // -------- Top bar --------
        JPanel top = new JPanel(null);
        top.setBounds(0, 0, size.width, 60);
        top.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        root.add(top);

        // Left: avatar + username (moved to top-left)
        ImageIcon im = new ImageIcon("usericon.png");
        Image scaledImg = im.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
        ImageIcon userIcon = new ImageIcon(scaledImg);

        JLabel avatar = new JLabel(userIcon);
        avatar.setBounds(10, 13, 34, 34);
        top.add(avatar);

        JLabel usernamedisplay = new JLabel(username);
        usernamedisplay.setFont(new Font("Arial", Font.PLAIN, 22));
        usernamedisplay.setBounds(54, 15, 300, 30);
        top.add(usernamedisplay);

        // Right: Home button (with icon)
        JButton homeBtn = new JButton("Home");
        try {
            ImageIcon homeIcon = new ImageIcon(new ImageIcon("home.png").getImage()
                    .getScaledInstance(22, 22, Image.SCALE_SMOOTH));
            homeBtn.setIcon(homeIcon);
        } catch (Exception ignored) {}
        homeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        homeBtn.setBounds(size.width - 150, 12, 130, 36);
        homeBtn.setBackground(new Color(220, 240, 255));
        homeBtn.setOpaque(true);
        homeBtn.setContentAreaFilled(true);
        top.add(homeBtn);

        // keep Home anchored on resize
        top.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                int w = top.getWidth();
                homeBtn.setBounds(w - 150, 12, 130, 36);
            }
        });

        // Clicking Home while already on landing should no-op
        homeBtn.addActionListener(ev -> showHomePage(frame, username));

        // Clicking Home while already on landing should no-op
        homeBtn.addActionListener(ev -> showHomePage(frame, username));

        // -------- Content (Landing) --------
        JPanel content = new JPanel(null);
        content.setBounds(0, 60, size.width, size.height - 60);
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        root.add(content);

        // Greeting
        User uobj = DataFiles.findUser(username);
        String full = (uobj == null) ? username : (uobj.getFirstName() + " " + uobj.getLastName()).trim();
        JLabel title = new JLabel("Welcome, " + full);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(30, 20, 800, 40);
        content.add(title);

        // Determine roles
        boolean isAdmin    = DataFiles.findAdmin(username)    != null;
        boolean isManager  = DataFiles.findManager(username)  != null;
        boolean isEmployee = DataFiles.findEmployee(username) != null;

        // Layout for rows
        final int bx = 30, bw = 320, bh = 80, gap = 20;
        final int logoutW = 160;
        final int spacing = 12; // gap between GoTo and Logout
        final java.util.concurrent.atomic.AtomicInteger y = new java.util.concurrent.atomic.AtomicInteger(90);

        // Helper to add a row: [Go to Panel] [Log out]
        java.util.function.BiConsumer<String, Runnable> addRow = (label, goAction) -> {
            int byLocal = y.get();

            JButton goBtn = new JButton(label);
            goBtn.setFont(new Font("Arial", Font.BOLD, 22));
            goBtn.setBounds(bx, byLocal, bw, bh);
            goBtn.setBackground(new Color(100, 245, 100));
            goBtn.setOpaque(true);
            goBtn.setContentAreaFilled(true);
            content.add(goBtn);
            goBtn.addActionListener(e -> goAction.run());

            JButton logoutBtnRow = new JButton("Log out");
            logoutBtnRow.setFont(new Font("Arial", Font.PLAIN, 20));
            logoutBtnRow.setBounds(bx + bw + spacing, byLocal, logoutW, bh);
            content.add(logoutBtnRow);
            logoutBtnRow.addActionListener(e -> goToLogin(frame));

            y.addAndGet(bh + gap);
        };

        // Add rows for whichever roles the user has
        if (isAdmin) {
            addRow.accept("Go to Admin Panel", () -> renderAdminHome(content, username));
        }
        if (isManager) {
            addRow.accept("Go to Manager Panel", () -> renderManagerHome(content, username));
        }
        if (isEmployee) {
            addRow.accept("Go to Employee Panel", () -> renderEmployeeHome(content, username));
        }

        // Install landing
        frame.setContentPane(root);
        frame.revalidate();
        frame.repaint();
    }

    public static void renderAdminHome(JPanel content, String username)
    {
        content.removeAll(); // clear whatever was showing before
        Dimension size = content.getSize();

        int w = content.getWidth();
        int h = content.getHeight(); // cache size for bounds

        //split the admin screen into 2: employee on left manager on right
        JPanel employeepanel = new JPanel(null);
        JPanel managerpanel = new JPanel(null);
        content.add(employeepanel);
        content.add(managerpanel);
        employeepanel.setBounds(0,0,w/2,h-200);
        employeepanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        managerpanel.setBounds(w/2,0,w/2,h-200);
        managerpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JPanel panelforresetbutton = new JPanel(null);
        panelforresetbutton.setBounds(0,h-200,w,200);
        panelforresetbutton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        content.add(panelforresetbutton);

        //reset button
        JButton resetBtn = new JButton("Reset All Balances");
        resetBtn.setFont(new Font("Arial", Font.BOLD, 24));
        resetBtn.setBackground(new Color(255, 230, 130)); // soft warning color
        resetBtn.setOpaque(true);
        resetBtn.setContentAreaFilled(true);

        // center it inside the bottom panel
        resetBtn.setBounds(w/2 - 200, 60, 400, 70);
        panelforresetbutton.setLayout(null);
        panelforresetbutton.add(resetBtn);

        JLabel empedit = new JLabel("Employee Edit");
        JLabel manedit = new JLabel("Manager Edit");
        empedit.setFont(new Font("Arial", Font.PLAIN, 30));
        manedit.setFont(new Font("Arial", Font.PLAIN, 30));
        employeepanel.add(empedit);
        managerpanel.add(manedit);

        // employee list display
        DefaultListModel<String> empModel = new DefaultListModel<>(); // JList that is mutable (dynamic)
        JList<String> empList = new JList<>(empModel);
        empList.setFixedCellHeight(36);

        // make borders between each row
        empList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
                return c;
            }
        });
        empList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane empScroll = new JScrollPane(empList); // make it scrollable
        employeepanel.add(empScroll);
        empScroll.setBounds(20, 190, w/2 - 40, h - 430);

        // keep a parallel list of objects for the rows to be rendering
        final java.util.List<Employee> currentEmployees = new java.util.ArrayList<>();

        reloadEmployees(empModel, currentEmployees);

        //actionlistener for reset button
        resetBtn.addActionListener(ev -> {
            int choice = JOptionPane.showConfirmDialog(
                    content,
                    "Set every employee's CURRENT balance to their MAX balance?",
                    "Confirm Reset",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.OK_OPTION) {
                try {
                    Admin.resetAllEmployeeBalances();   // Admin method
                    // refresh the employee list UI
                    reloadEmployees(empModel, currentEmployees);
                    JOptionPane.showMessageDialog(content, "All employee balances were reset.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(content, "Failed to reset balances: " + ex.getMessage());
                }
            }
        });

        // double-click to edit
        empList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent me) {
                if (me.getClickCount() < 2) return;
                int idx = empList.locationToIndex(me.getPoint());
                if (idx < 0 || idx >= currentEmployees.size()) return;
                Employee e = currentEmployees.get(idx);

                // Manager dropdown (choose by username)
                java.util.List<Manager> mgrs = DataFiles.createManagers(); // fresh
                java.util.List<String> mgrUsernames = new java.util.ArrayList<>();
                for (Manager m : mgrs) mgrUsernames.add(m.getUsername());
                if (mgrUsernames.isEmpty()) mgrUsernames.add(""); // allow blank
                JComboBox<String> mgrBox = new JComboBox<>(mgrUsernames.toArray(new String[0]));
                mgrBox.setSelectedItem(e.getManagerUsername());

                // fields
                JLabel unameLabel = new JLabel(e.getUsername()); // username not editable
                JPasswordField pass = new JPasswordField(e.getPassword());
                JTextField first   = new JTextField(e.getFirstName());
                JTextField last    = new JTextField(e.getLastName());
                JTextField cur     = new JTextField(String.valueOf(e.getCurrBalance()));
                JTextField max     = new JTextField(String.valueOf(e.getMaxBalance()));

                JPanel form = new JPanel(new GridLayout(7,2,8,8));
                form.add(new JLabel("Username:"));  form.add(unameLabel);
                form.add(new JLabel("Password:"));  form.add(pass);
                form.add(new JLabel("First name:"));form.add(first);
                form.add(new JLabel("Last name:")); form.add(last);
                form.add(new JLabel("Current:"));   form.add(cur);
                form.add(new JLabel("Max:"));       form.add(max);
                form.add(new JLabel("Manager:"));   form.add(mgrBox);

                int r = JOptionPane.showConfirmDialog(content, form, "Edit Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (r == JOptionPane.OK_OPTION) {
                    int curV, maxV;
                    try {
                        curV = Integer.parseInt(cur.getText().trim());
                        maxV = Integer.parseInt(max.getText().trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(content, "Balances must be whole numbers.");
                        return;
                    }
                    if (curV < 0 || maxV < 0 || curV > maxV) {
                        JOptionPane.showMessageDialog(content, "Invalid balances.");
                        return;
                    }

                    // push changes via setters (writes CSV)
                    e.setPassword(new String(pass.getPassword()).trim());
                    e.setFirstName(first.getText().trim());
                    e.setLastName(last.getText().trim());
                    e.setCurrBalance(curV);
                    e.setMaxBalance(maxV);
                    e.setManager((String)mgrBox.getSelectedItem());

                    // refresh in-memory and UI
                    DataFiles.createEmployees();
                    DataFiles.createUsers();
                    reloadEmployees(empModel, currentEmployees);
                }
            }
        });

        DefaultListModel<String> manModel = new DefaultListModel<>();
        JList<String> manList = new JList<>(manModel);
        manList.setFixedCellHeight(36);
        //make borders between each row
        manList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
                return c;
            }
        });
        manList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane manScroll = new JScrollPane(manList);
        managerpanel.add(manScroll);
        manScroll.setBounds(20, 190, w/2 - 40, h - 430);

        final java.util.List<Manager> currentManagers = new java.util.ArrayList<>();
        reloadManagers(manModel, currentManagers);

        manList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent me) {
                if (me.getClickCount() < 2) return;
                int idx = manList.locationToIndex(me.getPoint());
                if (idx < 0 || idx >= currentManagers.size()) return;
                Manager m = currentManagers.get(idx);

                JLabel unameLabel = new JLabel(m.getUsername()); // not editable
                JPasswordField pass = new JPasswordField(m.getPassword());
                JTextField first   = new JTextField(m.getFirstName());
                JTextField last    = new JTextField(m.getLastName());
                JTextField team    = new JTextField(m.getTeamName());

                JPanel form = new JPanel(new GridLayout(5,2,8,8));
                form.add(new JLabel("Username:"));  form.add(unameLabel);
                form.add(new JLabel("Password:"));  form.add(pass);
                form.add(new JLabel("First name:"));form.add(first);
                form.add(new JLabel("Last name:")); form.add(last);
                form.add(new JLabel("Team name:")); form.add(team);

                int r = JOptionPane.showConfirmDialog(content, form, "Edit Manager", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (r == JOptionPane.OK_OPTION) {
                    m.setPassword(new String(pass.getPassword()).trim());
                    m.setFirstName(first.getText().trim());
                    m.setLastName(last.getText().trim());
                    m.setTeamName(team.getText().trim());

                    DataFiles.createManagers();
                    DataFiles.createUsers();
                    reloadManagers(manModel, currentManagers);
                }
            }
        });


        empedit.setBounds(w/4-100,0,200,60);
        manedit.setBounds(w/4-100,0,200,60);


        // add employee button
        JButton addEmp = new JButton("Add Employee");
        addEmp.setFont(new Font("Arial", Font.BOLD, 30));
        addEmp.setBounds(w/4-145, 70, 290, 100);
        addEmp.setBackground(new Color(100, 245, 100));    // light green button
        addEmp.setOpaque(true);
        addEmp.setContentAreaFilled(true);
        employeepanel.add(addEmp);

        //add manager button
        JButton addManager = new JButton("Add Manager");
        addManager.setFont(new Font("Arial", Font.BOLD, 30));
        addManager.setBounds(w/4-145, 70, 290, 100);
        addManager.setBackground(new Color(100, 245, 100));    // light green button
        addManager.setOpaque(true);
        addManager.setContentAreaFilled(true);
        managerpanel.add(addManager);

        // add employee button actions:
        addEmp.addActionListener(ev -> {
            // Build the manager dropdown (display first+last, keep username+team in parallel lists)
            java.util.List<String> mgrUsernames = new java.util.ArrayList<>();
            java.util.List<String> mgrDisplay   = new java.util.ArrayList<>();
            try (java.util.Scanner sc = new java.util.Scanner(new java.io.File("managers.csv"), "UTF-8")) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split(",", 2); // username, teamName
                    if (parts.length >= 1) {
                        String managerusername   = parts[0].trim();
                        String team = parts.length > 1 ? parts[1].trim() : "";
                        User uo = DataFiles.findUser(managerusername); // retreives the manager object polymorphically
                        String label = managerusername;
                        if (uo != null) {
                            String fn = uo.getFirstName();
                            String ln = uo.getLastName();
                            if (!fn.isEmpty() || !ln.isEmpty()) label = fn + " " + ln;
                        }
                        mgrUsernames.add(managerusername);
                        mgrDisplay.add(label);
                    }
                }
            } catch (java.io.IOException ignored) {}

            if (mgrUsernames.isEmpty()) {
                JOptionPane.showMessageDialog(content, "Please add a manager first.");
                return;
            }

            javax.swing.JComboBox<String> mgrBox =
                    new javax.swing.JComboBox<>(mgrDisplay.toArray(new String[0]));

            // Fields for the employee
            JTextField userField   = new JTextField();
            JPasswordField passField = new JPasswordField();
            JTextField firstField  = new JTextField();
            JTextField lastField   = new JTextField();
            JTextField maxField    = new JTextField();
            JTextField curField    = new JTextField();

            JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
            form.add(new JLabel("Username:"));       form.add(userField);
            form.add(new JLabel("Password:"));       form.add(passField);
            form.add(new JLabel("First name:"));     form.add(firstField);
            form.add(new JLabel("Last name:"));      form.add(lastField);
            form.add(new JLabel("Max balance:"));    form.add(maxField);
            form.add(new JLabel("Current balance:"));//
            form.add(curField);
            form.add(new JLabel("Manager:"));        form.add(mgrBox);

            int result = JOptionPane.showConfirmDialog(
                    content, form, "Add Employee",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String u   = userField.getText().trim();
                String pw  = new String(passField.getPassword()).trim();
                String fn  = firstField.getText().trim();
                String ln  = lastField.getText().trim();
                String maxStr = maxField.getText().trim();
                String curStr = curField.getText().trim();

                if (u.isEmpty() || pw.isEmpty() || fn.isEmpty() || ln.isEmpty()
                        || maxStr.isEmpty() || curStr.isEmpty()) {
                    JOptionPane.showMessageDialog(content, "Please fill all fields.");
                    return;
                }
                if (DataFiles.findUser(u) != null) {
                    JOptionPane.showMessageDialog(content, "That username already exists.");
                    return;
                }

                int max;
                int cur;
                try {
                    max = Integer.parseInt(maxStr); // try to convert strings to int
                    cur = Integer.parseInt(curStr);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(content, "Balances must be whole numbers.");
                    return;
                }
                if (max < 0 || cur < 0) {
                    JOptionPane.showMessageDialog(content, "Balances must be non-negative.");
                    return;
                }
                if (cur > max) {
                    JOptionPane.showMessageDialog(content, "Current balance cannot exceed max balance.");
                    return;
                }

                int sel = mgrBox.getSelectedIndex();
                String mgrUsername = mgrUsernames.get(sel);

                DataFiles.upsertUser(u, pw, fn, ln);              // users.csv
                DataFiles.upsertEmployee(u, max, cur, mgrUsername); // employees.csv
                reloadEmployees(empModel, currentEmployees);
                // refresh in-memory lists so lookups see the new employee
                DataFiles.createEmployees();
                DataFiles.createUsers();

                JOptionPane.showMessageDialog(content, "Employee added: " + fn + " " + ln);
            }
        });

        addManager.addActionListener(ev -> {
            JTextField userField  = new JTextField(12);
            JPasswordField passField = new JPasswordField(12);
            JTextField firstField = new JTextField(12);
            JTextField lastField  = new JTextField(12);
            JTextField teamField  = new JTextField(12);

            JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
            form.add(new JLabel("Username:"));   form.add(userField);
            form.add(new JLabel("Password:"));   form.add(passField);
            form.add(new JLabel("First name:")); form.add(firstField);
            form.add(new JLabel("Last name:"));  form.add(lastField);
            form.add(new JLabel("Team name:"));  form.add(teamField);

            int result = JOptionPane.showConfirmDialog(
                    content, form, "Add Manager",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String u   = userField.getText().trim();
                String pw  = new String(passField.getPassword()).trim();
                String fn  = firstField.getText().trim();
                String ln  = lastField.getText().trim();
                String team= teamField.getText().trim();

                if (u.isEmpty() || pw.isEmpty() || fn.isEmpty() || ln.isEmpty()) {
                    JOptionPane.showMessageDialog(content, "Please fill username, password, first and last name.");
                    return;
                }
                if (DataFiles.findUser(u) != null) {
                    JOptionPane.showMessageDialog(content, "That username already exists.");
                    return;
                }


                // write to CSVs
                DataFiles.upsertUser(u, pw, fn, ln);               // users.csv -> username,password,first,last
                DataFiles.upsertManager(u, team);        // managers.csv -> username,cur,max,managerUsername,team
                // refresh in-memory lists so lookups see the new employee
                reloadManagers(manModel, currentManagers);                DataFiles.createManagers();
                DataFiles.createUsers();

                JOptionPane.showMessageDialog(content, "Manager added: " + fn + " " + ln);
            }
        });

        // apply all changes
        content.revalidate();
        content.repaint();

    }

    // Manager panel on left
    public static void renderManagerHome(JPanel content, String managerUsername) {
        content.removeAll();
        content.setLayout(null);

        int w = content.getWidth();
        int h = content.getHeight();

        // icon setup (single load per render)
        ImageIcon tickIcon  = new ImageIcon(new ImageIcon("tick.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ImageIcon crossIcon = new ImageIcon(new ImageIcon("cross.jpeg").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        final ImageIcon TICK_ICON  = tickIcon;
        final ImageIcon CROSS_ICON = crossIcon;

        final int ROW_HEIGHT = 36;

        JLabel title = new JLabel("Manager Home Page: Pending Approvals");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(20, 10, 600, 40);
        content.add(title);

        // Top lists area: reduce height by ~300 to leave room for restriction buttons
        int listsHeight = h - 380;

        JPanel left = new JPanel(null);
        left.setBounds(10, 60, w/2 - 20, listsHeight);
        left.setBorder(BorderFactory.createTitledBorder("Pending Time-Off Requests"));
        content.add(left);

        JPanel right = new JPanel(null);
        right.setBounds(w/2 + 10, 60, w/2 - 20, listsHeight);
        right.setBorder(BorderFactory.createTitledBorder("Pending Withdrawal Requests"));
        content.add(right);

        final DefaultListModel<TimeOffRequest> torModel = new DefaultListModel<>();
        final JList<TimeOffRequest> torList = new JList<>(torModel);
        torList.setFixedCellHeight(ROW_HEIGHT);

        final DefaultListModel<WithdrawalRequest> wrModel = new DefaultListModel<>();
        final JList<WithdrawalRequest> wrList = new JList<>(wrModel);
        wrList.setFixedCellHeight(ROW_HEIGHT);

        final int ICON_SIZE = 20;
        final int ICON_GAP = 10;
        final int ICON_RIGHT_PADDING = 6;

        torList.setCellRenderer(new ListCellRenderer<TimeOffRequest>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends TimeOffRequest> list, TimeOffRequest value, int index, boolean isSelected, boolean cellHasFocus) {
                String name = "";
                Employee e = value.getEmployee();
                if (e != null) {
                    name = e.getFirstName() + " " + e.getLastName();
                }
                String txt = name + " | " + value.getStartDate() + " \u2192 " + value.getEndDate()
                        + " | id=" + value.getID() + " | reason=" + value.getReason();

                JLabel lbl = new JLabel(txt) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        int crossLeft = getWidth() - ICON_SIZE - ICON_RIGHT_PADDING;
                        int tickLeft  = crossLeft - ICON_GAP - ICON_SIZE;
                        int y = (getHeight() - ICON_SIZE) / 2;
                        g.drawImage(TICK_ICON.getImage(),  tickLeft,  y, ICON_SIZE, ICON_SIZE, this);
                        g.drawImage(CROSS_ICON.getImage(), crossLeft, y, ICON_SIZE, ICON_SIZE, this);
                    }
                };
                lbl.setOpaque(true);
                if (isSelected == true) {
                    lbl.setBackground(list.getSelectionBackground());
                    lbl.setForeground(list.getSelectionForeground());
                } else {
                    lbl.setBackground(list.getBackground());
                    lbl.setForeground(list.getForeground());
                }
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
                lbl.setFont(new Font("Monospaced", Font.PLAIN, 14));
                lbl.setIconTextGap(10);
                return lbl;
            }
        });

        wrList.setCellRenderer(new ListCellRenderer<WithdrawalRequest>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends WithdrawalRequest> list, WithdrawalRequest value, int index, boolean isSelected, boolean cellHasFocus) {
                String name = "";
                Employee e = value.getEmployee();
                if (e != null) {
                    name = e.getFirstName() + " " + e.getLastName();
                }
                String range = "";
                String torId = "";
                TimeOffRequest tor = value.getTimeOffRequest();
                if (tor != null) {
                    range = tor.getStartDate() + " \u2192 " + tor.getEndDate();
                    torId = tor.getID();
                }
                String txt = name + " | " + range + " | tor=" + torId
                        + " | id=" + value.getID() + " | reason=" + value.getReason();

                JLabel lbl = new JLabel(txt) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        int crossLeft = getWidth() - ICON_SIZE - ICON_RIGHT_PADDING;
                        int tickLeft  = crossLeft - ICON_GAP - ICON_SIZE;
                        int y = (getHeight() - ICON_SIZE) / 2;
                        g.drawImage(TICK_ICON.getImage(),  tickLeft,  y, ICON_SIZE, ICON_SIZE, this);
                        g.drawImage(CROSS_ICON.getImage(), crossLeft, y, ICON_SIZE, ICON_SIZE, this);
                    }
                };
                lbl.setOpaque(true);
                if (isSelected == true) {
                    lbl.setBackground(list.getSelectionBackground());
                    lbl.setForeground(list.getSelectionForeground());
                } else {
                    lbl.setBackground(list.getBackground());
                    lbl.setForeground(list.getForeground());
                }
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
                lbl.setFont(new Font("Monospaced", Font.PLAIN, 14));
                lbl.setIconTextGap(10);
                return lbl;
            }
        });

        JScrollPane torScroll = new JScrollPane(torList);
        torScroll.setBounds(10, 70, left.getWidth() - 20, left.getHeight() - 80);
        left.add(torScroll);

        JScrollPane wrScroll = new JScrollPane(wrList);
        wrScroll.setBounds(10, 70, right.getWidth() - 20, right.getHeight() - 80);
        right.add(wrScroll);

        // Per-row approve/reject via mouse click near right edge
        torList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent me) {
                int idx = torList.locationToIndex(me.getPoint());
                if (idx < 0) return;
                Rectangle cellBounds = torList.getCellBounds(idx, idx);
                int clickX = me.getX();
                int rightEdge = cellBounds.x + cellBounds.width;

                int iconSize = 20;
                int gap = 10;
                int crossLeft = rightEdge - iconSize - 6; // padding ~6
                int tickLeft  = crossLeft - gap - iconSize;

                if (clickX >= tickLeft && clickX <= tickLeft + iconSize) {
                    TimeOffRequest sel = torModel.get(idx);
                    if (sel != null) {
                        sel.approve();
                        Employee emp = sel.getEmployee();
                        if (emp != null && emp.getPendingTimeOffRequests() != null) {
                            emp.getPendingTimeOffRequests().remove(sel);
                        }
                        DataFiles.createTimeOffRequests();
                        DataFiles.createEmployees();
                        JOptionPane.showMessageDialog(content, "Time-off approved for " + sel.getEmployee().getFirstName());
                        refreshManagerLists(torModel, wrModel, managerUsername, content);
                    }
                } else if (clickX >= crossLeft && clickX <= crossLeft + iconSize) {
                    TimeOffRequest sel = torModel.get(idx);
                    if (sel != null) {
                        sel.reject();
                        Employee emp = sel.getEmployee();
                        if (emp != null && emp.getPendingTimeOffRequests() != null) {
                            emp.getPendingTimeOffRequests().remove(sel);
                        }
                        DataFiles.createTimeOffRequests();
                        DataFiles.createEmployees();
                        JOptionPane.showMessageDialog(content, "Time-off rejected for " + sel.getEmployee().getFirstName());
                        refreshManagerLists(torModel, wrModel, managerUsername, content);
                    }
                }
            }
        });

        wrList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent me) {
                int idx = wrList.locationToIndex(me.getPoint());
                if (idx < 0) return;
                Rectangle cellBounds = wrList.getCellBounds(idx, idx);
                int clickX = me.getX();
                int rightEdge = cellBounds.x + cellBounds.width;

                int iconSize = 20;
                int gap = 10;
                int crossLeft = rightEdge - iconSize - 6;
                int tickLeft  = crossLeft - gap - iconSize;

                if (clickX >= tickLeft && clickX <= tickLeft + iconSize) {
                    WithdrawalRequest sel = wrModel.get(idx);
                    if (sel != null) {
                        sel.approve();
                        Employee emp = sel.getEmployee();
                        if (emp != null && emp.getPendingWithdrawalRequests() != null) {
                            emp.getPendingWithdrawalRequests().remove(sel);
                        }
                        DataFiles.createWithdrawalRequests();
                        DataFiles.createEmployees();
                        JOptionPane.showMessageDialog(content, "Withdrawal approved for " + sel.getEmployee().getFirstName());
                        refreshManagerLists(torModel, wrModel, managerUsername, content);
                    }
                } else if (clickX >= crossLeft && clickX <= crossLeft + iconSize) {
                    WithdrawalRequest sel = wrModel.get(idx);
                    if (sel != null) {
                        sel.reject();
                        Employee emp = sel.getEmployee();
                        if (emp != null && emp.getPendingWithdrawalRequests() != null) {
                            emp.getPendingWithdrawalRequests().remove(sel);
                        }
                        DataFiles.createWithdrawalRequests();
                        DataFiles.createEmployees();
                        JOptionPane.showMessageDialog(content, "Withdrawal rejected for " + sel.getEmployee().getFirstName());
                        refreshManagerLists(torModel, wrModel, managerUsername, content);
                    }
                }
            }
        });

        // Bottom panel for restrictions
        JPanel bottom = new JPanel(null);
        bottom.setBounds(10, 70 + listsHeight + 10, w - 20, h - (70 + listsHeight + 20));
        bottom.setBorder(BorderFactory.createTitledBorder("Team Restrictions"));
        content.add(bottom);

        JButton addRestriction = new JButton("Add Restriction");
        JButton removeRestriction = new JButton("Remove Restriction");
        addRestriction.setFont(new Font("Arial", Font.BOLD, 18));
        removeRestriction.setFont(new Font("Arial", Font.BOLD, 18));
        addRestriction.setBounds(20, 40, 220, 44);
        removeRestriction.setBounds(260, 40, 240, 44);

        //coloring buttons
        addRestriction.setBackground(new Color(100, 245, 100));
        addRestriction.setOpaque(true);
        addRestriction.setContentAreaFilled(true);

        removeRestriction.setBackground(new Color(245, 100, 100));
        removeRestriction.setOpaque(true);
        removeRestriction.setContentAreaFilled(true);

        bottom.add(addRestriction);
        bottom.add(removeRestriction);

        // Add restriction (blocked date OR max concurrent)
        addRestriction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                Manager me = DataFiles.findManager(managerUsername);
                String team = "";
                if (me != null && me.getTeamName() != null) team = me.getTeamName().trim();
                if (team.isEmpty()) {
                    JOptionPane.showMessageDialog(content, "No team found for this manager.");
                    return;
                }

                String[] choices = new String[]{ "Blocked Date", "Max Concurrent" };
                String pick = (String) JOptionPane.showInputDialog(
                        content,
                        "Choose restriction type:",
                        "Add Restriction",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        choices,
                        choices[0]
                );
                if (pick == null) return;

                if (pick.equals("Blocked Date")) {
                    JTextField dateField = new JTextField("2025-01-01");
                    JPanel form = new JPanel(new GridLayout(1,2,8,8));
                    form.add(new JLabel("Date (YYYY-MM-DD):"));
                    form.add(dateField);
                    int r = JOptionPane.showConfirmDialog(content, form, "Add Blocked Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (r == JOptionPane.OK_OPTION) {
                        String ds = dateField.getText().trim();
                        try {
                            java.time.LocalDate d = java.time.LocalDate.parse(ds);
                            DataFiles.upsertBlockedDateRestriction(team, d);
                            JOptionPane.showMessageDialog(content, "Blocked date added for team " + team + ": " + d);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(content, "Invalid date format.");
                        }
                    }
                } else if (pick.equals("Max Concurrent")) {
                    JTextField maxField = new JTextField("1");
                    JPanel form = new JPanel(new GridLayout(1,2,8,8));
                    form.add(new JLabel("Max concurrent on any day:"));
                    form.add(maxField);
                    int r = JOptionPane.showConfirmDialog(content, form, "Set Max Concurrent", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (r == JOptionPane.OK_OPTION) {
                        String ms = maxField.getText().trim();
                        try {
                            int m = Integer.parseInt(ms);
                            if (m < 0) {
                                JOptionPane.showMessageDialog(content, "Must be >= 0.");
                                return;
                            }
                            DataFiles.upsertMaxConcurrentRestriction(team, m);
                            JOptionPane.showMessageDialog(content, "Max concurrent set for team " + team + " = " + m);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(content, "Enter a whole number.");
                        }
                    }
                }
            }
        });

        // Remove restriction (pick existing blocked date OR clear max concurrent)
        removeRestriction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                Manager me = DataFiles.findManager(managerUsername);
                String team = "";
                if (me != null && me.getTeamName() != null) team = me.getTeamName().trim();
                if (team.isEmpty()) {
                    JOptionPane.showMessageDialog(content, "No team found for this manager.");
                    return;
                }

                String[] choices = new String[]{ "Blocked Date", "Max Concurrent" };
                String pick = (String) JOptionPane.showInputDialog(
                        content,
                        "Choose which to remove:",
                        "Remove Restriction",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        choices,
                        choices[0]
                );
                if (pick == null) return;

                if (pick.equals("Blocked Date")) {
                    ArrayList<BlockedDateRestriction> all = DataFiles.createBlockedDateRestrictions();
                    java.util.List<java.time.LocalDate> dates = new java.util.ArrayList<java.time.LocalDate>();
                    int i = 0;
                    while (i < all.size()) {
                        BlockedDateRestriction b = all.get(i);
                        String t = b.getTeamName() == null ? "" : b.getTeamName().trim();
                        if (t.equalsIgnoreCase(team)) dates.add(b.getDate());
                        i++;
                    }
                    if (dates.isEmpty()) {
                        JOptionPane.showMessageDialog(content, "No blocked dates for team " + team + ".");
                        return;
                    }
                    String[] dateOptions = new String[dates.size()];
                    int j = 0;
                    while (j < dates.size()) {
                        dateOptions[j] = dates.get(j).toString();
                        j++;
                    }
                    String choice = (String) JOptionPane.showInputDialog(
                            content,
                            "Select blocked date to remove:",
                            "Remove Blocked Date",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            dateOptions,
                            dateOptions[0]
                    );
                    if (choice == null) return;
                    try {
                        java.time.LocalDate d = java.time.LocalDate.parse(choice);
                        DataFiles.deleteBlockedDateRestriction(team, d);
                        JOptionPane.showMessageDialog(content, "Removed blocked date: " + d);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(content, "Invalid selection.");
                    }
                } else if (pick.equals("Max Concurrent")) {
                    int r = JOptionPane.showConfirmDialog(
                            content,
                            "Clear max concurrent limit for team " + team + "?",
                            "Clear Max Concurrent",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (r == JOptionPane.OK_OPTION) {
                        DataFiles.deleteMaxConcurrentRestriction(team);
                        JOptionPane.showMessageDialog(content, "Max concurrent cleared for team " + team + ".");
                    }
                }
            }
        });

        refreshManagerLists(torModel, wrModel, managerUsername, content);
    }
    //helpers:

    private static void reloadEmployees(DefaultListModel<String> model,
                                        java.util.List<Employee> objects) {
        objects.clear();
        objects.addAll(DataFiles.createEmployees()); // re-read from CSV
        model.clear();
        for (Employee e : objects) {
            model.addElement(e.getFirstName() + " " + e.getLastName());
        }
    }

    private static void showEmployeeWithdrawalDialog(JPanel parent, String employeeUsername) {
        User u = DataFiles.findUser(employeeUsername);
        if (!(u instanceof Employee)) {
            JOptionPane.showMessageDialog(parent, "Not an employee.");
            return;
        }
        Employee emp = (Employee) u;

        // Build list of this employee's APPROVED and NOT-YET-FINISHED TORs
        java.util.List<TimeOffRequest> myApproved = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();

        // optional: exclude TORs that already have a PENDING withdrawal request
        java.util.Set<String> torIdsWithPendingWr = new java.util.HashSet<>();
        for (WithdrawalRequest wr : DataFiles.createWithdrawalRequests()) {
            if (wr.getEmployee() != null
                    && wr.getEmployee().getUsername().equalsIgnoreCase(emp.getUsername())
                    && wr.getTimeOffRequest() != null
                    && wr.getStatus() == Status.PENDING) {
                torIdsWithPendingWr.add(wr.getTimeOffRequest().getID());
            }
        }

        for (TimeOffRequest tor : DataFiles.createTimeOffRequests()) {
            if (tor.getEmployee() != null
                    && tor.getEmployee().getUsername().equalsIgnoreCase(emp.getUsername())
                    && tor.getStatus() == Status.APPROVED
                    && !today.isAfter(tor.getEndDate())          // still ongoing or in future
                    && !torIdsWithPendingWr.contains(tor.getID()) // no existing pending WR
            ) {
                myApproved.add(tor);
            }
        }

        if (myApproved.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No eligible approved time-off requests to withdraw.\n" +
                            "(You can only request withdrawals for future/ongoing approved requests,\n" +
                            "and only one pending withdrawal per request.)");
            return;
        }

        // Build UI
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent));
        dlg.setModal(true);
        dlg.setTitle("Request Withdrawal");
        dlg.setLayout(null);
        dlg.setSize(700, 260);
        dlg.setLocationRelativeTo(parent);

        JLabel lTor = new JLabel("Time-Off to withdraw:");
        JLabel lReason = new JLabel("Reason:");
        lTor.setBounds(20, 30, 180, 28);
        lReason.setBounds(20, 80, 180, 28);
        dlg.add(lTor);
        dlg.add(lReason);

        // Combo box showing "id | yyyy-mm-dd → yyyy-mm-dd"
        String[] options = new String[myApproved.size()];
        String[] ids = new String[myApproved.size()];
        for (int i = 0; i < myApproved.size(); i++) {
            TimeOffRequest t = myApproved.get(i);
            options[i] = t.getID() + "  |  " + t.getStartDate() + " \u2192 " + t.getEndDate();
            ids[i] = t.getID();
        }
        JComboBox<String> torBox = new JComboBox<>(options);
        torBox.setBounds(210, 30, 440, 28);
        dlg.add(torBox);

        JTextField reasonField = new JTextField();
        reasonField.setBounds(210, 80, 440, 28);
        dlg.add(reasonField);

        JButton submit = new JButton("Submit");
        submit.setBounds(210, 130, 140, 36);
        dlg.add(submit);

        submit.addActionListener(e -> {
            int sel = torBox.getSelectedIndex();
            if (sel < 0) {
                JOptionPane.showMessageDialog(dlg, "Please choose a time-off request.");
                return;
            }
            String pickedId = ids[sel];
            String reason = reasonField.getText() == null ? "" : reasonField.getText().trim();

            try {
                // This enforces: non-empty reason, TOR exists, not past, and status == APPROVED
                WithdrawalRequest wr = emp.withdrawRequest(pickedId, reason);

                // The WR constructor should persist via DataFiles.appendWithdrawalRequest(...)
                // If your constructor doesn't append, you can also call:
                // DataFiles.appendWithdrawalRequest(wr);

                JOptionPane.showMessageDialog(dlg, "Withdrawal submitted. id=" + wr.getID());
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage());
            }
        });

        dlg.setVisible(true);
    }

    private static void reloadManagers(DefaultListModel<String> model,
                                       java.util.List<Manager> objects) {
        objects.clear();
        objects.addAll(DataFiles.createManagers()); // re-read from CSV
        model.clear();
        for (Manager m : objects) {
            model.addElement(m.getFirstName() + " " + m.getLastName());
        }
    }

    private static void refreshManagerLists(
            DefaultListModel<TimeOffRequest> torModel,
            DefaultListModel<WithdrawalRequest> wrModel,
            String managerUsername,
            JComponent content) {

        torModel.clear();
        wrModel.clear();

        ArrayList<TimeOffRequest> allTor = DataFiles.createTimeOffRequests();
        ArrayList<WithdrawalRequest> allWr = DataFiles.createWithdrawalRequests();

        Manager me = DataFiles.findManager(managerUsername);
        String team = "";
        if (me != null && me.getTeamName() != null) {
            team = me.getTeamName().trim();
        }

        int i = 0;
        while (i < allTor.size()) {
            TimeOffRequest tor = allTor.get(i);
            if (tor.getStatus() == Status.PENDING) {
                Employee e = tor.getEmployee();
                if (e != null) {
                    Manager m = DataFiles.findManager(e.getManagerUsername());
                    String empTeam = "";
                    if (m != null && m.getTeamName() != null) empTeam = m.getTeamName().trim();
                    if (empTeam.equalsIgnoreCase(team)) {
                        torModel.addElement(tor);
                    }
                }
            }
            i++;
        }

        i = 0;
        while (i < allWr.size()) {
            WithdrawalRequest wr = allWr.get(i);
            if (wr.getStatus() == Status.PENDING) {
                Employee e = wr.getEmployee();
                if (e != null) {
                    Manager m = DataFiles.findManager(e.getManagerUsername());
                    String empTeam = "";
                    if (m != null && m.getTeamName() != null) empTeam = m.getTeamName().trim();
                    if (empTeam.equalsIgnoreCase(team)) {
                        wrModel.addElement(wr);
                    }
                }
            }
            i++;
        }

        content.revalidate();
        content.repaint();
    }

    public static void renderEmployeeHome(JPanel content, String employeeUsername) {
        content.removeAll();
        content.setLayout(null);

        int w = content.getWidth();
        int h = content.getHeight();

        User u = DataFiles.findUser(employeeUsername);
        if (!(u instanceof Employee)) {
            content.revalidate();
            content.repaint();
            return;
        }
        Employee me = (Employee) u;

        JLabel balanceLbl = new JLabel("Balance: " + me.getCurrBalance() + "/" + me.getMaxBalance());
        balanceLbl.setFont(new Font("Arial", Font.BOLD, 20));
        balanceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        balanceLbl.setBounds(w - 320, 10, 300, 40);
        content.add(balanceLbl);

        JButton submitBtn = new JButton("Submit Time-Off Request");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 22));
        submitBtn.setBounds(20, 10, 380, 44);
        submitBtn.setBackground(new Color(100, 245, 100));
        submitBtn.setOpaque(true);
        submitBtn.setContentAreaFilled(true);
        content.add(submitBtn);

        JButton withdrawBtn = new JButton("Request Withdrawal");
        withdrawBtn.setFont(new Font("Arial", Font.BOLD, 22));
        withdrawBtn.setBounds(420, 10, 300, 44);
        withdrawBtn.setBackground(new Color(255, 225, 130)); // light amber
        withdrawBtn.setOpaque(true);
        withdrawBtn.setContentAreaFilled(true);
        content.add(withdrawBtn);

        int listsTop = 80;
        int listsHeight = h - listsTop - 20;
        int listsWidth = (w - 30) / 2;

        JPanel left = new JPanel(null);
        left.setBounds(10, listsTop, listsWidth, listsHeight);
        left.setBorder(BorderFactory.createTitledBorder("My Time-Off Requests"));
        content.add(left);

        JPanel right = new JPanel(null);
        right.setBounds(20 + listsWidth, listsTop, listsWidth, listsHeight);
        right.setBorder(BorderFactory.createTitledBorder("My Withdrawal Requests"));
        content.add(right);

        DefaultListModel<TimeOffRequest> torModel = new DefaultListModel<>();
        JList<TimeOffRequest> torList = new JList<>(torModel);
        torList.setFixedCellHeight(28);
        torList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                TimeOffRequest t = (TimeOffRequest) value;
                String s = t.getStartDate() + " \u2192 " + t.getEndDate() + " | id=" + t.getID() + " | " + t.getStatus();
                c.setText(s);
                c.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(220,220,220)));
                return c;
            }
        });
        JScrollPane torScroll = new JScrollPane(torList);
        torScroll.setBounds(10, 25, left.getWidth() - 20, left.getHeight() - 35);
        left.add(torScroll);

        DefaultListModel<WithdrawalRequest> wrModel = new DefaultListModel<>();
        JList<WithdrawalRequest> wrList = new JList<>(wrModel);
        wrList.setFixedCellHeight(28);
        wrList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                WithdrawalRequest wreq = (WithdrawalRequest) value;
                TimeOffRequest ref = wreq.getTimeOffRequest();
                String range = "";
                if (ref != null) range = ref.getStartDate() + " \u2192 " + ref.getEndDate();
                String s = "tor=" + (ref == null ? "" : ref.getID()) + " | " + range + " | id=" + wreq.getID() + " | " + wreq.getStatus();
                c.setText(s);
                c.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(220,220,220)));
                return c;
            }
        });
        JScrollPane wrScroll = new JScrollPane(wrList);
        wrScroll.setBounds(10, 25, right.getWidth() - 20, right.getHeight() - 35);
        right.add(wrScroll);

        reloadEmployeeLists(torModel, wrModel, me);

        withdrawBtn.addActionListener(ev -> {
            showEmployeeWithdrawalDialog(content, employeeUsername);
            reloadEmployeeLists(torModel, wrModel, me);
            balanceLbl.setText("Balance: " + me.getCurrBalance() + "/" + me.getMaxBalance());
        });

        submitBtn.addActionListener(ev -> {
            showEmployeeRequestDialog(content, employeeUsername);
            reloadEmployeeLists(torModel, wrModel, me);
            balanceLbl.setText("Balance: " + me.getCurrBalance() + "/" + me.getMaxBalance());
        });

        content.revalidate();
        content.repaint();
    }

    private static void reloadEmployeeLists(
            DefaultListModel<TimeOffRequest> torModel,
            DefaultListModel<WithdrawalRequest> wrModel,
            Employee me) {

        torModel.clear();
        wrModel.clear();

        ArrayList<TimeOffRequest> allTor = DataFiles.createTimeOffRequests();
        int i = 0;
        while (i < allTor.size()) {
            TimeOffRequest t = allTor.get(i);
            Employee e = t.getEmployee();
            if (e != null && e.getUsername().equalsIgnoreCase(me.getUsername())) {
                torModel.addElement(t);
            }
            i++;
        }

        ArrayList<WithdrawalRequest> allWr = DataFiles.createWithdrawalRequests();
        int j = 0;
        while (j < allWr.size()) {
            WithdrawalRequest w = allWr.get(j);
            Employee e = w.getEmployee();
            if (e != null && e.getUsername().equalsIgnoreCase(me.getUsername())) {
                wrModel.addElement(w);
            }
            j++;
        }
    }


    private static void showEmployeeRequestDialog(JPanel parent, String employeeUsername) {
        User u = DataFiles.findUser(employeeUsername);
        if (!(u instanceof Employee)) {
            JOptionPane.showMessageDialog(parent, "Not an employee.");
            return;
        }
        Employee emp = (Employee) u;

        Manager mgr = DataFiles.findManager(emp.getManagerUsername());
        String team = "";
        if (mgr != null && mgr.getTeamName() != null) team = mgr.getTeamName().trim();

        ArrayList<BlockedDateRestriction> blocked = DataFiles.createBlockedDateRestrictions();
        ArrayList<MaxConcurrentRestriction> maxlist = DataFiles.createMaxConcurrentRestrictions();
        int maxConc = Integer.MAX_VALUE;
        int i = 0;
        while (i < maxlist.size()) {
            MaxConcurrentRestriction r = maxlist.get(i);
            String t = r.getTeamName() == null ? "" : r.getTeamName().trim();
            if (t.equalsIgnoreCase(team)) {
                maxConc = r.getMax();
                break;
            }
            i = i + 1;
        }

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent));
        dlg.setModal(true);
        dlg.setTitle("Request Time Off");
        dlg.setLayout(null);
        dlg.setSize(780, 420);
        dlg.setLocationRelativeTo(parent);

        JLabel l1 = new JLabel("Start (ddmmyyyy):");
        JLabel l2 = new JLabel("End (ddmmyyyy):");
        JLabel l3 = new JLabel("Reason:");
        JTextField tfStart = new JTextField();
        JTextField tfEnd = new JTextField();
        JTextField tfReason = new JTextField();

        l1.setBounds(20, 30, 150, 28);
        tfStart.setBounds(180, 30, 180, 28);
        l2.setBounds(20, 70, 150, 28);
        tfEnd.setBounds(180, 70, 180, 28);
        l3.setBounds(20, 110, 150, 28);
        tfReason.setBounds(180, 110, 260, 28);

        JButton submit = new JButton("Submit");
        submit.setBounds(180, 160, 120, 36);

        dlg.add(l1); dlg.add(tfStart);
        dlg.add(l2); dlg.add(tfEnd);
        dlg.add(l3); dlg.add(tfReason);
        dlg.add(submit);

        final LocalDate[] shown = new LocalDate[]{ LocalDate.now().withDayOfMonth(1) };

        final String teamName = team;
        final int maxConcLocal = maxConc;

        JPanel calendar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                LocalDate first = shown[0];
                int year = first.getYear();
                int month = first.getMonthValue();
                LocalDate firstDay = LocalDate.of(year, month, 1);
                int len = firstDay.lengthOfMonth();

                int dow = firstDay.getDayOfWeek().getValue();
                if (dow == 7) dow = 0;

                int cols = 7;
                int rows = 6;
                int cw = getWidth() / cols;
                int ch = getHeight() / rows;

                int d = 1;
                int r = 0;
                int c = dow;

                while (d <= len) {
                    LocalDate date = LocalDate.of(year, month, d);
                    boolean isBlocked = false;
                    int j = 0;
                    while (j < blocked.size()) {
                        BlockedDateRestriction b = blocked.get(j);
                        String bt = b.getTeamName() == null ? "" : b.getTeamName().trim();
                        if (bt.equalsIgnoreCase(teamName)) {
                            if (date.equals(b.getDate())) {
                                isBlocked = true;
                                break;
                            }
                        }
                        j = j + 1;
                    }
                    boolean overMax = false;
                    if (maxConcLocal != Integer.MAX_VALUE) {
                        int used = DataFiles.countApprovedForTeamOn(teamName, date);
                        if (used >= maxConcLocal) {
                            overMax = true;
                        }
                    }
                    boolean restricted = false;
                    if (isBlocked == true || overMax == true) {
                        restricted = true;
                    }

                    int x = c * cw;
                    int y = r * ch;

                    if (restricted == true) {
                        g2.setColor(new Color(255, 150, 150));
                    } else {
                        g2.setColor(Color.white);
                    }
                    g2.fillRect(x + 1, y + 1, cw - 2, ch - 2);

                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRect(x, y, cw, ch);

                    g2.setColor(Color.black);
                    g2.setFont(getFont());
                    g2.drawString(String.valueOf(d), x + 8, y + 18);

                    c = c + 1;
                    if (c >= cols) {
                        c = 0;
                        r = r + 1;
                    }
                    d = d + 1;
                }
            }
        };
        calendar.setBounds(460, 20, 300, 280);
        dlg.add(calendar);

        JButton prev = new JButton("◀");
        JButton next = new JButton("▶");
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        prev.setBounds(460, 310, 60, 32);
        monthLabel.setBounds(520, 310, 180, 32);
        next.setBounds(700, 310, 60, 32);

        dlg.add(prev);
        dlg.add(monthLabel);
        dlg.add(next);

        LocalDate f0 = shown[0];
        monthLabel.setText(f0.getMonth().toString() + " " + f0.getYear());

        prev.addActionListener(e -> {
            LocalDate f = shown[0];
            int y = f.getYear();
            int m = f.getMonthValue() - 1;
            if (m < 1) {
                m = 12;
                y = y - 1;
            }
            shown[0] = LocalDate.of(y, m, 1);
            LocalDate f2 = shown[0];
            monthLabel.setText(f2.getMonth().toString() + " " + f2.getYear());
            calendar.repaint();
        });

        next.addActionListener(e -> {
            LocalDate f = shown[0];
            int y = f.getYear();
            int m = f.getMonthValue() + 1;
            if (m > 12) {
                m = 1;
                y = y + 1;
            }
            shown[0] = LocalDate.of(y, m, 1);
            LocalDate f2 = shown[0];
            monthLabel.setText(f2.getMonth().toString() + " " + f2.getYear());
            calendar.repaint();
        });

        submit.addActionListener(e -> {
            String s = tfStart.getText().trim();
            String t = tfEnd.getText().trim();
            String r = tfReason.getText().trim();
            if (s.length() != 8 || t.length() != 8) {
                JOptionPane.showMessageDialog(dlg, "Use ddMMyyyy.");
                return;
            }
            LocalDate sd;
            LocalDate ed;
            try {
                int sdD = Integer.parseInt(s.substring(0, 2));
                int sdM = Integer.parseInt(s.substring(2, 4));
                int sdY = Integer.parseInt(s.substring(4, 8));
                int edD = Integer.parseInt(t.substring(0, 2));
                int edM = Integer.parseInt(t.substring(2, 4));
                int edY = Integer.parseInt(t.substring(4, 8));
                sd = LocalDate.of(sdY, sdM, sdD);
                ed = LocalDate.of(edY, edM, edD);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid dates.");
                return;
            }
            try {
                boolean hitsBlocked = false;
                int k = 0;
                while (k < blocked.size()) {
                    BlockedDateRestriction b = blocked.get(k);
                    String bt = b.getTeamName() == null ? "" : b.getTeamName().trim();
                    if (bt.equalsIgnoreCase(teamName)) {
                        LocalDate bd = b.getDate();
                        if (!bd.isBefore(sd) && !bd.isAfter(ed)) { // bd ∈ [sd, ed]
                            hitsBlocked = true;
                            break;
                        }
                    }
                    k++;
                }
                if (hitsBlocked) {
                    JOptionPane.showMessageDialog(dlg,
                            "Your request includes a blocked date for your team.\nPlease choose different dates.");
                    return;
                }
                TimeOffRequest tor = emp.requestTimeOff(sd, ed, r);
                JOptionPane.showMessageDialog(dlg, "Submitted: " + tor.getID());
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage());
            }
        });

        dlg.setVisible(true);
    }


    public static void main(String[] args)
    {
        GUI.seedCsvIfEmpty();
        // Load core data
        ArrayList<Employee> employees  = DataFiles.createEmployees();
        ArrayList<Manager> managers    = DataFiles.createManagers();
        ArrayList<Admin> admins        = DataFiles.createAdmin();
        ArrayList<User> users          = DataFiles.createUsers();

        // Load requests from CSVs
        ArrayList<TimeOffRequest> timeOffRequests       = DataFiles.createTimeOffRequests();
        ArrayList<WithdrawalRequest> withdrawalRequests = DataFiles.createWithdrawalRequests();

        // Link requests back to each employee's in-memory lists
        for (TimeOffRequest tor : timeOffRequests) {
            Employee emp = tor.getEmployee();
            if (emp != null) {
                emp.getTimeOffRequestsSubmitted().add(tor);
                if (tor.getStatus() == Status.PENDING) {
                    emp.getPendingTimeOffRequests().add(tor);
                }
            }
        }
        for (WithdrawalRequest wr : withdrawalRequests) {
            Employee emp = wr.getEmployee();
            if (emp != null && wr.getStatus() == Status.PENDING) {
                emp.getPendingWithdrawalRequests().add(wr);
            }
        }

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        javax.swing.SwingUtilities.invokeLater(GUI::new);
    }



}
