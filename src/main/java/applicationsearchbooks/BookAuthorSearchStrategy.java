package applicationsearchbooks;

import java.util.List;
import java.util.stream.Collectors;

import domain.Book;

public class BookAuthorSearchStrategy implements BookSearchStrategy {

	@Override
	public List<Book> searchBook(List<Book> books, String keyword) {
		return books.stream()
	            .filter(b -> b.getAuthor() != null && b.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
	            .collect(Collectors.toList());
	    }

}