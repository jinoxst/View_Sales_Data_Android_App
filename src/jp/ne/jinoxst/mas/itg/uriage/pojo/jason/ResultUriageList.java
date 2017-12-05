package jp.ne.jinoxst.mas.itg.uriage.pojo.jason;

import java.util.List;

import jp.ne.jinoxst.mas.itg.uriage.pojo.Uriage;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class ResultUriageList {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String ym;

    @JsonKey(decamelize = true)
    private String message;

    public String getYm() {
        return ym;
    }

    public void setYm(String ym) {
        this.ym = ym;
    }

    @JsonKey(decamelize = true)
    private List<Uriage> list;

    public int getStatus() {
        return status;
    }

    public List<Uriage> getList() {
        return list;
    }

    public void setList(List<Uriage> list) {
        this.list = list;
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
}