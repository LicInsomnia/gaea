package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.core.base.component.support.ApplicationCheck;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.component.support.WebCheck;
import com.tincery.gaea.core.base.dao.CertDao;
import com.tincery.gaea.core.base.dao.SrcRuleDao;
import com.tincery.gaea.core.base.mgt.AlarmDictionary;
import com.tincery.gaea.datawarehouse.cer.config.property.CerProperties;

public class Config {
    public static CertDao certDao;
    public static CerProperties cerProperties;
    public static SrcRuleDao srcRuleDao;
    public static AlarmDictionary alarmDictionary;
    public static IpSelector ipSelector;
    public static AssetDetector assetDetector;
    public static ApplicationCheck appCheck;
    public static WebCheck webCheck;

    public static void init(CertDao certDaoIn, CerProperties cerPropertiesIn, SrcRuleDao srcRuleDaoIn, AlarmDictionary alarmDictionaryIn, IpSelector ipSelectorIn, AssetDetector assetDetectorIn, ApplicationCheck appCheckIn, WebCheck webCheckIn) {
        certDao = certDaoIn;
        cerProperties = cerPropertiesIn;
        srcRuleDao = srcRuleDaoIn;
        alarmDictionary = alarmDictionaryIn;
        ipSelector = ipSelectorIn;
        assetDetector = assetDetectorIn;
        appCheck = appCheckIn;
        webCheck = webCheckIn;
    }
}
