package com.tincery.gaea.core.base.plugin.csv;


/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@FunctionalInterface
public interface CsvFilter {

    public boolean filter(CsvRow csvRow);

}
