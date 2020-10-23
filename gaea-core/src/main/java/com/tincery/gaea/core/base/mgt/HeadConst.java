package com.tincery.gaea.core.base.mgt;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class HeadConst {

    public static final char CSV_SEPARATOR = 0x07;
    public static final String CSV_SEPARATOR_STR = String.valueOf(CSV_SEPARATOR);

    /* 通用csv文件头 */
    private final static String BASE_COMMON_HEADER = CSV.GROUP_NAME + CSV_SEPARATOR +
            CSV.TARGET_NAME + CSV_SEPARATOR +
            CSV.USER_ID + CSV_SEPARATOR +
            CSV.SERVER_ID + CSV_SEPARATOR +
            CSV.SOURCE + CSV_SEPARATOR +
            CSV.CAPTIME + CSV_SEPARATOR +
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
            CSV.IMEI + CSV_SEPARATOR +
            CSV.MSISDN + CSV_SEPARATOR +
            CSV.CASE_TAGS + CSV_SEPARATOR +
            CSV.FOREIGN + CSV_SEPARATOR;
    /* session csv文件头 */
    public final static String SESSION_HEADER = BASE_COMMON_HEADER +
            CSV.DURATION + CSV_SEPARATOR +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.UP_PAYLOAD + CSV_SEPARATOR +
            CSV.DOWN_PAYLOAD;
    /* dns csv文件头 */
    public final static String DNS_HEADER = BASE_COMMON_HEADER +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.DOMAIN + CSV_SEPARATOR +
            CSV.CNAMES + CSV_SEPARATOR +
            CSV.IPS + CSV_SEPARATOR +
            CSV.EXTENSION;
    /* ssl csv文件头 */
    public final static String SSL_HEADER = BASE_COMMON_HEADER +
            CSV.DURATION + CSV_SEPARATOR +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.HAND_SHAKE + CSV_SEPARATOR +
            CSV.HAS_APPLICATION_DATA + CSV_SEPARATOR +
            CSV.SERVER_NAME + CSV_SEPARATOR +
            CSV.DAUL_AUTH + CSV_SEPARATOR +
            CSV.VERSION + CSV_SEPARATOR +
            CSV.CIPHER_SUITES + CSV_SEPARATOR +
            CSV.KEY_EXCHANGE_ALGORITHM + CSV_SEPARATOR +
            CSV.AUTHENTICATION_ALGORITHM + CSV_SEPARATOR +
            CSV.ENCRYPTION_ALGORITHM + CSV_SEPARATOR +
            CSV.MESSAGE_AUTHENTICATION_CODES_ALGORITHM + CSV_SEPARATOR +
            CSV.SHA1 + CSV_SEPARATOR +
            CSV.CLIENT_CER_CHAIN + CSV_SEPARATOR +
            CSV.CLIENT_JA3 + CSV_SEPARATOR +
            CSV.CLIENT_FINGER_PRINT + CSV_SEPARATOR +
            CSV.CLIENT_CIPHER_SUITES + CSV_SEPARATOR +
            CSV.CLIENT_HASH_ALGORITHMS + CSV_SEPARATOR +
            CSV.SERVER_CER_CHAIN + CSV_SEPARATOR +
            CSV.SERVER_JA3 + CSV_SEPARATOR +
            CSV.SERVER_FINGER_PRINT + CSV_SEPARATOR +
            CSV.SERVER_ECDH_NAMED_CURVE + CSV_SEPARATOR +
            CSV.SERVER_ECDH_PUBLIC_KEY_DATA + CSV_SEPARATOR +
            CSV.SERVER_SIGNATURE_ALGORITHM + CSV_SEPARATOR +
            CSV.SERVER_ECDH_SIGNATURE_DATA + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD;
    /* openvpn csv文件头 */
    public final static String OPENVPN_HEADER = SSL_HEADER;
    /* ssh csv文件头 */
    public final static String SSH_HEADER = BASE_COMMON_HEADER +
            CSV.SYN_FLAG + CSV_SEPARATOR +
            CSV.FIN_FLAG + CSV_SEPARATOR +
            CSV.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            CSV.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            CSV.CLIENT_PROTOCOL + CSV_SEPARATOR +
            CSV.SERVER_PROTOCOL + CSV_SEPARATOR +
            CSV.CLIENT_KEX_ALGORITHMS + CSV_SEPARATOR +
            CSV.SERVER_KEX_ALGORITHMS + CSV_SEPARATOR +
            CSV.FINAL_KEX_ALGORITHMS + CSV_SEPARATOR +
            CSV.CLIENT_SERVER_HOST_KEY_ALGORITHMS + CSV_SEPARATOR +
            CSV.SERVER_SERVER_HOST_KEY_ALGORITHMS + CSV_SEPARATOR +
            CSV.FINAL_SERVER_HOST_KEY_ALGORITHMS + CSV_SEPARATOR +
            CSV.CLIENT_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.SERVER_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.FINAL_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.CLIENT_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.SERVER_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.FINAL_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.CLIENT_MAC_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.SERVER_MAC_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.FINAL_MAC_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.CLIENT_MAC_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.SERVER_MAC_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.FINAL_MAC_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.CLIENT_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.SERVER_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.FINAL_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER + CSV_SEPARATOR +
            CSV.SERVER_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.CLIENT_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT + CSV_SEPARATOR +
            CSV.FINAL_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT;
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

    private HeadConst() {
        throw new RuntimeException();
    }

    public static class CSV {
        public final static String MALFORMED_UP_PAYLOAD = "malformedUpPayload";
        public final static String MALFORMED_DOWN_PAYLOAD = "malformedDownPayload";
        /* DNS */
        public final static String KEY = "key";
        public final static String IPS = "ips";
        public final static String DOMAIN = "domain";
        public final static String CNAMES = "cname";
        /* SSL */
        public final static String HAND_SHAKE = "handshake";
        public final static String HAS_APPLICATION_DATA = "hasApplicationData";
        public final static String SERVER_NAME = "serverName";
        public final static String DAUL_AUTH = "daulAuth";
        public final static String VERSION = "version";
        public final static String CIPHER_SUITES = "cipherSuites";
        public final static String KEY_EXCHANGE_ALGORITHM = "keyExchangeAlgorithm";
        public final static String AUTHENTICATION_ALGORITHM = "authenticationAlgorithm";
        public final static String ENCRYPTION_ALGORITHM = "encryptionAlgorithm";
        public final static String MESSAGE_AUTHENTICATION_CODES_ALGORITHM = "messageAuthenticationCodesAlgorithm";
        public final static String SHA1 = "sha1";
        public final static String CLIENT_CER_CHAIN = "clientCerChain";
        public final static String CLIENT_JA3 = "clientJA3";
        public final static String CLIENT_FINGER_PRINT = "clientFingerPrint";
        public final static String CLIENT_CIPHER_SUITES = "clientCipherSuites";
        public final static String CLIENT_HASH_ALGORITHMS = "clientHashAlgorithms";
        public final static String SERVER_CER_CHAIN = "serverCerChain";
        public final static String SERVER_JA3 = "serverJA3";
        public final static String SERVER_FINGER_PRINT = "serverFingerPrint";
        public final static String SERVER_ECDH_NAMED_CURVE = "serverECDHNamedCurve";
        public final static String SERVER_ECDH_PUBLIC_KEY_DATA = "serverECDHPublicKeyData";
        public final static String SERVER_SIGNATURE_ALGORITHM = "serverECDHSignatureAlgorithm";
        public final static String SERVER_ECDH_SIGNATURE_DATA = "serverECDHSignatureData";
        /* SSH */
        private final static String CLIENT_PROTOCOL = "clientProtocol";
        private final static String SERVER_PROTOCOL = "serverProtocol";
        private final static String CLIENT_KEX_ALGORITHMS = "clientKexAlgorithms";
        private final static String SERVER_KEX_ALGORITHMS = "serverKexAlgorithms";
        private final static String FINAL_KEX_ALGORITHMS = "finalKexAlgorithms";
        private final static String CLIENT_SERVER_HOST_KEY_ALGORITHMS = "clientServerHostKeyAlgorithms";
        private final static String SERVER_SERVER_HOST_KEY_ALGORITHMS = "serverServerHostKeyAlgorithms";
        private final static String FINAL_SERVER_HOST_KEY_ALGORITHMS = "finalServerHostKeyAlgorithms";
        private final static String CLIENT_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER = "clientEncryptionAlgorithmsClientToServer";
        private final static String SERVER_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER = "serverEncryptionAlgorithmsClientToServer";
        private final static String FINAL_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER = "finalEncryptionAlgorithmsClientToServer";
        private final static String CLIENT_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT = "clientEncryptionAlgorithmsServerToClient";
        private final static String SERVER_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT = "serverEncryptionAlgorithmsServerToClient";
        private final static String FINAL_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT = "finalEncryptionAlgorithmsServerToClient";
        private final static String CLIENT_MAC_ALGORITHMS_CLIENT_TO_SERVER = "clientMacAlgorithmsClientToServer";
        private final static String SERVER_MAC_ALGORITHMS_CLIENT_TO_SERVER = "serverMacAlgorithmsClientToServer";
        private final static String FINAL_MAC_ALGORITHMS_CLIENT_TO_SERVER = "finalMacAlgorithmsClientToServer";
        private final static String CLIENT_MAC_ALGORITHMS_SERVER_TO_CLIENT = "clientMacAlgorithmsServerToClient";
        private final static String SERVER_MAC_ALGORITHMS_SERVER_TO_CLIENT = "serverMacAlgorithmsServerToClient";
        private final static String FINAL_MAC_ALGORITHMS_SERVER_TO_CLIENT = "finalMacAlgorithmsServerToClient";
        private final static String CLIENT_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER = "clientCompressionAlgorithmsClientToServer";
        private final static String SERVER_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER = "serverCompressionAlgorithmsClientToServer";
        private final static String FINAL_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER = "finalCompressionAlgorithmsClientToServer";
        private final static String SERVER_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT = "serverCompressionAlgorithmsServerToClient";
        private final static String CLIENT_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT = "clientCompressionAlgorithmsServerToClient";
        private final static String FINAL_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT = "finalCompressionAlgorithmsServerToClient";
        /* COMMON */
        public static String GROUP_NAME = "groupName";
        public static String TARGET_NAME = "targetName";
        public static String USER_ID = "userId";
        public static String SERVER_ID = "serverId";
        public static String SOURCE = "source";
        public static String CAPTIME = "capTime";
        public static String CLIENT_MAC = "clientMac";
        public static String SERVER_MAC = "serverMac";
        public static String PROTOCOL = "protocol";
        public static String PRONAME = "proName";
        public static String CLIENT_IP = "clientIp";
        public static String CLIENT_IP_N = "clientIp_n";
        public static String SERVER_IP = "serverIp";
        public static String SERVER_IP_N = "serverIp_n";
        public static String CLIENT_PORT = "clientPort";
        public static String SERVER_PORT = "serverPort";
        public static String CLIENT_IP_OUTER = "clientIpOuter";
        public static String SERVER_IP_OUTER = "serverIpOuter";
        public static String CLIENT_PORT_OUTER = "clientPortOuter";
        public static String SERVER_PORT_OUTER = "serverPortOuter";
        public static String PROTOCOL_OUTER = "protocolOuter";
        public static String UP_PKT = "upPkt";
        public static String UP_BYTE = "upByte";
        public static String DOWN_PKT = "downPkt";
        public static String DOWN_BYTE = "downByte";
        public static String DATA_TYPE = "dataType";
        public static String IMSI = "imsi";
        public static String IMEI = "imei";
        public static String MSISDN = "msisdn";
        public static String CASE_TAGS = "caseTags";
        public static String FOREIGN = "foreign";
        /* TCP */
        public static String DURATION = "duration";
        public static String SYN_FLAG = "synFlag";
        public static String FIN_FLAG = "finFlag";
        /* EXTENSION */
        public static String EXTENSION = "extension";
        /* ASSET */
        public static String ASSET_FLAG = "assetFlag";
        /* SESSION */
        public static String UP_PAYLOAD = "upPayload";
        public static String DOWN_PAYLOAD = "downPayload";
        /* HTTP */
        public static String HOST = "host";
        public static String URL_ROOT = "urlRoot";
        public static String METHOD = "method";
        public static String USER_AGENT = "userAgent";
        public static String CONTENT_LENGTH = "contentLength";
        /* ESP&AH */
        public static String SPI = "spi";
        /* ISAKMP */
        public static String MESSAGE_LIST = "messageList";
        public static String INITIATOR_INFORMATION = "initiatorInformation";
        public static String RESPONDER_INFORMATION = "responderInformation";
        public static String INITIATOR_VIDS = "initiatorVid";
        public static String RESPONDER_VID = "responderVid";
    }

    public static class PRONAME {
        public static String SSL = "SSL";
        public static String OPENVPN = "OPENVPN";
        public static String DNS = "DNS";
        public static String HTTP = "HTTP";
        public static String EMAIL = "EMAIL";
        public static String SSH = "SSH";
        public static String ISAKMP = "ISAKMP";
        public static String FTP = "FTP";
        public static String TELNET = "TELNET";
        public static String OTHER = "OTHER";
    }

    public static class MONGO {

        /* ABSTRUCT_DATAWAREHOUSE_SESSION_EXTENSION */
        public static String UP_PAYLOAD_STRING = "upPayload";
        public static String DOWN_PAYLOAD_STRING = "downPayload";
        public static String MALFORMED_UPPAYLOAD = "malformedUpPayload";
        public static String MALFORMED_DOWNPAYLOAD = "malformedDownPayload";
        public static String VERSION_STRING = "version";
        public static String CIPHER_SUITES_STRING = "cipherSuites";
        public static String HAND_SHAKE_STRING = "handShake";
        public static String CERCHAIN_STRING = "cerChain";
        public static String CLIENT_CERCHAIN_STRING = "clientCerChain";
        public static String IS_DOUBLE_STRING = "isDouble";
        public static String CLIENT_CIPHER_SUITE_STRING = "clientCipherSuite";
        public static String SERVER_NAME_STRING = "serverName";
        public static String RANDOM_STRING = "random";
        /* DNS */
        public static String DOMAIN_STRING = "domain";
        public static String IPS = "ips";
        /* HTTP */
        public static String HOST_STRING = "host";
        public static String PARAMETER_STRING = "parameter";
        public static String TLD_STRING = "tld";
        public static String HEADERS_LIST = "headers";
        public static String URL_ROOT_STRING = "urlRoot";
        public static String USER_AGENT_STRING = "userAgent";
        /* EMAIL */
        public static String EMAIL_KEY_STRING = "key";
        public static String MESSAGE_ID_STRING = "messageId";
        public static String RCPT_STRING = "rcpt";
        public static String SUBJECT_STRING = "subject";
        public static String RECEIVED_STRING = "received";
        public static String CHARSET_STRING = "charset";
        public static String CHARSET_OUT_STRING = "charsetOut";
        public static String CONTENT_STRING = "content";
        public static String CONTENT_TXT_STRING = "contentTxt";
        public static String CONTENT_LENGTH_STRING = "contentLength";
        public static String DATE_STRING = "date";
        public static String FROM_STRING = "from";
        public static String TO_STRING = "to";
        public static String CC_STRING = "cc";
        public static String BCC_STRING = "bcc";
        public static String MAIL_ADDRESS_STRING = "mailAddress";
        public static String ATTACH_LIST = "attach";
        public static String ATTACH_NUM_STRING = "attachNum";
        public static String PRIORITY_STRING = "priority";
        public static String DOMAIN_TAG_STRING = "domainTag";
        public static String FILENAME_STRING = "fileName";
        public static String MAIL_LENGTH_STRING = "mailLength";
        public static String TRANSMITTER_STRING = "transmitter";
        public static String LANGUAGE_LIST = "language";
        public static String IS_READ_BOOLEAN = "isread";
        /* CER */
        public static String CER_DOCUMENT = "cer";
        public static String SUBJECT_CN_STRING = "subject_cn";
        public static String SPI_STRING = "spi";

    }

}
