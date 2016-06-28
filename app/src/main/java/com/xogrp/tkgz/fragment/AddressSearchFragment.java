package com.xogrp.tkgz.fragment;

import android.database.Cursor;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.xogrp.tkgz.R;
import com.xogrp.tkgz.View.ItemDividerDecoration;
import com.xogrp.tkgz.adapter.RecyclerViewAdapter;
import com.xogrp.tkgz.util.AddrSearchDatabaseUtil;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.net.ssl.SSLContext;

/**
 * Created by hliao on 5/30/2016.
 */
public class AddressSearchFragment extends AbstractTKGZFragment implements View.OnClickListener ,RecyclerViewAdapter.OnClickListener {
    private EditText mEtAddress;
    private ImageView mIvBack, mIvCleanEdit;
    private TextView mTvRecordRemind;
    private RecyclerView mRvSearchedHistory, mRvSearchedResult;
    private ScrollView mSvHistoryRecord;
    private PoiSearch mPoiSearch;
    private List<PoiInfo> mListResult;
    private ReturnToCreateActicityPage mIfReturn;
    private Cursor mCursor;
    private RecyclerViewAdapter mRvAdapter;
    private android.os.Handler handler=new android.os.Handler();

    @Override
    protected int getLayoutResId() {
        return R.layout.address_search_layout;
    }

    @Override
    protected void onTKGZCreateView(View rootView) {
        mEtAddress = (EditText) rootView.findViewById(R.id.et_address);
        mTvRecordRemind = (TextView) rootView.findViewById(R.id.tv_record_remind);
        mIvBack = (ImageView) rootView.findViewById(R.id.iv_back);
        mIvCleanEdit = (ImageView) rootView.findViewById(R.id.iv_edit_clear);
        mRvSearchedResult = (RecyclerView) rootView.findViewById(R.id.rv_address_result);
        mRvSearchedHistory = (RecyclerView) rootView.findViewById(R.id.rv_searched_history);
        mSvHistoryRecord = (ScrollView) rootView.findViewById(R.id.sv_address_history);

        mRvSearchedHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvSearchedHistory.setHasFixedSize(true);
        mRvAdapter = new RecyclerViewAdapter(getContext());
        mRvSearchedHistory.setAdapter(mRvAdapter);
        GetDatabaseAndSetView();
    }

    @Override
    protected void onTKGZActivityCreated() {
        mPoiSearch = PoiSearch.newInstance();
        mIvBack.setOnClickListener(this);
        mIvCleanEdit.setOnClickListener(this);
        mTvRecordRemind.setOnClickListener(this);
        mEtAddress.addTextChangedListener(mTwAddrKey);
        mEtAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSvHistoryRecord.setVisibility(View.GONE);
                    mRvSearchedResult.setVisibility(View.VISIBLE);
                    mRvSearchedResult.setLayoutManager(new LinearLayoutManager(getContext()));
                    mIvCleanEdit.setVisibility(View.VISIBLE);
                    mIvCleanEdit.setClickable(true);
                }
            }
        });

        mRvAdapter.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                hideKeyboard();
                getOnScreenNavigationListener().goBackFromCurrentPage();
                break;
            case R.id.iv_edit_clear:
                mEtAddress.setText("");
                break;
            case R.id.tv_record_remind:
                AddrSearchDatabaseUtil.DeleteTable(getActivity());
                mTvRecordRemind.setTextColor(getResources().getColor(R.color.APP_GRAY));
                mTvRecordRemind.setText(getString(R.string.not_history_record));
                mRvSearchedHistory.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    protected void GetDatabaseAndSetView() {
        mCursor = AddrSearchDatabaseUtil.getSearchRecord(getActivity());
        mTvRecordRemind.setText(getString(mCursor.getCount() > 0 ? R.string.clean_history_record : R.string.not_history_record));
        mTvRecordRemind.setTextColor(getResources().getColor(mCursor.getCount() > 0 ? R.color.green_app_system : R.color.APP_GRAY));
        if (mCursor.getCount() > 0) {
            mRvAdapter.setPoiCursor(mCursor);
            mRvAdapter.notifyDataSetChanged();
           View itemView = LayoutInflater.from(getContext()).inflate(R.layout.poiresult_layout, null);
          itemView.measure(0, 0);
           int itemHeight = itemView.getMeasuredHeight();
           ViewGroup.LayoutParams params = mRvSearchedHistory.getLayoutParams();
           params.height = mCursor.getCount() * itemHeight;
          mRvSearchedHistory.setLayoutParams(params);
            mRvSearchedHistory.addItemDecoration(new ItemDividerDecoration(getContext(), LinearLayout.VERTICAL));
            mRvSearchedHistory.setItemAnimator(new DefaultItemAnimator());
        } else {
            mRvSearchedResult.setVisibility(View.VISIBLE);
            mRvSearchedHistory.setVisibility(View.GONE);
        }
    }


    TextWatcher mTwAddrKey = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String KeyAddress = s.toString();
            mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                @Override
                public void onGetPoiResult(PoiResult poiResult) {
                    mListResult = poiResult.getAllPoi();
                    if (mListResult != null) {
                        mRvAdapter.setPoiList(mListResult);
                        mRvSearchedResult.setVisibility(View.VISIBLE);
                        mRvSearchedResult.setAdapter(mRvAdapter);

                        mRvSearchedResult.addItemDecoration(new ItemDividerDecoration(getContext(), LinearLayout.VERTICAL));
                        mRvSearchedResult.setItemAnimator(new DefaultItemAnimator());
                    }
                }

                @Override
                public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

                }
            });
            mPoiSearch.searchInCity(new PoiCitySearchOption()
                    .city(getString(R.string.gz))
                    .keyword(KeyAddress)
                    .pageNum(1));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public String getTransactionTag() {
        return null;
    }

    @Override
    public boolean isHideActionBar() {
        return true;
    }

    @Override
    public String getActionBarTitle() {
        return null;
    }

    @Override
    public boolean isEmptyTopMargin() {
        return true;
    }

    @Override
    public void OnItemClick(int position, int flag) {
        if (flag == 0) {
            mCursor.moveToPosition(position);
            String addrName = mCursor.getString(mCursor.getColumnIndex("addrName"));
            String addrDetail = mCursor.getString(mCursor.getColumnIndex("addrDetail"));
            double latitude = mCursor.getDouble(mCursor.getColumnIndex("latitude"));
            double longitude = mCursor.getDouble(mCursor.getColumnIndex("longitude"));
            LatLng locat = new LatLng(latitude, longitude);
            getOnScreenNavigationListener().goBackFromCurrentPage();
            mIfReturn = (ReturnToCreateActicityPage) getActivity().getSupportFragmentManager().findFragmentByTag(CreateActivityPage1Fragment.TRANSACTION_TAG);
            mIfReturn.locatInBaiduMap(locat, addrName, addrDetail);
            mCursor.close();
        } else {
            String addrName = mListResult.get(position).name;
            String addrDetail = mListResult.get(position).address;
            LatLng locat = mListResult.get(position).location;

            AddrSearchDatabaseUtil.insertIntoTable(getActivity(), addrName, addrDetail, locat.latitude, locat.longitude);

            getOnScreenNavigationListener().goBackFromCurrentPage();
            mIfReturn = (ReturnToCreateActicityPage) getActivity().getSupportFragmentManager().findFragmentByTag(CreateActivityPage1Fragment.TRANSACTION_TAG);
            mIfReturn.locatInBaiduMap(locat, addrName, addrDetail);
            mPoiSearch.destroy();
        }
        mPoiSearch.destroy();
        AddrSearchDatabaseUtil.closeDatabase();
    }

    public interface ReturnToCreateActicityPage {
        void locatInBaiduMap(LatLng locat, String addrName, String addrDetail);
    }



}


