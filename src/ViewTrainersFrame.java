import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URL;

public class ViewTrainersFrame extends JFrame {
    public ViewTrainersFrame(String role) {
        setTitle("Gym Management System - View Trainers");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
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

        JLabel headerTitle = new JLabel("📋 Trainer Management");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerTitle.setForeground(new Color(41, 128, 185));
        leftHeaderPanel.add(headerTitle);
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightHeaderPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("⬅ Back to Dashboard");
        backBtn.setBackground(new Color(149, 165, 166)); backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> { new DashboardFrame(role).setVisible(true); dispose(); });
        rightHeaderPanel.add(backBtn);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- DYNAMIC TABLE AREA ---
        JPanel tablePanel = new JPanel(new BorderLayout(0, 20));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        tablePanel.setBackground(new Color(245, 247, 250));

        String[] columns = {"ID", "Full Name", "Phone Number", "Specialization", "Experience", "Shift", "Salary (₹)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // --- DATABASE FETCH ---
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trainers")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getString("specialization"),
                        rs.getInt("experience_years") + " Yrs",
                        rs.getString("shift_timing"),
                        rs.getDouble("monthly_salary")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);
        table.setRowHeight(35); table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(41, 128, 185)); table.setSelectionForeground(Color.WHITE);

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- ACTION BUTTONS (ADD, UPDATE, DELETE) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionPanel.setOpaque(false);

        JButton addBtn = new JButton("➕ Add New Trainer");
        addBtn.setBackground(new Color(39, 174, 96)); addBtn.setForeground(Color.WHITE);
        addBtn.setPreferredSize(new Dimension(220, 50)); addBtn.setFont(new Font("Arial", Font.BOLD, 15));

        JButton updateBtn = new JButton("✏️ Update Selected");
        updateBtn.setBackground(new Color(243, 156, 18)); updateBtn.setForeground(Color.WHITE);
        updateBtn.setPreferredSize(new Dimension(220, 50)); updateBtn.setFont(new Font("Arial", Font.BOLD, 15));

        JButton deleteBtn = new JButton("❌ Delete Selected");
        deleteBtn.setBackground(new Color(231, 76, 60)); deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setPreferredSize(new Dimension(220, 50)); deleteBtn.setFont(new Font("Arial", Font.BOLD, 15));

        addBtn.addActionListener(e -> {
            new AddTrainerFrame(role).setVisible(true);
            dispose();
        });

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String phone = table.getValueAt(row, 2).toString();
                new UpdateTrainerFrame(role, phone).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row first!");
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String phone = table.getValueAt(row, 2).toString();
                new DeleteTrainerFrame(role, phone).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row first!");
            }
        });

        actionPanel.add(addBtn); actionPanel.add(updateBtn); actionPanel.add(deleteBtn);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout());
        footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY)); footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>");
        footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);
    }
}