package java.com.tincery.gaea.source.ssl.execute;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.source.ssl.GaeaSourceSslApplication;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest(classes = GaeaSourceSslApplication.class)
class SslReceiverTest {


    @Autowired
    private Receiver receiver;
    @Test
    public void aa() throws Exception{
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("D:/data5/src/ssl/ssl_0.txt");
        receiver.receive(activeMQTextMessage);
    }


}
