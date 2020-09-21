package com.tincery.gaea.core.base.component;


import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.starter.base.mgt.NodeInfo;
import com.tincery.starter.mgt.ConstManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * src层执行器 抽象类
 **/
@Getter
@Slf4j
public abstract class AbstractSrcExecute<P extends AbstractSrcCommonProperties> implements Execute {


    protected P properties;

    protected String category;

    protected String srcPath;

    protected String tmpPath;

    protected String dataPath;

    protected String errPath;

    protected String bakPath;

    protected String eventPath;

    protected String alarmPath;




    /****
     * 此方法是Src层执行器通用初始化内容 如果子类有特殊需求
     * 请重写此方法并一定调用"super.init()"
     * @author gxz
     **/
    @Override
    public void init() {
        String category = properties.getCategory();
        String tinceryDataPath = NodeInfo.getTinceryDataPath();
        this.tmpPath = tinceryDataPath + "tmp/";
        this.eventPath = tinceryDataPath + "tmp/alarm_eventData/";
        this.alarmPath = tinceryDataPath + "tmp/alarmmaterial/";
        this.dataPath = tinceryDataPath + "data/"+category+"/";
        this.errPath = tinceryDataPath + "err/" + category+"/";
        this.bakPath = tinceryDataPath + "bak/"+category+"/";
        this.category = category;
        this.srcPath = properties.getSrcPath();
        FileUtils.checkPath(this.srcPath,this.tmpPath,this.dataPath,this.errPath,this.bakPath,this.alarmPath,this.eventPath);
        log.info("初始化srcPath[{}]",this.srcPath);
        log.info("初始化tmpPath[{}]",this.tmpPath);
        log.info("初始化dataPath[{}]",this.dataPath);
        log.info("初始化errPath[{}]",this.errPath);
        log.info("初始化bakPath[{}]",this.bakPath);
    }



    public abstract void setProperties(P properties);


    protected List<File> getTxtFiles() {
        return getFiles(this.srcPath+"/"+this.category+"/",null,".txt");
    }


    /**
     * @param path      检索目录
     * @param contain   检索文件包含字段
     * @param extension 检索文件后缀
     * @return 文件列表(最大数量为this.maxFile, 默认按时间升序)
     * @author Insomnia
     * 获取目录下指定元数类型的文件
     */
    public List<File> getFiles(String path, String contain, String extension){
        List<File> fileList = FileUtils.searchFiles(path,
                category,
                contain,
                extension,
                (int) ConstManager.getCommonConfig("srcdelaytime"));
        int maxFile = maxFile();
        if (fileList.size() > maxFile) {
            log.warn("从{}中得到文件[{}],超出了最大值[{}]，将有文件被忽略", path, fileList.size(), maxFile);
        }
        fileList = fileList.stream()
                .sorted(Comparator.comparingLong(File::lastModified))
                .limit(maxFile)
                .collect(Collectors.toList());
        log.info("从{}中获得了{}个文件", path, fileList.size());
        return fileList;
    }

    /****
     * 当容器中聚集了超过多少行的数据  就开始输出
     **/
    public int maxLine() {
        return properties.getMaxLine();
    }

    public int maxFile(){
        return properties.getMaxFile();
    }

    /****
     * 输出CSV内容
     **/
    public abstract void outputCsvData();

    public boolean isTest() {
        return properties.isTest();
    }

    /****
     * 什么时候需要保留原始文件
     **/
    public boolean isBak() {
        return properties.isBak();
    }

    /****
     * 是否离线
     **/
    public boolean isOffLine() {
        return properties.isOffLine();
    }
}
