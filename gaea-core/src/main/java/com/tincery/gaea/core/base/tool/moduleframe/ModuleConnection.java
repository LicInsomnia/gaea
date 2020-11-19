package com.tincery.gaea.core.base.tool.moduleframe;

import java.util.ArrayList;
import java.util.List;

public class ModuleConnection {
    private String className;
    private List<DataQueue> inputQueues = new ArrayList<>();
    private List<DataQueue> outputQueues = new ArrayList<>();

    public ModuleConnection(String className) {
        this.className = className;
    }

    public void addInputQueue(DataQueue queue) {
        inputQueues.add(queue);
    }

    public void addOutputQueue(DataQueue queue) {
        outputQueues.add(queue);
    }

    public String getClassName() {
        return className;
    }

    public List<DataQueue> getInputQueues() {
        return inputQueues;
    }

    public List<DataQueue> getOutputQueues() {
        return outputQueues;
    }
}
