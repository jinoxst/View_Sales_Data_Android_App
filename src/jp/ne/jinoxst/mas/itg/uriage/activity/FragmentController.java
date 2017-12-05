package jp.ne.jinoxst.mas.itg.uriage.activity;

import java.net.UnknownHostException;
import java.util.List;

import jp.ne.jinoxst.mas.itg.uriage.R;
import jp.ne.jinoxst.mas.itg.uriage.activity.dialog.copy.SimpleDialogFragment;
import jp.ne.jinoxst.mas.itg.uriage.pojo.Shop;
import jp.ne.jinoxst.mas.itg.uriage.pojo.Stock;
import jp.ne.jinoxst.mas.itg.uriage.pojo.Uriage;
import jp.ne.jinoxst.mas.itg.uriage.pojo.jason.ResultShopList;
import jp.ne.jinoxst.mas.itg.uriage.pojo.jason.ResultShopListGen;
import jp.ne.jinoxst.mas.itg.uriage.pojo.jason.ResultStockList;
import jp.ne.jinoxst.mas.itg.uriage.pojo.jason.ResultStockListGen;
import jp.ne.jinoxst.mas.itg.uriage.pojo.jason.ResultUriageList;
import jp.ne.jinoxst.mas.itg.uriage.pojo.jason.ResultUriageListGen;
import jp.ne.jinoxst.mas.itg.uriage.util.Constant;
import jp.ne.jinoxst.mas.itg.uriage.util.DateUtil;
import jp.ne.jinoxst.mas.itg.uriage.util.FileDownload;
import jp.ne.jinoxst.mas.itg.uriage.util.GlobalRegistry;
import jp.ne.jinoxst.mas.itg.uriage.util.StringUtil;
import jp.ne.jinoxst.mas.itg.uriage.util.https.HttpsClientConnector;
import net.vvakame.util.jsonpullparser.JsonFormatException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FragmentController {
    private Object item;
    private Context context;

    public FragmentController(Context context){
        this.context = context;
    }

    public FragmentController(Context context, Object item){
        this.context = context;
        this.item = item;
    }

    public Fragment getFramgment(int position){
        Log.d("","getItem:"+position);
        GlobalRegistry registry = GlobalRegistry.getInstance();
        if(position == 0){
            if(registry.getInt(Constant.DUMMY_PREFIX+position) == GlobalRegistry.NOT_FOUND){
                new AsyncTaskForUriage(position).execute();
                return UriageDummyFragment.newInstance(position);
            }else{
                if(registry.getInt(Constant.ASYNCTASK_DONE+position) == 1){
                    return UriageFragment.newInstance(position);
                }else{
                    return UriageDummyFragment.newInstance(position);
                }
            }
        }else if(position == 1){
            if(registry.getInt(Constant.DUMMY_PREFIX+position) == GlobalRegistry.NOT_FOUND){
                new AsyncTaskForShop(position).execute();
                return ShopDummyFragment.newInstance(position);
            }else{
                if(registry.getInt(Constant.ASYNCTASK_DONE+position) == 1){
                    return ShopFragment.newInstance(position);
                }else{
                    return ShopDummyFragment.newInstance(position);
                }
            }
        }else{
            if(registry.getInt(Constant.DUMMY_PREFIX+position) == GlobalRegistry.NOT_FOUND){
                new AsyncTaskForStock(position).execute();
                return StockDummyFragment.newInstance(position);
            }else{
                if(registry.getInt(Constant.ASYNCTASK_DONE+position) == 1){
                    return StockFragment.newInstance(position);
                }else{
                    return StockDummyFragment.newInstance(position);
                }
            }
        }
    }

    public int getFragmentPosition(){
        IPagerFragment fr = (IPagerFragment)item;
        int position = fr.getPosition();
        Log.d("","getItemPosition("+item+"):"+position);
        GlobalRegistry registry = GlobalRegistry.getInstance();
        if(registry.getInt(Constant.FGRAGMENT_PREFIX+position) == GlobalRegistry.NOT_FOUND){
            return android.support.v4.view.PagerAdapter.POSITION_NONE;
        }else{
            if(position == registry.getInt(Constant.CURRENT_POSITTION)){
                if(registry.getInt(Constant.REFRESH_IMG_PUSHED) == 1 ||
                        registry.getInt(Constant.CALENDAR_PUSHED) == 1){
                    return android.support.v4.view.PagerAdapter.POSITION_NONE;
                }else{
                    return position;
                }
            }else{
                return position;
            }
        }
    }

    private void setStartRefreshAnim(){
        GlobalRegistry registry = GlobalRegistry.getInstance();
        if(registry.getInt(Constant.REFRESH_IMG_PUSHED) == 1){
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.rotate);
            if(MainActivity.refreshItemMenu != null){
                MainActivity.refreshImageView.startAnimation(anim);
                MainActivity.refreshItemMenu.setActionView(MainActivity.refreshImageView);
            }
        }
    }

    private void showDialog(Context context, String msg) {
        DialogFragment df = SimpleDialogFragment.newInstance(msg);
        df.show(((Activity)context).getFragmentManager(), "dialog");
    }

    private void AsyncTaskCommonPost(int position){
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.ASYNCTASK_DONE+position, 1);
        MainActivity.adapter.notifyDataSetChanged();
    }

    private class AsyncTaskForUriage extends AsyncTask<Void, Void, Void> {
        private int position;
        public AsyncTaskForUriage(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            setStartRefreshAnim();
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE, Constant.METHOD_URIAGE);
            GlobalRegistry registry = GlobalRegistry.getInstance();
            String ym = registry.getString(Constant.YM);
            if(ym == null){
                ym = DateUtil.getCurrnetYYYYMM();
            }
            connector.setParameter(Constant.YM, ym);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                ResultUriageList res = ResultUriageListGen.get(entity.getContent());
                if(res.getStatus() != 0){
                    showDialog(context,res.getMessage());
                }else{
                    ym = res.getYm();
                    int year = Integer.parseInt(DateUtil.getDateYYYY(ym));
                    int month = Integer.parseInt(DateUtil.getDateM(ym));
                    registry.setRegistry(Constant.PARAM_YEAR, year);
                    registry.setRegistry(Constant.PARAM_MONTH, month);
                    List<Uriage> list = res.getList();
                    for(Uriage uriage : list){
                        if (uriage.getImgUrl() != null && !uriage.getImgUrl().equals("")) {
                            FileDownload file = new FileDownload(context, uriage.getImgUrl());
                            if(!file.exist(uriage.getBrandImg())){
                                file.save();
                            }
                        }
                    }
                    registry.setObject(Constant.URIAGE_RES_DATA, res);
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_protocol_contents));
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_system_contents));
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForUriage", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            AsyncTaskCommonPost(this.position);
        }
    }

    private class AsyncTaskForShop extends AsyncTask<Void, Void, Void> {
        private int position;
        public AsyncTaskForShop(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            setStartRefreshAnim();
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE, Constant.METHOD_SHOP);
            GlobalRegistry registry = GlobalRegistry.getInstance();
            String ym = registry.getString(Constant.YM);
            if(ym == null){
                ym = DateUtil.getCurrnetYYYYMM();
            }
            connector.setParameter(Constant.YM, ym);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                ResultShopList res = ResultShopListGen.get(entity.getContent());
                if(res.getStatus() != 0){
                    showDialog(context,res.getMessage());
                }else{
                    ym = res.getYm();
                    int year = Integer.parseInt(DateUtil.getDateYYYY(ym));
                    int month = Integer.parseInt(DateUtil.getDateM(ym));
                    registry.setRegistry(Constant.PARAM_YEAR, year);
                    registry.setRegistry(Constant.PARAM_MONTH, month);
                    registry.setObject(Constant.SHOP_RES_DATA, res);
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_protocol_contents));
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_system_contents));
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForShop", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            AsyncTaskCommonPost(this.position);
        }
    }

    private class AsyncTaskForStock extends AsyncTask<Void, Void, Void> {
        private int position;
        public AsyncTaskForStock(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            setStartRefreshAnim();
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE, Constant.METHOD_STOCK);
            GlobalRegistry registry = GlobalRegistry.getInstance();
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                ResultStockList res = ResultStockListGen.get(entity.getContent());
                if(res.getStatus() != 0){
                    showDialog(context,res.getMessage());
                }else{
                    registry.setObject(Constant.STOCK_RES_DATA, res);
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_protocol_contents));
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_system_contents));
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForStock", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            AsyncTaskCommonPost(this.position);
        }
    }

    private static void setDummyFragmentEndProcess(int position){
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.DUMMY_PREFIX+position, position);
        registry.removeRegistry(Constant.FGRAGMENT_PREFIX+position);
    }

    private static void setFragmentEndProcess(int position){
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.removeRegistry(Constant.DUMMY_PREFIX+position);
        registry.removeRegistry(Constant.ASYNCTASK_DONE+position);
        registry.setRegistry(Constant.FGRAGMENT_PREFIX+position, position);
        if(registry.getInt(Constant.REFRESH_IMG_PUSHED) == 1){
            Log.d("","*** MainActivity:"+MainActivity.refreshItemMenu);
            if(MainActivity.refreshItemMenu != null && MainActivity.refreshItemMenu.getActionView() != null){
                MainActivity.refreshItemMenu.getActionView().clearAnimation();
                MainActivity.refreshItemMenu.setActionView(null);
            }
            registry.removeRegistry(Constant.REFRESH_IMG_PUSHED);
        }
        if(registry.getInt(Constant.CALENDAR_PUSHED) == 1){
            registry.removeRegistry(Constant.CALENDAR_PUSHED);
        }
    }

    public static class UriageFragment extends Fragment implements IPagerFragment{
        private static final String ARG_POSITION = "position";
        private int position;

        public static UriageFragment newInstance(int position) {
            UriageFragment f = new UriageFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getPosition(){
            return this.position;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments().getInt(ARG_POSITION);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("UriageFragment","*** onCreateView:"+position);
            View rootView = null;

            GlobalRegistry registry = GlobalRegistry.getInstance();
            Object obj = registry.getObject(Constant.URIAGE_RES_DATA);
            if(obj == null){
                rootView = inflater.inflate(R.layout.blank, container, false);
            }else{
                ResultUriageList res = (ResultUriageList)obj;
                rootView = inflater.inflate(R.layout.uriage, container, false);
                TextView resultDateV = (TextView)rootView.findViewById(R.id.result_date);
                resultDateV.setText(DateUtil.getDateYYYYHyphenMM(res.getYm()));
                TextView currentTimeV = (TextView)rootView.findViewById(R.id.current_time);
                currentTimeV.setText(DateUtil.getDateTime());
                List<Uriage> list = res.getList();

                TableLayout tableLayout = (TableLayout)rootView.findViewById(R.id.uriagelist_tablelayout);
                tableLayout.setColumnStretchable(0, true);
                TableRow titleRow = (TableRow)inflater.inflate(R.layout.uriagelist_def_tablerow_title, null);
                tableLayout.addView(titleRow, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
                int cnt = 0, sum = 0;
                for(Uriage uriage : list){
                    if(uriage.getEmmId() != 1){
                        cnt += uriage.getSelledCnt();
                        sum += uriage.getSelledSum();
                    }
                    TableRow tr = (TableRow)inflater.inflate(R.layout.uriagelist_def_tablerow_data, null);
                    Bitmap bitmap = BitmapFactory.decodeFile(getActivity().getFilesDir().getPath()+"/"+uriage.getBrandImg());
                    ImageView imgV = (ImageView)tr.findViewById(R.id.brand_image);
                    imgV.setImageBitmap(bitmap);
                    TextView priceV = (TextView)tr.findViewById(R.id.uriage_unit_price);
                    priceV.setText(StringUtil.getCurrencyFormat(uriage.getPrice())+getResources().getString(R.string.yen_mark_jp));
                    tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
                    TextView cntV = (TextView)tr.findViewById(R.id.uriage_item_cnt);
                    cntV.setText(StringUtil.getCurrencyFormat(uriage.getSelledCnt()));
                    TextView sumV = (TextView)tr.findViewById(R.id.uriage_item_sum);
                    sumV.setText(StringUtil.getCurrencyFormat(uriage.getSelledSum()));
                }
                TextView totalCntV = (TextView)rootView.findViewById(R.id.uriage_total_cnt);
                totalCntV.setText(StringUtil.getCurrencyFormat(cnt));
                TextView totalSumV = (TextView)rootView.findViewById(R.id.uriage_total_sum);
                totalSumV.setText(StringUtil.getCurrencyFormat(sum));
            }

            setFragmentEndProcess(getPosition());

            return rootView;
        }
    }

    public static class UriageDummyFragment extends Fragment implements IPagerFragment{
        private static final String ARG_POSITION = "position";

        public static UriageDummyFragment newInstance(int position) {
            UriageDummyFragment f = new UriageDummyFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getPosition(){
            return getArguments().getInt(ARG_POSITION);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("UriageDummyFragment","*** onCreateView:");
            View rootView = inflater.inflate(R.layout.progress, container, false);

            setDummyFragmentEndProcess(getPosition());

            return rootView;
        }
    }

    public static class ShopFragment extends Fragment implements IPagerFragment{
        private static final String ARG_POSITION = "position";

        private int position;

        public static ShopFragment newInstance(int position) {
            ShopFragment f = new ShopFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments().getInt(ARG_POSITION);
        }

        @Override
        public int getPosition(){
            return this.position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("ShopFragment","*** onCreateView:"+position);
            View rootView = null;

            GlobalRegistry registry = GlobalRegistry.getInstance();
            Object obj = registry.getObject(Constant.SHOP_RES_DATA);
            if(obj == null){
                rootView = inflater.inflate(R.layout.blank, container, false);
            }else{
                ResultShopList res = (ResultShopList)obj;
                rootView = inflater.inflate(R.layout.shop, container, false);
                TextView resultDateV = (TextView)rootView.findViewById(R.id.result_date);
                resultDateV.setText(DateUtil.getDateYYYYHyphenMM(res.getYm()));
                TextView currentTimeV = (TextView)rootView.findViewById(R.id.current_time);
                currentTimeV.setText(DateUtil.getDateTime());
                List<Shop> list = res.getList();

                for(Shop shop : list){
                    int honbuTcnt = shop.gethTotal();
                    int honbuDcnt = shop.gethDelete();
                    TextView honbuAliveCntV = (TextView)rootView.findViewById(R.id.honbu_alive_cnt);
                    honbuAliveCntV.setText(StringUtil.getCurrencyFormat(honbuTcnt-honbuDcnt));
                    TextView honbuTCntV = (TextView)rootView.findViewById(R.id.honbu_total_cnt);
                    honbuTCntV.setText(StringUtil.getCurrencyFormat(honbuTcnt));
                    TextView honbuDCntV = (TextView)rootView.findViewById(R.id.honbu_delete_cnt);
                    honbuDCntV.setText(StringUtil.getCurrencyFormat(honbuDcnt));

                    int shopTcnt = shop.getsTotal();
                    int shopDcnt = shop.getsDelete();
                    TextView shopAliveCntV = (TextView)rootView.findViewById(R.id.shop_alive_cnt);
                    shopAliveCntV.setText(StringUtil.getCurrencyFormat(shopTcnt-shopDcnt));
                    TextView shopTCntV = (TextView)rootView.findViewById(R.id.shop_total_cnt);
                    shopTCntV.setText(StringUtil.getCurrencyFormat(shopTcnt));
                    TextView shopDCntV = (TextView)rootView.findViewById(R.id.shop_delete_cnt);
                    shopDCntV.setText(StringUtil.getCurrencyFormat(shopDcnt));
                }
            }

            setFragmentEndProcess(getPosition());

            return rootView;
        }
    }

    public static class ShopDummyFragment extends Fragment implements IPagerFragment{
        private static final String ARG_POSITION = "position";

        public static ShopDummyFragment newInstance(int position) {
            ShopDummyFragment f = new ShopDummyFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getPosition(){
            return getArguments().getInt(ARG_POSITION);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("ShopDummyFragment","*** onCreateView:");
            View rootView = inflater.inflate(R.layout.progress, container, false);

            setDummyFragmentEndProcess(getPosition());

            return rootView;
        }
    }

    public static class StockFragment extends Fragment implements IPagerFragment{
        private static final String ARG_POSITION = "position";

        private int position;

        public static StockFragment newInstance(int position) {
            StockFragment f = new StockFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments().getInt(ARG_POSITION);
        }

        @Override
        public int getPosition(){
            return this.position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("StockFragment","*** onCreateView:"+position);
            View rootView = null;

            GlobalRegistry registry = GlobalRegistry.getInstance();
            Object obj = registry.getObject(Constant.STOCK_RES_DATA);
            if(obj == null){
                rootView = inflater.inflate(R.layout.blank, container, false);
            }else{
                ResultStockList res = (ResultStockList)obj;
                rootView = inflater.inflate(R.layout.stock, container, false);
                TextView currentTimeV = (TextView)rootView.findViewById(R.id.current_time);
                currentTimeV.setText(DateUtil.getDateTime());
                List<Stock> list = res.getList();

                TableLayout tableLayout = (TableLayout)rootView.findViewById(R.id.stock_tablelayout);
                tableLayout.setColumnStretchable(0, true);
                TableRow titleRow = (TableRow)inflater.inflate(R.layout.stock_def_tablerow_title, null);
                tableLayout.addView(titleRow, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
                for(Stock stock : list){
                    TableRow tr = (TableRow)inflater.inflate(R.layout.stock_def_tablerow_data, null);
                    Bitmap bitmap = BitmapFactory.decodeFile(getActivity().getFilesDir().getPath()+"/"+stock.getBrandImg());
                    ImageView imgV = (ImageView)tr.findViewById(R.id.brand_image);
                    imgV.setImageBitmap(bitmap);
                    TextView priceV = (TextView)tr.findViewById(R.id.price);
                    priceV.setText(StringUtil.getCurrencyFormat(stock.getPrice())+getResources().getString(R.string.yen_mark_jp));
                    TextView stockV = (TextView)tr.findViewById(R.id.stock);
                    stockV.setText(StringUtil.getCurrencyFormat(stock.getStock()));
                    tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
                }
            }

            setFragmentEndProcess(getPosition());

            return rootView;
        }
    }

    public static class StockDummyFragment extends Fragment implements IPagerFragment{
        private static final String ARG_POSITION = "position";

        public static StockDummyFragment newInstance(int position) {
            StockDummyFragment f = new StockDummyFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getPosition(){
            return getArguments().getInt(ARG_POSITION);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("StockDummyFragment","*** onCreateView:");
            View rootView = inflater.inflate(R.layout.progress, container, false);

            setDummyFragmentEndProcess(getPosition());

            return rootView;
        }
    }
}
