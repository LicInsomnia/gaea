package com.tincery.gaea.core.base.tool;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MathUtils {

    /**
     * 求平均数
     */
    public static double getAverage(Double[] values) {
        double sum = 0.0;
        int count = 0;
        for (Double value : values) {
            if (null == value) {
                continue;
            }
            sum += value;
            count++;
        }
        return sum / count;
    }

    public static double getAverage(Integer[] values) {
        double sum = 0.0;
        int count = 0;
        for (Integer value : values) {
            if (null == value) {
                continue;
            }
            sum += value;
            count++;
        }
        return sum / count;
    }

    public static double getAverage(Long[] values) {
        double sum = 0.0;
        int count = 0;
        for (Long value : values) {
            if (null == value) {
                continue;
            }
            sum += value;
            count++;
        }
        return sum / count;
    }

    /**
     * 求标准差
     */
    public static double getStandardDeviation(Double[] values) {
        double sum = 0.0;
        double average = getAverage(values);
        for (Double value : values) {
            if (null == value) {
                continue;
            }
            sum += Math.pow(value - average, 2);
        }
        return Math.sqrt(sum);
    }

    public static double getStandardDeviation(Integer[] values) {
        double sum = 0.0;
        double average = getAverage(values);
        for (Integer value : values) {
            if (null == value) {
                continue;
            }
            sum += Math.pow(value - average, 2);
        }
        return Math.sqrt(sum);
    }

    public static double getStandardDeviation(Long[] values) {
        double sum = 0.0;
        double average = getAverage(values);
        for (Long value : values) {
            if (null == value) {
                continue;
            }
            sum += Math.pow(value - average, 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * 字符串词袋转换(BOW)
     */
    public static int[] string2Bow(String str, String charset) {
        try {
            /* 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 */
            /* a b c d e f g h i j k  l  m  n  o  p  q  r  s  t  u  v  w  x  y  z  0  1  2  3  4 */
            /* 31 32 33 34 35 36 37 38 */
            /* 5  6  7  8  9  -  _  else */
            int[] result = new int[39];
            byte[] bytes = str.toLowerCase().getBytes(charset);
            for (byte b : bytes) {
                if (b == '.') {
                    continue;
                }
                if (b >= 'a' && b <= 'z') {
                    result[b - 97]++;
                    continue;
                }
                if (b >= '0' && b <= '9') {
                    result[b - 48]++;
                    continue;
                }
                if (b == '-') {
                    result[36]++;
                    continue;
                }
                if (b == '_') {
                    result[37]++;
                    continue;
                }
                result[38]++;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static int[] string2Bow(String str) {
        return string2Bow(str, "UTF-8");
    }

    /**
     * 字符串余弦相似性
     */
    public static double cosineSimilarity(String str1, String str2) {
        int[] bytes1 = string2Bow(str1);
        int[] bytes2 = string2Bow(str2);
        int length = bytes1.length;
        double sum = 0.0;
        double vp1 = 0.0;
        double vp2 = 0.0;
        for (int i = 0; i < length; i++) {
            sum += bytes1[i] * bytes2[i];
            vp1 += Math.pow(bytes1[i], 2);
            vp2 += Math.pow(bytes2[i], 2);
        }
        return sum / (Math.sqrt(vp1) * Math.sqrt(vp2));
    }

    /**
     * 字符串欧式距离
     */
    public static double euclideanDistance(String str1, String str2) {
        int[] bytes1 = string2Bow(str1);
        int[] bytes2 = string2Bow(str2);
        int length = bytes1.length;
        double result = 0.0;
        for (int i = 0; i < length; i++) {
            result += Math.pow(bytes1[i] - bytes2[i], 2);
        }
        return Math.sqrt(result);
    }

    public static double periodicCheck4BinarySequence(int[] seq, int per) {
        int len = seq.length;
        if (len <= 2 * per) {
            return 0.0;
        }
        double value = 0.0;
        int max = 0;
        for (int i = 0; i < per; i++) {
            double buffer = 0.0;
            int count = 0;
            for (int j = i; j < len; j += per) {
                if (seq[j] != 0) {
                    buffer++;
                }
                max = Math.max(seq[j], max);
                count++;
            }
            value = Math.max(value, buffer / count);
        }
        return value * max;
    }


    /**
     * 合并带间隔的documents
     */
    public static List<Document> intervalMerge(List<Document> intervals, String startKey, String endKey, Integer space) {
        if (intervals.size() <= 1) {
            return intervals;
        }
        List<Document> result = new ArrayList<>();
        intervals.sort(Comparator.comparing(o -> o.getLong(startKey)));
        Long startCursor = intervals.get(0).getLong(startKey);
        Long endCursor = intervals.get(0).getLong(endKey);
        for (Document interval : intervals) {
            Long start = interval.getLong(startKey);
            Long end = interval.getLong(endKey);
            if (endCursor >= (start - space) && startCursor <= (end + space)) {
                startCursor = Math.min(start, startCursor);
                endCursor = Math.max(endCursor, end);
            } else {
                Document insorted = new Document(interval);
                insorted.put(startKey, startCursor);
                insorted.put(endKey, endCursor);
                result.add(insorted);
                startCursor = start;
                endCursor = end;
            }
        }
        Document lastInterval = new Document(intervals.get(intervals.size() - 1));
        lastInterval.put(startKey, startCursor);
        lastInterval.put(endKey, endCursor);
        result.add(lastInterval);
        return result;
    }
}