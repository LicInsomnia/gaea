package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.core.base.tool.util.NgramUtils;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author liuming
 */
@Component
@Slf4j
public class DgaCheck implements InitializationRequired, Serializable {
    double thresh;
    double[][] probMatrix;
    String accepted_chars;
    private NgramUtils ngram;

    public DgaCheck() {
    }

    public void init() {
    }

    public DgaCheck(String accepted_chars) {
        ngram = new NgramUtils();
        ngram.initiate(accepted_chars);
    }

    public Model train(String trainFile, String badFile, String goodFile) {
        probMatrix = ngram.trainProbMatrix(trainFile);
        thresh = ngram.getThresh(probMatrix, badFile, goodFile);
        accepted_chars = ngram.getAccepted_chars();
        return new Model(thresh, probMatrix, accepted_chars);
    }

    public void dump(Model model, String modelPath) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(modelPath))) {
            os.writeObject(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String modelPath) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(modelPath))) {
            Model model = (Model) is.readObject();
            this.thresh = model.thresh;
            this.probMatrix = model.probMatrix;
            this.accepted_chars = model.accepted_chars;
            ngram = new NgramUtils();
            ngram.initiate(accepted_chars);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean check(String line) {
        Double result = this.ngram.getAvgTransitionProb(this.probMatrix, line);
        return result < this.thresh;
    }

    public Double checkByDistribution(String line) {
        Double result = this.ngram.getAvgTransitionProb(this.probMatrix, line);
        return result;
    }

    public double getThresh() {
        return thresh;
    }

    class Model implements Serializable {
        double thresh;
        double[][] probMatrix;
        String accepted_chars;

        Model(double thresh, double[][] probMatrix, String accepted_chars) {
            this.thresh = thresh;
            this.probMatrix = probMatrix;
            this.accepted_chars = accepted_chars;
        }
    }
}
