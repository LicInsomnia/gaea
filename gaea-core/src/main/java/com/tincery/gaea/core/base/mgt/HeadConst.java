package com.tincery.gaea.core.base.mgt;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class HeadConst {

    private HeadConst(){
        throw new RuntimeException();
    }

    public static final char CSV_SEPARATOR = 0x07;


    /* 通用csv文件头 */
    private final static String BASE_COMMON_HEADER = CSV.GROUP_NAME + CSV_SEPARATOR +
            CSV.TARGET_NAME + CSV_SEPARATOR +
            CSV.USER_ID + CSV_SEPARATOR +
            CSV.SERVER_ID + CSV_SEPARATOR +
            CSV.SOURCE + CSV_SEPARATOR +
            CSV.CAPTIME + CSV_SEPARATOR +
            CSV.CAPTIME_N + CSV_SEPARATOR +
            CSV.CLIENT_MAC + CSV_SEPARATOR +
            CSV.SERVER_MAC + CSV_SEPARATOR +
            CSV.PROTOCOL + CSV_SEPARATOR +
            CSV.PRONAME + CSV_SEPARATOR +
            CSV.CLIENT_IP + CSV_SEPARATOR +
            CSV.CLIENT_IP_N + CSV_SEPARATOR +
            CSV.SERVER_IP + CSV_SEPARATOR +
            CSV.SERVER_IP_N + CSV_SEPARATOR +
            CSV.CLIENT_PORT + CSV_SEPARATOR +
            CSV.SERVER_PORT + CSV_SEPARATOR +
            CSV.CLIENT_IP_OUTER + CSV_SEPARATOR +
            CSV.SERVER_IP_OUTER + CSV_SEPARATOR +
            CSV.CLIENT_PORT_OUTER + CSV_SEPARATOR +
            CSV.SERVER_PORT_OUTER + CSV_SEPARATOR +
            CSV.PROTOCOL_OUTER + CSV_SEPARATOR +
            CSV.UP_PKT + CSV_SEPARATOR +
            CSV.UP_BYTE + CSV_SEPARATOR +
            CSV.DOWN_PKT + CSV_SEPARATOR +
            CSV.DOWN_BYTE + CSV_SEPARATOR +
            CSV.DATA_TYPE + CSV_SEPARATOR +
            CSV.IMSI + CSV_SEPARATOR +
            CSV.CASE_TAGS + CSV_SEPARATOR;

    /* session csv文件头 */
    public final static String SESSION_HEADER = BASE_COMMON_HEADER +
            CSV.DURATION + CSV_SEPARATOR +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.UP_PAYLOAD + CSV_SEPARATOR +
            CSV.DOWN_PAYLOAD;
    /* dns csv文件头 */
    public final static String DNS_HEADER = BASE_COMMON_HEADER +
            CSV.DOMAIN + CSV_SEPARATOR +
            CSV.IPS + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.EXTENSION;
    /* ssl csv文件头 */
    public final static String SSL_HEADER = BASE_COMMON_HEADER +
            CSV.DURATION + CSV_SEPARATOR +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.SERVER_NAME + CSV_SEPARATOR +
            CSV.SHA1 + CSV_SEPARATOR +
            CSV.CERCHAIN + CSV_SEPARATOR +
            CSV.CLIENT_CERCHAIN + CSV_SEPARATOR +
            CSV.IS_DOUBLE + CSV_SEPARATOR +
            CSV.RANDOM + CSV_SEPARATOR +
            CSV.VERSION + CSV_SEPARATOR +
            CSV.CIPHER_SUITES + CSV_SEPARATOR +
            CSV.CLIENT_CIPHER_SUITE + CSV_SEPARATOR +
            CSV.HAND_SHAKE + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD;
    /* openvpn csv文件头 */
    public final static String OPENVPN_HEADER = BASE_COMMON_HEADER +
            CSV.DURATION + CSV_SEPARATOR +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.SERVER_NAME + CSV_SEPARATOR +
            CSV.SHA1 + CSV_SEPARATOR +
            CSV.CERCHAIN + CSV_SEPARATOR +
            CSV.RANDOM + CSV_SEPARATOR +
            CSV.VERSION + CSV_SEPARATOR +
            CSV.CIPHER_SUITES + CSV_SEPARATOR +
            CSV.CLIENT_CIPHER_SUITE + CSV_SEPARATOR +
            CSV.HAND_SHAKE + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD;
    /* ssh csv文件头 */
    public final static String SSH_HEADER = BASE_COMMON_HEADER +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.EXTENSION;
    /* isakmp csv文件头 */
    public final static String ISAKMP_HEADER = BASE_COMMON_HEADER +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.MESSAGE_LIST + CSV_SEPARATOR +
            CSV.INITIATOR_INFORMATION + CSV_SEPARATOR +
            CSV.RESPONDER_INFORMATION + CSV_SEPARATOR +
            CSV.INITIATOR_VIDS + CSV_SEPARATOR +
            CSV.RESPONDER_VID + CSV_SEPARATOR +
            CSV.EXTENSION;
    /* ftpandtelnet csv文件头 */
    public final static String FTPANDTELNET_HEADER = BASE_COMMON_HEADER +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.EXTENSION;
    /* email csv文件头 */
    public final static String EMAIL_HEADER = BASE_COMMON_HEADER +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.EXTENSION;
    /* ESP&AH csv文件头 */
    public final static String ESPANDAH_HEADER = BASE_COMMON_HEADER +
            CSV.SPI + CSV_SEPARATOR +
            CSV.UP_PAYLOAD + CSV_SEPARATOR +
            CSV.DOWN_PAYLOAD;
    /* http csv文件头 */
    public final static String HTTP_HEADER = BASE_COMMON_HEADER +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.HOST + CSV_SEPARATOR +
            CSV.METHOD + CSV_SEPARATOR +
            CSV.URL_ROOT + CSV_SEPARATOR +
            CSV.USER_AGENT + CSV_SEPARATOR +
            CSV.CONTENT_LENGTH + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.EXTENSION;


    public static class CSV {

        /* COMMON */
        public static String GROUP_NAME = "groupname";
        public static String TARGET_NAME = "targetname";
        public static String USER_ID = "userid";
        public static String SERVER_ID = "serverid";
        public static String SOURCE = "source";
        public static String CAPTIME = "captime";
        public static String CAPTIME_N = "captime_n";
        public static String CLIENT_MAC = "clientmac";
        public static String SERVER_MAC = "servermac";
        public static String PROTOCOL = "protocol";
        public static String PRONAME = "proname";
        public static String CLIENT_IP = "clientip";
        public static String CLIENT_IP_N = "clientip_n";
        public static String SERVER_IP = "serverip";
        public static String SERVER_IP_N = "serverip_n";
        public static String CLIENT_PORT = "clientport";
        public static String SERVER_PORT = "serverport";
        public static String CLIENT_IP_OUTER = "clientip_outer";
        public static String SERVER_IP_OUTER = "serverip_outer";
        public static String CLIENT_PORT_OUTER = "clientport_outer";
        public static String SERVER_PORT_OUTER = "serverport_outer";
        public static String PROTOCOL_OUTER = "protocol_outer";
        public static String UP_PKT = "uppkt";
        public static String UP_BYTE = "upbyte";
        public static String DOWN_PKT = "downpkt";
        public static String DOWN_BYTE = "downbyte";
        public static String DATA_TYPE = "datatype";
        public static String IMSI = "imsi";
        public static String CASE_TAGS = "casetags";
        /* TCP */
        public static String DURATION = "duration";
        public static String SYN_FLAG = "synflag";
        public static String FIN_FLAG = "finflag";
        /* EXTENSION */
        public static String EXTENSION = "extension";
        /* SESSION */
        public static String UP_PAYLOAD = "uppayload";
        public static String DOWN_PAYLOAD = "downpayload";
        /* HTTP */
        public static String HOST = "host";
        public static String URL_ROOT = "url_root";
        public static String METHOD = "method";
        public static String USER_AGENT = "user_agent";
        public static String CONTENT_LENGTH = "content_length";
        /* ESP&AH */
        public static String SPI = "spi";
        /* ISAKMP */
        public static String MESSAGE_LIST = "message_list";
        public static String INITIATOR_INFORMATION = "initiator_information";
        public static String RESPONDER_INFORMATION = "responder_information";
        public static String INITIATOR_VIDS = "initiator_vid";
        public static String RESPONDER_VID = "responder_vid";
        /* SPECIAL */
        public static String KEY = "key";
        public static String IPS = "ips";
        public static String DOMAIN = "domain";
        public static String SERVER_NAME = "servername";
        public static String SHA1 = "sha1";
        public static String CERCHAIN = "cerchain";
        public static String CLIENT_CERCHAIN = "clientcerchain";
        public static String IS_DOUBLE = "isdouble";
        public static String RANDOM = "random";
        public static String VERSION = "version";
        public static String CIPHER_SUITES = "ciphersuites";
        public static String CLIENT_CIPHER_SUITE = "clientciphersuite";
        public static String HAND_SHAKE = "handshake";
        public static String MALFORMED_UP_PAYLOAD = "malformed_uppayload";
        public static String MALFORMED_DOWN_PAYLOAD = "malformed_downpayload";

    }

}
