package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

/**
 * Created by Thinkpad on 2017/12/18 0018.
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableEurekaClient
@EnableHystrix
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class,args);
    }

    @Component
    public static class CustomizedRestMvcConfiguration extends RepositoryRestConfigurerAdapter {

        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
            config.setBasePath("/api");
        }
    }

    @LoadBalanced
    @Bean
    public RestTemplate loadRestTemplate() {
        return new RestTemplate();
    }

}
