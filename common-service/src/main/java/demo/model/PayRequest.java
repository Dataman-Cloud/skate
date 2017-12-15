package demo.model;

/**
 * Created by Thinkpad on 2017/12/12 0012.
 */
public class PayRequest {

    private String id;

    private String userId;

    private Integer chargeMoney;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getChargeMoney() {
        return chargeMoney;
    }

    public void setChargeMoney(Integer chargeMoney) {
        this.chargeMoney = chargeMoney;
    }
}
