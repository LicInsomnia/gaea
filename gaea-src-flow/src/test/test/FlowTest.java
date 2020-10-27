import com.tincery.gaea.source.flow.GaeaSourceFlowApplication;
import com.tincery.gaea.source.flow.execute.MessageListener;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GaeaSourceFlowApplication.class)
public class FlowTest {

    @Autowired
    private MessageListener messageListener;

    @Test
    public void flowTest() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        try {
            activeMQTextMessage.setText("D:\\data5\\src\\flow\\flow_tzjw_1601750411326926.txt");
            messageListener.receive(activeMQTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
