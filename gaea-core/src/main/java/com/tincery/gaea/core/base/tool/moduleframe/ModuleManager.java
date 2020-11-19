package com.tincery.gaea.core.base.tool.moduleframe;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ModuleManager {
    private Map<String, ModuleConnection> topology;
    private Map<String, BaseModule> formedModuleMap = null;
    private Integer maxThreadNum;
    private ArrayList<BaseModule> moduleList = new ArrayList<>();
    private String packageName;
    private String[] args;

    public ModuleManager(Map<String, ModuleConnection> topology, Integer maxThreadNum, String packageName, String[] args) {
        this.topology = topology;
        this.maxThreadNum = maxThreadNum;
        this.packageName = packageName;
        this.args = args;
    }

    public ModuleManager(Map<String, ModuleConnection> topology, Map<String, BaseModule> formedModuleMap, Integer maxThreadNum, String packageName, String[] args) {
        this.topology = topology;
        this.formedModuleMap = formedModuleMap;
        this.maxThreadNum = maxThreadNum;
        this.packageName = packageName;
        this.args = args;
    }

    public boolean init() {
        try {
            for (String className : topology.keySet()) {
                BaseModule module;
                if (formedModuleMap != null && formedModuleMap.containsKey(className)) {
                    module = formedModuleMap.get(className);
                } else {
                    module = createModule(packageName, className, args);
                }
                if (module == null) {
                    System.err.println(className + " module create error.");
                    return false;
                }
                if (!initModule(module, topology.get(className).getInputQueues(), topology.get(className).getOutputQueues())) {
                    System.err.println(className + " module init error.");
                    return false;
                }
                moduleList.add(module);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void run(boolean monitorFlag) {
        ExecutorService executorService = new ScheduledThreadPoolExecutor(maxThreadNum, new BasicThreadFactory.Builder().namingPattern("scheduled-pool-%d").daemon(true).build());
        for (BaseModule module : moduleList) {
            if (((ThreadPoolExecutor) executorService).getActiveCount() < maxThreadNum) {
                executorService.execute(module);
            } else {
                System.err.println("Module threads number exceeds.");
            }
        }
        if (monitorFlag) {
            Monitor monitor = new Monitor(executorService);
            Thread thread = new Thread(monitor);
            thread.start();
        } else {
            executorService.shutdown();
        }
        try {
            if(!executorService.awaitTermination(20, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            e.printStackTrace();
        }
    }

    private BaseModule createModule(String packageName, String moduleName, String[] args) {
        try {
            Class moduleClass = Class.forName(packageName + "." + moduleName);
            Constructor moduleConstructor = moduleClass.getConstructor();
            return (BaseModule) moduleConstructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean initModule(BaseModule module, List<DataQueue> inputQueues, List<DataQueue> outputQueues) {
        try {
            if (module.setInput(inputQueues) && module.setOutput(outputQueues)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class Monitor implements Runnable {
        ExecutorService executorService;
        Long interval = 1000L;

        public Monitor(ExecutorService executorService, Long interval) {
            this.executorService = executorService;
            this.interval = interval;
        }

        public Monitor(ExecutorService executorService) {
            this.executorService = executorService;
        }

        public void run() {
            Map<Integer, ArrayList<String>> levelClassMap = analyzeTopologyLevel();
            try {
                while (((ThreadPoolExecutor) executorService).getActiveCount() > 0) {
                    renewExecutor(levelClassMap);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            executorService.shutdown();
        }

        private void renewExecutor(Map<Integer, ArrayList<String>> levelClassMap) {
            for (Integer level = 0; level <= getHighestLevel(levelClassMap); level++) {
                ArrayList<String> classList = levelClassMap.get(level);
                for (String className : classList) {
                    List<DataQueue> inputQueues = topology.get(className).getInputQueues();
                    for (DataQueue queue : inputQueues) {
                        if (queue.size() > queue.getCapacity() / 2 && ((ThreadPoolExecutor) executorService).getActiveCount() < maxThreadNum) {
                            BaseModule module = createModule(packageName, className, args);
                            if (module == null) {
                                System.err.println(className + " module create error.");
                                continue;
                            }
                            if (!initModule(module, topology.get(className).getInputQueues(), topology.get(className).getOutputQueues())) {
                                System.err.println(className + " module init error.");
                                continue;
                            }
                            executorService.execute(module);
                            return;
                        }
                    }
                }
            }
        }

        private Map<Integer, ArrayList<String>> analyzeTopologyLevel() {
            Map<String, Integer> classLevelMap = new HashMap<>();
            Map<Integer, ArrayList<String>> levelClassMap = new HashMap<>();
            for (String className : topology.keySet()) {
                if (topology.get(className).getOutputQueues().size() == 0) {
                    classLevelMap.put(className, 0);
                    ArrayList newList = levelClassMap.getOrDefault(0, new ArrayList<>());
                    newList.add(className);
                    levelClassMap.put(0, newList);
                }
            }
            for (Integer level = 0; level <= getHighestLevel(levelClassMap); level++) {
                for (String classNameChild : levelClassMap.get(level)) {
                    Set<DataQueue> inputQueues = new HashSet<>();
                    inputQueues.addAll(topology.get(classNameChild).getInputQueues());
                    for (String classNameParent : topology.keySet()) {
                        Set<DataQueue> outputQueues = new HashSet<>();
                        outputQueues.addAll(topology.get(classNameParent).getOutputQueues());
                        if (!Sets.intersection(inputQueues, outputQueues).isEmpty()) {
                            Integer parentLevel = getParentLevel(classNameParent, level, classLevelMap);
                            if (classLevelMap.containsKey(classNameParent)) {
                                ArrayList newList = levelClassMap.get(classLevelMap.get(classNameParent));
                                newList.remove(classNameParent);
                                levelClassMap.put(classLevelMap.get(classNameParent), newList);
                            }
                            classLevelMap.put(classNameParent, parentLevel);
                            ArrayList newList = levelClassMap.getOrDefault(parentLevel, new ArrayList<>());
                            newList.add(classNameParent);
                            levelClassMap.put(parentLevel, newList);
                        }
                    }
                }
            }
            return levelClassMap;
        }

        private Integer getParentLevel(String classNameParent, Integer childLevel, Map<String, Integer> classLevelMap) {
            Integer parentLevel;
            if (classLevelMap.containsKey(classNameParent)) {
                parentLevel = Math.max(classLevelMap.get(classNameParent), childLevel + 1);
            } else {
                parentLevel = childLevel + 1;
            }
            return parentLevel;
        }

        private Integer getHighestLevel(Map<Integer, ArrayList<String>> levelClassMap) {
            Integer highestLevel = 0;
            for (Integer level : levelClassMap.keySet()) {
                if (level > highestLevel) {
                    highestLevel = level;
                }
            }
            return highestLevel;
        }
    }
}
