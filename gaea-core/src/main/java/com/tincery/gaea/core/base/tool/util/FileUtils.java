package com.tincery.gaea.core.base.tool.util;

import com.google.common.io.Files;
import com.tincery.gaea.core.base.mgt.CommonConst;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    private static final String ZIP_SUFFIX = ".zip";

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
    public static List<File> searchFiles(String filePath, String prefix, String contains, String extension, long delayTime) {
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

    /**
     * 字节读取文件的方法
     * @param file 文件
     * @return 文件数据  行。 key为前面的（new String(byte[])）字节数组 数据对应common等需要装载的属性
     * value 为content 是比较大的byte[] 内容
     */
    public static Map<String, Pair<Integer, byte[]>> readByteArray(File file) {
        Map<String, Pair<Integer, byte[]>> mapContent = new LinkedHashMap<>();
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return new HashMap<>();
        }
        try {
            byte[] records = Files.toByteArray(file);
            if (null == records) {
                return null;
            }
            int size = records.length;
            int index = 0;
            int i = 0;
            while (i < size) {
                byte[] bufferLength = new byte[4];
                if (size - i < 4) {
                    break;
                }
                System.arraycopy(records, i, bufferLength, 0, 4);
                int targetLength = byteArray2Int(bufferLength, 4);
                i += 4;
                byte[] bufferKey = new byte[256];
                if (size - i < 256) {
                    break;
                }
                System.arraycopy(records, i, bufferKey, 0, 256);
                i += 256;
                if (size - i < targetLength) {
                    break;
                }
                byte[] bufferValue = new byte[targetLength];
                System.arraycopy(records, i, bufferValue, 0, targetLength);
                i += targetLength;
                mapContent.put(new String(bufferKey, Charset.forName(CommonConst.DEFAULT_CHARSET)), new Pair(index, bufferValue));
                index++;
            }
            return mapContent.isEmpty() ? null : mapContent;
        } catch (IOException e) {
            log.error("解析文件 {}是时出错", file.getName());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * 字节读取文件的方法 （重载）
     * @param file 文件
     * @param commonLength common属性的长度  ex：512
     * @param contentLength 几个字节代表content的长度，一版在文件开头 ex：4
     * @return 文件数据
     */
    public static Map<String, Pair<Integer, byte[]>> readByteArray(File file,Integer commonLength,Integer contentLength) {
        Map<String, Pair<Integer, byte[]>> mapContent = new LinkedHashMap<>();
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return new HashMap<>();
        }
        try {
            byte[] records = Files.toByteArray(file);
            if (null == records) {
                return null;
            }
            int size = records.length;
            int index = 0;
            int i = 0;
            while (i < size) {
                byte[] bufferLength = new byte[contentLength];
                if (size - i < contentLength) {
                    break;
                }
                System.arraycopy(records, i, bufferLength, 0, contentLength);
                int targetLength = byteArray2Int(bufferLength, contentLength);
                i += contentLength;
                byte[] bufferKey = new byte[commonLength];
                if (size - i < commonLength) {
                    break;
                }
                System.arraycopy(records, i, bufferKey, 0, commonLength);
                i += commonLength;
                if (size - i < targetLength) {
                    break;
                }
                byte[] bufferValue = new byte[targetLength];
                System.arraycopy(records, i, bufferValue, 0, targetLength);
                i += targetLength;
                mapContent.put(new String(bufferKey, Charset.forName(CommonConst.DEFAULT_CHARSET)), new Pair(index, bufferValue));
                index++;
            }
            return mapContent.isEmpty() ? null : mapContent;
        } catch (IOException e) {
            log.error("解析文件 {}是时出错", file.getName());
            e.printStackTrace();
            return new HashMap<>();
        }
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
        return "/" + dirName + "/" + fileName;
    }


    public static int byteArray2Int(byte[] byteArray, int arrayLength) {
        int returnValue = 0;
        for (int i = 0; i < arrayLength; i++) {
            int number = Byte.toUnsignedInt(byteArray[i]);
            returnValue += (number << i * 8);
        }
        return returnValue;
    }

    /**
     * 把文件压缩成zip格式
     *
     * @param files       需要压缩的文件
     * @param zipFilePath 压缩后的zip文件路径   ,如"D:/test/aa.zip";
     */
    public static void compressFiles2Zip(File[] files, String zipFilePath) {
        if (files != null && files.length > 0) {
            if (org.springframework.util.StringUtils.endsWithIgnoreCase(zipFilePath, ZIP_SUFFIX)) {
                File zipFile = new File(zipFilePath);
                try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(zipFile)) {
                    //Use Zip64 extensions for all entries where they are required
                    zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);
                    //再用ZipArchiveOutputStream写到压缩文件中
                    for (File file : files) {
                        if (file != null) {
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
                            zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
                            try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                                byte[] buffer = new byte[1024 * 5];
                                int len;
                                while ((len = is.read(buffer)) != -1) {
                                    //把缓冲区的字节写入到ZipArchiveEntry
                                    zipArchiveOutputStream.write(buffer, 0, len);
                                }
                                //Writes all necessary data for this entry.
                                zipArchiveOutputStream.closeArchiveEntry();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    zipArchiveOutputStream.finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }
    public static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[1024];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }

                }
            }
        }
    }


}
