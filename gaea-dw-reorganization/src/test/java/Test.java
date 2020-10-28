import com.tincery.gaea.datawarehouse.reorganization.GaeaDwReorganizationApplication;
import com.tincery.gaea.datawarehouse.reorganization.execute.AssetCsvFilter;
import com.tincery.gaea.datawarehouse.reorganization.execute.ReorganizationReceiver;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.jms.MessageNotWriteableException;

@SpringBootTest(classes = GaeaDwReorganizationApplication.class)
public class Test {

    @Autowired
    private ReorganizationReceiver reorganizationExecute;

    @Autowired
    private AssetCsvFilter assetCsvFilter;

    @Autowired
    private ApplicationContext applicationContext;


    @org.junit.jupiter.api.Test
    public void aa() {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        try {
            activeMQTextMessage.setText("asdf");
        } catch (MessageNotWriteableException e) {
            e.printStackTrace();
        }
        reorganizationExecute.receive(activeMQTextMessage);
    }


}
