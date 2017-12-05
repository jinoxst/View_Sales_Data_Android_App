package jp.ne.jinoxst.mas.itg.uriage.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xmlpull.v1.XmlPullParser;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.util.Xml;

public class StringUtil {
    public static String getCurrencyFormat(String s) {
        double amount = Double.parseDouble(s);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String getCurrencyFormat(int s) {
        double amount = Double.parseDouble(String.valueOf(s));
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String putSpaceChar(int cnt) {
        String s = "";
        for (int i = 0; i < cnt; i++) {
            s += " ";
        }
        return s;
    }

    public static String[] getLeftmenuList(Resources resources) {
        List<String> list = getAssetsDataForList(resources, "salerecord_leftmenu.xml", null);
        return list.toArray(new String[0]);
    }

    public static List<String> getAssetsDataForList(Resources resources, String filename, String attrname) {
        if (attrname == null) {
            attrname = "name";
        }
        List<String> list = new ArrayList<String>();
        InputStream is = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            AssetManager asset = resources.getAssets();
            is = asset.open(filename);
            InputStreamReader isr = new InputStreamReader(is);
            parser.setInput(isr);
            String tag = "";
            String value = "";
            for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser.next()) {
                switch (type) {
                case XmlPullParser.START_TAG:
                    tag = parser.getName();
                    break;
                case XmlPullParser.TEXT:
                    value = parser.getText();
                    if (value.trim().length() != 0) {
                        if (tag.equals(attrname)) {
                            list.add(value);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("JsonFormatException", ExceptionUtils.getStackTrace(e));
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }

        return list;
    }

    public static String getAssetLineChartHeader(Resources resource) {
        return getAssetTextData(resource, "wv_def_line_header.txt");
    }

    public static String getAssetPieChartHeader(Resources resource) {
        return getAssetTextData(resource, "wv_def_pie_header.txt");
    }

    public static String getAssetChartFooter(Resources resource) {
        return getAssetTextData(resource, "wv_def_footer.txt");
    }

    public static String getAssetTextData(Resources resource, String filename){
        AssetManager as = resource.getAssets();
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = as.open(filename);
            br = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            Log.e("JsonFormatException", ExceptionUtils.getStackTrace(e));
        } finally {
            if (br != null)
                try{
                    br.close();
                }catch(Exception e){}
        }

        return sb.toString();
    }
}
