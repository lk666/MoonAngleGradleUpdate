package cn.com.bluemoon.delivery.module.card;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.card.ResultWorkPlaceList;
import cn.com.bluemoon.delivery.app.api.model.card.Workplace;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class GetWorkPlaceActivity extends KJActivity {

    private String TAG = "GetWorkPlaceActivity";
    private GetWordPlaceAdapter getWordPlaceAdapter;
    @BindView(id=R.id.txt_workplace_address)
    private TextView txtWorkplaceAddress;
    @BindView(id=R.id.listview_workplace)
    private PullToRefreshListView listviewWorkplace;
    @BindView(id=R.id.btn_ok,click = true)
    private Button btnOk;
    @BindView(id=R.id.et_search)
    private CommonClearEditText etSearch;
    @BindView(id=R.id.layout_bottom)
    private LinearLayout layoutBottom;
    @BindView(id=R.id.txt_search,click = true)
    private TextView txtSearch;
    private CommonProgressDialog progressDialog;
    private long timestamp = 0;
    private boolean isPullUp;
    private boolean isPullDown;
    private List<Workplace> items;
    private List<Workplace> listHistory;
    private Workplace workplaceResult;
    private boolean isSearch;
    private final static int HISTORY_SIZE = 5;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_get_workplace);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        initCustomActionBar();
        progressDialog = new CommonProgressDialog(aty);
        etSearch.setOnKeyListener(onKeyListener);
        etSearch.setCallBack(editTextCallBack);
        getWordPlaceAdapter = new GetWordPlaceAdapter(aty);
        listviewWorkplace.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = true;
                isPullUp = false;
                getList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = false;
                isPullUp = true;
                getList();
            }
        });
        isPullDown = false;
        isPullUp = false;
        getList();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        if(v==btnOk){
            if(workplaceResult ==null||StringUtils.isEmpty(workplaceResult.getWorkplaceCode())){
                PublicUtil.showToast(R.string.card_workplace_cannot_empty);
                return;
            }
            refreshHistoryItem(workplaceResult,true);
            Intent intent = new Intent();
            intent.putExtra("code", workplaceResult.getWorkplaceCode());
            setResult(RESULT_OK, intent);
            finish();
        }else if(v == txtSearch){
            search(isSearch);
        }
    }

    private void search(boolean isSearch){
        if(isSearch){
            isPullDown = false;
            isPullUp = false;
            getList();
        }else{
            isPullDown = false;
            isPullUp = false;
            setData(items);
        }
        etSearch.clearFocus();
    }

    CommonEditTextCallBack editTextCallBack = new CommonEditTextCallBack() {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            if(etSearch.isFocused()&&etSearch.getText().toString().length()>0) {
                txtSearch.setText(getString(R.string.card_btn_search));
                isSearch = true;
            }else if(etSearch.isFocused()&&etSearch.getText().toString().length()==0){
                txtSearch.setText(getString(R.string.card_btn_cancle));
                isSearch = false;
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            super.onFocusChange(v, hasFocus);
            if(hasFocus){
                if(txtSearch.getVisibility()==View.GONE){
                    Animation animation = AnimationUtils.loadAnimation(aty,R.anim.activity_translate_right);
                    txtSearch.setAnimation(animation);
                    txtSearch.setVisibility(View.VISIBLE);
                }
                showHistoryView();
            }else{
                LibViewUtil.hideIM(v);
                if(txtSearch.getVisibility()==View.VISIBLE){
                    txtSearch.setVisibility(View.GONE);
                }
            }
        }
    };

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                LibViewUtil.hideIM(v);
                isPullDown = false;
                isPullUp = false;
                getList();
                return true;
            }
            return false;
        }
    };

    private void getList(){
        if(!isPullUp){
            timestamp = 0;
        }
        if(!isPullUp&&!isPullDown&&progressDialog!=null){
            progressDialog.show();
        }
        DeliveryApi.getWorkplaceList(ClientStateManager.getLoginToken(aty), etSearch.getText().toString(),
                AppContext.PAGE_SIZE * 2, timestamp, getWorkPlaceListHandler);
    }

    private void setData(List<Workplace> workplaces){
        if(items==null){
            items = new ArrayList<Workplace>();
        }
        if(workplaces==null||workplaces.size()==0){
            if(isPullUp){
                PublicUtil.showToast(R.string.card_no_list_data);
                return;
            }else{
                items.clear();
            }
        }else{
            List<Workplace> list = new ArrayList<Workplace>();
            for (int i=0;i<workplaces.size();i++){
                list.add(workplaces.get(i));
            }
            if(isPullUp){
                items.addAll(list);
            }else{
                items = list;
            }
        }
        getWordPlaceAdapter.setList(items, false);
        if(isPullUp){
            getWordPlaceAdapter.notifyDataSetChanged();
        } else {
            listviewWorkplace.setAdapter(getWordPlaceAdapter);
        }
    }

    private void refreshHistoryItem(Workplace workplace,boolean isAdd){
        if(workplace==null||StringUtils.isEmpty(workplace.getWorkplaceCode())){
            return;
        }
        if(listHistory==null){
            listHistory = ClientStateManager.getCardSearchHistory(aty);
        }
        if(isAdd){
            for (int i=0;i<listHistory.size();i++){
                if(i>=HISTORY_SIZE-1){
                    listHistory.remove(i);
                    i--;
                }else{
                    if(listHistory.get(i).getWorkplaceCode().equals(workplace.getWorkplaceCode())){
                        listHistory.remove(i);
                        i--;
                    }
                }

            }
            listHistory.add(0, workplace);
        }else{
            listHistory.remove(workplace);
        }
        ClientStateManager.setCardSearhHistory(aty, listHistory);

    }

    private void showHistoryView(){
        if(layoutBottom.getVisibility()==View.VISIBLE){
            layoutBottom.setVisibility(View.GONE);
        }
        if(listHistory==null){
            listHistory = ClientStateManager.getCardSearchHistory(aty);
        }
        getWordPlaceAdapter.setList(listHistory, true);
        listviewWorkplace.setAdapter(getWordPlaceAdapter);
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.card_choose_address));
            }

            @Override
            public void onBtnRight(View v) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBtnLeft(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    AsyncHttpResponseHandler getWorkPlaceListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getWorkPlaceList result = " + responseString);
            if(progressDialog != null)
                progressDialog.dismiss();
            listviewWorkplace.onRefreshComplete();
            try {
                ResultWorkPlaceList resultWorkPlaceList = JSON.parseObject(responseString, ResultWorkPlaceList.class);
                if(resultWorkPlaceList.getResponseCode()== Constants.RESPONSE_RESULT_SUCCESS){
                    if(resultWorkPlaceList.getTimestamp()!=0){
                        timestamp = resultWorkPlaceList.getTimestamp();
                    }
                    setData(resultWorkPlaceList.getWorkplaceList());
                    if(!isPullUp){
                        layoutBottom.setVisibility(View.GONE);
                    }
                }else{
                    PublicUtil.showErrorMsg(aty, resultWorkPlaceList);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if(progressDialog != null)
                progressDialog.dismiss();
            listviewWorkplace.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

    class GetWordPlaceAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<Workplace> list;
        private boolean isHistory;

        public GetWordPlaceAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public void setList(List<Workplace> list,boolean isHistory){
            this.list = list;
            this.isHistory = isHistory;
            if(isHistory){
                listviewWorkplace.setMode(PullToRefreshBase.Mode.DISABLED);
            }else{
                listviewWorkplace.setMode(PullToRefreshBase.Mode.BOTH);
            }
        }

        @Override
        public int getCount() {
            if(!isHistory&&(list==null||list.size()<=0)){
                return 1;
            }
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if(list==null||list.size()<=0){
                View view = mInflater.inflate(R.layout.layout_no_workplacce, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, listviewWorkplace.getHeight());
                view.setLayoutParams(params);
                return view;
            }
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_workplace_list, null);
            }
            final LinearLayout layoutWorkPlace = ViewHolder.get(convertView, R.id.layout_workplace);
            TextView txtWorkPlace = ViewHolder.get(convertView, R.id.txt_workplace);
            final ImageView imgDelete = ViewHolder.get(convertView,R.id.img_delete);
            final CheckBox cb = ViewHolder.get(convertView, R.id.cb_select);
            View lineDotted = ViewHolder.get(convertView, R.id.line_dotted);
            View lineSilde = ViewHolder.get(convertView, R.id.line_silde);
            if (position == list.size()-1) {
                lineDotted.setVisibility(View.GONE);
                lineSilde.setVisibility(View.GONE);
            } else {
                lineDotted.setVisibility(View.VISIBLE);
                lineSilde.setVisibility(View.GONE);
            }
            final Workplace workplace = list.get(position);
            txtWorkPlace.setText(CardUtils.getWorkPlaceItem(workplace));
            cb.setChecked(workplace.isSelect());
            if(workplace.isSelect()&&!isHistory&&layoutBottom.getVisibility()==View.GONE){
                layoutBottom.setVisibility(View.VISIBLE);
            }

            if(isHistory){
                imgDelete.setVisibility(View.VISIBLE);
                cb.setVisibility(View.GONE);
            }else{
                imgDelete.setVisibility(View.GONE);
                cb.setVisibility(View.VISIBLE);
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(v==imgDelete){
                        list.remove(position);
                        refreshHistoryItem(workplace,false);
                        notifyDataSetChanged();
                    }else if(v==cb||v==layoutWorkPlace){
                        if(isHistory){
                            etSearch.setText(list.get(position).getWorkplaceCode());
                            search(true);
                            return;
                        }

                        if (v == cb && !cb.isChecked()) {
                            cb.setChecked(true);
                        }
                        for (int i = 0; i< list.size(); i++) {
                            Workplace workplace = list.get(i);
                            workplace.setIsSelect(i == position);
                            list.set(i, workplace);
                        }
                        txtWorkplaceAddress.setText(CardUtils.getWorkPlaceAddress(workplace));
                        workplaceResult = workplace;
                        if(layoutBottom.getVisibility()==View.GONE){
                            layoutBottom.setVisibility(View.VISIBLE);
                        }
                        notifyDataSetChanged();
                    }
                }
            };
            imgDelete.setOnClickListener(listener);
            cb.setOnClickListener(listener);
            layoutWorkPlace.setOnClickListener(listener);

            return convertView;
        }

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(TAG);
    }

}
