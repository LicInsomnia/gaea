package com.tincery.gaea.datawarehouse.cer.execute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tincery.gaea.api.base.CertDo;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataSourceCheckSigModule extends BaseModule implements BaseModuleInterface {
    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 0);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 1);
    }

    @Override
    public void run() {
        System.out.println("DataSourceCheckSigModule starts.");
        List<String> sha1List = prepareData();
        List<CertDo> dataList = Config.certDao.getDataList();
        for(CertDo certDo : dataList) {
            CerData data = new CerData();
            BeanUtils.copyProperties(certDo, data);
            for (DataQueue queue : queuesOutput) {
                queue.put(data);
            }
        }
        for (DataQueue queue : queuesOutput) {
            queue.detach();
        }
        System.out.println("DataSourceCheckSigModule ends.");
    }

    private List<String> prepareData() {
        String filePath = NodeInfo.getCerChainPath();
        String toCheckFileName = "toCheckCerChain.json";
        String oldFileName = "oldCerChain.json";
        FileUtils.checkPath(filePath);
        List<File> newFileList = FileUtils.searchFiles(filePath, "cerChain", "", "json", 0);
        List<File> toCheckFileList = FileUtils.searchFiles(filePath, "", toCheckFileName, "", 0);
        List<File> oldFileList = FileUtils.searchFiles(filePath, "", oldFileName, "", 0);
        File toCheckFile = toCheckFileList.size() == 1 ? toCheckFileList.get(0) : null;
        File oldFile = oldFileList.size() == 1 ? oldFileList.get(0) : null;
        Map<String, Set<String>> baseMap = getToCheckCerChain(toCheckFile, newFileList);
        return null;
    }

    private Map<String, Set<String>> getSha1ChainMap(File file) {
        Map<String, Set<String>> sha1ChainMap = new HashMap<>();
        List<String> CerChainStrList = FileUtils.readLine(file);
        for(String cerChainStr : CerChainStrList) {
            Map cerChain = jsonStr2Document(cerChainStr);
            String key = cerChain.get("key").toString();
            Set<String> cerChainSet = sha1ChainMap.getOrDefault(key,new HashSet<>());
            List<String> chain = (List<String>)cerChain.get("cerChain");
            cerChainSet.add(mergeChain(chain));
            sha1ChainMap.put(key, cerChainSet);
        }
        return sha1ChainMap;
    }

    private String mergeChain(List<String> list) {
        StringBuilder strMerge = new StringBuilder();
        for(String str : list) {
            strMerge.append(str);
            strMerge.append(";");
        }
        return strMerge.toString();
    }

    private void merge(Map<String, Set<String>> baseMap, Map<String, Set<String>> newMap) {
        for(String key : newMap.keySet()) {
            if(baseMap.containsKey(key)) {
                Set<String> valueSet = baseMap.get(key);
                valueSet.addAll(newMap.get(key));
                baseMap.put(key, valueSet);
            }
        }
    }

    private Map<String, Set<String>> getToCheckCerChain(File toCheckFile, List<File> newFileList) {
        Map<String, Set<String>> baseMap = getSha1ChainMap(toCheckFile);
        for(File fp : newFileList) {
            merge(baseMap, getSha1ChainMap(fp));
        }
        return baseMap;
    }

    public static Map jsonStr2Document(String jsonStr) {
        if (null == jsonStr) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonStr, Map.class);
        } catch (IOException e) {
            return null;
        }
    }
}
