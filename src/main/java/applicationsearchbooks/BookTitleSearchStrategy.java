package applicationsearchbooks;

import java.util.List;
import java.util.stream.Collectors;

import domain.Book;

public class BookTitleSearchStrategy implements BookSearchStrategy {

	@Override
	public List<Book> searchBook(List<Book> books, String keyword) {
		// TODO Auto-generated method stub
		return books.stream()
	            .filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
	            .collect(Collectors.toList());
	    }	
	}
