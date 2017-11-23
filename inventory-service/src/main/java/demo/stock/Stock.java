package demo.stock;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;

import demo.product.Product;

/**
 * 进货表，用于增加商品的数据
 */
@NodeEntity
public class Stock {

    @GraphId
    private Long id;

    @Relationship(type = "PRODUCT_TYPE", direction = "OUTGOING")
    private Product product;

    private Long number; //进货数量

    private String stockUser; //进货人

    private Date stockTime; //进货时间

    private Date syncTime; //同步时间

    private boolean sync=false; //false未同步，true已同步,默认是false

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getStockUser() {
        return stockUser;
    }

    public void setStockUser(String stockUser) {
        this.stockUser = stockUser;
    }

    public Date getStockTime() {
        return stockTime;
    }

    public void setStockTime(Date stockTime) {
        this.stockTime = stockTime;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }
}
