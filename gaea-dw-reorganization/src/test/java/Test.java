import com.tincery.gaea.datawarehouse.reorganization.GaeaDwReorganizationApplication;
import com.tincery.gaea.datawarehouse.reorganization.execute.AssetCsvFilter;
import com.tincery.gaea.datawarehouse.reorganization.execute.ReorganizationReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest (classes = GaeaDwReorganizationApplication.class)
public class Test {

    @Autowired
    private ReorganizationReceiver reorganizationExecute;

    @Autowired
    private AssetCsvFilter assetCsvFilter;

    @Autowired
    private ApplicationContext applicationContext;


    @org.junit.jupiter.api.Test
    public void aa() {
        reorganizationExecute.receive();
    }


}
