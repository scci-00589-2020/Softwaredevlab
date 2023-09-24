import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LibraryManagementSystemGUI extends JFrame {
    private static final String CREDENTIALS_FILE = "credentials.txt";
    private static Map<String, String> userCredentials = new HashMap<>();

    private JTextField usernameField;
    private JPasswordField passwordField;

    private Library library;

    public LibraryManagementSystemGUI() {
        setTitle("Library Management System");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();

        loadCredentials();

        setVisible(true);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(new CreateAccountButtonListener());

        mainPanel.add(usernameLabel);
        mainPanel.add(usernameField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(loginButton);
        mainPanel.add(createAccountButton);

        add(mainPanel);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, password)) {
                dispose();
                showLibraryManagementSystem();
            } else {
                JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                        "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CreateAccountButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (createAccount(username, password)) {
                JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                        "Account created successfully!", "Account Creation", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                        "Account creation failed. Username already exists.", "Account Creation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean authenticateUser(String username, String password) {
        // Check if the provided username exists in the userCredentials map
        if (userCredentials.containsKey(username)) {
            // Compare the provided password with the stored password for the username
            String storedPassword = userCredentials.get(username);
            return storedPassword.equals(password);
        }
        return false; // User not found
    }

    private boolean createAccount(String username, String password) {
        if (!userCredentials.containsKey(username)) {
            userCredentials.put(username, password);
            saveCredentials();
            return true;
        }
        return false; // Account with the same username already exists
    }

    private void showLibraryManagementSystem() {
        library = new Library();

        // Add books to the library
        library.addBook("The Innocent", "David Baldacci");
        library.addBook("Lock Every Door", "Riley Sager");
        library.addBook("The Silent Patient", "Alex Michaelides");
        library.addBook("The Lord of the Rings", "J.R.R. Tolkien");
        library.addBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling");
        library.addBook("The Hunger Games", "Suzanne Collins");

        // Display the list of books in the library
        library.displayBooks();

        // Get the user to select a book
        String bookTitle = JOptionPane.showInputDialog(LibraryManagementSystemGUI.this,
                "Which book would you like to borrow or return?", "Book Selection", JOptionPane.PLAIN_MESSAGE);

        // Check if the book is available
        Book book = library.getBook(bookTitle);
        if (book == null) {
            JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                    "That book is not available.", "Book Not Found", JOptionPane.ERROR_MESSAGE);
        } else {
            // Borrow the book if it is not already borrowed
            if (!book.isBorrowed()) {
                String[] options = {"Borrow", "Return"};
                int choice = JOptionPane.showOptionDialog(LibraryManagementSystemGUI.this,
                        "Would you like to borrow or return the book?", "Action Selection",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    library.borrowBook(book);
                    JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                            "You have successfully borrowed the book " + book.getTitle(), "Borrow Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (choice == 1) {
                    library.returnBook(book);
                    JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                            "You have successfully returned the book " + book.getTitle(), "Return Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                            "Invalid action. Please try again.", "Invalid Action", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(LibraryManagementSystemGUI.this,
                        "The book is already borrowed.", "Book Unavailable", JOptionPane.ERROR_MESSAGE);
            }
        }

        saveCredentials();
    }

    private void loadCredentials() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
                    userCredentials.put(username, password);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading credentials: " + e.getMessage());
        }
    }

    private void saveCredentials() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving credentials: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystemGUI::new);
    }
}

class Library {
    private Map<String, Book> books = new HashMap<>();

    public void addBook(String title, String author) {
        Book book = new Book(title, author);
        books.put(title, book);
    }

    public void displayBooks() {
        System.out.println("Books available in the library:");
        for (Book book : books.values()) {
            System.out.println(book.getTitle() + " by " + book.getAuthor());
        }
    }

    public Book getBook(String title) {
        return books.get(title);
    }

    public void borrowBook(Book book) {
        book.setBorrowed(true);
    }

    public void returnBook(Book book) {
        book.setBorrowed(false);
    }
}

class Book {
    private String title;
    private String author;
    private boolean borrowed;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }
}
