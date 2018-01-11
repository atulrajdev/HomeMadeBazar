package com.homemadebazar.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.homemadebazar.R;
import com.homemadebazar.adapter.TransactionHistoryAdapter;
import com.homemadebazar.model.BaseModel;
import com.homemadebazar.model.TransactionModel;
import com.homemadebazar.model.UserModel;
import com.homemadebazar.network.HttpRequestHandler;
import com.homemadebazar.network.api.ApiCall;
import com.homemadebazar.network.apicall.GetTransactionReportApiCall;
import com.homemadebazar.util.Constants;
import com.homemadebazar.util.DialogUtils;
import com.homemadebazar.util.SharedPreference;
import com.homemadebazar.util.Utils;

import java.util.ArrayList;

public class TransactionHistoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<TransactionModel> transactionModelArrayList = new ArrayList<>();
    private TransactionHistoryAdapter transactionHistoryAdapter;
    private UserModel userModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        setupToolbar();
    }

    @Override
    protected void initUI() {
        userModel = SharedPreference.getUserModel(TransactionHistoryActivity.this);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);

    }

    private void setupToolbar() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("TRANSACTION HISTORY");

    }

    @Override
    protected void initialiseListener() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void setData() {
        recyclerView.setLayoutManager(linearLayoutManager);
        transactionHistoryAdapter = new TransactionHistoryAdapter(this, transactionModelArrayList);
        recyclerView.setAdapter(transactionHistoryAdapter);
        getTransactionHistoryApiCall();
    }

    private void getTransactionHistoryApiCall() {
        try {
            swipeRefreshLayout.setRefreshing(true);
            final GetTransactionReportApiCall apiCall = new GetTransactionReportApiCall(userModel.getUserId());
            HttpRequestHandler.getInstance(this.getApplicationContext()).executeRequest(apiCall, new ApiCall.OnApiCallCompleteListener() {

                @Override
                public void onComplete(Exception e) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (e == null) { // Success
                        try {
                            BaseModel baseModel = apiCall.getBaseModel();
                            if (baseModel.getStatusCode() == Constants.ServerResponseCode.SUCCESS) {
                                ArrayList<TransactionModel> tempTransactionList = apiCall.getResult();
                                transactionModelArrayList.clear();
                                transactionModelArrayList.addAll(tempTransactionList);
                                transactionHistoryAdapter.notifyDataSetChanged();
                            } else {
                                DialogUtils.showAlert(TransactionHistoryActivity.this, baseModel.getStatusMessage());
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else { // Failure
                        Utils.handleError(e.getMessage(), TransactionHistoryActivity.this, null);
                    }
                }
            });
        } catch (Exception e) {
            Utils.handleError(e.getMessage(), TransactionHistoryActivity.this, null);
        }
    }

    @Override
    public void onRefresh() {
        getTransactionHistoryApiCall();
    }
}
