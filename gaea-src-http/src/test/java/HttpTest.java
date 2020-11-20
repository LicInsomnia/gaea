import com.tincery.gaea.source.http.GaeaSrcHttpApplication;
import com.tincery.gaea.source.http.execute.HttpReceiver;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@SpringBootTest(classes = GaeaSrcHttpApplication.class)
public class HttpTest {

@Autowired
private HttpReceiver httpReceiver;

    @org.junit.jupiter.api.Test
    public void http() throws JMSException {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("D:\\gaeaData\\http-1605160308771598-0.dat");
        httpReceiver.receive(activeMQTextMessage);
    }
}
