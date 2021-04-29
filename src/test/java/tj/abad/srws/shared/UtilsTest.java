package tj.abad.srws.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UtilsTest {

    @Autowired
    private Utils utils;

    @Test
    void generateUserId() {
        String userId = utils.generateUserId(20);
        assertNotNull(userId);
        assertTrue(userId.length()== 20);
    }
}