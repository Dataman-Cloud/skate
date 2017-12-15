package demo.api.v1;

import com.dataman.squid.client.SquidCallbackClient;
import com.dataman.squid.client.SquidClientBuilder;
import com.dataman.squid.core.SquidService;
import com.google.common.collect.Sets;
import demo.account.Account;
import demo.account.AccountRepository;
import demo.model.PayRequest;
import demo.model.PayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Set;


/**
 * Created by Thinkpad on 2017/12/12 0012.
 */
public class ChargeServiceV1 implements SquidService<PayRequest,PayResponse> {


    @Autowired
    private AccountRepository accountRepository;

    private static Logger logger = LoggerFactory.getLogger(ChargeServiceV1.class);
    private SquidCallbackClient<PayRequest,PayResponse> squidCallbackClient;

    private Set<String> set;

    @Override
    public PayResponse execute(PayRequest payRequest, long l, long l1) throws InterruptedException {
        logger.debug("received parameters:{}",payRequest);
        if (payRequest==null)
            return null;

        try {
            //初始化client实例
            init();
            Account account = accountRepository.findByUserId(payRequest.getUserId());
            account.setAccountNumber(account.getAccountNumber()+payRequest.getChargeMoney());
            accountRepository.save(account);

            set.add(payRequest.getId());

            if (squidCallbackClient==null){
                logger.error("squidclient is init failed");
                return null;
            }



        }catch (Exception e){
            logger.error(e.getMessage());

        }finally {
            //关闭client实例
            shutdown();
            logger.info("更新的账户数："+set.size());
        }
        return new PayResponse(payRequest.getId());
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public String getVersion() {
        return "master-SNAPSHOT";
    }

    private void init( ){
        set = Sets.newConcurrentHashSet();
        squidCallbackClient = SquidClientBuilder.newCallbackClient(PayRequest.class,PayResponse.class)
                .dependencyServiceName(System.getenv("SQUID_DEMO_CLIENT_DEPENDENCY_SERVICE_NAME"))
                .dependencyServiceNamespace(System.getenv("SQUID_DEMO_CLIENT_DEPENDENCY_SERVICE_NAMESPACE"))
                .responseTimeout(5000)
                .build();
        logger.info("onComplete build client, client:{}", squidCallbackClient);
    }

    private void shutdown(){
        if (squidCallbackClient !=null){
            squidCallbackClient.shutdown();
            squidCallbackClient = null;
        }
        set = null;
    }


}
