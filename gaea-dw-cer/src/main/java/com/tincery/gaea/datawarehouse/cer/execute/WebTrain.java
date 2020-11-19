package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.support.DgaCheck;
import weka.classifiers.meta.RandomSubSpace;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

public class WebTrain {
    String configPath;
    private boolean isInitiated = false;

    public boolean initiate() {
        try {
            configPath = NodeInfo.getConfig() + "\\webcheck\\";
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        isInitiated = true;
        return true;
    }

    public DgaCheck dgaTrain(String accepted_chars, String trainFile, String badFile, String goodFile, String modelName) {
        if(!isInitiated) {
            return null;
        }
        try {
            DgaCheck dga = new DgaCheck(accepted_chars);
            dga.dump(dga.train(trainFile, badFile, goodFile),configPath + modelName + ".model");
            return dga;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RandomSubSpace classifyTrain(String dataSource, String modelName) {
        if(!isInitiated) {
            return null;
        }
        try {
            ConverterUtils.DataSource sourceTrain = new ConverterUtils.DataSource(dataSource);
            Instances dataTrain = sourceTrain.getDataSet();
            if (dataTrain.classIndex() == -1)
                dataTrain.setClassIndex(dataTrain.numAttributes() - 1);
            RandomSubSpace classifyMeta = new RandomSubSpace();
            classifyMeta.buildClassifier(dataTrain);
            SerializationHelper.write(configPath + modelName + ".model", classifyMeta);
            return classifyMeta;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
