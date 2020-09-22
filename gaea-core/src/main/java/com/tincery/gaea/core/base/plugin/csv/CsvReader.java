package com.tincery.gaea.core.base.plugin.csv;


import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author gongxuanzhang
 */
@Slf4j
public class CsvReader {

    private final File csv;

    private Map<String, Integer> headIndex;

    private List<CsvFilter> csvFilterList;

    private BufferedReader csvBufferedReader;

    private Map<Class<? extends CsvFilter>, Queue<CsvRow>> filterRows;

    private Map<Class<? extends CsvFilter>, BlockingQueue<CsvRow>> blockFilterRows;

    private boolean block;

    private TimeUnit timeUnit;

    private int blockTime;

    private int queueCapacity;


    /****
     * @author gxz
     * @param file csv文件
     * @param head 表头 如果首行是表头则不需要
     * @throws IllegalAccessException 如果文件不存在会报错
     **/
    private CsvReader(File file, String[] head, TimeUnit timeUnit, int blockTime, int queueCapacity, List<CsvFilter> csvFilters) throws IllegalAccessException {
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalAccessException("csv不是文件或者不是文件夹");
        } else {
            csv = file;
        }
        // 设置表头
        if (head == null) {
            try {
                // 这个句柄不关闭
                csvBufferedReader = new BufferedReader(new FileReader(this.csv));
                String first = csvBufferedReader.readLine();
                head = StringUtils.FileLineSplit(first);
            } catch (IOException e) {
                throw new IllegalAccessException("初始化失败,表头信息获取不到");
            }
        }
        headIndex = new HashMap<>();
        for (int i = 0; i < head.length; i++) {
            headIndex.put(head[i], i);
        }
        if (timeUnit != null) {
            this.block = true;
            this.timeUnit = timeUnit;
            this.blockTime = blockTime;
            this.queueCapacity = queueCapacity;
        }
        registerFilter(csvFilters);
    }

    public static CsvReaderBuilder builder() {
        return new CsvReaderBuilder();
    }

    private boolean isBlock() {
        return this.block;
    }

    private void registerFilter(List<CsvFilter> csvFilter) {
        if (this.csvFilterList == null) {
            csvFilterList = new ArrayList<>();
        }
        if (isBlock()) {
            registerBlockFilter(csvFilter);
        } else {
            registerSimpleFilter(csvFilter);
        }
    }

    private void registerBlockFilter(List<CsvFilter> csvFilters) {
        if (this.blockFilterRows == null) {
            this.blockFilterRows = new HashMap<>(16);
        }
        for (CsvFilter csvFilter : csvFilters) {
            this.blockFilterRows.put(csvFilter.getClass(), new ArrayBlockingQueue<>(this.queueCapacity));
        }
        this.csvFilterList.addAll(csvFilters);
    }

    private void registerSimpleFilter(List<CsvFilter> csvFilters) {
        if (this.filterRows == null) {
            this.filterRows = new HashMap<>(16);
        }
        for (CsvFilter csvFilter : csvFilters) {
            this.filterRows.put(csvFilter.getClass(), new ArrayDeque<>());
        }
        this.csvFilterList.addAll(csvFilters);
    }


    public CsvRow nextRow() {
        if (this.csvBufferedReader == null) {
            // 句柄已经关闭 返回null
            return null;
        }
        CsvRow csvRow = null;
        try {
            // 如果读取出null 说明没有下一行了 关闭 返回null
            String csvLine = csvBufferedReader.readLine();
            if (csvLine == null) {
                this.csvBufferedReader.close();
                return null;
            }
            csvRow = new CsvRow(this.headIndex, csvLine);
            // 根据过滤器添加内容
            if (this.csvFilterList != null) {
                for (CsvFilter csvFilter : this.csvFilterList) {
                    if (csvFilter.filter(csvRow)) {
                        if (isBlock()) {
                            BlockingQueue<CsvRow> csvRows = this.blockFilterRows.get(csvFilter.getClass());
                            csvRows.offer(csvRow, this.blockTime, this.timeUnit);
                        } else {
                            Queue<CsvRow> csvRows = this.filterRows.get(csvFilter.getClass());
                            csvRows.offer(csvRow);
                        }
                    }
                }
            }

        } catch (IOException e) {
            if (csvBufferedReader != null) {
                try {
                    csvBufferedReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            log.warn("阻塞队列阻塞过程中被中断");
        }
        return csvRow;
    }

    public CsvRow nextRow(Class<? extends CsvFilter> clazz) throws IllegalAccessException {
        if (this.csvFilterList == null) {
            throw new IllegalAccessException(clazz + "此过滤器没注册");
        }
        Queue<CsvRow> queue = isBlock() ? blockFilterRows.get(clazz) : filterRows.get(clazz);
        if (queue == null) {
            throw new IllegalAccessException(clazz + "此过滤器没注册");
        }
        while (queue.isEmpty()) {
            CsvRow csvRow = this.nextRow();
            if (csvRow == null) {
                break;
            }
        }
        return queue.poll();
    }


    private static class CsvReaderBuilder {
        private File csv;
        private String[] head;
        private List<CsvFilter> csvFilterList;
        private TimeUnit timeUnit;
        private int blockTime;
        private int capacity;

        public CsvReaderBuilder registerFilter(CsvFilter... csvFilter) {
            return registerFilter(Arrays.asList(csvFilter));
        }

        public CsvReaderBuilder registerFilter(List<CsvFilter> csvFilters) {
            if (this.csvFilterList == null) {
                this.csvFilterList = new ArrayList<>();
            }
            csvFilterList.addAll(csvFilters);
            return this;
        }

        public CsvReaderBuilder file(String fileName) {
            this.file(new File(fileName));
            return this;
        }

        public CsvReaderBuilder file(File file) {
            this.csv = file;
            return this;
        }

        public CsvReaderBuilder head(String[] head) {
            this.head = head;
            return this;
        }

        public CsvReaderBuilder block(TimeUnit timeUnit, int blockTime, int capacity) {
            this.timeUnit = timeUnit;
            this.blockTime = blockTime;
            this.capacity = capacity;
            return this;
        }

        public CsvReader build() throws IllegalAccessException {
            return new CsvReader(this.csv, this.head, this.timeUnit, this.blockTime, this.capacity, this.csvFilterList);
        }
    }

    public static void main(String[] args) throws IllegalAccessException {
        CsvReader csvReader = CsvReader.builder().registerFilter(new Filter1(),new Filter2(),new Filter3())
                .block(TimeUnit.SECONDS,1,1024).file("aaa.csv").build();

        CsvRow f30 = csvReader.nextRow(Filter3.class);
        CsvRow f31 = csvReader.nextRow(Filter3.class);
        CsvRow f10 = csvReader.nextRow(Filter1.class);
        CsvRow f11 = csvReader.nextRow(Filter1.class);
        CsvRow f20 = csvReader.nextRow(Filter2.class);
        CsvRow f21 = csvReader.nextRow(Filter2.class);
        CsvRow f32 = csvReader.nextRow(Filter3.class);
        CsvRow f33 = csvReader.nextRow(Filter3.class);
        System.out.println("f30" + f30);
        System.out.println("f31" + f31);
        System.out.println("f10" + f10);
        System.out.println("f11" + f11);
        System.out.println("f20" + f20);
        System.out.println("f21" + f21);


        System.out.println("f32" + f32);
        System.out.println("f33" + f33);


    }

    public static class Filter1 implements CsvFilter {
        @Override
        public boolean filter(CsvRow csvRow) {
            return csvRow.get("serverip").equals("13.250.94.254");
        }
    }

    public static class Filter2 implements CsvFilter {
        @Override
        public boolean filter(CsvRow csvRow) {
            return csvRow.get("serverip").equals("13.250.94.254");
        }
    }

    public static class Filter3 implements CsvFilter {
        @Override
        public boolean filter(CsvRow csvRow) {
            return true;
        }
    }


}
