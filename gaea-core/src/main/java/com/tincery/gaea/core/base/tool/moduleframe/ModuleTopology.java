package com.tincery.gaea.core.base.tool.moduleframe;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuming
 */
public class ModuleTopology {
    private Map<String, String> inputTagClassMap = new HashMap<>();
    private Map<String, String> outputTagClassMap = new HashMap<>();
    private Map<String, ModuleConnection> classConnectionMap = new HashMap<>();

    public void addInputTag(String className, String tag) {
        inputTagClassMap.put(tag, className);
    }

    public void addOutputTag(String className, String tag) {
        outputTagClassMap.put(tag, className);
    }

    public boolean construct() {
        if (!inputTagClassMap.keySet().equals(outputTagClassMap.keySet())) {
            System.err.println("Input tags differs from output tags;");
            return false;
        }
        for (String tag : inputTagClassMap.keySet()) {
            String inputClassName = inputTagClassMap.get(tag);
            String outputClassName = outputTagClassMap.get(tag);
            DataQueue queue = new DataQueue(tag);
            ModuleConnection inputConnection = classConnectionMap.getOrDefault(inputClassName, new ModuleConnection(inputClassName));
            inputConnection.addInputQueue(queue);
            classConnectionMap.put(inputClassName, inputConnection);
            ModuleConnection outputConnection = classConnectionMap.getOrDefault(outputClassName, new ModuleConnection(outputClassName));
            outputConnection.addOutputQueue(queue);
            classConnectionMap.put(outputClassName, outputConnection);
            //classConnectionMap.getOrDefault(outputClassName, new ModuleConnection(outputClassName)).addOutputQueue(queue);
        }
        return true;
    }

    public Map<String, ModuleConnection> getTopology() {
        return classConnectionMap;
    }
}
