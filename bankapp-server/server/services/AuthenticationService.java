package server.services;

import common.User;
import server.dao.UserDAO;

/**
 * AuthenticationService:
 * Business logic for user registration and login on the server side.
 */
public class AuthenticationService {
    private UserDAO userDAO;

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new user.
     * @return "OK:<fullName>" if successful, "ERROR:exists" if the username already exists, "ERROR:fail" for other errors.
     */
    public String register(String username, String password, String fullName) {
        if (userDAO.findUserByUsername(username) != null) {
            return "ERROR:exists";
        }
        User user = new User(username, password, fullName);
        boolean res = userDAO.addUser(user);
        if (res) {
            return "OK:" + fullName;
        } else {
            return "ERROR:fail";
        }
    }

    /**
     * Logs in the user.
     * @return "OK:<fullName>" if successful, "ERROR:notfound" if the user does not exist, "ERROR:wrongpass" if the password is incorrect.
     */
    public String login(String username, String password) {
        User user = userDAO.findUserByUsername(username);
        if (user == null) {
            return "ERROR:notfound";
        }
        if (!user.getPassword().equals(password)) {
            return "ERROR:wrongpass";
        }
        return "OK:" + user.getFullName();
    }
}