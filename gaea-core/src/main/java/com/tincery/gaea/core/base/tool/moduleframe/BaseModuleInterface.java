package com.tincery.gaea.core.base.tool.moduleframe;

import java.util.List;

/**
 * @author liuming
 */
public interface BaseModuleInterface extends Runnable {
    boolean setInput(List<DataQueue> queues);

    boolean setOutput(List<DataQueue> queues);
}
