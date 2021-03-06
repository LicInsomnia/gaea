package com.tincery.gaea.source.ftpandtelnet.execute;

import com.tincery.gaea.api.src.FtpandtelnetData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import com.tincery.gaea.source.ftpandtelnet.constant.FtpandtelnetConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class FtpandtelnetReceiver extends AbstractSrcReceiver<FtpandtelnetData> {

    @Autowired
    private PassRule passRule;
    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    public void setAnalysis(FtpandtelnetLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.FTPANDTELNET_HEADER;
    }

    @Override
    protected List<String> getLines(File file) {
        return FileUtils.readByteArray(file,512,4).entrySet().stream().map(entry -> entry.getKey() +
                FtpandtelnetConstant.FTPANDTELNET_CONSTANT + entry.getValue().getKey() +
                FtpandtelnetConstant.FTPANDTELNET_CONSTANT + new String(entry.getValue().getValue())).collect(Collectors.toList());
    }

    @Override
    public void init() {
        registryRules(passRule);
        registryRules(alarmRule);
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

}
