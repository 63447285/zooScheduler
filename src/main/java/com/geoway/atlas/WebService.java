package com.geoway.atlas;

/**
 * zookeeper节点存储的服务对象
 */
public class WebService implements java.io.Serializable {
    public String ip;
    public String host;
    public Double cpu;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String scale;

    public double getTotalMemorySize() {
        return totalMemorySize;
    }

    public void setTotalMemorySize(double totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    public double totalMemorySize;
    public double usedMemorySize;
    public double usableMemorySize;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public double getUsedMemorySize() {
        return usedMemorySize;
    }

    public void setUsedMemorySize(double usedMemorySize) {
        this.usedMemorySize = usedMemorySize;
    }

    public double getUsableMemorySize() {
        return usableMemorySize;
    }

    public void setUsableMemorySize(double usableMemorySize) {
        this.usableMemorySize = usableMemorySize;
    }



    @Override
    public String toString() {
        return "WebService{" +
                "ip='" + ip + '\'' +
                ", host='" + host + '\'' +
                ", cpu=" + cpu +
                ", scale=" + scale + '\'' +
                ", totalMemorySize=" + totalMemorySize +
                ", usedMemorySize=" + usedMemorySize +
                ", usableMemorySize=" + usableMemorySize +
                '}';
    }
}
