import com.tincery.gaea.source.espandah.GaeaSourceEspAndAhApplication;
import com.tincery.gaea.source.espandah.execute.MessageListener;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GaeaSourceEspAndAhApplication.class)
public class EspAndAhTest {

    @Autowired
    private MessageListener messageListener;

    @Test
    public void Test() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        try {
            activeMQTextMessage.setText("D:\\data5\\src\\espandah\\espandah_1.txt");
            messageListener.receive(activeMQTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
