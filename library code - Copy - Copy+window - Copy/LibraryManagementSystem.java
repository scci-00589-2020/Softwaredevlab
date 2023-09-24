import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LibraryManagementSystem {
    private static final String CREDENTIALS_FILE = "credentials.txt";
    private static Map<String, String> userCredentials = new HashMap<>();

    public static void main(String[] args) {
        loadCredentials();

        Scanner scanner = new Scanner(System.in);

        // Login or create a new account
        User user = loginOrCreateAccount(scanner);

        if (user == null) {
            // User failed to log in or create an account, exit the program
            return;
        }

        // Create a new library
        Library library = new Library();

        // Add books to the library
        library.addBook("The Lord of the Rings", "J.R.R. Tolkien");
        library.addBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling");
        library.addBook("The Hunger Games", "Suzanne Collins");

        // Display the list of books in the library
        library.displayBooks();

        // Get the user to select a book
        System.out.println("Which book would you like to borrow or return?");
        String bookTitle = scanner.nextLine();

        // Check if the book is available
        Book book = library.getBook(bookTitle);
        if (book == null) {
            System.out.println("That book is not available.");
        } else {
            // Borrow the book if it is not already borrowed
            if (!book.isBorrowed()) {
                System.out.println("Would you like to borrow the book (B) or return the book (R)?");
                String action = scanner.nextLine();
                if (action.equalsIgnoreCase("B")) {
                    library.borrowBook(book);
                    System.out.println("You have successfully borrowed the book " + book.getTitle());
                } else if (action.equalsIgnoreCase("R")) {
                    library.returnBook(book);
                    System.out.println("You have successfully returned the book " + book.getTitle());
                } else {
                    System.out.println("Invalid action. Please try again.");
                }
            } else {
                System.out.println("The book is already borrowed.");
            }
        }

        saveCredentials();
    }

    private static User loginOrCreateAccount(Scanner scanner) {
        System.out.println("Welcome to the Library Management System!");

        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Create a new account");
            System.out.println("0. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.println("Enter your username:");
                    String username = scanner.nextLine();

                    System.out.println("Enter your password:");
                    String password = scanner.nextLine();

                    if (authenticateUser(username, password)) {
                        System.out.println("Login successful!");
                        return new User(username);
                    } else {
                        System.out.println("Invalid username or password. Please try again.");
                    }
                    break;
                case 2:
                    System.out.println("Enter a username for your new account:");
                    String newUsername = scanner.nextLine();

                    System.out.println("Enter a password for your new account:");
                    String newPassword = scanner.nextLine();

                    if (createAccount(newUsername, newPassword)) {
                        System.out.println("Account created successfully!");
                        return new User(newUsername);
                    } else {
                        System.out.println("Failed to create the account. Please try again.");
                    }
                    break;
                case 0:
                    System.out.println("Exiting the program...");
                    return null;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static boolean authenticateUser(String username, String password) {
        // Check if the provided username exists in the userCredentials map
        if (userCredentials.containsKey(username)) {
            // Compare the provided password with the stored password for the username
            String storedPassword = userCredentials.get(username);
            return storedPassword.equals(password);
        }

        return false; // User not found
    }

    private static boolean createAccount(String username, String password) {
        if (!userCredentials.containsKey(username)) {
            userCredentials.put(username, password);
            return true;
        }
        return false; // Account with the same username already exists
    }

    private static void loadCredentials() {
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

    private static void saveCredentials() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving credentials: " + e.getMessage());
        }
    }
}

class User {
    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
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
        this.borrowed = false;
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

