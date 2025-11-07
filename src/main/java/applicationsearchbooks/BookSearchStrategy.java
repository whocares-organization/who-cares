package applicationsearchbooks;
import java.util.List;

import domain.Book;

public interface BookSearchStrategy {
	
	public List<Book> searchBook(List<Book> books, String keyword);

}
