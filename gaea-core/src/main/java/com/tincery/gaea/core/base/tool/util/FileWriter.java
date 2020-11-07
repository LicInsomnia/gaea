package com.tincery.gaea.core.base.tool.util;

import java.io.*;
import java.util.List;

import static com.tincery.gaea.core.base.mgt.CommonConst.DEFAULT_CHARSET;
import static com.tincery.gaea.core.base.mgt.CommonConst.MB;


/**
 * 文件输出操作类
 *
 * @author Insomnia
 * @date 2018/12/29
 */
public class FileWriter implements Closeable {

    private BufferedWriter bw;
    private String file;
    private String tmpFile;
    private int length;
    private boolean append;

    /**
     * @param file    写入目的文件绝对路径
     * @param charset 写入编码
     * @param append  是否续写
     * @author Insomnia
     * @see File | Writer
     */
    public FileWriter(String file, String charset, boolean append) {
        this.set(file, charset, append);
    }

    public FileWriter(String file, boolean append) {
        this.set(file, append);
    }

    public FileWriter(String file, String charset) {
        this.set(file, charset);
    }

    public FileWriter(String file) {
        this.set(file);
    }

    public FileWriter() {
    }

    public String getFile() {
        return file;
    }


    /**
     * @param str       写入内容
     * @param isNewLine 当前写入后是否换行（无该参数则默认换行）
     * @author Insomnia
     * @see File | Writer
     */
    public void write(String str, boolean isNewLine) {
        if (null == this.bw) {
            return;
        }
        try {
            this.bw.write(str);
            this.length += str.length();
            if (this.length > 10 * MB) {
                bw.flush();
                this.length = 0;
            }
            if (isNewLine) {
                this.bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String str) {
        if (null == str) {
            return;
        }
        this.write(str, true);
    }

    /**
     * @param list:写入内容按行分割List
     * @author Insomnia
     * @see File | Writer
     */
    public void write(List<String> list) {
        if (null == this.bw) {
            return;
        }
        try {
            for (String str : list) {
                this.bw.write(str);
                this.bw.newLine();
                this.length += str.length();
                if (this.length > 10 * MB) {
                    bw.flush();
                    this.length = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    /**
     * @author Insomnia
     * @see File | Writer
     */
    @Override
    public void close() {
        if (null == bw) {
            return;
        }
        try {
            bw.close();
            bw = null;
            File bf = new File(this.file);
            if (append) {
                if (bf.exists() && bf.isFile() && bf.length() == 0) {
                    bf.delete();
                }
            } else {
                File tbf = new File(this.tmpFile);
                if (tbf.exists() && tbf.isFile()) {
                    if (tbf.length() == 0) {
                        tbf.deleteOnExit();
                    } else {
                        tbf.renameTo(bf);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param file:写入目的文件绝对路径
     * @param charset:写入编码
     * @param append:是否续写
     * @author Insomnia
     * @see File | Writer
     */
    public void set(String file, String charset, boolean append) {
        this.close();
        this.file = file;
        this.tmpFile = file + ".tmp";
        this.append = append;
        if (null == charset) {
            charset = DEFAULT_CHARSET;
        }
        try {
            String bFile = append ? this.file : this.tmpFile;
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(bFile, append), charset);
            this.bw = new BufferedWriter(writerStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String file, boolean append) {
        this.close();
        this.file = file;
        this.tmpFile = file + ".tmp";
        try {
            this.append = append;
            String bFile = append ? this.file : this.tmpFile;
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(bFile, append), DEFAULT_CHARSET);
            this.bw = new BufferedWriter(writerStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String file, String charset) {
        this.close();
        this.file = file;
        this.tmpFile = file + ".tmp";
        if (null == charset) {
            charset = DEFAULT_CHARSET;
        }
        try {
            this.close();
            String bFile = append ? this.file : this.tmpFile;
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(bFile, append), charset);
            this.bw = new BufferedWriter(writerStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String file) {
        this.set(file, false);
    }

    /**
     * @param file    写入目的文件绝对路径
     * @param charset 写入编码
     * @param header  csv文件头(","分割)
     * @author Insomnia
     * @see File | Writer
     */
    public void setCSV(String file, String charset, String header, boolean append) {
        this.set(file, charset, append);
        this.write(header);
    }

    public void setCSV(String file, String charset, String header) {
        this.set(file, charset);
        this.write(header);
    }

    public void setCSV(String file, String header, boolean append) {
        this.set(file, append);
        this.write(header);
    }

    public void setCSV(String file, String header) {
        this.set(file);
        this.write(header);
    }
}
