import com.tincery.gaea.source.http.GaeaSrcHttpApplication;
import com.tincery.gaea.source.http.execute.MessageListener;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@SpringBootTest(classes = GaeaSrcHttpApplication.class)
public class HttpTest {

    @Autowired
    private MessageListener messageListener;

    @org.junit.jupiter.api.Test
    public void http() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        try {
            activeMQTextMessage.setText("D:\\gaeaData\\http-1587617242963013-0.dat");
            messageListener.receive(activeMQTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
