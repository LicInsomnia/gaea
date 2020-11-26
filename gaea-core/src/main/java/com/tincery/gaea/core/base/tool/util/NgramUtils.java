package com.tincery.gaea.core.base.tool.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuming
 */
public class NgramUtils implements Serializable {
    private String accepted_chars;
    private Map<String, Integer> posMap;

    public void initiate() {
        posMap = getPos(accepted_chars);
    }

    public void initiate(String accepted_chars) {
        this.accepted_chars = accepted_chars;
        posMap = getPos(accepted_chars);
    }

    public String getAccepted_chars() {
        return accepted_chars;
    }

    public double[][] trainProbMatrix(String trainFile) {
        double[][] probMatrix = new double[accepted_chars.length()][accepted_chars.length()];
        for(int i=0; i<accepted_chars.length(); i++) {
            for(int j=0; j< accepted_chars.length(); j++) {
                probMatrix[i][j] = 10;
            }
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(trainFile));
            String line;
            while ((line = in.readLine()) != null) {
                for(String subStr : ngramSplit(2, line)) {
                    probMatrix[posMap.get(subStr.substring(0,1))][posMap.get(subStr.substring(1,2))]++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0; i<accepted_chars.length(); i++) {
            int sum = 0;
            for(int j=0; j<accepted_chars.length(); j++) {
                sum += probMatrix[i][j];
            }
            for(int j=0; j<accepted_chars.length(); j++) {
                probMatrix[i][j] = Math.log(probMatrix[i][j] / sum);
            }
        }
        return probMatrix;
    }

    public double getThresh(double[][] trainProbMatrix, String badFile, String goodFile) {
        ArrayList<Double> probListGood = getAvgTransitionProbList(trainProbMatrix, goodFile);
        ArrayList<Double> probListBad = getAvgTransitionProbList(trainProbMatrix, badFile);
        Double avgGood;
        Double sum = 0d;
        for(Double element : probListGood) {
            sum += element;
        }
        avgGood = sum / probListGood.size();
        Double avgBad;
        sum = 0d;
        for(Double element : probListBad) {
            sum += element;
        }
        avgBad = sum / probListBad.size();
        Double max = Collections.max(probListBad);
        Double min = Collections.min(probListGood);
        //assert max < min;
        Double thresh = ((max + min) / 2) * 0.5;
        thresh = max;
        return thresh;
    }

    public double getAvgTransitionProb(double[][] trainProbMatrix, String line) {
        try {
            Double prob = 0d;
            int count = 0;
            for(String subStr : ngramSplit(2, line)) {
                prob += trainProbMatrix[posMap.get(subStr.substring(0,1))][posMap.get(subStr.substring(1,2))];
                count++;
            }
            return Math.exp(prob / Math.max(count, 1));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Map<String, Integer> getPos(String chars) {
        Map<String, Integer> posMap = new HashMap<>();
        for (int index = 0; index < chars.length(); index++) {
            posMap.put(chars.substring(index, index + 1), index);
        }
        return posMap;
    }

    private String normalize(String line) {
        StringBuilder newLine = new StringBuilder();
        for (int index = 0; index < line.length(); index++) {
            String subStr = line.substring(index, index + 1).toLowerCase();
            if(accepted_chars.contains(subStr)) {
                newLine.append(subStr);
            }
        }
        return newLine.toString();
    }

    private ArrayList<String> ngramSplit(int n, String line) {
        ArrayList<String> list = new ArrayList<>();
        String filtered = normalize(line);
        for(int index=0; index<filtered.length()-n+1; index++) {
            list.add(filtered.substring(index, index+n));
        }
        return list;
    }

    private ArrayList<Double> getAvgTransitionProbList(double[][] trainProbMatrix, String file) {
        ArrayList<Double> probList = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                probList.add(getAvgTransitionProb(trainProbMatrix, line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return probList;
    }
}