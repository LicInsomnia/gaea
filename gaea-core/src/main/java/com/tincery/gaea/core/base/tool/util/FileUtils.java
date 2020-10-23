package com.tincery.gaea.core.base.tool.util;

import com.google.common.io.Files;
import com.tincery.gaea.core.base.mgt.CommonConst;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件相关操作类（静态操作）
 *
 * @author Insomnia
 * @date 2018/12/29
 * 1.0.1: 修复getCsvDataSetsPath，使用秒->毫秒获取subpath,间隔微秒->毫秒 Elon
 * 1.0.2: 修复修复getCsvDataSetsPath，路径少“/”
 */
@Slf4j
public class FileUtils {

    /***默认的时间间距  1分钟一个文件*/
    private static final int TIME_SPACE = 1;

    private FileUtils() {
    }

    /*****
     * 寻找符合标准的子文件
     * @author gxz
     * @param filePath 父文件夹
     * @param prefix 前缀
     * @param contains 包含文件
     * @param extension 后缀
     * @param delayTime 目标文件最后修改时间距离当前需要超过多少秒
     * @return 子文件集合
     **/
    public static List<File> searchFiles(String filePath, String prefix, String contains, String extension, int delayTime) {
        List<File> result = new ArrayList<>();
        File root = new File(filePath);
        if (!root.exists()) {
            throw new NullPointerException(filePath + "不存在");
        }
        File[] files = root.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(searchFiles(file.getAbsolutePath(), prefix, contains, extension, delayTime));
                } else {
                    if (StringUtils.checkStr(file.getName(), prefix, contains, extension) &&
                            (Instant.now().toEpochMilli() - file.lastModified()) > delayTime * 1000) {
                        result.add(file);
                    }
                }
            }
        }
        return result;
    }


    /**
     * 创建所有有效文件夹
     *
     * @param paths:文件夹路径
     * @author gxz
     */
    public static void checkPath(String... paths) {
        Arrays.stream(paths).filter(StringUtils::isNotEmpty).map(File::new).forEach(File::mkdirs);
    }

    /**
     * @param f:源文件路径
     * @param t:目的文件路径
     * @author Insomnia
     * @see File
     */
    public static void fileMove(String f, String t) {
        File from = new File(f);
        File to = new File(t);
        if (!from.exists() || to.exists()) {
            return;
        }
        try {
            Files.move(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param f:源文件路径
     * @param t:目的文件路径
     * @author Insomnia
     * @see File
     */
    public static void fileCopy(String f, String t) {
        File from = new File(f);
        File to = new File(t);
        if (!from.exists() || to.exists()) {
            return;
        }
        try {
            Files.copy(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /****
     * 读文件
     * @author gxz
     * @param file 文件
     * @return 每行的内容 封装成集合
     **/
    public static List<String> readLine(File file) {
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return new ArrayList<>();
        }
        try {
            return Files.readLines(file, Charset.forName(CommonConst.DEFAULT_CHARSET));
        } catch (IOException e) {
            log.error("解析文件 {}是时出错", file.getName());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<String> readLine(String fileName) {
        return readLine(new File(fileName));
    }


    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }

    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        return delFile(file);
    }

    public static String getSuffix(String fileName) {
        if (fileName.contains(".")) {
            String[] elements = fileName.split("\\.", -1);
            return elements[elements.length - 1];
        } else {
            return "";
        }
    }

    public static String getSuffix(File file) {
        return getSuffix(file.getName());
    }


    public static String getCsvDataFile(String category, long capTime, String nodeName) {
        // 目前默认是1分钟一个文件 所以不用取整  如果以后输出间隔改了   此处需要取整
        String dirName = DateUtils.format(capTime, "yyyyMMdd");
        String fileName = category + "_" + DateUtils.format(capTime, "yyyyMMddHHmm") + "." + nodeName + ".csv";
        return "/" + dirName + "/" +fileName;
    }

}
