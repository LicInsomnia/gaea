import com.tincery.gaea.source.dns.quartz.GaeaSourceDnsApplication;
import com.tincery.gaea.source.dns.quartz.execute.DnsExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest (classes = GaeaSourceDnsApplication.class)
public class Test {

    @Autowired
    private DnsExecute dnsExecute;


    @org.junit.jupiter.api.Test
    public void aa() {
        dnsExecute.execute();
    }


}
