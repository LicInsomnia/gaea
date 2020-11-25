package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.security.cert.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CerCheckSigModule extends BaseModule implements BaseModuleInterface {
    PKIXParameters params;

    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 1);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 1);
    }

    @Override
    public void run() {
        System.out.println("CerCheckSigModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        DataQueue queueOutput = queuesOutput.get(0);
        params = getAnchors();
        while (true) {
            CerData cer = (CerData)queueInput.poll(1, TimeUnit.SECONDS);
            if(cer != null) {
                try {
                    if(cer.getSignatureCheck() == null || !cer.getSignatureCheck()) {
                        cer.setSignatureCheck(checkSig(cer));
                        queueOutput.put(cer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (queueInput.isEnd()) {
                break;
            }
        }
        queueOutput.detach();
        System.out.println("CerCheckSigModule ends.");
    }

    private boolean checkSig(CerData cer) {
        Set<String> cerChainSet = cer.getCerChain();
        for(String cerChain : cerChainSet) {
            List<File> cerFileList = new ArrayList<>();
            String[] cerChainArray = cerChain.split(";");
            for(String cerSha : cerChainArray) {
                cerFileList.add(getCerFile(getRealSha(cerSha)));
            }
            if(checkCerFileChain(cerFileList)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCerFileChain(List<File> cerFileList) {
        List<X509Certificate> myList = getChain(cerFileList);
        if(myList.size() == 0) {
            return false;
        }
        List<X509Certificate> resortList = resortChain(myList);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            CertPath cp = cf.generateCertPath(resortList);
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            cpv.validate(cp, params);
        } catch (CertPathValidatorException cpve) {
            //System.out.println("fail");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private List<X509Certificate> resortChain(List<X509Certificate> myList) {
        if(myList.size() == 0) {
            return new ArrayList<>();
        }
        List<X509Certificate> cerList = new ArrayList<>();
        String issuerCN = "";
        for(X509Certificate cert : myList) {
            String subject = cert.getSubjectX500Principal().getName();
            String subjectCN;
            if(!subject.contains("CN=")) {
                continue;
            }
            if(subject.contains(",")) {
                subjectCN = subject.substring(subject.indexOf("CN=") + 3, subject.indexOf(","));
            } else {
                subjectCN = subject.substring(subject.indexOf("CN=") + 3);
            }
            if(issuerCN.equals("") || subjectCN.equals(issuerCN)) {
                cerList.add(cert);
                String issuer = cert.getIssuerX500Principal().getName();
                if(!issuer.contains("CN=")) {
                    continue;
                }
                if(issuer.contains(",")) {
                    issuerCN = issuer.substring(issuer.indexOf("CN=") + 3, issuer.indexOf(","));
                } else {
                    issuerCN = issuer.substring(issuer.indexOf("CN=") + 3);
                }
            }
        }
        return cerList;
    }

    private List<X509Certificate> getChain(List<File> cerFileList) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            List<X509Certificate> myList = new ArrayList<>();
            for(File file : cerFileList) {
                if(!file.exists()) {
                    return new ArrayList<>();
                }
                FileInputStream fis = new FileInputStream(file);
                X509Certificate cert = (X509Certificate)cf.generateCertificate(fis);
                myList.add(cert);
            }
            return myList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private PKIXParameters getAnchors() {
        Set<TrustAnchor> trustAnchors = new HashSet<>();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String filePath = NodeInfo.getConfig() + "/rootCert/";
            List<File> rootFileList = FileUtils.searchFiles(filePath, "", "", "cer", 0);
            for(File file : rootFileList) {
                FileInputStream fis = new FileInputStream(file);
                Certificate trust = cf.generateCertificate(fis);
                TrustAnchor anchor = new TrustAnchor((X509Certificate) trust, null);
                trustAnchors.add(anchor);
            }
            PKIXParameters params = new PKIXParameters(trustAnchors);
            params.setRevocationEnabled(false);
            return params;
        } catch (Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private File getCerFile(String sha) {
//        String basePath = "D:\\data5\\datawarehouse\\json\\cerChain";
        String basePath = "/opt/gaea/data/data/cer/";
        String folder = sha.substring(0, 2);
        String path = basePath + "/" + folder + "/" + sha + ".cer";
        return new File(path);
    }

    private String getRealSha(String sha) {
        return sha.substring(0, sha.indexOf("_"));
    }
}
