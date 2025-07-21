package server.services;

import common.User;
import server.dao.UserDAO;

/**
 * AuthenticationService:
 * Sunucu tarafında kullanıcı kayıt ve giriş işlemlerinin iş mantığı.
 */
public class AuthenticationService {
    private UserDAO userDAO;

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Kayıt işlemi.
     * @return Başarılıysa "OK:<fullName>", kullanıcı adı zaten varsa "ERROR:exists", hata olursa "ERROR:fail"
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
     * Giriş işlemi.
     * @return Başarılıysa "OK:<fullName>", kullanıcı yoksa "ERROR:notfound", yanlış şifre ise "ERROR:wrongpass"
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