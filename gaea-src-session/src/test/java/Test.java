import com.tincery.gaea.source.session.GaeaSourceSessionApplication;
import com.tincery.gaea.source.session.execute.MessageListener;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@SpringBootTest (classes = GaeaSourceSessionApplication.class)
public class Test {

    @Autowired
    private MessageListener messageListener;

    @org.junit.jupiter.api.Test
    public void aa() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        try {
            activeMQTextMessage.setText("asdf");
            messageListener.receive(activeMQTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
