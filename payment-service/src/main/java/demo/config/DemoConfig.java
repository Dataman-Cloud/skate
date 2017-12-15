package demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Thinkpad on 2017/12/11 0011.
 */
@Configuration
@ConfigurationProperties(prefix = "squid.demo.client")
public class DemoConfig {
    private String serviceName;

    private String dependencyServiceName;

    private String dependencyServiceNamespace;

    private Integer sleepTime;

    private Integer serverSleepTime;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDependencyServiceName() {
        return dependencyServiceName;
    }

    public void setDependencyServiceName(String dependencyServiceName) {
        this.dependencyServiceName = dependencyServiceName;
    }

    public String getDependencyServiceNamespace() {
        return dependencyServiceNamespace;
    }

    public void setDependencyServiceNamespace(String dependencyServiceNamespace) {
        this.dependencyServiceNamespace = dependencyServiceNamespace;
    }

    public Integer getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Integer getServerSleepTime() {
        return serverSleepTime;
    }

    public void setServerSleepTime(Integer serverSleepTime) {
        this.serverSleepTime = serverSleepTime;
    }
}
