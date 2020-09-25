package com.tincery.reorganization.important;

import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.reorganization.AbstractReorganizationExecute;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class ImpReorganizationExecute extends AbstractReorganizationExecute {


    @Override
    public void init() {
        // TODO: 2020/9/23 给filter赋值
        //
    }

    @Override
    public void free() {
        super.free();
    }

    @Override
    public void analysis(CsvReader csvReader) {
        super.analysis(csvReader);
    }
}
