package com.tincery.gaea.core.base.mgt;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class HeadConst {

    public static final char CSV_SEPARATOR = 0x07;
    public static final String CSV_SEPARATOR_STR = String.valueOf(CSV_SEPARATOR);

    /* 分割线 http分割请求响应用 */
    public static final String GORGEOUS_DIVIDING_LINE = "|------------------------------------------------------------------------------|";


    /* 通用csv文件头 */
    private final static String BASE_COMMON_HEADER = FIELD.GROUP_NAME + CSV_SEPARATOR +
            FIELD.TARGET_NAME + CSV_SEPARATOR +
            FIELD.USER_ID + CSV_SEPARATOR +
            FIELD.SERVER_ID + CSV_SEPARATOR +
            FIELD.SOURCE + CSV_SEPARATOR +
            FIELD.CAPTIME + CSV_SEPARATOR +
            FIELD.CLIENT_MAC + CSV_SEPARATOR +
            FIELD.SERVER_MAC + CSV_SEPARATOR +
            FIELD.PROTOCOL + CSV_SEPARATOR +
            FIELD.PRONAME + CSV_SEPARATOR +
            FIELD.CLIENT_IP + CSV_SEPARATOR +
            FIELD.CLIENT_IP_N + CSV_SEPARATOR +
            FIELD.SERVER_IP + CSV_SEPARATOR +
            FIELD.SERVER_IP_N + CSV_SEPARATOR +
            FIELD.CLIENT_PORT + CSV_SEPARATOR +
            FIELD.SERVER_PORT + CSV_SEPARATOR +
            FIELD.CLIENT_IP_OUTER + CSV_SEPARATOR +
            FIELD.SERVER_IP_OUTER + CSV_SEPARATOR +
            FIELD.CLIENT_PORT_OUTER + CSV_SEPARATOR +
            FIELD.SERVER_PORT_OUTER + CSV_SEPARATOR +
            FIELD.PROTOCOL_OUTER + CSV_SEPARATOR +
            FIELD.UP_PKT + CSV_SEPARATOR +
            FIELD.UP_BYTE + CSV_SEPARATOR +
            FIELD.DOWN_PKT + CSV_SEPARATOR +
            FIELD.DOWN_BYTE + CSV_SEPARATOR +
            FIELD.DATA_TYPE + CSV_SEPARATOR +
            FIELD.IMSI + CSV_SEPARATOR +
            FIELD.IMEI + CSV_SEPARATOR +
            FIELD.MSISDN + CSV_SEPARATOR +
            FIELD.CASE_TAGS + CSV_SEPARATOR +
            FIELD.FOREIGN + CSV_SEPARATOR +
            FIELD.DURATION + CSV_SEPARATOR +
            FIELD.SYN_FLAG + CSV_SEPARATOR +
            FIELD.FIN_FLAG + CSV_SEPARATOR;

    /* ssl csv文件头 */
    public final static String SSL_HEADER = BASE_COMMON_HEADER +
            FIELD.COMPLETE_SESSION + CSV_SEPARATOR +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.HANDSHAKE + CSV_SEPARATOR +
            FIELD.HAS_APPLICATION_DATA + CSV_SEPARATOR +
            FIELD.SERVER_NAME + CSV_SEPARATOR +
            FIELD.VERSION + CSV_SEPARATOR +
            FIELD.CIPHER_SUITES + CSV_SEPARATOR +
            FIELD.KEY_EXCHANGE_ALGORITHM + CSV_SEPARATOR +
            FIELD.AUTHENTICATION_ALGORITHM + CSV_SEPARATOR +
            FIELD.ENCRYPTION_ALGORITHM + CSV_SEPARATOR +
            FIELD.MESSAGE_AUTHENTICATION_CODES_ALGORITHM + CSV_SEPARATOR +
            FIELD.SHA1 + CSV_SEPARATOR +
            FIELD.CLIENT_CER_CHAIN + CSV_SEPARATOR +
            FIELD.CLIENT_JA3 + CSV_SEPARATOR +
            FIELD.CLIENT_FINGER_PRINT + CSV_SEPARATOR +
            FIELD.CLIENT_CIPHER_SUITES + CSV_SEPARATOR +
            FIELD.CLIENT_HASH_ALGORITHMS + CSV_SEPARATOR +
            FIELD.SERVER_CER_CHAIN + CSV_SEPARATOR +
            FIELD.SERVER_JA3 + CSV_SEPARATOR +
            FIELD.SERVER_FINGER_PRINT + CSV_SEPARATOR +
            FIELD.SERVER_ECDH_NAMED_CURVE + CSV_SEPARATOR +
            FIELD.SERVER_ECDH_PUBLIC_KEY_DATA + CSV_SEPARATOR +
            FIELD.SERVER_SIGNATURE_ALGORITHM + CSV_SEPARATOR +
            FIELD.SERVER_ECDH_SIGNATURE_DATA + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* session csv文件头 */
    public final static String SESSION_HEADER = BASE_COMMON_HEADER +
            FIELD.UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* dns csv文件头 */
    public final static String DNS_HEADER = BASE_COMMON_HEADER +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.DOMAIN + CSV_SEPARATOR +
            FIELD.CNAMES + CSV_SEPARATOR +
            FIELD.IPS + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* openvpn csv文件头 */
    public final static String OPENVPN_HEADER = SSL_HEADER;
    /* ssh csv文件头 */
    public final static String SSH_HEADER = BASE_COMMON_HEADER +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* isakmp csv文件头 */
    public final static String ISAKMP_HEADER = BASE_COMMON_HEADER +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* ftpandtelnet csv文件头 */
    public final static String FTPANDTELNET_HEADER = BASE_COMMON_HEADER +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* email csv文件头 */
    public final static String EMAIL_HEADER = BASE_COMMON_HEADER +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.EML_NAME + CSV_SEPARATOR +
            FIELD.LOGIN_USER + CSV_SEPARATOR +
            FIELD.LOGIN_PASS + CSV_SEPARATOR +
            FIELD.PROPER + CSV_SEPARATOR +
            FIELD.I_DIRECT + CSV_SEPARATOR +
            FIELD.SENDER + CSV_SEPARATOR +
            FIELD.RCPT_TO + CSV_SEPARATOR +
            FIELD.IF_IMAP_PART + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* ESP&AH csv文件头 */
    public final static String ESPANDAH_HEADER = BASE_COMMON_HEADER +
            FIELD.C2S_SPI + CSV_SEPARATOR +
            FIELD.S2C_SPI + CSV_SEPARATOR +
            FIELD.UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* http csv文件头 */
    public final static String HTTP_HEADER = BASE_COMMON_HEADER +
            FIELD.HOST + CSV_SEPARATOR +
            FIELD.METHOD + CSV_SEPARATOR +
            FIELD.URL_ROOT + CSV_SEPARATOR +
            FIELD.USER_AGENT + CSV_SEPARATOR +
            FIELD.CONTENT_LENGTH + CSV_SEPARATOR +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.META_LIST + CSV_SEPARATOR +
            FIELD.EXTENSION;

    /* pptpandl2tp csv文件头 */
    public final static String PPTPANDL2TP_HEADER = BASE_COMMON_HEADER +
            FIELD.MALFORMED_UP_PAYLOAD + CSV_SEPARATOR +
            FIELD.MALFORMED_DOWN_PAYLOAD + CSV_SEPARATOR +
            FIELD.EXTENSION;
    /* wechat csv文件头 */
    public final static String WECHAT_HEADER = BASE_COMMON_HEADER +
            FIELD.WXNUM + CSV_SEPARATOR +
            FIELD.VERSION + CSV_SEPARATOR +
            FIELD.OSTYPE;
    public final static String QQ_HEADER = BASE_COMMON_HEADER +
            FIELD.QQ;
    public final static String SNMP_HEADER = BASE_COMMON_HEADER +
            FIELD.VERSION + CSV_SEPARATOR +
            FIELD.COMMUNITY + CSV_SEPARATOR +
            FIELD.PDUTYPE;

    private HeadConst() {
        throw new RuntimeException();
    }

    public static class FIELD {
        /* COMMON */
        public final static String ID = "id";
        public final static String GROUP_NAME = "groupName";
        public final static String TARGET_NAME = "targetName";
        public final static String USER_ID = "userId";
        public final static String SERVER_ID = "serverId";
        public final static String SOURCE = "source";
        public final static String CAPTIME = "capTime";
        public final static String CLIENT_MAC = "clientMac";
        public final static String SERVER_MAC = "serverMac";
        public final static String PROTOCOL = "protocol";
        public final static String PRONAME = "proName";
        public final static String CLIENT_IP = "clientIp";
        public final static String CLIENT_IP_N = "clientIpN";
        public final static String SERVER_IP = "serverIp";
        public final static String SERVER_IP_N = "serverIpN";
        public final static String CLIENT_PORT = "clientPort";
        public final static String SERVER_PORT = "serverPort";
        public final static String CLIENT_IP_OUTER = "clientIpOuter";
        public final static String SERVER_IP_OUTER = "serverIpOuter";
        public final static String CLIENT_PORT_OUTER = "clientPortOuter";
        public final static String SERVER_PORT_OUTER = "serverPortOuter";
        public final static String PROTOCOL_OUTER = "protocolOuter";
        public final static String UP_PKT = "upPkt";
        public final static String UP_BYTE = "upByte";
        public final static String DOWN_PKT = "downPkt";
        public final static String DOWN_BYTE = "downByte";
        public final static String DATA_TYPE = "dataType";
        public final static String IMSI = "imsi";
        public final static String IMEI = "imei";
        public final static String MSISDN = "msisdn";
        public final static String CASE_TAGS = "caseTags";
        public final static String FOREIGN = "foreign";
        public final static String MALFORMED_UP_PAYLOAD = "malformedUpPayload";
        public final static String MALFORMED_DOWN_PAYLOAD = "malformedDownPayload";
        /* EXTENSION */
        public final static String EXTENSION = "extension";
        /* TCP */
        public final static String DURATION = "duration";
        public final static String SYN_FLAG = "synFlag";
        public final static String FIN_FLAG = "finFlag";
        /* DNS */
        public final static String KEY = "key";
        public final static String IPS = "ips";
        public final static String DOMAIN = "domain";
        public final static String CNAMES = "cname";
        /* SSL */
        public final static String SSL_EXTENSION = "sslExtension";
        public final static String HANDSHAKE = "handshake";
        public final static String COMPLETE_SESSION = "completeSession";
        public final static String HAS_APPLICATION_DATA = "hasApplicationData";
        public final static String SERVER_NAME = "serverName";
        public final static String DAUL_AUTH = "daulAuth";
        public final static String VERSION = "version";
        public final static String CIPHER_SUITES = "cipherSuite";
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
        /* ASSET */
        public final static String ASSET_FLAG = "assetFlag";
        /* SESSION */
        public final static String UP_PAYLOAD = "upPayload";
        public final static String DOWN_PAYLOAD = "downPayload";
        /* HTTP */
        public final static String HOST = "hostList";
        public final static String URL_ROOT = "urlRootList";
        public final static String METHOD = "methodList";
        public final static String USER_AGENT = "userAgentList";
        public final static String CONTENT_LENGTH = "contentLengthList";
        public final static String META_LIST = "metaList";
        /* ESP&AH */
        public final static String C2S_SPI = "c2sSpi";
        public final static String S2C_SPI = "s2cSpi";
        /* ISAKMP */
        public final static String IASKMP_EXTENSION = "isakmpExtension";
        public final static String MESSAGE_LIST = "messageList";
        public final static String INITIATOR_INFORMATION = "initiatorInformation";
        public final static String RESPONDER_INFORMATION = "responderInformation";
        public final static String INITIATOR_VIDS = "initiatorVid";
        public final static String RESPONDER_VID = "responderVid";
        public final static String FIRST_MODE = "firstMode";
        public final static String SECOND_MODE = "secondMode";
        public final static String PROTOCOL_VERSION = "protocolVersion";
        public final static String ISAKMP_VERSION = "version";
        public final static String ENCRYPTION_MESSAGE_PROTOCOL = "encryptedMessageProtocol";
        public final static String SECOND_COMPLETE = "secondComplete";
        public final static String RESPONDER_FIRST_COMPLETE = "responderFirstComplete";
        public final static String RESPONDER_ENCRYPTION_ALGORITM = "responderEncryptionAlgorithm";
        public final static String RESPONDER_KEY_LENGTH = "responderKeyLength";
        public final static String RESPONDER_HASH_ALGORITHM = "responderHashAlgorithm";
        public final static String RESPONDER_AUTHENTICATION_METHOD = "responderAuthenticationMethod";
        public final static String RESPONDER_KEY_EXCHANGE = "responderKeyExchange";
        public final static String RESPONDER_LIFE_DURATION = "responderLifeDuration";
        public final static String RESPONDER_ISAKMP_CER = "responderIsakmpCer";
        public final static String INITIATOR_FIRST_COMPLETE = "initiatorFirstComplete";
        public final static String INITIATOR_ENCRYPTION_ALGORITM = "initiatorEncryptionAlgorithm";
        public final static String INITIATOR_KEY_LENGTH = "initiatorKeyLength";
        public final static String INITIATOR_HASH_ALGORITHM = "initiatorHashAlgorithm";
        public final static String INITIATOR_AUTHENTICATION_METHOD = "initiatorAuthenticationMethod";
        public final static String INITIATOR_KEY_EXCHANGE = "initiatorKeyExchange";
        public final static String INITIATOR_LIFE_DURATION = "initiatorLifeDuration";
        public final static String INITIATOR_ISAKMP_CER = "initiatorIsakmpCer";
        /* PPTPANDL2TP */
        public final static String PPTP_L2TP_EXTENSION = "pptpAndL2tpExtension";
        public final static String RESPONSE = "response";
        public final static String CHALLENGE = "challenge";
        public final static String RESPONSE_NAME = "responseName";
        public final static String CHALLENGE_NAME = "challengeName";
        public final static String AUTHENTICATION_PROTOCOL = "authenticationProtocol";
        public final static String PPTP_AND_L2TP_AUTHENTICATION_ALGORITHM = "authenticationAlgorithm";
        public final static String SUCCESS_MESSAGE = "successMessage";
        public final static String PPTP_AND_L2TP_ENCRYPTION_ALGORITHM = "encryptionAlgorithm";
        /* WECHAT */
        public final static String WXNUM = "wxNum";
        public final static String OSTYPE = "osType";
        /* LOCATION */
        public final static String CLIENT_LOCATION = "clientLocation";
        public final static String SERVER_LOCATION = "serverLocation";
        /* SSH */
        public final static String SSH_EXTENSION = "sshExtension";
        public final static String CLIENT_PROTOCOL = "clientProtocol";
        public final static String SERVER_PROTOCOL = "serverProtocol";
        public final static String CLIENT_KEX_ALGORITHMS = "clientKexAlgorithms";
        public final static String SERVER_KEX_ALGORITHMS = "serverKexAlgorithms";
        public final static String FINAL_KEX_ALGORITHMS = "finalKexAlgorithms";
        public final static String CLIENT_SERVER_HOST_KEY_ALGORITHMS = "clientServerHostKeyAlgorithms";
        public final static String SERVER_SERVER_HOST_KEY_ALGORITHMS = "serverServerHostKeyAlgorithms";
        public final static String FINAL_SERVER_HOST_KEY_ALGORITHMS = "finalServerHostKeyAlgorithms";
        public final static String CLIENT_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER = "clientEncryptionAlgorithmsClient2Server";
        public final static String SERVER_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER = "serverEncryptionAlgorithmsClient2Server";
        public final static String FINAL_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER = "finalEncryptionAlgorithmsClient2Server";
        public final static String CLIENT_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT = "clientEncryptionAlgorithmsServer2Client";
        public final static String SERVER_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT = "serverEncryptionAlgorithmsServer2Client";
        public final static String FINAL_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT = "finalEncryptionAlgorithmsServer2Client";
        public final static String CLIENT_MAC_ALGORITHMS_CLIENT_TO_SERVER = "clientMacAlgorithmsClient2Server";
        public final static String SERVER_MAC_ALGORITHMS_CLIENT_TO_SERVER = "serverMacAlgorithmsClient2Server";
        public final static String FINAL_MAC_ALGORITHMS_CLIENT_TO_SERVER = "finalMacAlgorithmsClient2Server";
        public final static String CLIENT_MAC_ALGORITHMS_SERVER_TO_CLIENT = "clientMacAlgorithmsServer2Client";
        public final static String SERVER_MAC_ALGORITHMS_SERVER_TO_CLIENT = "serverMacAlgorithmsServer2Client";
        public final static String FINAL_MAC_ALGORITHMS_SERVER_TO_CLIENT = "finalMacAlgorithmsServer2Client";
        public final static String CLIENT_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER = "clientCompressionAlgorithmsClient2Server";
        public final static String SERVER_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER = "serverCompressionAlgorithmsClient2Server";
        public final static String FINAL_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER = "finalCompressionAlgorithmsClient2Server";
        public final static String SERVER_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT = "serverCompressionAlgorithmsServer2Client";
        public final static String CLIENT_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT = "clientCompressionAlgorithmsServer2Client";
        public final static String FINAL_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT = "finalCompressionAlgorithmsServer2Client";
        public final static String SERVER_PUBLIC_KEY = "clientPublicKey";
        public final static String CLIENT_PUBLIC_KEY = "serverPublicKey";
        public final static String FINAL_PUBLIC_KEY_ALGORITHMS = "finalPublicKeyAlgorithms";
        /* CER */
        public final static String SUBJECT_CN_STRING = "subject_cn";
        /* QQ */
        public static final String QQ = "qq";
        /* SNMP */
        public static final String COMMUNITY = "community";
        public static final String PDUTYPE = "pduType";
        /* EMAIL */
        public static final String EML_NAME = "emlName";
        public static final String LOGIN_USER = "loginUser";
        public static final String LOGIN_PASS = "loginPass";
        public static final String PROPER = "proper";
        public static final String I_DIRECT = "iDirect";
        public static final String SENDER = "sender";
        public static final String RCPT_TO = "rcptTo";
        public static final String IF_IMAP_PART = "ifImapPart";
    }

    public static class PRONAME {
        public final static String SESSION = "SESSION";
        public final static String SSL = "SSL";
        public final static String OPENVPN = "OPENVPN";
        public final static String DNS = "DNS";
        public final static String HTTP = "HTTP";
        public final static String EMAIL = "EMAIL";
        public final static String SSH = "SSH";
        public final static String ISAKMP = "ISAKMP";
        public final static String FTP = "FTP";
        public final static String TELNET = "TELNET";
        public final static String ESP = "ESP";
        public final static String AH = "AH";
        public final static String MALFORMED = "MALFORMED";
        public final static String OTHER = "OTHER";
        public final static String WECHAT = "WECHAT";
        public final static String PPTP = "PPTP";
        public final static String L2TP = "L2TP";
        public final static String QQ = "QQ";
    }

    public static class APPLICATION_DETECT_MODE {
        public static String PAYLOAD_STRING = "payload";
        public static String DYNAMIC_STRING = "dynamic";
        public static String HTTP_DETECTOR_STRING = "httpdetector";
        public static String KEYOWRD_STRING = "keyword";
        public static String KEYOWRD_TO_APP_STRING = "keyword2app";
        public static String CER_STRING = "cer";
        public static String CONTEXT_STRING = "context";
        public static String SERVERIP_STRING = "serverip";
        public static String DNSREQUEST_STRING = "dnsrequest";
        public static String PROTOCOL_STRING = "protocol";
        public static String DPI_STRING = "dpi";
    }

    public static class LOCATION {
        public static String COUNTRY = "country";
        public static String COUNTRY_ZH = "country_zh";
        public static String REGION = "region";
        public static String REGION_ZH = "region_zh";
        public static String CITY = "city";
        public static String CITY_ZH = "city_zh";
        public static String LNG = "lng";
        public static String LAT = "lat";
        public static String ORGANIZATION = "organization";
        public static String CONNECTION_TYPE = "connectionType";
        public static String ISP = "isp";
        public static String TYPE = "type";
        public static String AUTONOMOUS_SYSTEM_ORGANIZATION = "autonomousSystemOrganization";
        public static String CLOUD_SERVICE = "cloudService";
    }

}
