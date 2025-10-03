package application;

import domain.*;
import persistence.*;
import java.io.*;
import java.util.*;

/**
 * Loads administrator data from an external file and converts it into Admin objects.
 * <p>
 * This class implements the AdminSourceLoader interface and provides functionality
 * to read a CSV or text file where each line contains an admin's username and password,
 * separated by a comma. The loaded Admin objects can then be stored in a repository.
 * <p>
 * Future extensions could include reading from different sources or allowing dynamic
 * addition of admins at runtime.
 */
public class AdminFileLoader implements AdminSourceLoader {

    private final String fileName;

    /**
     * Constructs an AdminFileLoader for the specified file.
     * 
     * @param fileName the name of the file containing admin data (must be in resources)
     */
    public AdminFileLoader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Reads the file and creates Admin objects for each entry.
     * 
     * @return a list of Admin objects loaded from the file
     */
    @Override
    public List<Admin> loadAdmins() {
        List<Admin> admins = new ArrayList<>();
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            System.out.println("File not found in resources: " + fileName);
            return admins;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    admins.add(new Admin(username, password));
                }
            }
            System.out.println("Admins loaded successfully from " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return admins;
    }
}
