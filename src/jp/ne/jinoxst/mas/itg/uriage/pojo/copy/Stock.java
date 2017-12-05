package jp.ne.jinoxst.mas.itg.uriage.pojo.copy;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Stock implements Serializable {
    @JsonKey(decamelize = true)
    private int emmId;

    @JsonKey(decamelize = true)
    private String emmNm;

    @JsonKey(decamelize = true)
    private int price;

    @JsonKey(decamelize = true)
    private String brandImg;

    @JsonKey(decamelize = true)
    private int stock;

    public int getEmmId() {
        return emmId;
    }

    public void setEmmId(int emmId) {
        this.emmId = emmId;
    }

    public String getEmmNm() {
        return emmNm;
    }

    public void setEmmNm(String emmNm) {
        this.emmNm = emmNm;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBrandImg() {
        return brandImg;
    }

    public void setBrandImg(String brandImg) {
        this.brandImg = brandImg;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
