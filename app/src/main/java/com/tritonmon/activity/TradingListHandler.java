package com.tritonmon.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.tritonmon.asynctask.trades.GetTrades;
import com.tritonmon.asynctask.trades.SetViewedTrade;
import com.tritonmon.fragment.dialog.ViewAcceptanceDialog;
import com.tritonmon.fragment.dialog.ViewDeclineDialog;
import com.tritonmon.fragment.dialog.ViewTradeDialog;
import com.tritonmon.fragment.tabs.OffersInTab;
import com.tritonmon.fragment.tabs.OffersOutTab;
import com.tritonmon.fragment.tabs.TradingListTab;
import com.tritonmon.model.Trade;

public class TradingListHandler extends ActionBarActivity implements ActionBar.TabListener, ViewTradeDialog.NoticeDialogListener,
        ViewDeclineDialog.NoticeDialogListener, ViewAcceptanceDialog.NoticeDialogListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "All", "Incoming", "Outgoing" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_list_handler);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("acceptedEarlierTrade")) {
            Log.e("back to incoming tab we go!", "hihi");
            actionBar.setSelectedNavigationItem(1);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_trading_list_handler;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_in_menu;
    }

    @Override
    public void onViewTradeDialogPositiveClick(DialogFragment dialog, Trade trade) {
        Log.e("tradinglisthandler", "trade ACCEPTED");
        new SetViewedTrade(trade, SetViewedTrade.Choices.ACCEPTED).execute();
    }

    @Override
    public void onViewTradeDialogNeutralClick(DialogFragment dialog, Trade trade) {
        Log.e("tradinglisthandler", "trade POSTPONED");
        new SetViewedTrade(trade, SetViewedTrade.Choices.NEUTRAL).execute();
    }

    @Override
    public void onViewTradeDialogNegativeClick(DialogFragment dialog, Trade trade) {
        Log.e("tradinglisthandler", "trade declined");
        new SetViewedTrade(trade, SetViewedTrade.Choices.DECLINED).execute();
    }

    @Override
    public void onViewDeclineDialogPositiveClick(DialogFragment dialog) {
        Log.e("tradinglist", "decline RECOGNIZED");
//        new SetViewedDecisions(CurrentUser.getName()).execute();
    }

    @Override
    public void onViewAcceptanceDialogPositiveClick(DialogFragment dialog) {
        Log.e("tradinglist", "acceptance RECOGNIZED");
//        new SetViewedDecisions(CurrentUser.getName()).execute();
    }


    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    private class TabsPagerAdapter extends FragmentStatePagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index){

            switch (index) {
                case 0:
                    return new TradingListTab();
                case 1:
                    return new OffersInTab();
                case 2:
                    return new OffersOutTab();
            }

            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }
    }

    @Override
    public void onBackPressed() {
        new GetTrades().execute();
        Intent i = new Intent(getApplicationContext(), TrainerCard.class);
        startActivity(i);
    }

}
