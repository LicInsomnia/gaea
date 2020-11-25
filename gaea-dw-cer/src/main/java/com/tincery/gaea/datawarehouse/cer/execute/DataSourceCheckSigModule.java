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
        Map<String, Set<String>> toCheckMap = prepareData();
        Set<String> sha1List = toCheckMap.keySet();
        List<CertDo> dataList = Config.certDao.getDataList(sha1List);
        for(CertDo certDo : dataList) {
            CerData data = new CerData();
            BeanUtils.copyProperties(certDo, data);
            if(data.getSignatureCheck() == null || !data.getSignatureCheck()) {
                data.setCerChain(toCheckMap.get(data.getId()));
                for (DataQueue queue : queuesOutput) {
                    queue.put(data);
                }
            }
        }
        for (DataQueue queue : queuesOutput) {
            queue.detach();
        }
        System.out.println("DataSourceCheckSigModule ends.");
    }

    private Map<String, Set<String>> prepareData() {
        String filePath = NodeInfo.getCerChainPath();
        String waitFileName = "wait.json";
        String finishFileName = "finish.json";
        FileUtils.checkPath(filePath);
        List<File> newFileList = FileUtils.searchFiles(filePath, "cerChain", "", "json", 600);
        List<File> waitFileList = FileUtils.searchFiles(filePath, "", waitFileName, "", 0);
        List<File> finishFileList = FileUtils.searchFiles(filePath, "", finishFileName, "", 0);
        File waitFile = waitFileList.size() == 1 ? waitFileList.get(0) : null;
        File finishFile = finishFileList.size() == 1 ? finishFileList.get(0) : null;
        return getToCheckCerChain(finishFile, waitFile, newFileList);
    }

    private String getRealSha(String sha) {
        return sha.substring(0, sha.indexOf("_"));
    }

    private Map<String, Set<String>> getSha1ChainMap(File file) {
        Map<String, Set<String>> sha1ChainMap = new HashMap<>();
        List<String> CerChainStrList = FileUtils.readLine(file);
        for(String cerChainStr : CerChainStrList) {
            Map cerChain = jsonStr2Document(cerChainStr);
            String key = cerChain.get("key").toString();
            String realKey = getRealSha(key);
            Set<String> cerChainSet = sha1ChainMap.getOrDefault(realKey,new HashSet<>());
            List<String> chain = (List<String>)cerChain.get("cerChain");
            cerChainSet.add(mergeChain(chain));
            sha1ChainMap.put(realKey, cerChainSet);
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

    private void mergeMap(Map<String, Set<String>> baseMap, Map<String, Set<String>> newMap) {
        for(String key : newMap.keySet()) {
            Set<String> valueSet = baseMap.getOrDefault(key, new HashSet<>());
            valueSet.addAll(newMap.get(key));
            baseMap.put(key, valueSet);
        }
    }

    private Map<String, Set<String>> getToCheckCerChain(File finishFile, File waitFile, List<File> newFileList) {
        Map<String, Set<String>> toCheckMap = new HashMap<>();
        Map<String, Set<String>> finishMap = getSha1ChainMap(finishFile);
        Map<String, Set<String>> waitMap = getSha1ChainMap(waitFile);
        for(File fp : newFileList) {
            mergeMap(waitMap, getSha1ChainMap(fp));
            fp.delete();
        }
        for(String key : waitMap.keySet()) {
            if(!finishMap.keySet().contains(key)) {
                toCheckMap.put(key, waitMap.get(key));
            }
        }
        return toCheckMap;
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
