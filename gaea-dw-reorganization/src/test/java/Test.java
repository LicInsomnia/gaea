import com.tincery.gaea.core.base.plugin.csv.CsvFilter;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.datawarehouse.reorganization.GaeaDwReorganizationApplication;
import com.tincery.gaea.datawarehouse.reorganization.execute.AssetCsvFilter;
import com.tincery.gaea.datawarehouse.reorganization.execute.ReorganizationExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = GaeaDwReorganizationApplication.class)
public class Test {

    @Autowired
    private ReorganizationExecute reorganizationExecute;

    @Autowired
    private AssetCsvFilter assetCsvFilter;


    @org.junit.jupiter.api.Test
    public void aa(){
      reorganizationExecute.execute();
    }


}
