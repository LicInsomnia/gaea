import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.source.email.GaeaSourceSessionApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@SpringBootTest(classes = GaeaSourceSessionApplication.class)
public class Test {

    @org.junit.jupiter.api.Test
    public void aa(){
        JSONObject jsonObject = new JSONObject((Map) CommonConfig.get("reorganization"));
        Date starttime = jsonObject.getDate("starttime");
        Instant instant = starttime.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        System.out.println(localDateTime);
    }
}
