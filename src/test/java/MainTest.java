import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.Entities.User;
import org.junit.jupiter.api.Test;

public class MainTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    String str = "{\"id\":1,\"name\":\"test\"}";

    @Test
    public void test() throws Exception {
        JsonNode jsonNode = objectMapper.readTree(str);
//        System.out.println(name);
    }

}
