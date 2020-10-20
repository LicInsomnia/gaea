package com.tincery.gaea.core.base.component.support;


import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.base.PayloadDetectorDO;
import com.tincery.gaea.core.base.dao.PayloadDetectorDao;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gxz
 * 加载sys.payload_detector生成载荷应用检测器
 */
@Component
@Slf4j
public class PayloadDetector implements InitializationRequired {


    @Autowired
    private PayloadDetectorDao payloadDetectorDao;


    private Map<String, List<PayloadDetectorDO>> hasApplicationAndProtocolPort;

    private List<PayloadDetectorDO> hasApplicationDontHaveProtocolPort;

    private Map<String, List<PayloadDetectorDO>> hasProNameAndProtocolPort;

    private List<PayloadDetectorDO> hasProNameDontHaveProtocolPort;

    public ApplicationInformationBO getApplication(int protocol,
                                                   int serverPort,
                                                   int clientPort,
                                                   String upPayLoad,
                                                   String downPayLoad,
                                                   Map<String, Object> extension) {
        String serverPortKey = protocol + "_" + serverPort;
        String clientPortKey = protocol + "_" + clientPort;
        Optional<PayloadDetectorDO> maybeHit = hasApplicationAndProtocolPort.getOrDefault(serverPortKey, new ArrayList<>())
                .stream().filter(p -> p.hitPayload(downPayLoad, upPayLoad) && p.hitExtension(extension)).findFirst();
        if (maybeHit.isPresent()) {
            return maybeHit.get().getApplication();
        }
        maybeHit = hasApplicationAndProtocolPort.getOrDefault(clientPortKey, new ArrayList<>())
                .stream().filter(p -> p.hitPayload(downPayLoad, upPayLoad) && p.hitExtension(extension)).findFirst();
        if (maybeHit.isPresent()) {
            return maybeHit.get().getApplication();
        }
        maybeHit = hasApplicationDontHaveProtocolPort.stream()
                .filter(p -> p.hitPayload(downPayLoad, upPayLoad) && p.hitExtension(extension)).findFirst();
        return maybeHit.map(PayloadDetectorDO::getApplication).orElse(null);


    }


    public String getProName(AbstractMetaData data) {
        return getProName(data.getProtocol(), data.getServerPort(), data.getClientPort(),
                data.getMalformedUpPayload(), data.getMalformedDownPayload());
    }

    public String getProName(int protocol,
                             int serverPort,
                             int clientPort,
                             String upPayLoad,
                             String downPayLoad) {
        String serverPortKey = protocol + "_" + serverPort;
        String clientPortKey = protocol + "_" + clientPort;
        Optional<PayloadDetectorDO> maybeHit =
                hasProNameAndProtocolPort.getOrDefault(serverPortKey, new ArrayList<>())
                        .stream().filter(p -> p.hitPayload(downPayLoad, upPayLoad)).findFirst();
        if (maybeHit.isPresent()) {
            return maybeHit.get().getProName();
        }
        maybeHit = hasProNameAndProtocolPort.getOrDefault(clientPortKey, new ArrayList<>())
                .stream().filter(p -> p.hitPayload(downPayLoad, upPayLoad)).findFirst();
        if (maybeHit.isPresent()) {
            return maybeHit.get().getProName();
        }
        maybeHit = hasProNameDontHaveProtocolPort.stream().filter(p -> p.hitPayload(downPayLoad, upPayLoad)).findFirst();
        return maybeHit.map(PayloadDetectorDO::getProName).orElse("other");
    }


    @Override
    public void init() {
        List<PayloadDetectorDO> payloadDetectors = payloadDetectorDao.findAll();
        List<PayloadDetectorDO> hasApplication = payloadDetectors.stream().filter(PayloadDetectorDO::hasApplication).collect(Collectors.toList());
        List<PayloadDetectorDO> hasProName = payloadDetectors.stream().filter(PayloadDetectorDO::hasProName).collect(Collectors.toList());

        hasApplicationAndProtocolPort = hasApplication.stream().filter(PayloadDetectorDO::hasProtocolAndPort)
                .collect(Collectors.groupingBy(PayloadDetectorDO::getGroupKey));
        hasApplicationDontHaveProtocolPort = hasApplication.stream().filter(PayloadDetectorDO::dontHaveProtocolAndPort)
                .collect(Collectors.toList());
        hasProNameAndProtocolPort = hasProName.stream().filter(PayloadDetectorDO::hasProtocolAndPort)
                .collect(Collectors.groupingBy(PayloadDetectorDO::getGroupKey));
        hasProNameDontHaveProtocolPort = hasProName.stream().filter(PayloadDetectorDO::dontHaveProtocolAndPort)
                .collect(Collectors.toList());

    }


}
