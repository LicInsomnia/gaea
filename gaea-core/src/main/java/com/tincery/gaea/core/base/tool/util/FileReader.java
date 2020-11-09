package com.tincery.gaea.core.base.tool.util;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.tincery.gaea.core.base.mgt.CommonConst.DEFAULT_CHARSET;

public class FileReader {

    private FileReader() {
    }

    /**
     * @param file 目标文件
     * @return 返回文件行信息列表
     * 若读取文件错误或文件为空则返回 null
     * @author Insomnia
     * @see File | Reader
     */
    public static List<String> readLine(String file) {
        File fileUtils = new File(file);
        return readLine(fileUtils);
    }

    @SuppressWarnings("unchecked")
    public static int readLine2Arrays(String file, List<String>[] lists) {
        int partition = lists.length;
        try (BufferedReader bufferedReader = java.nio.file.Files.newBufferedReader(Paths.get(file))) {
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                int index = count % partition;
                lists[index].add(line);
                count++;
            }
            return count;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int readLine2Arrays(File file, List<String>[] lists) {
        return readLine2Arrays(file.getAbsolutePath(), lists);
    }

    public static List<String> readLine(File file) {
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return new ArrayList<>();
        }
        try {
            final List<String> lines = Files.readLines(file, Charset.forName(DEFAULT_CHARSET));
            if (lines.isEmpty()) {
                return new ArrayList<>();
            }
            return lines;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * @param file       目标文件
     * @param charsetStr 编码格式("GBK", "utf8", "utf16")
     * @return 返回文件行信息列表
     * 若读取文件错误或文件为空则返回 null
     * @author Insomnia
     * @see File | Reader
     */
    public static List<String> readLine(String file, String charsetStr) {
        File fileUtils = new File(file);
        return readLine(fileUtils, charsetStr);
    }

    public static List<String> readLine(File file, String charsetStr) {
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return null;
        }
        Charset charset = Charset.forName(charsetStr);
        try {
            final List<String> lines = Files.readLines(file, charset);
            if (lines.size() <= 0) {
                return null;
            }
            return lines;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param file 目标文件
     * @return 返回文件行数
     * 若读取文件错误或文件为空则返回 0
     * @author Insomnia
     * @see File | GetLineCount
     */
    public static long getLineCount(File file) {
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return 0;
        }
        try {
            return java.nio.file.Files.lines(file.toPath()).count();
        } catch (IOException e) {
            return 0;
        }
    }

    public static long getLineCount(String file) {
        if (null == file) {
            return 0;
        }
        return getLineCount(new File(file));
    }

    /**
     * @param file 目标文件
     * @return 返回文件信息字符串
     * 若读取文件错误或文件为空则返回 null
     * @author Insomnia
     * @see File | Reader
     */
    public static String read(String file) {
        File fileUtils = new File(file);
        return read(fileUtils);
    }

    public static String read(File file) {
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return null;
        }
        StringBuilder context = new StringBuilder();
        BufferedReader reader = null;
        String line;
        try {
            reader = Files.newReader(file, Charset.forName(DEFAULT_CHARSET));
            while ((line = reader.readLine()) != null) {
                context.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return context.toString();
    }

    /**
     * @param file:目标文件
     * @param charsetStr:编码格式("GBK", "UTF-8", "UTF-16")
     * @return 返回文件信息列表
     * 若读取文件错误或文件为空则返回 null
     * @author Insomnia
     * @see File | Reader
     */
    public static String read(String file, String charsetStr) {
        File fileUtils = new File(file);
        return read(fileUtils, charsetStr);
    }

    public static String read(File file, String charsetStr) {
        if (null == file || (!file.exists()) || file.isDirectory()) {
            return null;
        }
        StringBuffer context = new StringBuffer();
        Charset charset = Charset.forName(charsetStr);
        BufferedReader reader = null;
        String line;
        try {
            reader = Files.newReader(file, charset);
            while ((line = reader.readLine()) != null) {
                context.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return context.toString();
    }

    public static byte[] readAsByte(File file) {
        try {
            return Files.toByteArray(file);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] readAsByte(String fileName) {
        File file = new File(fileName);
        return readAsByte(file);
    }

}
