package application;

import domain.Admin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads administrator data from an external file into Admin objects.
 * Implements AdminSourceLoader to read CSV/text files with username,password.
 * 
 * Loaded Admin objects can be stored in a repository.
 * Future extensions may include different sources or dynamic addition of admins.
 */
public class AdminFileLoader implements AdminSourceLoader {
	
	/** Logger for logging info and errors in this class */
	private static final Logger LOGGER = Logger.getLogger(AdminFileLoader.class.getName());

	/** Name of the admin file to load from resources */
	private final String fileName;

	/**
	 * Constructs an AdminFileLoader for the specified file.
	 *
	 * @param fileName the name of the file containing admin data
	 *                 (must be in resources)
	 */

  public AdminFileLoader(final String fileName) {
    this.fileName = fileName;
  }

  /**
   * Reads the file and creates Admin objects for each entry.
   *
   * @return a list of Admin objects loaded from the file
   */
  @Override
  public List<Admin> loadAdmins() throws Exception  {
    final List<Admin> admins = new ArrayList<>();

    final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    if (inputStream == null) {
    	throw new IOException("Error reading file: " + fileName);
    }

    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = buffer.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length == 2) {
          String username = parts[0].trim();
          String password = parts[1].trim();
          admins.add(new Admin(username, password));
        }
      }
      LOGGER.info("Admins loaded successfully from" + fileName + "/n");
    } catch (IOException e) {
    	throw new IOException("Error reading file: " + fileName, e);
    } 

    return admins;
  }
}
