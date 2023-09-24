class Library {

    private Book[] books;
    private int bookCount;

    public Library() {
        books = new Book[10];
        bookCount = 0;
    }

    public void addBook(String title, String author) {
        if (bookCount < books.length) {
            books[bookCount] = new Book(title, author);
            bookCount++;
        } else {
            System.out.println("The library is full. Cannot add more books.");
        }
    }

    public Book getBook(String title) {
        for (Book book : books) {
            if (book != null && book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    public void borrowBook(Book book) {
        if (book.isBorrowed()) {
            System.out.println("The book is already borrowed.");
        } else {
            book.setBorrowed(true);
        }
    }

    public void returnBook(Book book) {
        if (!book.isBorrowed()) {
            System.out.println("The book is not currently borrowed.");
        } else {
            book.setBorrowed(false);
        }
    }

    public void displayBooks() {
        for (int i = 0; i < bookCount; i++) {
            Book book = books[i];
            System.out.println(book.getTitle() + " by " + book.getAuthor());
        }
    }
}

