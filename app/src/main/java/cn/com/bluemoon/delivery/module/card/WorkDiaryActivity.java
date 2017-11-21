package cn.com.bluemoon.delivery.module.card;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.punchcard.Product;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultGetProduct;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultGetWorkDiaryList;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.module.event.PunchCardEvent;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * 日报
 * Created by liangjiangli on 2016/3/30.
 */
public class WorkDiaryActivity extends Activity {

    private final String TAG = "WorkDiaryActivity";
    private ListView listview;
    private CommonProgressDialog progressDialog;
    private WorkDiaryActivity mContext;
    private WorkDiaryAdapter adapter;
    private List<Product> workDiaryList;
    private List<Product> workDiaryListOld;
    private TextView txtTotalBreedNum;
    private TextView txtTotalNum;
    private int breedNum;
    private int count;

    public static void startAct(Context mContext) {
        Intent intent = new Intent(mContext, WorkDiaryActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);
        mContext = this;
        progressDialog = new CommonProgressDialog(mContext);
        setContentView(R.layout.activiy_work_diary);
        txtTotalBreedNum = (TextView) findViewById(R.id.txt_total_breed_num);
        txtTotalNum = (TextView) findViewById(R.id.txt_total_num);
        txtTotalBreedNum.setText(String.format(getString(R.string.total_breed_num), 0));
        txtTotalNum.setText(String.format(getString(R.string.total_num), 0));
        listview = (ListView) findViewById(R.id.listview_work_diary);
        Button btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (workDiaryList != null) {
                    progressDialog.show();
                    int totalBreedSalesNum = 0;
                    int totalSalesNum = 0;
                    Product[] wds = new Product[workDiaryList.size()];
                    for (Product wd : workDiaryList) {
                        totalSalesNum += wd.getSalesNum();
                        wds[totalBreedSalesNum] = wd;
                        totalBreedSalesNum++;
                    }
                    DeliveryApi.confirmWorkDaily(ClientStateManager.getLoginToken(mContext), totalBreedSalesNum, totalSalesNum, wds, confirmWorkDailyHandler);
                }

            }
        });
        initCustomActionBar();
        progressDialog.show();
        DeliveryApi.getWorkDiaryList(ClientStateManager.getLoginToken(mContext), getWorkDiaryListHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            boolean tag = true;
            ResultGetProduct.Product product = (ResultGetProduct.Product)data.getSerializableExtra("product");
            if (product != null && workDiaryList != null && workDiaryList.size() > 0) {
                for (Product wd : workDiaryList) {
                    if (wd.getProductCode().equals(product.getProductCode())) {
                        tag = false;
                    }
                }
            }
            if (tag) {
                Product wd = new Product();
                wd.setSalesNum(0);
                wd.setProductCode(product.getProductCode());
                wd.setProductName(product.getProductName());
                if (workDiaryList == null || workDiaryList.size() == 0) {
                    workDiaryList = new ArrayList<>();
                    workDiaryList.add(wd);
                    adapter = new WorkDiaryAdapter(workDiaryList);
                    listview.setAdapter(adapter);
                } else {
                    workDiaryList.add(wd);
                    adapter.notifyDataSetChanged();
                }
                setBottomAmount();
            }

        }
    }

    private void initCustomActionBar() {
        CommonActionBar bar = new CommonActionBar(this.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

                Intent intent = new Intent(WorkDiaryActivity.this, SearchProductActivity.class);
                startActivityForResult(intent, 0);
            }

            @Override
            public void onBtnLeft(View v) {
                showDialog();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.work_diary_title));
            }

        });
        bar.getImgRightView().setImageResource(R.mipmap.add_work_diary);
        bar.getImgRightView().setVisibility(View.VISIBLE);
    }
    AsyncHttpResponseHandler confirmWorkDailyHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s) {
            LogUtils.d("confirmWorkDailyHandler", "getWorkDiaryListHandler result = " + s);
            progressDialog.dismiss();
            try {
                ResultBase result = JSON.parseObject(s, ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(result.getResponseMsg());
                    EventBus.getDefault().post(new PunchCardEvent()); //刷新打卡信息
                    finish();
                } else {
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }
    };


    AsyncHttpResponseHandler getWorkDiaryListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s) {
            LogUtils.d("getWorkDiaryListHandler", "getWorkDiaryListHandler result = " + s);
            progressDialog.dismiss();
            try {
                ResultGetWorkDiaryList result = JSON.parseObject(s, ResultGetWorkDiaryList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    workDiaryList = result.getWorkDailyList();
                    if (workDiaryList != null && workDiaryList.size() > 0) {
                        workDiaryListOld = new ArrayList<>();
                        for (Product p : workDiaryList) {
                            Product product = new Product();
                            product.setProductCode(p.getProductCode());
                            product.setProductName(p.getProductName());
                            product.setSalesNum(p.getSalesNum());
                            workDiaryListOld.add(product);
                        }
                        breedNum =  result.getTotalBreedSalesNum();
                        count = result.getTotalSalesNum();
                        txtTotalBreedNum.setText(String.format(getString(R.string.total_breed_num), result.getTotalBreedSalesNum()));
                        txtTotalNum.setText(String.format(getString(R.string.total_num), result.getTotalSalesNum()));
                        adapter = new WorkDiaryAdapter(workDiaryList);
                        listview.setAdapter(adapter);
                    }
                } else {
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }
    };

    class WorkDiaryAdapter extends BaseAdapter {

        private List<Product> list;

        public WorkDiaryAdapter(List<Product> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(WorkDiaryActivity.this).inflate(R.layout.item_work_diary_list, null);
            }
            final Product wd = list.get(position);
            View lineDotted = ViewHolder.get(convertView, R.id.line_dotted);
            View lineSilde = ViewHolder.get(convertView, R.id.line_silde);
            ImageView bookAdd = ViewHolder.get(convertView, R.id.book_add);
            final EditText etAmount = ViewHolder.get(convertView, R.id.book_count);
            ImageView bookReduce = ViewHolder.get(convertView, R.id.book_reduce);
            TextView txtProductCode = ViewHolder.get(convertView, R.id.txt_product_code);
            TextView txtProductName = ViewHolder.get(convertView, R.id.txt_product_name);
            TextView txtDelete = ViewHolder.get(convertView, R.id.txt_delete);

            bookAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.valueOf(etAmount.getText().toString());
                    if (count < Constants.SALE_MAX_NUM) {
                        count = ++count;
                        wd.setSalesNum(count);
                        workDiaryList.set(position, wd);
                        etAmount.setText(String.valueOf(count));
                        setBottomAmount();
                    } else {
                        PublicUtil.showToast(getString(R.string.input_huge_num));
                    }

                }
            });

            bookReduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.valueOf(etAmount.getText().toString());
                    if (count == 0) {
                        return;
                    }
                    count = --count;
                    wd.setSalesNum(count);
                    workDiaryList.set(position, wd);
                    etAmount.setText(String.valueOf(count));
                    setBottomAmount();
                }
            });

            if (position == list.size() -1) {
                lineDotted.setVisibility(View.GONE);
                lineSilde.setVisibility(View.VISIBLE);
            } else {
                lineSilde.setVisibility(View.GONE);
                lineDotted.setVisibility(View.VISIBLE);
            }
            txtProductCode.setText(wd.getProductCode());
            txtProductName.setText(wd.getProductName());
            etAmount.setText(String.valueOf(wd.getSalesNum()));
            txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(wd);
                    adapter.notifyDataSetChanged();
                    setBottomAmount();
                }
            });
            etAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(WorkDiaryActivity.this);
                    dialog.setTitle(R.string.sales_num_title);
                    View view = LayoutInflater.from(WorkDiaryActivity.this).inflate(R.layout.dialog_iput_amount, null);
                    final EditText etCount = (EditText) view.findViewById(R.id.et_amount);
                    final ImageView imgReduce = (ImageView) view.findViewById(R.id.img_reduce);
                    final ImageView imgAdd = (ImageView) view.findViewById(R.id.img_add);
                    imgReduce.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!"".equals(etCount.getText().toString())) {
                                int count = Integer.valueOf(etCount.getText().toString());
                                if (count >= 1) {
                                    count--;
                                    etCount.setText(String.valueOf(count));
                                    etCount.setSelection(String.valueOf(count).length());
                                    imgAdd.setImageResource(R.mipmap.add_normal3);
                                }


                            }

                        }
                    });
                    imgAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!"".equals(etCount.getText().toString())) {
                                int count = Integer.valueOf(etCount.getText().toString());
                                if (count < Constants.SALE_MAX_NUM) {
                                    etCount.setText(String.valueOf(++count));
                                    etCount.setSelection(String.valueOf(count).length());
                                } else {
                                    imgAdd.setImageResource(R.mipmap.add_disable3);
                                    PublicUtil.showToast(getString(R.string.input_huge_num));
                                }
                            } else {
                                etCount.setText("1");
                                etCount.setSelection(1);
                            }
                        }
                    });
                    etCount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            if ("0".equals(s.toString())) {
                                imgReduce.setImageResource(R.mipmap.minus_disable3);
                            } else {
                                imgReduce.setImageResource(R.mipmap.minus_normal3);
                            }

                            if (StringUtils.isNotBlank(s.toString()) && s.toString().length() > String.valueOf(Constants.SALE_MAX_NUM).length()) {
                                etCount.setText(String.valueOf(Constants.SALE_MAX_NUM));
                                etCount.setSelection(String.valueOf(Constants.SALE_MAX_NUM).length());
                                PublicUtil.showToast(getString(R.string.input_huge_num));
                                imgAdd.setImageResource(R.mipmap.add_disable3);
                            } else {

                                if (String.valueOf(Constants.SALE_MAX_NUM).equals(s.toString())) {
                                    imgAdd.setImageResource(R.mipmap.add_disable3);
                                } else {
                                    imgAdd.setImageResource(R.mipmap.add_normal3);
                                }
                            }

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if ("".equals(s.toString())) {
                                imgReduce.setImageResource(R.mipmap.minus_disable3);
                            }

                        }
                    });
                    String amount = ((EditText) v).getText().toString();
                    etCount.setText(amount);
                    if ("0".equals(amount)) {
                        imgReduce.setImageResource(R.mipmap.minus_disable3);
                    } else {
                        imgReduce.setImageResource(R.mipmap.minus_normal3);
                    }
                    etCount.setGravity(Gravity.CENTER);
                    etCount.selectAll();
                    dialog.setView(view);
                    dialog.setPositiveButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(getString(R.string.btn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int count = 0;
                                    try {
                                        String countStr = etCount.getText().toString();
                                        if (StringUtils.isNotBlank(countStr)) {
                                            count = Integer.valueOf(etCount.getText().toString());
                                        }
                                    } catch (Exception e) {
                                        PublicUtil.showToast(getString(R.string.input_huge_num));
                                    }
                                    if (count > 0) {
                                        wd.setSalesNum(count);
                                        workDiaryList.set(position, wd);
                                        etAmount.setText(String.valueOf(count));
                                        setBottomAmount();
                                    }

                                }
                            });
                    dialog.setCancelable(false);
                    dialog.show();

                    etCount.requestFocus();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) etAmount.getContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(etCount, 0);
                        }

                    }, 100);
                }
            });
            return convertView;
        }
    }

    private void setBottomAmount() {
        int totalCount = 0;
        int totalBreedNum = 0;
        if (workDiaryList == null || workDiaryList.size() > 0) {
            for (Product wd :workDiaryList) {
                totalBreedNum ++;
                totalCount += wd.getSalesNum();
            }
        }
        breedNum = totalBreedNum;
        count = totalCount;
        txtTotalBreedNum.setText(String.format(getString(R.string.total_breed_num), totalBreedNum));
        txtTotalNum.setText(String.format(getString(R.string.total_num), totalCount));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {

        if (workDiaryList != null && workDiaryList.size() > 0) {
            boolean tag = false;
            if (workDiaryListOld != null && workDiaryList.size() != workDiaryListOld.size()) {
                tag = true;
            } else {
                if (workDiaryListOld != null && workDiaryListOld.size() > 0) {
                    a : for (Product product : workDiaryList) {
                        for (Product p : workDiaryListOld) {
                            if (product.getProductCode().equals(p.getProductCode()) && product.getSalesNum() != p.getSalesNum()) {
                                tag = true;
                                break a;
                            }
                        }
                    }
                } else {
                    tag = true;
                }

            }

            if (tag) {
                new CommonAlertDialog.Builder(mContext)
                        .setMessage(R.string.work_diary_not_save_content)
                        .setNegativeButton(R.string.btn_ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        finish();
                                    }
                                }).setPositiveButton(R.string.btn_cancel, null)
                        .show();
            } else {
                finish();
            }
        } else {
            finish();
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
