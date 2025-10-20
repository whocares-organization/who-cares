package domain;

/**
 * Represents a book in the library system.
 * 
 * <p>Stores information about the book's title, author, ISBN, and whether it is currently borrowed.
 * Provides methods to access and modify this information.
 * </p>
 */
public class Book {

  private String title;
  private String author;
  private String isbn;
  private boolean isBorrowed;

  public Book(String title, String author, String isbn) {
	super();
	this.title = title;
	this.author = author;
	this.isbn = isbn;
}

  /**
   * Default constructor. Initializes an empty book object.
   */
  public Book() {
    super();
  }

  /**
   * Constructs a Book with the specified details.
   *
   * @param title      the title of the book
   * @param author     the author of the book
   * @param isbn       the ISBN of the book
   * @param isBorrowed true if the book is currently borrowed, false otherwise
   */
  public Book(String title, String author, String isbn, boolean isBorrowed) {
    super();
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.isBorrowed = isBorrowed;
  }

  /**
   * Returns the title of the book.
   *
   * @return the book title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the title of the book.
   *
   * @param title the new title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the author of the book.
   *
   * @return the author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Updates the author of the book.
   *
   * @param author the new author
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Returns the ISBN of the book.
   *
   * @return the ISBN
   */
  public String getIsbn() {
    return isbn;
  }

  /**
   * Updates the ISBN of the book.
   *
   * @param isbn the new ISBN
   */
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  /**
   * Returns whether the book is currently borrowed.
   *
   * @return true if borrowed, false otherwise
   */
  public boolean isBorrowed() {
    return isBorrowed;
  }

  /**
   * Updates the borrowed status of the book.
   *
   * @param borrowed true if the book is borrowed, false otherwise
   */
  public void setBorrowed(boolean borrowed) {
    isBorrowed = borrowed;
  }

  /**
   * Returns a string representation of the book object.
   *
   * @return a string containing the title, author, and ISBN
   */
  @Override
  public String toString() {
    return "Book{" 
        + "title='" + title + '\'' 
        + ", author='" + author + '\'' 
        + ", isbn='" + isbn + '\'' 
        + '}';
  }
}
