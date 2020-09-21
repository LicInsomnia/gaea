package com.tincery.gaea.core.base.tool.util;


import com.tincery.gaea.core.base.tool.ToolUtils;

import java.util.Arrays;
import java.util.List;

public class LevelDomainUtils {

    // nTLDs: 国家顶级域名
    private static List<String> nTLDs = Arrays.asList("ac", "ad", "ae", "af", "ag", "ai",
            "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "az",
            "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn",
            "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca", "cc", "cf",
            "cd", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cq", "cr", "cu",
            "cv", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec",
            "ee", "eg", "eh", "er", "es", "et", "eu", "ev", "fi", "fj", "fk",
            "fm", "fo", "fr", "ga", "gd", "ge", "gf", "gg", "gh", "gi", "gl",
            "gm", "gn", "gp", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm",
            "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io", "iq",
            "ir", "is", "it", "jm", "jo", "jp", "je", "ke", "kg", "kh", "ki",
            "km", "kn", "kp", "kr", "kw", "ky", "kz", "la", "lb", "lc", "li",
            "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "me",
            "mg", "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms",
            "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nc", "ne", "nf",
            "ng", "ni", "nl", "no", "np", "nr", "nt", "nu", "nz", "om", "qa",
            "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "pt",
            "pw", "py", "re", "rs", "ro", "ru", "rw", "sa", "sd", "sc", "sd",
            "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr",
            "st", "sv", "su", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj",
            "tk", "tl", "tm", "tn", "to", "tr", "tt", "tv", "tw", "tz", "ua",
            "ug", "uk", "um", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi",
            "vn", "vu", "wf", "ws", "ye", "yt", "za", "zm", "zw");

    // gTLDs: 通用顶级域名
    private static List<String> gTLDs = Arrays.asList("ac", "academy", "accountant",
            "accountants", "actor", "adult", "aero", "agency", "airforce",
            "apartments", "archi", "army", "arpa", "asia", "associates",
            "attorney", "auction", "audio", "band", "bar", "bargains", "beer",
            "berlin", "best", "bid", "bike", "bingo", "bio", "biz", "black",
            "blackfriday", "blue", "boutique", "build", "builders", "business",
            "buzz", "cab", "cafe", "camera", "camp", "capital", "cards",
            "care", "career", "careers", "casa", "cash", "casino", "cat",
            "catering", "cc", "center", "ceo", "chat", "cheap", "christmas",
            "church", "city", "claims", "cleaning", "click", "clinic",
            "clothing", "club", "co", "co", "coach", "codes", "coffee",
            "college", "cologne", "com", "community", "company", "computer",
            "condos", "construction", "consulting", "contractors", "cooking",
            "cool", "coop", "country", "coupons", "credit", "creditcard",
            "cricket", "cruises", "cymru", "dance", "date", "dating", "deals",
            "degree", "delivery", "democrat", "dental", "dentist", "desi",
            "design", "diamonds", "diet", "digital", "direct", "directory",
            "discount", "dog", "domains", "download", "earth", "edu",
            "education", "email", "energy", "engineer", "engineering",
            "enterprises", "equipment", "estate", "events", "exchange",
            "expert", "exposed", "express", "fail", "faith", "family", "fans",
            "farm", "fashion", "feedback", "finance", "financial", "fish",
            "fishing", "fit", "fitness", "flights", "florist", "flowers",
            "football", "forsale", "foundation", "fund", "furniture", "futbol",
            "fyi", "gallery", "game", "garden", "gift", "gifts", "gives",
            "glass", "global", "gold", "golf", "gov", "graphics", "gratis",
            "green", "gripe", "guide", "guitars", "guru", "haus", "healthcare",
            "help", "hiphop", "hockey", "holdings", "holiday", "horse", "host",
            "hosting", "house", "how", "idv", "im", "immo", "immobilien",
            "industries", "info", "ink", "institute", "insure", "int",
            "international", "investments", "jetzt", "jewelry", "jobs",
            "juegos", "kaufen", "kim", "kitchen", "kiwi", "koeln", "land",
            "lat", "lawyer", "lease", "legal", "lgbt", "life", "lighting",
            "limited", "limo", "link", "live", "loan", "loans", "lol",
            "london", "love", "luxury", "maison", "management", "market",
            "marketing", "mba", "me", "media", "memorial", "men", "menu",
            "miami", "mil", "mobi", "moda", "moe", "money", "mortgage",
            "moscow", "movie", "museum", "nagoya", "name", "navy", "net",
            "network", "news", "ngo", "ninja", "nyc", "okinawa", "onl",
            "online", "ooo", "opr", "org", "organic", "osaka", "partners",
            "parts", "party", "photo", "photography", "photos", "pics",
            "pictures", "pink", "pizza", "place", "plumbing", "plus", "poker",
            "porn", "post", "press", "pro", "productions", "properties",
            "property", "pub", "pw", "qpon", "quebec", "racing", "recipes",
            "red", "rehab", "reise", "reisen", "ren", "rent", "rentals",
            "repair", "report", "republican", "rest", "restaurant", "review",
            "reviews", "rich", "rip", "rocks", "rodeo", "ruhr", "run", "sale",
            "sarl", "school", "schule", "science", "scot", "services", "sexy",
            "shiksha", "shoes", "show", "singles", "site", "ski", "so",
            "soccer", "social", "software", "solar", "solutions", "soy",
            "space", "studio", "style", "sucks", "supplies", "supply",
            "support", "surf", "surgery", "sx", "systems", "tattoo", "tax",
            "taxi", "team", "tech", "technology", "tel", "tennis", "theater",
            "tickets", "tienda", "tips", "tires", "tm", "today", "tokyo",
            "tools", "top", "tours", "town", "toys", "trade", "training",
            "travel", "tv", "university", "uno", "vacations", "vegas",
            "ventures", "vet", "viajes", "video", "villas", "vision", "vodka",
            "vote", "voting", "voto", "voyage", "wales", "wang", "watch",
            "webcam", "website", "wedding", "wien", "wiki", "win", "work",
            "works", "world", "ws", "wtf", "xxx", "xyz", "yoga", "yokohama",
            "zone", "москва", "онлайн", "сайт", "بازار", "بھارت", "شبكة",
            "भारत", "संगठन", "ভারত", "ਭਾਰਤ", "ભારત", "இந்தியா", "భారత్", "みんな",
            "世界", "中国", "中文网", "公司", "在线", "机构", "移动", "网络");

    public static String getRD(String url) {
        String tld = TLD(url);
        if (null == tld) {
            return null;
        }
        String[] elements = tld.split("\\.");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < elements.length; i++) {
            stringBuilder.append(elements[i]).append(".");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static String getTD(String url) {
        String tld = TLD(url);
        return null == tld ? null : tld.split("\\.")[0];
    }

    public static String getSD(String url) {
        String sld = SLD(url);
        return null == sld ? null : sld.split("\\.")[0];
    }

    public static String TLD(String url) {
        try {
            if (null == url || url.isEmpty() || ":".equals(url)) {
                return null;
            }
            String src = url;
            if (url.contains(":") && url.length() > 1) {
                url = url.split(":")[0];
            }
            if (!ToolUtils.IP2long(url).equals(-1L)) {
                return src;
            }
            String[] field = url.split("\\.");
            int idx = field.length - 1;
            if (idx < 1) {
                return null;
            }
            if (field[idx].contains("/")) {
                field[idx] = field[idx].split("/")[0];
            }
            if (idx == 1) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String ele : field) {
                    stringBuilder.append(ele).append(".");
                }
                stringBuilder.setLength(stringBuilder.length() - 1);
                return stringBuilder.toString();
            }
            String TLD;
            if (nTLDs.contains(field[idx])) {
                if (gTLDs.contains(field[idx - 1])) {
                    TLD = field[idx - 2] + "." + field[idx - 1] + "." + field[idx];
                } else {
                    TLD = field[idx - 1] + "." + field[idx];
                }
            } else {
                TLD = field[idx - 1] + "." + field[idx];
            }
            return TLD;
        } catch (Exception e) {
            System.out.println("Wrong url: " + url);
            e.printStackTrace();
            return url;
        }
    }

    public static String SLD(String url) {
        try {
            if (null == url || url.isEmpty() || ":".equals(url)) {
                return null;
            }
            String src = url;
            if (url.contains(":") && url.length() > 1) {
                url = url.split(":")[0];
            }
            if (!ToolUtils.IP2long(url).equals(-1L)) {
                return src;
            }
            String[] field = url.split("\\.");
            int idx = field.length - 1;
            if (idx < 1) {
                return null;
            }
            if (field[idx].contains("/")) {
                field[idx] = field[idx].split("/")[0];
            }
            if (idx <= 2) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String ele : field) {
                    stringBuilder.append(ele).append(".");
                }
                stringBuilder.setLength(stringBuilder.length() - 1);
                return stringBuilder.toString();
            }
            String SLD;
            if (nTLDs.contains(field[idx])) {
                if (gTLDs.contains(field[idx - 1])) {
                    SLD = field[idx - 3] + "." + field[idx - 2] + "." + field[idx - 1] + "." + field[idx];
                } else {
                    SLD = field[idx - 2] + "." + field[idx - 1] + "." + field[idx];
                }
            } else {
                SLD = field[idx - 2] + "." + field[idx - 1] + "." + field[idx];
            }
            return SLD;
        } catch (Exception e) {
            System.out.println("Wrong url: " + url);
            e.printStackTrace();
            return url;
        }
    }

    public static String ThLD(String url) {
        try {
            if (null == url || url.isEmpty() || ":".equals(url)) {
                return null;
            }
            String src = url;
            if (url.contains(":") && url.length() > 1) {
                url = url.split(":")[0];
            }
            if (!ToolUtils.IP2long(url).equals(-1L)) {
                return src;
            }
            String[] field = url.split("\\.");
            int idx = field.length - 1;
            if (idx < 1) {
                return null;
            }
            if (field[idx].contains("/")) {
                field[idx] = field[idx].split("/")[0];
            }
            if (idx <= 3) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String ele : field) {
                    stringBuilder.append(ele).append(".");
                }
                stringBuilder.setLength(stringBuilder.length() - 1);
                return stringBuilder.toString();
            }
            String ThLD;
            if (nTLDs.contains(field[idx])) {
                if (gTLDs.contains(field[idx - 1])) {
                    ThLD = field[idx - 4] + "." + field[idx - 3] + "." + field[idx - 2] + "." + field[idx - 1] + "." + field[idx];
                } else {
                    ThLD = field[idx - 3] + "." + field[idx - 2] + "." + field[idx - 1] + "." + field[idx];
                }
            } else {
                ThLD = field[idx - 3] + "." + field[idx - 2] + "." + field[idx - 1] + "." + field[idx];
            }
            return ThLD;
        } catch (Exception e) {
            System.out.println("Wrong url: " + url);
            e.printStackTrace();
            return url;
        }
    }

}
