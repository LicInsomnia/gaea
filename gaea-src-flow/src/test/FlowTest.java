import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.source.flow.GaeaSourceFlowApplication;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GaeaSourceFlowApplication.class)
public class FlowTest {

    @Autowired
    private Receiver receiver;

    @Test
    public void Test() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        try {
            activeMQTextMessage.setText("D:\\data5\\src\\flow\\flow_0.txt");
            receiver.receive(activeMQTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
