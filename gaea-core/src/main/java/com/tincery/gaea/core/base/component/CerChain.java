package com.tincery.gaea.core.base.component;


import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/*****
 * 证书链
 **/

@Component
public class CerChain {

    private final Set<String> cerChainList = new CopyOnWriteArraySet<>();
    private long startTime;
    private long endTime;

    public final boolean isEmpty() {
        return this.cerChainList.isEmpty();
    }

    public final String getSubPath() {
        return ToolUtils.stamp2Date(this.endTime, "yyyyMMdd");
    }

    public final void append(List<String> cerChain, long capTime) {
        if (null == cerChain || cerChain.size() < 2) {
            return;
        }
        for (int i = 0; i < cerChain.size(); i++) {
            StringBuilder id = new StringBuilder();
            for (int j = i; j < cerChain.size(); j++) {
                id.append(cerChain.get(j)).append(";");
            }
            this.cerChainList.add(id.toString());
        }
        if (this.startTime > capTime || this.startTime == 0) {
            this.startTime = capTime;
        }
        this.endTime = Math.max(this.endTime, capTime);
    }

    public final void output(String path) {
        path += "/cerchain_" + this.startTime + "_" + this.endTime + ".txt";
        FileWriter fileWriter = new FileWriter(path);
        for (String cerChainUtils : this.cerChainList) {
            fileWriter.write(cerChainUtils);
        }
        fileWriter.close();
    }

    public final void clear() {
        this.cerChainList.clear();
    }

}
