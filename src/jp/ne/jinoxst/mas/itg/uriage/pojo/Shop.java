package jp.ne.jinoxst.mas.itg.uriage.pojo;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Shop implements Serializable {
    @JsonKey(decamelize = true)
    private int hTotal;

    @JsonKey(decamelize = true)
    private int hDelete;

    @JsonKey(decamelize = true)
    private int sTotal;

    @JsonKey(decamelize = true)
    private int sDelete;

    public int gethTotal() {
        return hTotal;
    }

    public void sethTotal(int hTotal) {
        this.hTotal = hTotal;
    }

    public int gethDelete() {
        return hDelete;
    }

    public void sethDelete(int hDelete) {
        this.hDelete = hDelete;
    }

    public int getsTotal() {
        return sTotal;
    }

    public void setsTotal(int sTotal) {
        this.sTotal = sTotal;
    }

    public int getsDelete() {
        return sDelete;
    }

    public void setsDelete(int sDelete) {
        this.sDelete = sDelete;
    }
}
