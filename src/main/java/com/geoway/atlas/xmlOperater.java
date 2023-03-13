package com.geoway.atlas;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class xmlOperater {

    public static final String xmlConfigPath="/zooSchedulerConfig.xml";

    /**
     * 获取zkserve地址
     * @return
     */
    public String getZkAddress(){
        String zkAddress = null;
        SAXReader saxReader = new SAXReader();
        InputStream is = Agent.class.getResourceAsStream(xmlConfigPath);
        Document document;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element root = document.getRootElement();
        Element contactEle = root.element("zkServer");
        zkAddress =contactEle.attributeValue("address");
        return zkAddress;
    }

    public String getPluginsPath(){
        String pluginsPath = null;
        SAXReader saxReader = new SAXReader();
        InputStream is = Agent.class.getResourceAsStream(xmlConfigPath);
        Document document;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element root = document.getRootElement();
        Element contactEle = root.element("plugins");
        pluginsPath =contactEle.attributeValue("path");
        return pluginsPath;
    }

    public String getServicesPath(){
        String servicesPath= null;
        SAXReader saxReader = new SAXReader();
        InputStream is = Agent.class.getResourceAsStream(xmlConfigPath);
        Document document;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element root = document.getRootElement();
        Element contactEle = root.element("services");
        servicesPath =contactEle.attributeValue("path");
        return servicesPath;
    }

    /**
     * 任务提交时，根据配置文件的参数设定，将任务规模转换为资源占比，为后续资源调度服务
     * @param scale
     * @return
     */
    public double scale2Percentage(String scale) {
        double result = 0;
        SAXReader saxReader = new SAXReader();
        InputStream is = Agent.class.getResourceAsStream(xmlConfigPath);
        Document document;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element root = document.getRootElement();
        Element contactEles = root.element("TaskScales");
        List<Element> contactElesList = contactEles.elements("TaskScale");
        for (Element contactEle : contactElesList) {
            if(contactEle.attributeValue("id").equals(scale)){
                result=Double.parseDouble(contactEle.attributeValue("precents"));
            }
        }
        return result;
    }

    /**
     * 在zookeeper另外注册一套服务地址时，需要根据服务的资源占比与配置文件参数来为服务打上规模标签
     * @param percentage
     * @return
     */
    public String percentage2Scale(double percentage){
        String result = "";
        double largePercent =0;
        double smallPercent =0;
        SAXReader saxReader = new SAXReader();
        InputStream is = Agent.class.getResourceAsStream(xmlConfigPath);
        Document document;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element root = document.getRootElement();
        Element contactEles = root.element("ServiceScales");
        List<Element> contactElesList = contactEles.elements("ServiceScale");
        for (Element contactEle : contactElesList) {
            String temp = contactEle.attributeValue("id");
            switch (temp) {
                case "Large": largePercent = Double.parseDouble(contactEle.attributeValue("precents"));
                case "Small": smallPercent = Double.parseDouble(contactEle.attributeValue("precents"));
            }
        }
        if(percentage >= largePercent ){
            result= "Large";
        }else if(percentage <= smallPercent){
            result = "Small";
        }else {
            result = "Medium";
        }
        return result;
    }

}
