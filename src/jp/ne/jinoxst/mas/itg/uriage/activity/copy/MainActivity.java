package jp.ne.jinoxst.mas.itg.uriage.activity.copy;

import jp.ne.jinoxst.mas.itg.uriage.R;
import jp.ne.jinoxst.mas.itg.uriage.lib.ICallback;
import jp.ne.jinoxst.mas.itg.uriage.lib.PagerSlidingTabStrip;
import jp.ne.jinoxst.mas.itg.uriage.util.Constant;
import jp.ne.jinoxst.mas.itg.uriage.util.GlobalRegistry;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ICallback {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    public static ItgPagerAdapter adapter;
    private TextView actionBarTitleV;
    private int currentColor = 0xFFF4842D;
    private int onIconArr[] = {R.drawable.uriage_on, R.drawable.shop_on, R.drawable.stock_on};
    private int offIconArr[] = {R.drawable.uriage_off, R.drawable.shop_off, R.drawable.stock_off};
    private int[] tabTitleArr = {R.string.actionbar_title1,R.string.actionbar_title2,R.string.actionbar_title3};
    public static ImageView refreshImageView;
    public static MenuItem refreshItemMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflator.inflate(R.layout.actionbar_titleview, null);
        actionBarTitleV = (TextView)layout.findViewById(R.id.actionbar_title);
        getActionBar().setCustomView(layout);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //tabs.setBackground(getResources().getDrawable(R.drawable.tab_background));
        adapter = new ItgPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager, this);
        getActionBar().setDisplayShowTitleEnabled(false);
        changeColor(currentColor);
        setRefreshAnimMenu();
    }

    private void setRefreshAnimMenu() {
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        refreshImageView = (ImageView) inflater.inflate(R.layout.actionbar_refresh_unit, null);
    }

    @Override
    public void pageSelected(int position){
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.CURRENT_POSITTION, position);
        actionBarTitleV.setText(tabTitleArr[position]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GlobalRegistry registry = GlobalRegistry.getInstance();
        switch (item.getItemId()) {
            case R.id.refresh:
                registry.setRegistry(Constant.REFRESH_IMG_PUSHED, 1);
                refreshItemMenu = item;
                adapter.notifyDataSetChanged();
                break;
            case R.id.calendar_month:
                registry.setRegistry(Constant.CALENDAR_PUSHED, 1);
                CalendarMonthFragment dialog = new CalendarMonthFragment();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(),"CalendarMonthFragment");
                break;
        }

        return true;
    }

    private void changeColor(int newColor) {
        tabs.setIndicatorColor(newColor);
        currentColor = newColor;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    public class ItgPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.IconSwitchTabProvider {

        @Override
        public int getPageOffIconResId(int position){
            return offIconArr[position % 3];
        }

        @Override
        public int getPageOnIconResId(int position){
            return onIconArr[position % 3];
        }

        public ItgPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            FragmentController controller = new FragmentController(MainActivity.this);
            return controller.getFramgment(position);
        }

        @Override
        public int getItemPosition(Object item) {
            FragmentController controller = new FragmentController(MainActivity.this, item);
            return controller.getFragmentPosition();
        }
    }
}