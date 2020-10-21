package com.tincery.gaea.core.base.tool;

import javafx.util.Pair;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import static com.tincery.gaea.core.base.mgt.CommonConst.*;


/**
 * 工具类
 *
 * @author Insomnia
 * @date 2018/12/29
 * @version 1.0.1
 */
public class ToolUtils {

    private ToolUtils() {
    }

    /**
     * @param dateStr     时间字符串
     * @param datePattern 时间字符串格式，若输入为null或不输入,则默认为"yyyy-MM-dd HH:mm:ss"
     * @return 返回long型时间戳默认为毫秒
     * 若转换错误则返回 null
     * @author Insomnia
     * @see Date & TimeStamp
     */
    public static long date2Stamp(String dateStr, String datePattern) {
        try {
            if (null == datePattern) {
                datePattern = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static Long date2Stamp(String dateStr) {
        return date2Stamp(dateStr, null);
    }

    /**
     * @param ts          毫秒时间戳
     * @param datePattern 时间字符串格式，若输入为null或不输入,则默认为"yyyy-MM-dd HH:mm:ss"
     * @return 格式化时间
     * 若转换错误则返回null
     * @author Insomnia
     * @see Date & TimeStamp
     */
    public static String stamp2Date(long ts, String datePattern) {
            if (null == datePattern) {
                datePattern = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            Date date = new Date(ts);
            return simpleDateFormat.format(date);
    }


    public static String stamp2Date(long ts) {
        return stamp2Date(ts, null);
    }

    /**
     * @param collAndSub:表名&分表间隔(0为不分表,单位：小时)
     * @param t_n:信息时间戳(单位：微秒)
     * @return 表名
     * @author Insomnia
     * 获取分表表名
     */
    public static String getCollName(Pair<String, Integer> collAndSub, Long t_n) {
        if (null == t_n || collAndSub.getValue() == 0) {
            return collAndSub.getKey();
        }
        return collAndSub.getKey() + "-" + subTime(t_n, collAndSub.getValue());
    }

    public static String subTime(Long timestamp_n, Integer sub) {
        long spc = sub * 60L * 60L * 1000L;
        Long time = (timestamp_n + (8L * 60L * 60L * 1000L)) / spc * spc;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        return sdf.format(time);
    }

    public static String getHexMac(String decMac) {
        String[] buf = decMac.split(":");
        StringBuilder hm = new StringBuilder();
        for (String n : buf) {
            String b = Integer.toHexString(Integer.parseInt(n));
            if (b.length() == 1) {
                b = "0" + b;
            }
            hm.append(b).append(":");
        }
        return hm.toString();
    }

    /**
     * Unicode编码字符转汉字
     *
     * @param ori Unicode编码字符
     * @return 汉字UTF8编码
     */
    public static String convertUnicode(String ori) {
        char aChar;
        int len = ori.length();
        StringBuilder outBuffer = new StringBuilder(len);
        for (int x = 0; x < len; ) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);

        }
        return outBuffer.toString();
    }

    public static String generateId(Map<String, Object> map, String... keyField) {
        StringBuilder id = new StringBuilder();
        for (String key : keyField) {
            id.append(map.getOrDefault(key, "").toString());
        }
        return getMD5(id.toString());
    }

    /**
     * @param inStr:输入字符串
     * @return 返回md5编码后字符串
     * 若转换错误则返回 null
     * @author Insomnia
     */
    public static String getMD5(String inStr) {
        final MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return null;
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * @param ip_n 十进制ipv4地址
     * @return IPv4地址字符串，以'.'分割
     * 若转换错误则返回 null
     * @author Insomnia
     */
    public static String long2IP(long ip_n) {
        if (ip_n < 0 || ip_n > 4294967295L) {
            return null;
        }
        return (ip_n >>> 24) +
                "." +
                ((ip_n & 0x00FFFFFF) >>> 16) +
                "." +
                ((ip_n & 0x0000FFFF) >>> 8) +
                "." +
                (ip_n & 0x000000FF);
    }

    /**
     * @param ip IPv4地址字符串，以'.'分割
     * @return 十进制ip地址
     * 若转换错误则返回 -1L
     * @author Insomnia
     */
    public static Long IP2long(String ip) {
        if (null == ip) {
            return -1L;
        }
        long ip_n = 0;
        String[] numbers = ip.split("\\.");
        if (numbers.length != 4) {
            return -1L;
        }
        for (int i = 0; i < 4; ++i) {
            int num;
            try {
                num = Integer.parseInt(numbers[i]);
            } catch (Exception e) {
                return -1L;
            }
            if (num < 0 || num > 255) {
                return -1L;
            }
            ip_n = ip_n << 8 | num;
        }
        return ip_n;
    }

    /**
     * 是否ipv4
     *
     * @param ip IP地址字符串
     * @return 是否ipv4
     */
    public static boolean isIpv4(String ip) {
        try {
            if (ip == null || ip.length() == 0) {
                return false;
            }
            String[] elements = ip.split("\\.");
            if (elements.length != 4) {
                return false;
            }
            for (String element : elements) {
                int n = Integer.parseInt(element);
                if (n < 0 || n > 255) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || "".equals(hexString)) {
            return null;
        }
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toLowerCase().toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * @param buffer ipv6 32位串
     * @return ipv6简写串
     * 若转换错误则返回 null
     * @author Insomnia
     */
    public static String IPv6Hex2Host(String buffer) {
        try {
            byte[] bytes = hexStringToBytes(buffer);
            InetAddress addr = Inet6Address.getByAddress(bytes);
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * @param s1,s2:输入集和
     * @return 去重后集和
     * @author Insomnia
     */
    public static Set<String> collectionMerge(Set<String> s1, Set<String> s2) {
        Set<String> s = new HashSet<>();
        if (null != s1) {
            s.addAll(s1);
        }
        if (null != s2) {
            s.addAll(s2);
        }
        if (s.size() == 0) {
            return null;
        }
        return s;
    }

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @method encrypt
     * @since v1.0
     */
    public static String encrypt_AES(String content, String password) {
        try {//1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keygen.init(128, random);
            //3.产生原始对称密钥
            SecretKey original_key = keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] raw = original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key = new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byte_encode = content.getBytes(StandardCharsets.UTF_8);
            //9.根据密码器的初始化方式--加密：将数据加密
            byte[] byte_AES = cipher.doFinal(byte_encode);
            //10.将加密后的数据转换为字符串
            //这里用Base64Encoder中会找不到包
            //解决办法：
            //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
            Encoder encoder = Base64.getEncoder();
            //11.将字符串返回
            return encoder.encodeToString(byte_AES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @method decrypt
     * @since v1.0
     */
    public static String decrypt_AES(String content, String password) {
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keygen.init(128, random);
            //3.产生原始对称密钥
            SecretKey original_key = keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] raw = original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key = new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            //8.将加密并编码后的内容解码成字节数组
            Decoder decoder = Base64.getDecoder();
            byte[] byte_content = decoder.decode(content);
            /* 解密 */
            byte[] byte_decode = cipher.doFinal(byte_content);
            return new String(byte_decode, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf 2进制字符串
     * @since v1.0
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr 16进制字符串
     * @method parseHexStr2Byte
     * @since v1.0
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;
        for (byte b : bs) {
            bit = (b & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = b & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 需要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1) {
            return null;
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            if ((b & 0xff) < 0x10) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArray(String hexString) {
        if (hexString.isEmpty()) {
            return new byte[0];
        }
        hexString = hexString.toLowerCase();
        byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * @param collection 集和
     * @param splitChar  按spiltChar分割
     * @return 转换后字符串
     * @author Insomnia
     * 集和按特定字符转字符串
     */
    public static String convertString(Collection<?> collection, String splitChar) {
        if (null == collection || collection.isEmpty()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : collection) {
            stringBuilder.append(obj.toString()).append(splitChar);
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * @param collection 集和
     * @return 转换后字符串
     * @author Insomnia 集和按特定字符转字符串
     */
    public static String convertString(Collection<?> collection) {
        return convertString(collection, ";");
    }

    /**
     * @param toCovert  字符串
     * @param splitChar 按spiltChar分割
     * @return 转换后列表
     * @author Elon 将字符串按特定分隔符分开并生成列表
     */
    public static List<String> string2List(String toCovert, String splitChar) {
        if (null == toCovert || toCovert.isEmpty()) {
            return new ArrayList<>();
        }
        String[] spces = toCovert.split(splitChar);
        return new ArrayList<>(Arrays.asList(spces));
    }

    static boolean checkStr(String str, String strPrefix, String strContains, String strExtension) {
        if (null == str || str.isEmpty()) {
            return false;
        }
        int strLen = str.length();
        boolean prRet = (null == strPrefix || (strLen > strPrefix.length() && strPrefix.equals(str.substring(0, strPrefix.length()))));
        boolean coRet = (null == strContains || str.contains(strContains));
        boolean exRet = (null == strExtension || (strLen > strExtension.length()) && strExtension.equals(str.substring(strLen - strExtension.length())));
        return prRet && coRet && exRet;
    }


    public static Map<String, Double> resortMapByDouble(Map<String, Double> map) {
        ArrayList<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());
        list.sort((map1, map2) -> ((map2.getValue() - map1.getValue() == 0) ? 0 : (map2.getValue() - map1.getValue() > 0) ? 1 : -1));
        Map<String, Double> linkMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            linkMap.put(entry.getKey(), entry.getValue());
        }
        return linkMap;
    }

    public static Map<String, Integer> resortMapByInteger(Map<String, Integer> map) {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((map1, map2) -> (Integer.compare(map2.getValue() - map1.getValue(), 0)));
        Map<String, Integer> linkMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            linkMap.put(entry.getKey(), entry.getValue());
        }
        return linkMap;
    }

    public static Map<String, Long> resortMapByLong(Map<String, Long> map) {
        ArrayList<Map.Entry<String, Long>> list = new ArrayList<>(map.entrySet());
        list.sort((map1, map2) -> ((map2.getValue() - map1.getValue() == 0) ? 0 : (map2.getValue() - map1.getValue() > 0) ? 1 : -1));
        Map<String, Long> linkMap = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : list) {
            linkMap.put(entry.getKey(), entry.getValue());
        }
        return linkMap;
    }

    public static boolean contains(String str, String[] fields) {
        if (null == fields) {
            return true;
        }
        if (null == str) {
            return false;
        }
        if (fields.length == 0) {
            return false;
        }
        for (String field : fields) {
            if (null == field || field.isEmpty()) {
                continue;
            }
            if (str.contains(field)) {
                return true;
            }
        }
        return false;
    }

    public static boolean endsWith(String str, String[] fields) {
        if (null == fields) {
            return true;
        }
        if (null == str) {
            return false;
        }
        if (fields.length == 0) {
            return false;
        }
        for (String field : fields) {
            if (null == field || field.isEmpty()) {
                continue;
            }
            if (str.endsWith(field)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWith(String str, String[] fields) {
        if (null == fields) {
            return true;
        }
        if (null == str) {
            return false;
        }
        if (fields.length == 0) {
            return false;
        }
        for (String field : fields) {
            if (null == field || field.isEmpty()) {
                continue;
            }
            if (str.startsWith(field)) {
                return true;
            }
        }
        return false;
    }

    public static String getSizeUnit(long value, String unit) {
        String exp;
        if (KB < value && value <= MB) {
            value /= KB;
            exp = "K";
        } else if (MB < value && value <= GB) {
            value /= MB;
            exp = "M";
        } else if (GB < value && value <= TB) {
            value /= GB;
            exp = "G";
        } else if (TB < value) {
            value /= TB;
            exp = "T";
        } else {
            exp = "B";
        }
        return value + exp + unit;
    }



    /**
     * 对象实例化为String
     *
     * @param obj 待实例化对象
     * @return 对象实例化后的String
     */
    public static String serialize2String(Object obj) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        return byteOut.toString("ISO-8859-1");
    }

    /**
     * 实例化String转为对象
     *
     * @param str 实例化后String
     * @return 对象
     */
    public static Object deserialize2Object(String str) throws Exception {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        return objIn.readObject();
    }


    public static Object clone(Object obj) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
