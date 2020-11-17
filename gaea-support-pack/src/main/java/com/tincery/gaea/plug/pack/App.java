package com.tincery.gaea.plug.pack;

import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;

import java.io.File;
import java.util.Properties;

public class App {

    private static final int BASE_PORT = 11110;
    private static final String NACOS_ADDR = "172.16.1.102:8848";
    private static final String NODE_NAME = "TINCERY_101";
    private static final String[] MODELS = {
            "gaea-controller",
            "gaea-src-flow",
            "gaea-src-session",
            "gaea-src-impsession",
            "gaea-src-ssl",
            "gaea-src-openvpn",
            "gaea-src-dns",
            "gaea-src-http",

            "gaea-src-ssh",
            "gaea-src-ftpandtelnet",
            "gaea-src-pptpandl2tp",
            "gaea-src-isakmp",
            "gaea-src-espandah",
            "gaea-src-wechat",
            "gaea-ods-httpanalysis",
//            "gaea-src-qq",
            "gaea-dw-reorganization",
            "gaea-dm-asset",
            "gaea-dm-alarmcombine",
            "gaea-support-mongostash"
    };
    private static String gaeaPath;

    private static String getBootStrapYml(int index, String modelName) {
        String[] element = modelName.split("-");
        int len = element.length;
        String applicationName;
        switch (len) {
            case 2:
                applicationName = "core_" + element[len - 1];
                break;
            case 3:
                applicationName = element[len - 2] + "_" + element[len - 1];
                break;
            default:
                System.err.println("模块[" + modelName + "]名称定义错误");
                return null;
        }
        return "server:\n" +
                "  port: " + (BASE_PORT + index) + "\n" +
                "node:\n" +
                "  name: " + NODE_NAME + "\n" +
                "spring:\n" +
                "  application:\n" +
                "    name: " + applicationName + "\n" +
                "  profiles:\n" +
                "    active: " + NODE_NAME + "\n" +
                "  cloud:\n" +
                "    nacos:\n" +
                "      config:\n" +
                "        server-addr: " + NACOS_ADDR + "\n" +
                "        name: gaea\n" +
                "        file-extension: yml\n" +
                "        group: GAEA_GROUP\n" +
                "        extension-configs:\n" +
                "          - dataId: gaea_environment.yml\n" +
                "            group: GAEA_GROUP\n" +
                "          - dataId: common_config.yml\n" +
                "            group: GAEA_GROUP\n" +
                "logging:\n" +
                "  level:\n" +
                "    root: info\n";
    }

    private static String getJar(String modelName) {
        File targetPath = new File(gaeaPath + "/" + modelName + "/target/");
        if (!targetPath.exists() || !targetPath.isDirectory()) {
            System.err.println("模块[" + modelName + "]路径不存在");
            return null;
        }
        File[] files = targetPath.listFiles();
        if (null == files) {
            System.err.println("模块[" + modelName + "]路径不存在");
            return null;
        }
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        gaeaPath = properties.getProperty("user.dir");
        String packPath = gaeaPath + "/pack/";
        File pack = new File(packPath);
        if (pack.exists()) {
            deleteFile(pack);
        }
        FileUtils.checkPath(packPath);
        for (int i = 0; i < MODELS.length; i++) {
            String model = MODELS[i];
            String jarFile = getJar(model);
            String bootStrapYmlString = getBootStrapYml(i, model);
            if (null == jarFile || null == bootStrapYmlString) {
                System.out.println("模块[" + model + "]打包失败");
                continue;
            }
            String modelPackPath = packPath + model + "/";
            FileUtils.checkPath(modelPackPath);
            FileUtils.fileCopy(jarFile, modelPackPath + model + ".jar");
            FileWriter fileWriter = new FileWriter(modelPackPath + "bootstrap.yml");
            fileWriter.write(bootStrapYmlString);
            fileWriter.close();
            System.out.println("模块[" + model + "]打包成功");
        }
        // FileUtils.compressFiles2Zip(pack.listFiles(),packPath+"gaea.zip");
    }

    private static void deleteFile(File pack) {
        File[] files = pack.listFiles();
        for (File file : files) {
            if(file.isDirectory()){
                deleteFile(file);
            }else{
                file.delete();
            }
        }
    }

}
