package com.tincery.gaea.core.base.tool.moduleframe;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseModule implements BaseModuleInterface {
    private static final long serialVersionUID = 1L;
    protected String[] args;
    protected List<DataQueue> queuesInput;
    protected List<DataQueue> queuesOutput;

    public BaseModule() {
    }

    public BaseModule(String[] args) {
        this.args = args;
    }

    public boolean setInput(List<DataQueue> queues) {
        queuesInput = queues;
        return true;
    }

    public boolean setOutput(List<DataQueue> queues) {
        for (DataQueue queue : queues) {
            queue.attach();
        }
        queuesOutput = queues;
        return true;
    }

    public boolean setInput(List<DataQueue> queues, Integer queueCount) {
        if (queues.size() == queueCount) {
            queuesInput = queues;
            return true;
        } else {
            return false;
        }
    }

    public boolean setOutput(List<DataQueue> queues, Integer queueCount) {
        if (queues.size() == queueCount) {
            for (DataQueue queue : queues) {
                queue.attach();
            }
            queuesOutput = queues;
            return true;
        } else {
            return false;
        }
    }
}
