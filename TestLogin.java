import com.techsikho.dao.UserDAO;
import com.techsikho.models.User;

public class TestLogin {
    public static void main(String[] args) {
        // Test register
        User u = new User();
        u.setUsername("test");
        u.setEmail("test@test.com");
        u.setPasswordHash("test123");
        u.setFullName("Test User");
        boolean reg = UserDAO.registerUser(u);
        System.out.println("Register: " + reg);
        
        // Test login
        User logged = UserDAO.loginUser("test", "test123");
        System.out.println("Login: " + (logged != null ? "SUCCESS" : "FAILED"));
    }
}