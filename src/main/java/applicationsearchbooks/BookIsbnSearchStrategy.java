package applicationsearchbooks;

import java.util.List;
import java.util.stream.Collectors;

import domain.Book;

public class BookIsbnSearchStrategy implements BookSearchStrategy {

	@Override
	public List<Book> searchBook(List<Book> books, String keyword) {
		// TODO Auto-generated method stub
		 return books.stream()
		            .filter(b -> b.getIsbn() != null && b.getIsbn().equalsIgnoreCase(keyword))
		            .collect(Collectors.toList());
		    }
		
	}

