package demo.model;

/**
 * Created by Thinkpad on 2017/12/12 0012.
 */
public class PayResponse {

    public PayResponse(){}

    public PayResponse(String id){
        this.id = id;
    }

    private String id;

    private Double chargeMoney;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getChargeMoney() {
        return chargeMoney;
    }

    public void setChargeMoney(Double chargeMoney) {
        this.chargeMoney = chargeMoney;
    }
}
