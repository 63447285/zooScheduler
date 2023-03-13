package com.geoway.atlas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent {

    private static Logger log = LoggerFactory.getLogger(Agent.class);
    private static Agent ourInstance = new Agent();

    static String connectString = "";

    //static String connectString = "172.16.76.129:2181";

    private CuratorFramework client;

    //插件目录
    private static final String pluginsPath = "/atlas/plugins";
    //服务目录
    private static final String servicesPath = "/atlas/services";

    private String nodePath; ///qiurunze-manger/service0000001 当前节点路径

    private Map<String, WebService> serverNodes =new HashMap<>();

    private Thread stateThread;

    public static Agent getInstance() {
        xmlOperater xmloperater=new xmlOperater();
        connectString = xmloperater.getZkAddress();
        return ourInstance;
    }

    private Agent() {
    }

    public static void premain(String args, Instrumentation instrumentation) {
        Agent.getInstance().init(connectString, servicesPath);
        //this.init(connectString, servicePath);
    }

    /**
     * 初始化zookeeper连接，并获取注册的服务信息
     * @param connectString
     * @param Path
     */
    public void init(String connectString, String Path) {
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        /**
         *
         */
        log.info("初始化zookeeper连接..." + connectString);
        //CuratorFramework client= CuratorFrameworkFactory.newClient(connectString,retryPolicy);
        this.client = CuratorFrameworkFactory.builder().connectString(connectString)
                .sessionTimeoutMs(60 * 1000).connectionTimeoutMs(15 * 1000).retryPolicy(retryPolicy).build();
        //开启
        this.client.start();
        //获取到根目录下下的节点名称与内容
        getServerNodes(Path);
        //注册另一套

    }

    /**
     * 获取所有插件或服务信息
     * @param path
     */
    public void getServerNodes(String path) {
        List<String> nodepaths = null;
        try {
            nodepaths = client.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("获取zookeeper节点失败");
            throw new RuntimeException(e);
        }
        for (String nodepath : nodepaths) {
            byte[] bytes;
            try {
                bytes = this.client.getData().forPath(path+"/"+ nodepath);
            } catch (Exception e) {
                log.error("获取zookeeper节点" + nodepath + "的值失败");
                throw new RuntimeException(e);
            }
            String nodeValue = new String(bytes);
            System.out.println(nodepath + "---" + nodeValue);
            serverNodes.put(nodepath, Json2WebService(nodeValue));
        }
    }

    /**
     * 注册个服务目录，与插件目录分开
     */
    public  void regisMyServers() {
        init(connectString,pluginsPath);
        double totalResources= getTotalResources(serverNodes);
        try {
            client.create().withMode(CreateMode.PERSISTENT).forPath("/"+servicesPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int i=1;
        for (Map.Entry<String, WebService> entry : serverNodes.entrySet()){
            try {
                WebService ws = entry.getValue();
                ws.scale= percentage2Scale((ws.getTotalMemorySize()/totalResources)*100);
                ws.setTotalMemorySize((ws.getTotalMemorySize()/totalResources)*100);
                ws.setUsableMemorySize((ws.getUsableMemorySize()/totalResources)*100);
                client.create().withMode(CreateMode.PERSISTENT).forPath("/"+servicesPath+"/"+"service"+i, WebService2Json(ws).getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }

//    public  String getServerNodeByMS(double serverTotalMemorySize, double PlanedMemorySize) {
//        String result = null;
//        Map<String, WebService> serverNodes = this.serverNodes;
//        //遍历寻找符合资源要求的服务
//        for (Map.Entry<String, WebService> entry : serverNodes.entrySet()) {
//            WebService tempserver = entry.getValue();
//            if (tempserver.getTotalMemorySize() == serverTotalMemorySize) {
//                //判断该服务剩余资源是否能够支持请求
//                if (tempserver.usableMemorySize < PlanedMemorySize) {
//                    log.info("服务" + entry.getKey() + "资源不足,正在将请求调度到其他服务...");
//                    //获取剩余资源最多的服务
//                    double tempMS = tempserver.usableMemorySize;
//                    WebService newserver = null;
//                    String newserverName = null;
//                    for (Map.Entry<String, WebService> entry1 : serverNodes.entrySet()) {
//                        WebService tempserver1 = entry1.getValue();
//                        if (tempMS < tempserver1.usableMemorySize) {
//                            tempMS = tempserver1.usableMemorySize;
//                            newserver = tempserver1;
//                            newserverName = entry1.getKey();
//                        }
//                    }
//                    //当前服务即为剩余资源最多的服务
//                    if (tempMS == tempserver.usableMemorySize) {
//                        log.info("无符合资源要求的可用服务");
//                    }
//                    //剩余资源最多的为其他服务
//                    else {
//                        //判断剩余资源最多的服务能否满足请求需要
//                        if(tempMS>=PlanedMemorySize){
//                        //获取结果前更改serverNodes的值
//                        newserver.usedMemorySize = newserver.usedMemorySize + PlanedMemorySize;
//                        newserver.usableMemorySize = newserver.usableMemorySize - PlanedMemorySize;
//                        setServerNodesValue(newserverName , newserver);
//                        updateServerNodes();
//                        result = newserver.ip + ":" + newserver.host;}
//                        else{
//                            log.info("无符合资源要求的可用服务");
//                        }
//                    }
//                }
//                else {
//                    //获取结果前更新节点资源数据
//                    tempserver.usedMemorySize= tempserver.usedMemorySize + PlanedMemorySize;
//                    tempserver.usableMemorySize= tempserver.usableMemorySize - PlanedMemorySize;
//                    setServerNodesValue(entry.getKey(), tempserver);
//                    updateServerNodes();
//                    result = tempserver.ip + ":" + tempserver.host;
//                }
//            }
//        }
//        log.info("已将请求调度至" + result + "服务");
//        return result;
//    }

    /**
     * 根据任务规模，获取服务地址，所有服务剩余资源都不能满足任务资源要求，返回为空
     * @param scale
     * @return
     */
    public  String getServerNodeByMS(String scale) {
        double requiredMS = scale2Percentage(scale);
        String result = null;
        Map<String, WebService> serverNodes = this.serverNodes;
        Map<String, WebService> tempMap= new HashMap<>();
        //遍历寻找规模符合要求的服务
        for (Map.Entry<String, WebService> entry : serverNodes.entrySet()) {
            WebService tempServer = entry.getValue();
            if (tempServer.getScale().equals(scale)) {
               tempMap.put(entry.getKey(), tempServer);
            }
        }
        //寻找规模符合要求的服务列表中的剩余资源最多的服务
        double tempMS = 0;
        WebService newServer = null;
        String newServerName = null;
        for (Map.Entry<String, WebService> entry : tempMap.entrySet()){
            WebService tempServer = entry.getValue();
            if(tempServer.getUsableMemorySize() >= tempMS){
                tempMS = tempServer.getUsableMemorySize();
                newServerName = entry.getKey();
                newServer = entry.getValue();
            }
        }
        //判断规模符合要求的服务列表中的剩余资源最多的服务是否支持请求
        if(newServer.usableMemorySize >= requiredMS){
            newServer.usedMemorySize= newServer.usedMemorySize + requiredMS;
            newServer.usableMemorySize= newServer.usableMemorySize - requiredMS;
            setServerNodesValue(newServerName, newServer);
            updateServerNodes();
            result = newServer.ip + ":" + newServer.host;
        } else{
            for (Map.Entry<String, WebService> entry : serverNodes.entrySet()){
                WebService tempServer = entry.getValue();
                if(tempServer.getUsableMemorySize() > tempMS){
                    tempMS = tempServer.getUsableMemorySize();
                    newServerName = entry.getKey();
                    newServer = entry.getValue();
                }
                if(newServer.usableMemorySize >= requiredMS){
                    newServer.usedMemorySize= newServer.usedMemorySize + requiredMS;
                    newServer.usableMemorySize= newServer.usableMemorySize - requiredMS;
                    setServerNodesValue(newServerName, newServer);
                    updateServerNodes();
                    result = newServer.ip + ":" + newServer.host;
                } else{
                    log.info("无符合资源要求的可用服务");
                }
            }
        }
        log.info("已将请求调度至" + result + "服务");
        return result;
    }
    /**
     * 任务执行结束或任务失败，释放任务所占用的资源
     * @param serviceAddress 服务地址 ip:host example: 172.16.67.176:8000
     * @param scale 任务规模(大、中、小) example: medium
     */
    public void releaseServerMemory(String serviceAddress,String scale) {
        Map<String, WebService> serverNodes = this.serverNodes;
        for (Map.Entry<String, WebService> entry : serverNodes.entrySet()){
            WebService tempServer = entry.getValue();
            String tempAddress = tempServer.ip + ":" + tempServer.host;
            if(serviceAddress.equals(tempAddress)){
                tempServer.usedMemorySize= tempServer.usedMemorySize - scale2Percentage(scale);
                tempServer.usableMemorySize= tempServer.usableMemorySize + scale2Percentage(scale);
                setServerNodesValue(entry.getKey(), tempServer);
                updateServerNodes();
            }
        }

    }

    /**
     * 更新节点信息,两种种情况下调用：
     * 1.获取服务地址，代表资源被占用
     * 2.任务运行结束或任务执行失败，释放资源
     */
    public void updateServerNodes() {
        for (Map.Entry<String, WebService> entry : serverNodes.entrySet()) {
            try {
                client.setData().forPath(servicesPath+"/"+entry.getKey(), WebService2Json(entry.getValue()).getBytes());
            } catch (Exception e) {
                log.error("更新zookeeper节点信息失败！");
                throw new RuntimeException(e);
            }
        }
    }

    public String WebService2Json(WebService webservice) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(webservice);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public WebService Json2WebService(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            WebService webService=mapper.readValue(json, WebService.class);
            return webService;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setServerNodesValue(String serverNodeName,WebService webService){
        for (Map.Entry<String, WebService> entry : serverNodes.entrySet()){
            if(entry.getKey().equals(serverNodeName)){
                entry.setValue(webService);
            }
        }

    }

    public double scale2Percentage(String scale){
        xmlOperater xmloperater = new xmlOperater();
        double v = xmloperater.scale2Percentage(scale);
        return  v;
    }

    public String percentage2Scale(double percentage){
        xmlOperater xmloperater = new xmlOperater();
        String s = xmloperater.percentage2Scale(percentage);
        return s;
    }

    /**
     * 获取资源总量，用于服务注册时计算资源占比以及给服务贴规模标签
     * @param Nodes
     * @return
     */
    public double getTotalResources(Map<String, WebService> Nodes) {
        double total = 0;
        for (Map.Entry<String, WebService> entry : Nodes.entrySet()) {
        total += entry.getValue().getTotalMemorySize();
        }
        return total;
    }


}
