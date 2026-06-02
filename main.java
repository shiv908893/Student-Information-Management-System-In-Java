import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.*; 
import java.util.regex.Pattern; 
public class StudentManagementSystem extends JFrame { 
    private static final String DB_NAME = "student_db"; 
    private static final String BASE_DB_URL = 
"jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
; 
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"; 
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "root"; // Change as per your MySQL configuration 
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver"; 
    private JPanel formPanel, buttonPanel, tablePanel; 
    private JTextField studentIdField, rollNoField, firstNameField, lastNameField, emailField, 
phoneField, courseField; 
    private JTable studentsTable; 
    private DefaultTableModel tableModel; 
    private JComboBox<String> searchTypeCombo; 
    private JTextField searchField; 
    private Connection connection; 
    public StudentManagementSystem() { 
        try { 
            Class.forName(DRIVER_CLASS); 
            initializeDatabase(); 
            setTitle("Student Information Management System"); 
            setSize(1200, 700); 
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            setLocationRelativeTo(null); 
            createGUI(); 
            loadAllStudents(); 
        } catch (ClassNotFoundException e) { 
            showError("Database Driver Error", 
                    "MySQL JDBC Driver not found. Please ensure mysql-connector-java-*.jar is in your 
classpath.\nError: " 
                            + e.getMessage()); 
            e.printStackTrace(); 
        } 
    } 
    private void initializeDatabase() { 
        try { 
 
            Connection conn = DriverManager.getConnection(BASE_DB_URL, DB_USER, 
DB_PASSWORD); 
            Statement stmt = conn.createStatement(); 
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME); 
            stmt.close(); 
            conn.close(); 
            connection = getConnection(); 
            Statement tableStmt = connection.createStatement(); 
            String createTableSql = "CREATE TABLE IF NOT EXISTS students (" 
                    + "student_id INT AUTO_INCREMENT PRIMARY KEY, " 
                    + "roll_no VARCHAR(50) UNIQUE NOT NULL, " 
                    + "first_name VARCHAR(50) NOT NULL, " 
                    + "last_name VARCHAR(50) NOT NULL, " 
                    + "email VARCHAR(100), " 
                    + "phone VARCHAR(20), " 
                    + "course VARCHAR(50), " 
                    + "enrollment_date DATE)"; 
            tableStmt.executeUpdate(createTableSql); 
            tableStmt.close(); 
        } catch (SQLException e) { 
            System.err.println("Database Initialization Error: " + e.getMessage()); 
            e.printStackTrace(); 
            showError("Database Initialization Error", "Could not initialize database: " + e.getMessage()); 
        } finally { 
            closeConnection(); 
        } 
    } 
    private void createGUI() { 
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); 
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        formPanel = createFormPanel(); 
        mainPanel.add(formPanel, BorderLayout.NORTH); 
        buttonPanel = createButtonPanel(); 
        mainPanel.add(buttonPanel, BorderLayout.WEST); 
        tablePanel = createTablePanel(); 
        mainPanel.add(tablePanel, BorderLayout.CENTER); 
        JPanel searchPanel = createSearchPanel(); 
        mainPanel.add(searchPanel, BorderLayout.SOUTH); 
        this.add(mainPanel); 
        this.setVisible(true); 
    } 
    private JPanel createFormPanel() { 
        JPanel panel = new JPanel(new GridBagLayout()); 
        panel.setBorder(BorderFactory.createTitledBorder("Student Information Form")); 
        panel.setBackground(new Color(240, 240, 240)); 
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.anchor = GridBagConstraints.WEST; 
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        panel.add(new JLabel("ID:"), gbc); 
        gbc.gridx = 1; 
        studentIdField = new JTextField(15); 
        studentIdField.setEditable(false); 
        panel.add(studentIdField, gbc); 
        gbc.gridx = 2; 
        panel.add(new JLabel("Roll No:"), gbc); 
        gbc.gridx = 3; 
        rollNoField = new JTextField(15); 
        panel.add(rollNoField, gbc); 
        gbc.gridx = 0; 
        gbc.gridy = 1; 
        panel.add(new JLabel("First Name:"), gbc); 
        gbc.gridx = 1; 
        firstNameField = new JTextField(15); 
        panel.add(firstNameField, gbc); 
        gbc.gridx = 2; 
        panel.add(new JLabel("Last Name:"), gbc); 
        gbc.gridx = 3; 
        lastNameField = new JTextField(15); 
        panel.add(lastNameField, gbc); 
        gbc.gridx = 0; 
        gbc.gridy = 2; 
        panel.add(new JLabel("Email:"), gbc); 
        gbc.gridx = 1; 
        emailField = new JTextField(15); 
        panel.add(emailField, gbc); 
        gbc.gridx = 2; 
        panel.add(new JLabel("Phone:"), gbc); 
        gbc.gridx = 3; 
        phoneField = new JTextField(15); 
        panel.add(phoneField, gbc); 
        gbc.gridx = 0; 
        gbc.gridy = 3; 
        panel.add(new JLabel("Course:"), gbc); 
        gbc.gridx = 1; 
        gbc.gridwidth = 3; 
        courseField = new JTextField(15); 
        panel.add(courseField, gbc); 
        return panel; 
    } 
    private JPanel createButtonPanel() { 
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10)); 
        panel.setBorder(BorderFactory.createTitledBorder("Operations")); 
        panel.setBackground(new Color(240, 240, 240)); 
        JButton createBtn = new JButton("Add Student"); 
        createBtn.setBackground(new Color(76, 175, 80)); 
        createBtn.setForeground(Color.WHITE); 
        createBtn.setFont(new Font("Arial", Font.BOLD, 12)); 
        createBtn.addActionListener(new java.awt.event.ActionListener() { 
            public void actionPerformed(java.awt.event.ActionEvent e) { 
                createStudent(); 
            } 
        }); 
        panel.add(createBtn); 
        JButton updateBtn = new JButton("Update Student"); 
        updateBtn.setBackground(new Color(33, 150, 243)); 
        updateBtn.setForeground(Color.WHITE); 
        updateBtn.setFont(new Font("Arial", Font.BOLD, 12)); 
        updateBtn.addActionListener(new java.awt.event.ActionListener() { 
            public void actionPerformed(java.awt.event.ActionEvent e) { 
                updateStudent(); 
            } 
        }); 
        panel.add(updateBtn); 
        JButton deleteBtn = new JButton("Delete Student"); 
        deleteBtn.setBackground(new Color(244, 67, 54)); 
        deleteBtn.setForeground(Color.WHITE); 
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 12)); 
        deleteBtn.addActionListener(new java.awt.event.ActionListener() { 
            public void actionPerformed(java.awt.event.ActionEvent e) { 
                deleteStudent(); 
            } 
        }); 
        panel.add(deleteBtn); 
        JButton clearBtn = new JButton("Clear Form"); 
        clearBtn.setBackground(new Color(255, 152, 0)); 
        clearBtn.setForeground(Color.WHITE); 
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12)); 
        clearBtn.addActionListener(new java.awt.event.ActionListener() { 
            public void actionPerformed(java.awt.event.ActionEvent e) { 
                clearForm(); 
            } 
        }); 
        panel.add(clearBtn); 
        JButton loadBtn = new JButton("Load All Records"); 
        loadBtn.setBackground(new Color(156, 39, 176)); 
        loadBtn.setForeground(Color.WHITE); 
        loadBtn.setFont(new Font("Arial", Font.BOLD, 12)); 
        loadBtn.addActionListener(new java.awt.event.ActionListener() { 
            public void actionPerformed(java.awt.event.ActionEvent e) { 
 
                loadAllStudents(); 
            } 
        }); 
        panel.add(loadBtn); 
        return panel; 
    } 
    private JPanel createTablePanel() { 
        JPanel panel = new JPanel(new BorderLayout()); 
        panel.setBorder(BorderFactory.createTitledBorder("Student Records")); 
        panel.setBackground(new Color(240, 240, 240)); 
        String[] columnNames = { "ID", "Roll No", "First Name", "Last Name", "Email", "Phone", 
"Course", 
                "Enrollment Date" }; 
        tableModel = new DefaultTableModel(columnNames, 0); 
        studentsTable = new JTable(tableModel); 
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        studentsTable.setFont(new Font("Arial", Font.PLAIN, 11)); 
        studentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); 
        studentsTable.setRowHeight(25); 
        studentsTable.getSelectionModel().addListSelectionListener(new 
javax.swing.event.ListSelectionListener() { 
            public void valueChanged(javax.swing.event.ListSelectionEvent e) { 
                if (!e.getValueIsAdjusting() && studentsTable.getSelectedRow() != -1) { 
                    loadSelectedRowData(); 
                } 
            } 
        }); 
        JScrollPane scrollPane = new JScrollPane(studentsTable); 
        panel.add(scrollPane, BorderLayout.CENTER); 
        return panel; 
    } 
    private JPanel createSearchPanel() { 
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        panel.setBorder(BorderFactory.createTitledBorder("Search Records")); 
        panel.setBackground(new Color(240, 240, 240)); 
        panel.add(new JLabel("Search by:")); 
        searchTypeCombo = new JComboBox<>(new String[] { "Roll Number", "ID" }); 
        panel.add(searchTypeCombo); 
        panel.add(new JLabel("Search Value:")); 
        searchField = new JTextField(15); 
        panel.add(searchField); 
        JButton searchBtn = new JButton("Search"); 
        searchBtn.setBackground(new Color(63, 81, 181)); 
        searchBtn.setForeground(Color.WHITE); 
        searchBtn.setFont(new Font("Arial", Font.BOLD, 11)); 
        searchBtn.addActionListener(new java.awt.event.ActionListener() { 
            public void actionPerformed(java.awt.event.ActionEvent e) { 
 
                searchStudents(); 
            } 
        }); 
        panel.add(searchBtn); 
        return panel; 
    } 
    private boolean validateForm() { 
        if (rollNoField.getText().trim().isEmpty()) { 
            showError("Validation Error", "Roll Number cannot be empty!"); 
            return false; 
        } 
        if (firstNameField.getText().trim().isEmpty()) { 
            showError("Validation Error", "First Name cannot be empty!"); 
            return false; 
        } 
        if (lastNameField.getText().trim().isEmpty()) { 
            showError("Validation Error", "Last Name cannot be empty!"); 
            return false; 
        } 
        if (!emailField.getText().trim().isEmpty() && !isValidEmail(emailField.getText().trim())) { 
            showError("Validation Error", "Please enter a valid email address!"); 
            return false; 
        } 
        if (!phoneField.getText().trim().isEmpty() && !isValidPhone(phoneField.getText().trim())) { 
            showError("Validation Error", "Phone number must contain only digits (10-15 characters)!"); 
            return false; 
        } 
        return true; 
    } 
    private boolean isValidEmail(String email) { 
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"; 
        return Pattern.matches(emailRegex, email); 
    } 
    private boolean isValidPhone(String phone) { 
        return phone.matches("\\d{10,15}"); 
    } 
    private void createStudent() { 
        if (!validateForm()) { 
            return; 
        } 
        String sql = "INSERT INTO students (roll_no, first_name, last_name, email, phone, course, 
enrollment_date) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
        try { 
            connection = getConnection(); 
            PreparedStatement statement = connection.prepareStatement(sql); 
            statement.setString(1, rollNoField.getText().trim()); 
            statement.setString(2, firstNameField.getText().trim());  
            statement.setString(3, lastNameField.getText().trim()); 
            statement.setString(4, emailField.getText().trim()); 
            statement.setString(5, phoneField.getText().trim()); 
            statement.setString(6, courseField.getText().trim()); 
            statement.setDate(7, new java.sql.Date(System.currentTimeMillis())); 
            int rowsInserted = statement.executeUpdate(); 
            if (rowsInserted > 0) { 
                showSuccess("Student Created", "Student record added successfully!"); 
                clearForm(); 
                loadAllStudents(); 
            } 
            statement.close(); 
        } catch (SQLException e) { 
            if (e.getMessage().contains("Duplicate entry")) { 
                showError("Database Error", "Roll Number already exists! Please use a unique Roll 
Number."); 
            } else { 
                showError("Database Error", "Failed to create student: " + e.getMessage()); 
            } 
            e.printStackTrace(); 
        } finally { 
            closeConnection(); 
        } 
    } 
    private void updateStudent() { 
        if (studentIdField.getText().trim().isEmpty()) { 
            showError("Validation Error", "Please select a student to update!"); 
            return; 
        } 
        if (!validateForm()) { 
            return; 
        } 
        String sql = "UPDATE students SET roll_no=?, first_name=?, last_name=?, email=?, phone=?, 
course=? WHERE student_id=?"; 
        try { 
            connection = getConnection(); 
            PreparedStatement statement = connection.prepareStatement(sql); 
            statement.setString(1, rollNoField.getText().trim()); 
            statement.setString(2, firstNameField.getText().trim()); 
            statement.setString(3, lastNameField.getText().trim()); 
            statement.setString(4, emailField.getText().trim()); 
            statement.setString(5, phoneField.getText().trim()); 
            statement.setString(6, courseField.getText().trim()); 
            statement.setInt(7, Integer.parseInt(studentIdField.getText().trim())); 
            int rowsUpdated = statement.executeUpdate(); 
            if (rowsUpdated > 0) { 
                showSuccess("Student Updated", "Student record updated successfully!"); 
                clearForm(); 
                loadAllStudents(); 
            } else { 
                showError("Update Failed", "No student found with the given ID!"); 
            } 
            statement.close(); 
        } catch (SQLException e) { 
            if (e.getMessage().contains("Duplicate entry")) { 
                showError("Database Error", "Roll Number already exists!"); 
            } else { 
                showError("Database Error", "Failed to update student: " + e.getMessage()); 
            } 
            e.printStackTrace(); 
        } finally { 
            closeConnection(); 
        } 
    } 
    private void deleteStudent() { 
        if (studentIdField.getText().trim().isEmpty()) { 
            showError("Validation Error", "Please select a student to delete!"); 
            return; 
        } 
        int confirm = JOptionPane.showConfirmDialog( 
                this, 
                "Are you sure you want to delete this student record? This action cannot be undone.", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE); 
        if (confirm != JOptionPane.YES_OPTION) { 
            return; 
        } 
        String sql = "DELETE FROM students WHERE student_id=?"; 
        try { 
            connection = getConnection(); 
            PreparedStatement statement = connection.prepareStatement(sql); 
            statement.setInt(1, Integer.parseInt(studentIdField.getText().trim())); 
            int rowsDeleted = statement.executeUpdate(); 
            if (rowsDeleted > 0) { 
                showSuccess("Student Deleted", "Student record deleted successfully!"); 
                clearForm(); 
                loadAllStudents(); 
            } else { 
                showError("Delete Failed", "No student found with the given ID!"); 
            } 
            statement.close(); 
        } catch (SQLException e) { 
            showError("Database Error", "Failed to delete student: " + e.getMessage()); 

            e.printStackTrace(); 
        } finally { 
            closeConnection(); 
        } 
    } 
    private void loadAllStudents() { 
        String sql = "SELECT * FROM students"; 
        try { 
            connection = getConnection(); 
            PreparedStatement statement = connection.prepareStatement(sql); 
            ResultSet resultSet = statement.executeQuery(); 
            tableModel.setRowCount(0); 
            while (resultSet.next()) { 
                Object[] row = { 
                        resultSet.getInt("student_id"), 
                        resultSet.getString("roll_no"), 
                        resultSet.getString("first_name"), 
                        resultSet.getString("last_name"), 
                        resultSet.getString("email"), 
                        resultSet.getString("phone"), 
                        resultSet.getString("course"), 
                        resultSet.getDate("enrollment_date") 
                }; 
                tableModel.addRow(row); 
            } 
            resultSet.close(); 
            statement.close(); 
        } catch (SQLException e) { 
            showError("Database Error", "Failed to load students: " + e.getMessage()); 
            e.printStackTrace(); 
        } finally { 
            closeConnection(); 
        } 
    } 
    private void searchStudents() { 
        String searchValue = searchField.getText().trim(); 
        if (searchValue.isEmpty()) { 
            showError("Validation Error", "Please enter a search value!"); 
            return; 
        } 
        String sql = ""; 
        if (searchTypeCombo.getSelectedItem().equals("Roll Number")) { 
            sql = "SELECT * FROM students WHERE roll_no LIKE ?"; 
        } else { 
            sql = "SELECT * FROM students WHERE student_id = ?"; 
        } 
        try { 
            connection = getConnection(); 
            PreparedStatement statement = connection.prepareStatement(sql); 
            if (searchTypeCombo.getSelectedItem().equals("Roll Number")) { 
                statement.setString(1, "%" + searchValue + "%"); 
            } else { 
                statement.setInt(1, Integer.parseInt(searchValue)); 
            } 
            tableModel.setRowCount(0); 
            ResultSet resultSet = statement.executeQuery(); 
            boolean found = false; 
            while (resultSet.next()) { 
                found = true; 
                Object[] row = { 
                        resultSet.getInt("student_id"), 
                        resultSet.getString("roll_no"), 
                        resultSet.getString("first_name"), 
                        resultSet.getString("last_name"), 
                        resultSet.getString("email"), 
                        resultSet.getString("phone"), 
                        resultSet.getString("course"), 
                        resultSet.getDate("enrollment_date") 
                }; 
                tableModel.addRow(row); 
            } 
            if (!found) { 
                showInfo("Search Result", "No records found matching your search criteria."); 
            } 
            resultSet.close(); 
            statement.close(); 
        } catch (NumberFormatException e) { 
            showError("Validation Error", "Please enter a valid ID (numeric value)!"); 
        } catch (SQLException e) { 
            showError("Database Error", "Failed to search students: " + e.getMessage()); 
            e.printStackTrace(); 
        } finally { 
            closeConnection(); 
        } 
    } 
    private void loadSelectedRowData() { 
        int selectedRow = studentsTable.getSelectedRow(); 
        if (selectedRow >= 0) { 
            studentIdField.setText(tableModel.getValueAt(selectedRow, 0).toString()); 
            rollNoField.setText(tableModel.getValueAt(selectedRow, 1).toString()); 
            firstNameField.setText(tableModel.getValueAt(selectedRow, 2).toString()); 
            lastNameField.setText(tableModel.getValueAt(selectedRow, 3).toString()); 
            emailField.setText(tableModel.getValueAt(selectedRow, 4).toString()); 
            phoneField.setText(tableModel.getValueAt(selectedRow, 5).toString()); 
            courseField.setText(tableModel.getValueAt(selectedRow, 6).toString()); 
        } 
    } 
    private void clearForm() { 
        studentIdField.setText(""); 
        rollNoField.setText(""); 
        firstNameField.setText(""); 
        lastNameField.setText(""); 
        emailField.setText(""); 
        phoneField.setText(""); 
        courseField.setText(""); 
        studentsTable.clearSelection(); 
    } 
    private Connection getConnection() throws SQLException { 
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); 
    } 
    private void closeConnection() { 
        try { 
            if (connection != null && !connection.isClosed()) { 
                connection.close(); 
            } 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
    } 
    private void showError(String title, String message) { 
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE); 
    } 
    private void showSuccess(String title, String message) { 
        JOptionPane.showMessageDialog(this, message, title, 
JOptionPane.INFORMATION_MESSAGE); 
    } 
    private void showInfo(String title, String message) { 
        JOptionPane.showMessageDialog(this, message, title, 
JOptionPane.INFORMATION_MESSAGE); 
    } 
    public void windowShown() { 
        this.setVisible(true); 
    } 
    public void windowHidden() { 
        this.setVisible(false); 
    } 
    public void cleanup() { 
        closeConnection(); 
        this.dispose(); 
    } 
    public static void main(String[] args) { 
        SwingUtilities.invokeLater(new Runnable() { 
            public void run() { 
                new StudentManagementSystem(); 
            } 
        }); 
    } 
} 
