package com.tincery.gaea.api.src.email;

import cn.hutool.core.util.ArrayUtil;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 邮件附件
 */
@Getter
@Setter
public class EmailAnnexDetail {
    /*附件名  eml中提取	对应原attachname字段*/
    private List<String> annexName;
    /*附件大小 数组.Int64 获取提取出的文件大小 对应原attachsize字段*/
    private List<Long> annexSize;
    /*附件字符集 	eml中提取 对应原attachcharset字段*/
    private List<String> annexCharset;
    /*	附件存储路径 	服务器存储地址*/
    private List<String> annexPath;
    /*	附件类型  	1.后缀 2.文件内容分析*/
    private List<String> annexType;
    /*加密否 0-未加密 1-加密 2-未知 3-隐写（文档镶嵌）
    * 1.后缀 2.文件内容分析*/
    private List<String> isEncrypt;
    /** 附件状态
     * 0：等待处理 1：处理成功 2：处理失败 3：无需处理
     * 初始状态：0：等待处理，加密原来为3，后改为4
     * JAVA提取-遍历数据
     */
    private List<Integer> annexFlag;
    /*附件内容 JAVA提取-遍历数据*/
    private List<String> annexContent;
    /* 空：初始状态  1:处理 */
    private List<Integer> hashFlag;
    /* 空：初始状态  指纹  	提取密文附件的指纹
    * 1.提取密文附件的指纹 2.如果是隐写就不提取指纹*/
    private List<String> annexHash;


    public void fixAnnexName(String fileName) {
        if (StringUtils.isEmpty(fileName)){
            return;
        }
        if (CollectionUtils.isEmpty(this.annexName)){
            this.annexName = new ArrayList<>();
        }
        annexName.add(fileName);
    }

    public void fixAnnexSize(long size) {
        if (CollectionUtils.isEmpty(this.annexSize)){
            this.annexSize = new ArrayList<>();
        }
        annexSize.add(size);
    }

    public void fixAnnexCharset(String[] header) {
        if (ArrayUtil.isEmpty(header)){
            return;
        }
        if (CollectionUtils.isEmpty(this.annexCharset)){
            this.annexCharset = new ArrayList<>();
        }
        this.annexCharset.addAll(Arrays.asList(header));
    }

    public void fixAnnexPath(String filePath) {
        if (StringUtils.isEmpty(filePath)){
            return;
        }
        if (CollectionUtils.isEmpty(this.annexPath)){
            this.annexPath = new ArrayList<>();
        }
        this.annexPath.add(filePath);
    }
}
