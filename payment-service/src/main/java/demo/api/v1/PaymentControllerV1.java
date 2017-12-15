package demo.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by Thinkpad on 2017/12/11 0011.
 */

@RestController
@RequestMapping("/v1")
public class PaymentControllerV1 {

    private PaymentServiceV1 paymentServiceV1;

    @Autowired
    public PaymentControllerV1(PaymentServiceV1 paymentServiceV1){
        this.paymentServiceV1 = paymentServiceV1;
    }

    @RequestMapping(path = "/charge", method = RequestMethod.GET, name = "charge")
    public ResponseEntity charge(@PathVariable Integer chargeCount,@PathVariable Integer chargeMoney,@PathVariable String userId) {
        return Optional.ofNullable(paymentServiceV1.charge(chargeCount,chargeMoney,userId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
