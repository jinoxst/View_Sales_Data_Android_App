package jp.ne.jinoxst.mas.itg.uriage.pojo.jason;

import java.util.List;

import jp.ne.jinoxst.mas.itg.uriage.pojo.Stock;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class ResultStockList {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String message;

    @JsonKey(decamelize = true)
    private List<Stock> list;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Stock> getList() {
        return list;
    }

    public void setList(List<Stock> list) {
        this.list = list;
    }
}