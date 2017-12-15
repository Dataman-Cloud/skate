package demo.api.v1;


import com.dataman.squid.client.SquidBlockingClientBuilder;
import com.dataman.squid.client.SquidCallbackClient;
import com.dataman.squid.core.SquidCallback;
import com.dataman.squid.core.exception.SquidException;
import com.google.common.collect.Sets;
import demo.config.DemoConfig;
import demo.model.PayRequest;
import demo.model.PayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Thinkpad on 2017/12/11 0011.
 */
@Service
public class PaymentServiceV1 {


    @Autowired
    private DemoConfig demoConfig;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Set<String> iniSet ;



    SquidCallbackClient<PayRequest, PayResponse> callbackClientInstances;



    public ResponseEntity<String> charge(Integer chargeCount,Integer chargeMoney,String userId){


        //初始化callback实例
        init();
        //开始发送请求
        newThread(chargeCount,chargeMoney,userId);
        //销毁callback实例
        destroy();
        log.info("iniSet size:"+iniSet.size());
        return new ResponseEntity<String>("T",new HttpHeaders(), HttpStatus.OK);
    }

    private void init(){
        callbackClientInstances = SquidBlockingClientBuilder.newCallbackClient(PayRequest.class,PayResponse.class).
                dependencyServiceName(demoConfig.getDependencyServiceName()).
                dependencyServiceNamespace(demoConfig.getDependencyServiceNamespace()).
                serviceName(demoConfig.getServiceName()).
                responseTimeout(5000).build();

        iniSet = Sets.newConcurrentHashSet();
    }


    private void newThread(Integer chargeCount,Integer chargeMoney,String userId){
        Thread thread = new Thread(()->{
            for (int i=0;i<chargeCount;i++){
                try {
                    PayRequest payRequest = new PayRequest();
                    payRequest.setId(UUID.randomUUID().toString());
                    payRequest.setChargeMoney(chargeMoney);
                    payRequest.setUserId(userId);


                    callbackClientInstances.send(payRequest, new SquidCallback<PayResponse>() {
                        @Override
                        public void onComplete(PayResponse payResponse) {
                            log.info("调用成功");
                            iniSet.add(payResponse.getId());
                        }

                        @Override
                        public void onError(SquidException e) {
                            log.info("调用错误");
                        }

                        @Override
                        public void onTimeout() {
                            log.info("调用超时");
                        }
                    });
                }catch (Exception e){
                       log.error(e.getMessage());
                       log.error("squid调用过程出错,serviceName:{},namespace:{},dependencyNamespace:{},"+
                       "dependencyServiceName:{}",demoConfig.getServiceName(),System.getenv("SQUID_SERVICE_NAMESPACE"),demoConfig.getDependencyServiceNamespace(),demoConfig.getDependencyServiceName());
                }finally {

                }
            }
        });

        thread.start();
    }

    private void destroy(){
        iniSet =null;
        callbackClientInstances.shutdown();
    }


}
