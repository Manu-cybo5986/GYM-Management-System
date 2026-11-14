import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL;

public class UpdateTrainerFrame extends JFrame {
    private JTextField searchField, nameField, experienceField, salaryField;
    private JComboBox<String> specializationBox, shiftBox;
    private JButton searchButton, updateButton, backButton;

    public UpdateTrainerFrame(String role, String searchPhone) {
        setTitle("Gym Management System - Update Trainer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)); leftHeaderPanel.setBackground(Color.WHITE);
        URL imgURL = getClass().getResource("/poornim logo2.jpeg");
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(150, 30, Image.SCALE_SMOOTH);
            leftHeaderPanel.add(new JLabel(new ImageIcon(img)));
        }
        JLabel headerTitle = new JLabel("✏️ Update Trainer Record"); headerTitle.setFont(new Font("Arial", Font.BOLD, 24)); headerTitle.setForeground(new Color(41, 128, 185));
        leftHeaderPanel.add(headerTitle); headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15)); rightHeaderPanel.setBackground(Color.WHITE);
        backButton = new JButton("⬅ Back"); backButton.setBackground(new Color(149, 165, 166)); backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> { new ViewTrainersFrame(role).setVisible(true); dispose(); });
        rightHeaderPanel.add(backButton); headerPanel.add(rightHeaderPanel, BorderLayout.EAST); add(headerPanel, BorderLayout.NORTH);

        // --- FORM ---
        JPanel centerPanel = new JPanel(new GridBagLayout()); centerPanel.setBackground(new Color(245, 247, 250));
        JPanel formCard = new JPanel(new GridBagLayout()); formCard.setBackground(Color.WHITE); formCard.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 20); Font fieldFont = new Font("Arial", Font.PLAIN, 18); Dimension fieldSize = new Dimension(400, 45);

        gbc.gridwidth = 1; gbc.insets = new Insets(10, 20, 20, 20);
        formCard.add(createLabel("Search Trainer Phone:", labelFont), setGbc(gbc, 0, 0));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchField = createTextField(new Dimension(250, 45), fieldFont);
        searchButton = new JButton("Search"); searchButton.setBackground(new Color(52, 152, 219)); searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchField, BorderLayout.CENTER); searchPanel.add(searchButton, BorderLayout.EAST);
        formCard.add(searchPanel, setGbc(gbc, 1, 0));

        gbc.insets = new Insets(15, 20, 15, 20);
        formCard.add(createLabel("Full Name:", labelFont), setGbc(gbc, 0, 1)); nameField = createTextField(fieldSize, fieldFont); formCard.add(nameField, setGbc(gbc, 1, 1));

        formCard.add(createLabel("Specialization:", labelFont), setGbc(gbc, 0, 2));
        String[] specializations = {"Bodybuilding & Powerlifting", "Cardio & Fat Loss", "Yoga & Flexibility", "CrossFit", "General Fitness"};
        specializationBox = new JComboBox<>(specializations); specializationBox.setPreferredSize(fieldSize); specializationBox.setFont(fieldFont); formCard.add(specializationBox, setGbc(gbc, 1, 2));

        formCard.add(createLabel("Experience (Years):", labelFont), setGbc(gbc, 0, 3)); experienceField = createTextField(fieldSize, fieldFont); formCard.add(experienceField, setGbc(gbc, 1, 3));

        formCard.add(createLabel("Shift Timing:", labelFont), setGbc(gbc, 0, 4));
        String[] shifts = {"Morning (6 AM - 2 PM)", "Evening (2 PM - 10 PM)", "Full Day (Split Shift)"};
        shiftBox = new JComboBox<>(shifts); shiftBox.setPreferredSize(fieldSize); shiftBox.setFont(fieldFont); formCard.add(shiftBox, setGbc(gbc, 1, 4));

        formCard.add(createLabel("Monthly Salary (₹):", labelFont), setGbc(gbc, 0, 5)); salaryField = createTextField(fieldSize, fieldFont); formCard.add(salaryField, setGbc(gbc, 1, 5));

        updateButton = new JButton("🔄 Update Trainer"); updateButton.setBackground(new Color(243, 156, 18)); updateButton.setForeground(Color.WHITE); updateButton.setPreferredSize(new Dimension(400, 50));
        formCard.add(new JLabel(""), setGbc(gbc, 0, 6)); formCard.add(updateButton, setGbc(gbc, 1, 6));

        centerPanel.add(formCard); add(centerPanel, BorderLayout.CENTER);

        // Locks fields until a valid search happens
        setFieldsEnabled(false);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout()); footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY)); footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>"); footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);

        // --- DATABASE ACTIONS ---
        searchButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM trainers WHERE phone_number = ?");
                pstmt.setString(1, searchField.getText());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("full_name"));
                    specializationBox.setSelectedItem(rs.getString("specialization"));
                    experienceField.setText(String.valueOf(rs.getInt("experience_years")));
                    shiftBox.setSelectedItem(rs.getString("shift_timing"));
                    salaryField.setText(String.valueOf(rs.getDouble("monthly_salary")));
                    setFieldsEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "No trainer found with this phone number!"); setFieldsEnabled(false);
                }
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage()); }
        });

        updateButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE trainers SET full_name = ?, specialization = ?, experience_years = ?, shift_timing = ?, monthly_salary = ? WHERE phone_number = ?");
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, specializationBox.getSelectedItem().toString());
                pstmt.setInt(3, Integer.parseInt(experienceField.getText()));
                pstmt.setString(4, shiftBox.getSelectedItem().toString());
                pstmt.setDouble(5, Double.parseDouble(salaryField.getText()));
                pstmt.setString(6, searchField.getText());

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Trainer Record Updated Successfully!");
                new ViewTrainersFrame(role).setVisible(true); dispose();
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage()); }
        });

        if (searchPhone != null && !searchPhone.isEmpty()) { searchField.setText(searchPhone); searchButton.doClick(); }
    }

    private void setFieldsEnabled(boolean e) { nameField.setEnabled(e); specializationBox.setEnabled(e); experienceField.setEnabled(e); shiftBox.setEnabled(e); salaryField.setEnabled(e); updateButton.setEnabled(e); }
    private JLabel createLabel(String t, Font f) { JLabel l = new JLabel(t); l.setFont(f); return l; }
    private JTextField createTextField(Dimension s, Font f) { JTextField t = new JTextField(); t.setPreferredSize(s); t.setFont(f); return t; }
    private GridBagConstraints setGbc(GridBagConstraints g, int x, int y) { g.gridx = x; g.gridy = y; return g; }
}