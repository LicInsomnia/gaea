import com.tincery.gaea.source.openven.GaeaSourceOpenVpnApplication;
import com.tincery.gaea.source.openven.execute.MessageListener;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GaeaSourceOpenVpnApplication.class)
public class OpenVpnTest {

    @Autowired
    private MessageListener messageListener;

    @Test
    public void Test() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        try {
            activeMQTextMessage.setText("D:\\gaeaData\\openvpn_0.dat");
            messageListener.receive(activeMQTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
