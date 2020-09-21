package com.tincery.gaea.core.base.mgt;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class MongoConst {

    public static class MONGO {

        /* COMMON */
        public static String EMPTY_STRING = "";
        public static String ID_OBJECTID = "_id";
        public static String SOURCE_STRING = "source";
        public static String CAPTIME_STRING = "captime";
        public static String CAPTIME_DATE = "captime";
        public static String CAPTIME_LONG = "captime_n";
        public static String STARTTIME_STRING = "starttime";
        public static String STARTTIME_DATE = "starttime";
        public static String STARTTIME_LONG = "starttime_n";
        public static String ENDTIME_STRING = "endtime";
        public static String ENDTIME_DATE = "endtime";
        public static String ENDTIME_LONG = "endtime_n";
        public static String USER_ID_STRING = "userid";
        public static String SERVER_ID_STRING = "serverid";
        public static String PROTOCOL_INTEGER = "protocol";
        public static String PRONAME_STRING = "proname";
        public static String CLIENT_MAC_STRING = "clientmac";
        public static String CLIENT_IP_STRING = "clientip";
        public static String CLIENT_IP_LONG = "clientip_n";
        public static String SERVER_MAC_STRING = "servermac";
        public static String SERVER_IP_STRING = "serverip";
        public static String SERVER_IP_LONG = "serverip_n";
        public static String CLIENT_PORT_INTEGER = "clientport";
        public static String SERVER_PORT_INTEGER = "serverport";
        public static String CLIENT_IP_OUTER_STRING = "clientip_outer";
        public static String CLIENT_LOCATION_OUTER_DOCUMENT = "clientlocation_outer";
        public static String SERVER_IP_OUTER_STRING = "serverip_outer";
        public static String SERVER_LOCATION_OUTER_DOCUMENT = "serverlocation_outer";
        public static String CLIENT_PORT_OUTER_INTEGER = "clientport_outer";
        public static String SERVER_PORT_OUTER_INTEGER = "serverport_outer";
        public static String PROTOCOL_OUTER_INTEGER = "protocol_outer";
        public static String CLIENT_LOCATION_DOCUMENT = "clientlocation";
        public static String SERVER_LOCATION_DOCUMENT = "serverlocation";
        public static String TARGET_NAME_STRING = "targetname";
        public static String GROUP_NAME_STRING = "groupname";
        public static String UP_PAYLOAD_STRING = "uppayload";
        public static String DOWN_PAYLOAD_STRING = "downpayload";
        public static String MALFORMED_UPPAYLOAD = "malformed_uppayload";
        public static String MALFORMED_DOWNPAYLOAD = "malformed_downpayload";
        public static String IMSI_STRING = "imsi";
        public static String DATA_TYPE_INTEGER = "datatype";
        public static String IS_ENC_BOOLEAN = "isenc";
        public static String IS_TRASH_BOOLEAN = "istrash";
        public static String TYPE_STRING = "type";
        public static String CASE_TAGS_LIST = "casetags";
        public static String IS_MAC2OUTER_INTEGER = "ismac2outer";
        /* LOCATION */
        public static String COUNTRY_STRING = "country";
        public static String COUNTRY_ZH_STRING = "country_zh";
        public static String REGION_STRING = "region";
        public static String REGION_ZH_STRING = "region_zh";
        public static String CITY_STRING = "city";
        public static String CITY_ZH_STRING = "city_zh";
        public static String LNG_DOUBLE = "lng";
        public static String LAT_DOUBLE = "lat";
        public static String CONNECTION_TYPE_STRING = "connection_type";
        public static String ISP_STRING = "isp";
        public static String AUTONOMOUS_SYSTEM_ORGANIZATION_STRING = "autonomous_system_organization";
        public static String ORGANIZATION_STRING = "organization";
        public static String CLOUD_SERVICE_STRING = "cloud_service";
        /* FLOW */
        public static String IS_IMPORTANT_BOOLEAN = "isimp";
        public static String IS_ASSET_BOOLEAN = "isasset";
        public static String FLOW_DETAILS_LIST = "flow_details";
        public static String LEVEL_TYPE_INTEGER = "type";
        public static String SESSION_COUNT_LONG = "sessioncount";
        public static String UP_PKT_LONG = "uppkt";
        public static String UP_BYTE_LONG = "upbyte";
        public static String DOWN_PKT_LONG = "downpkt";
        public static String DOWN_BYTE_LONG = "downbyte";
        public static String PKTNUM_LONG = "pktnum";
        public static String PKT_LONG = "pkt";
        public static String BYTENUM_LONG = "bytenum";
        public static String HEAT_VALUE_DOUBLE = "heat";
        /* SESSION */
        public static String DURATION_TIME_LONG = "durationtime";
        /* SSL */
        public static String VERSION_STRING = "version";
        public static String CIPHER_SUITES_STRING = "ciphersuites";
        public static String HAND_SHAKE_STRING = "handshake";
        public static String CERCHAIN_STRING = "cerchain";
        public static String CLIENT_CERCHAIN_STRING = "clientcerchain";
        public static String IS_DOUBLE_STRING = "isdouble";
        public static String CLIENT_CIPHER_SUITE_STRING = "clientciphersuite";
        public static String SERVER_NAME_STRING = "servername";
        public static String RANDOM_STRING = "random";
        /* DNS */
        public static String DOMAIN_STRING = "domain";
        public static String IPS = "ips";
        /* HTTP */
        public static String HOST_STRING = "host";
        public static String PARAMETER_STRING = "parameter";
        public static String TLD_STRING = "tld";
        public static String HEADERS_LIST = "headers";
        public static String URL_ROOT_STRING = "url_root";
        public static String USER_AGENT_STRING = "user_agent";
        /* EMAIL */
        public static String EMAIL_KEY_STRING = "key";
        public static String MESSAGE_ID_STRING = "message_id";
        public static String RCPT_STRING = "rcpt";
        public static String SUBJECT_STRING = "subject";
        public static String RECEIVED_STRING = "received";
        public static String CHARSET_STRING = "charset";
        public static String CHARSET_OUT_STRING = "charset_out";
        public static String CONTENT_STRING = "content";
        public static String CONTENT_TXT_STRING = "content_txt";
        public static String CONTENT_LENGTH_STRING = "content_length";
        public static String DATE_STRING = "date";
        public static String FROM_STRING = "from";
        public static String TO_STRING = "to";
        public static String CC_STRING = "cc";
        public static String BCC_STRING = "bcc";
        public static String MAIL_ADDRESS_STRING = "mail_address";
        public static String ATTACH_LIST = "attach";
        public static String ATTACH_NUM_STRING = "attach_num";
        public static String PRIORITY_STRING = "priority";
        public static String DOMAIN_TAG_STRING = "domain_tag";
        public static String FILENAME_STRING = "file_name";
        public static String MAIL_LENGTH_STRING = "mail_length";
        public static String TRANSMITTER_STRING = "transmitter";
        public static String LANGUAGE_LIST = "language";
        public static String IS_READ_BOOLEAN = "isread";
        /* CER */
        public static String CER_DOCUMENT = "cer";
        public static String SUBJECT_CN_STRING = "subject_cn";
        /* APPLICATION */
        public static String APPLICATION_DOCUMENT = "application";
        public static String APPLICATION_TITLE_STRING = "title";
        public static String APPLICATION_TYPE_LIST = "type";
        public static String APPLICATION_SPECIAL_TAG_LIST = "specialtag";
        public static String APPLICATION_IGNORE_BOOLEAN = "ignore";
        public static String APPLICATION_CHECK_MODE_STRING = "checkmode";
        public static String APPLICATON_MODE_STRING = "mode";
        public static String APPLICATION_DETECT_PAYLOAD_STRING = "payload";
        public static String APPLICATION_DETECT_DYNAMIC_STRING = "dynamic";
        public static String APPLICATION_DETECT_HTTP_DETECTOR_STRING = "httpdetector";
        public static String APPLICATION_DETECT_KEYOWRD_STRING = "keyword";
        public static String APPLICATION_DETECT_KEYOWRD_TO_APP_STRING = "keyword2app";
        public static String APPLICATION_DETECT_CER_STRING = "cer";
        public static String APPLICATION_DETECT_CONTEXT_STRING = "context";
        public static String APPLICATION_DETECT_SERVERIP_STRING = "serverip";
        public static String APPLICATION_DETECT_DNSREQUEST_STRING = "dnsrequest";
        public static String APPLICATION_DETECT_PROTOCOL_STRING = "protocol";
        public static String APPLICATION_DETECT_DPI_STRING = "dpi";
        /* DNS_REQUEST */
        public static String DNS_DOCUMENT = "dns";
        public static String DNS_REQUEST_KEY_STRING = "key";
        /* ALARM_MATERIAL & ALARM */
        public static String RULE_NAME_STRING = "rulename";
        public static String CATEGORY_STRING = "category";
        public static String SUBCATEGORY_STRING = "subcategory";
        public static String CATEGORY_DESC_STRING = "category_desc";
        public static String SUBCATEGORY_DESC_STRING = "subcategory_desc";
        public static String CREATE_USER_STRING = "createuser";
        public static String TITLE_STRING = "title";
        public static String LEVEL_INTEGER = "level";
        public static String LEVEL_STRING = "level";
        public static String TASK_STRING = "task";
        public static String EVENT_DATA_LIST = "eventdata";
        public static String REMARK_STRING = "remark";
        public static String ALARM_KEY_STRING = "key";
        public static String CHECK_MODE_INTEGER = "checkmode";
        public static String CHECK_MODE_STRING = "checkmode";
        public static String ORG_LINK_STRING = "orgLink";
        public static String IS_SYSTEM_BOOLEAN = "isSystem";
        public static String ACCURACY_INTEGER = "accuracy";
        public static String ACCURACY_STRING = "accuracy";
        public static String DESCRIPTION_STRING = "description";
        public static String VIEW_USERS_LIST = "view_users";
        public static String CONTEXT_STRING = "context";
        public static String EXTENSION_DOCUMENT = "extension";
        public static String PATTERN_INTEGER = "pattern";
        public static String PATTERN_STRING = "pattern";
        public static String ASSET_IP_STRING = "assetip";
        public static String ASSET_INFO_DOCUMENT = "assetinfo";
        public static String SHA1 = "sha1";
        public static String PUBLISHER = "publisher";
        /* SESSION_MERGE */
        public static String DATA_SOURCE_STRING = "datasource";
        public static String IS_FOREIGN_BOOLEAN = "isforeign";
        public static String IS_PROTOCOL_KNOWN_BOOLEAN = "isprotocolknown";
        public static String IS_APPLICATION_KNOWN_BOOLEAN = "isappknown";
        public static String IS_MALFORMED_BOOLEAN = "ismalformed";
        public static String APPLICATION_TYPE_STRING = "apptype";
        public static String ASSET_FLAG_INTEGER = "assetflag";
        public static String EXT_LABEL_DOCUMENT = "extlabel";
        public static String TAG_STRING = "tag";
        public static String APPLICATION_ELEMENTS_LIST = "applicationElements";
        public static String KEYWORD_STRING = "keyword";
        public static String ATTACH_DOCUMENT = "attach";
        public static String LABEL_DOCUMENT = "label";
        /* ESP&AH */
        public static String SPI_STRING = "spi";
        /* WECHAT&QQ */
        public static String QQ_STRING = "qq";
        public static String WECHAT_ID_STRING = "wechat_id";
        public static String WECHAT_VERSION_STRING = "wechat_version";
        public static String OS_TYPE_STRING = "os_type";

    }
}
