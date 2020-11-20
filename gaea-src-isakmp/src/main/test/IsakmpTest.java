import com.tincery.gaea.source.isakmp.GaeaSourceIsakmpApplication;
import com.tincery.gaea.source.isakmp.execute.IsakmpReceiver;
import com.tincery.gaea.source.isakmp.execute.MessageListener;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GaeaSourceIsakmpApplication.class)
public class IsakmpTest {

    @Autowired
    private MessageListener messageListener;
    @Autowired
    private IsakmpReceiver isakmpReceiver;

    @Test
    public void Test() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();

        try {
            activeMQTextMessage.setText("D:\\data5\\src\\isakmp\\isakmp_0.txt");
            isakmpReceiver.receive(activeMQTextMessage);
            messageListener.receive(activeMQTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
