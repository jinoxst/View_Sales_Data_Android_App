package jp.ne.jinoxst.mas.itg.uriage.pojo;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Uriage implements Serializable {
    @JsonKey(decamelize = true)
    private int emmId;

    @JsonKey(decamelize = true)
    private String emmNm;

    @JsonKey(decamelize = true)
    private int price;

    @JsonKey(decamelize = true)
    private int selledCnt;

    @JsonKey(decamelize = true)
    private int selledSum;

    @JsonKey(decamelize = true)
    private String brandImg;

    @JsonKey(decamelize = true)
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

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

    public int getSelledCnt() {
        return selledCnt;
    }

    public void setSelledCnt(int selledCnt) {
        this.selledCnt = selledCnt;
    }

    public int getSelledSum() {
        return selledSum;
    }

    public void setSelledSum(int selledSum) {
        this.selledSum = selledSum;
    }

    public String getBrandImg() {
        return brandImg;
    }

    public void setBrandImg(String brandImg) {
        this.brandImg = brandImg;
    }
}
