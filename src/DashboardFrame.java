import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DashboardFrame extends JFrame {
    private final String role;
    private JPanel contentPanel;

    public DashboardFrame(String role) {
        this.role = role;

        // --- 1. FULL SCREEN SETTINGS ---
        setTitle("Gym Management System - Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 2. HEADER BANNER (Top-Left Logo) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        leftHeaderPanel.setBackground(Color.WHITE);

        URL imgURL = getClass().getResource("/poornim logo2.jpeg");
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(150, 30, Image.SCALE_SMOOTH);
            leftHeaderPanel.add(new JLabel(new ImageIcon(img)));
        }

        JLabel headerTitle = new JLabel("🏋️ FitLife Gym Dashboard");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerTitle.setForeground(new Color(41, 128, 185));
        leftHeaderPanel.add(headerTitle);
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        rightHeaderPanel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Role: " + role);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightHeaderPanel.add(userLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        rightHeaderPanel.add(logoutButton);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 3. SIDEBAR PANEL (Left) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        sidebarPanel.setBackground(new Color(44, 62, 80));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));

        JLabel menuLabel = new JLabel("MAIN MENU");
        menuLabel.setForeground(new Color(149, 165, 166));
        menuLabel.setFont(new Font("Arial", Font.BOLD, 14));
        menuLabel.setPreferredSize(new Dimension(200, 40));
        sidebarPanel.add(menuLabel);

        buildSidebarMenu(sidebarPanel);
        add(sidebarPanel, BorderLayout.WEST);

        // --- 4. MAIN CONTENT AREA (Center) ---
        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBackground(new Color(245, 247, 250));

        buildDashboardContent();
        add(contentPanel, BorderLayout.CENTER);

        // --- 5. CUSTOM FOOTER ---
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY));
        footerPanel.setPreferredSize(new Dimension(0, 60));
        footerPanel.setLayout(new GridBagLayout());

        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>");
        footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14));
        footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Dynamically builds sidebar menu options based on the user's role.
     */
    private void buildSidebarMenu(JPanel sidebar) {
        sidebar.add(createMenuButton("🏠 Dashboard Home"));

        if (role.equals("Admin")) {
            sidebar.add(createMenuButton("➕ Add Member"));
            sidebar.add(createMenuButton("📋 View & Edit Records"));
            // CONNECTED THE NEW TRAINER VIEW LIST HERE:
            sidebar.add(createMenuButton("🏋️ Add Trainer"));
            sidebar.add(createMenuButton("📋 View & Edit Trainers"));
            sidebar.add(createMenuButton("🔑 Change Password"));
        } else if (role.equals("Trainer")) {
            sidebar.add(createMenuButton("📅 My Schedule"));
            sidebar.add(createMenuButton("👥 Assigned Members"));
            sidebar.add(createMenuButton("📝 Workout Plans"));
            sidebar.add(createMenuButton("🔑 Change Password"));
        } else if (role.equals("Member")) {
            sidebar.add(createMenuButton("👤 My Profile"));
            sidebar.add(createMenuButton("🎫 Membership Status"));
            sidebar.add(createMenuButton("📝 My Workout Plan"));
            sidebar.add(createMenuButton("🔑 Change Password"));
        }
    }

    /**
     * Creates stylish buttons for the sidebar and adds the link to other frames.
     */
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(230, 45));
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(0, 20, 0, 0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(52, 73, 94)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(44, 62, 80)); }
        });

        // --- NEW ROUTING LOGIC ---
        btn.addActionListener(e -> {
            if (text.contains("Add Member")) { new AddMemberFrame(role).setVisible(true); dispose(); }
            else if (text.contains("View & Edit Records")) { new ViewMembersFrame(role).setVisible(true); dispose(); }
            else if (text.contains("Add Trainer")) { new AddTrainerFrame(role).setVisible(true); dispose(); }
            else if (text.contains("View & Edit Trainers")) { new ViewTrainersFrame(role).setVisible(true); dispose(); } // Routes to the table
            else if (text.contains("Change Password")) { new ForgotPasswordFrame().setVisible(true); dispose(); }
            else if (text.contains("Dashboard Home")) { /* Do nothing, already here */ }
            else {
                JOptionPane.showMessageDialog(this, text + " module coming soon!", "Under Construction", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return btn;
    }

    /**
     * Helper method to fetch live row counts from the database.
     */
    private int getDatabaseCount(String tableName) {
        int count = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Could not fetch data for table: " + tableName);
        }
        return count;
    }

    /**
     * Dynamically populates dashboard statistics depending on the role.
     */
    private void buildDashboardContent() {
        JLabel welcomeLabel = new JLabel("Welcome back, " + role + "!");
        welcomeLabel.setBounds(50, 40, 500, 40);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        contentPanel.add(welcomeLabel);

        // Fetch Live Database Numbers
        String totalMembers = String.valueOf(getDatabaseCount("members"));
        String totalTrainers = String.valueOf(getDatabaseCount("trainers"));
        String totalUsers = String.valueOf(getDatabaseCount("users"));

        if (role.equals("Admin")) {
            contentPanel.add(createStatCard("Total Members", totalMembers, 50, 110, new Color(41, 128, 185)));
            contentPanel.add(createStatCard("Active Trainers", totalTrainers, 320, 110, new Color(39, 174, 96)));
            contentPanel.add(createStatCard("Total Accounts", totalUsers, 590, 110, new Color(142, 68, 173)));
            contentPanel.add(createStatCard("Pending Renewals", "14", 860, 110, new Color(243, 156, 18)));
        }
        else if (role.equals("Trainer")) {
            contentPanel.add(createStatCard("Gym Members", totalMembers, 50, 110, new Color(41, 128, 185)));
            contentPanel.add(createStatCard("Classes Today", "4", 320, 110, new Color(39, 174, 96)));
            contentPanel.add(createStatCard("Pending Plans", "3", 590, 110, new Color(243, 156, 18)));
        }
        else if (role.equals("Member")) {
            contentPanel.add(createStatCard("Membership Status", "ACTIVE", 50, 110, new Color(39, 174, 96)));
            contentPanel.add(createStatCard("Days Remaining", "142", 320, 110, new Color(41, 128, 185)));
            contentPanel.add(createStatCard("Assigned Trainer", "Assigned", 590, 110, new Color(142, 68, 173)));
        }
    }

    private JPanel createStatCard(String title, String value, int x, int y, Color bannerColor) {
        JPanel card = new JPanel();
        card.setBounds(x, y, 240, 130);
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        JPanel topStrip = new JPanel();
        topStrip.setBounds(0, 0, 240, 5);
        topStrip.setBackground(bannerColor);
        card.add(topStrip);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setBounds(20, 20, 200, 20);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.GRAY);
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setBounds(20, 50, 200, 50);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(new Color(44, 62, 80));
        card.add(valueLabel);

        return card;
    }
}