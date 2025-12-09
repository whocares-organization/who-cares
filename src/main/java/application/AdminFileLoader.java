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
 */
public class AdminFileLoader implements AdminSourceLoader {

    private static final Logger LOGGER = Logger.getLogger(AdminFileLoader.class.getName());
    private final String fileName;

    /**
     * Creates a new loader that reads admin records from a classpath resource.
     *
     * @param fileName the resource name to load (e.g., "admins.csv")
     */
    public AdminFileLoader(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Reads the file and creates Admin objects with username and password.
     *
     * @return a list of Admin objects loaded from the file
     * @throws Exception if reading the resource fails
     */
    @Override
    public List<Admin> loadAdmins() throws Exception {
        final List<Admin> admins = new ArrayList<>();
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IOException("Error reading file: " + fileName);
        }

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = buffer.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    admins.add(new Admin(username, password));
                }
            }
            LOGGER.info("Admins loaded successfully from " + fileName);
        } catch (IOException e) {
            throw new IOException("Error reading file: " + fileName, e);
        }

        return admins;
    }
}
