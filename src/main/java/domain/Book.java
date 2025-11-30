package domain;

import java.time.LocalDate;

/**
 * Represents a book in the library system.
 * 
 * <p>Stores information about the book's title, author, and ISBN, and whether it is currently borrowed.
 * Provides methods to access and modify this information.</p>
 */
public class Book extends Media {

  private String author;
  private String isbn;

  /**
   * Constructs a Book with the specified details.
   *
   * @param title the title of the book
   * @param author the author of the book
   * @param isbn the ISBN of the book
   */
  public Book(String title, String author, String isbn) {
    super(isbn, title);
    this.author = author;
    this.isbn = isbn;
  }

  /**
   * Default constructor. Initializes an empty book object.
   */
  public Book() { super(); }

  /**
   * Constructs a Book with the specified details and borrowed state.
   *
   * @param title the title of the book
   * @param author the author of the book
   * @param isbn the ISBN of the book
   * @param isBorrowed {@code true} if the book is currently borrowed, {@code false} otherwise
   */
  public Book(String title, String author, String isbn, boolean isBorrowed) {
    super(isbn, title);
    this.author = author;
    this.isbn = isbn;
    setBorrowed(isBorrowed);
  }

  /**
   * Returns the title of the book.
   * @return the book title
   */
  public String getTitle() { return super.getTitle(); }

  /**
   * Updates the title of the book.
   * @param title the new title
   */
  public void setTitle(String title) { super.setTitle(title); }

  /**
   * Returns the author of the book.
   * @return the author name
   */
  public String getAuthor() { return author; }

  /**
   * Updates the author of the book.
   * @param author the new author name
   */
  public void setAuthor(String author) { this.author = author; }

  /**
   * Returns the ISBN of the book.
   * @return the ISBN value
   */
  public String getIsbn() { return isbn; }

  /**
   * Updates the ISBN of the book and the media id.
   * @param isbn the new ISBN value
   */
  public void setIsbn(String isbn) {
    this.isbn = isbn;
    setId(isbn);
  }

  /**
   * Returns whether the book is currently borrowed.
   * @return {@code true} if borrowed; {@code false} otherwise
   */
  public boolean isBorrowed() { return super.isBorrowed(); }

  /**
   * Updates the borrowed status of the book.
   * @param borrowed {@code true} if the book is borrowed; {@code false} otherwise
   */
  public void setBorrowed(boolean borrowed) { super.setBorrowed(borrowed); }

  /**
   * Returns a string representation of the book object.
   * @return a string containing the title, author, and ISBN
   */
  @Override
  public String toString() {
    return "Book{" + "title='" + getTitle() + '\'' + ", author='" + author + '\'' + ", isbn='" + isbn + '\'' + '}';
  }

  /**
   * Returns the borrow period policy for books.
   * @return number of days a book can be borrowed
   */
  @Override
  public int getBorrowPeriod() { return 28; }

  /**
   * Returns the fine per day policy for books.
   * @return fine amount per overdue day
   */
  @Override
  public double getFinePerDay() { return 10.0; }
}