package cn.com.bluemoon.delivery.jobrecord;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ImageInfo;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PeopleFlow;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PromoteInfo;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.ShowMultipleImageView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * Created by LIANGJIANGLI on 2016/6/27.
 */
public class PromoteDetailActivity extends Activity {
    private String TAG = "PromoteDetailActivity";
    private CommonProgressDialog progressDialog;
    private PromoteDetailActivity mContext;
    private String bpCode;
    private TextView txtArea;
    private TextView txtPlace;
    private TextView txtPlaceType;
    private TextView txtWorkPrice;
    private TextView txtHolidayPrice;
    private TextView txtDeposit;
    private TextView txtOtherFee;
    private TextView txtWifi;
    private TextView txtNetwork;
    private TextView txtBpname;
    private TextView txtBpname1;
    private GridView gridView;
    private ListView listview;
    private LinearLayout layoutImage;
    private LinearLayout layoutFlow;
    private List<ImageInfo> images;
    private List<PeopleFlow> people;
    private KJBitmap kjBitmap = new KJBitmap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote_detail);
        mContext = this;
        progressDialog = new CommonProgressDialog(this);
        initCustomActionBar();
        txtArea = (TextView) findViewById(R.id.txt_area);
        txtPlace = (TextView) findViewById(R.id.txt_place);
        txtPlaceType = (TextView) findViewById(R.id.txt_place_type);
        txtWorkPrice = (TextView) findViewById(R.id.txt_work_price);
        txtHolidayPrice = (TextView) findViewById(R.id.txt_holiday_price);
        txtDeposit = (TextView) findViewById(R.id.txt_deposit);
        txtOtherFee = (TextView) findViewById(R.id.txt_other_fee);
        txtWifi = (TextView) findViewById(R.id.txt_wifi);
        txtNetwork = (TextView) findViewById(R.id.txt_network);
        txtBpname = (TextView) findViewById(R.id.txt_bpname);
        txtBpname1 = (TextView) findViewById(R.id.txt_bpname1);
        gridView = (GridView) findViewById(R.id.gridview_img);
        listview = (ListView) findViewById(R.id.listview_people_flow);
        layoutImage = (LinearLayout) findViewById(R.id.layout_image);
        layoutFlow = (LinearLayout) findViewById(R.id.layout_flow);
        bpCode = getIntent().getStringExtra("bpCode");
        progressDialog.show();
        DeliveryApi.getPromoteInfo(ClientStateManager.getLoginToken(this), bpCode, getPromoteInfoHandler );
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (images != null && images.size() > 0) {
                    String[] urls = new String[images.size()];
                    for (int i = 0; i < images.size(); i++) {
                        urls[i] = images.get(i).getFilePath();
                    }
                    Intent intent = new Intent(PromoteDetailActivity.this, ShowMultipleImageView.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bitmaps", urls);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
    AsyncHttpResponseHandler getPromoteInfoHandler = new TextHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d("test", "getPromoteInfoHandler result = " + responseString);
            progressDialog.dismiss();
            try {
                ResultPromoteInfo result = JSON.parseObject(responseString,
                        ResultPromoteInfo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PromoteInfo info = result.getPromoteInfo();
                    txtArea.setText(StringUtil.formatArea(info.getUseArea()));
                    txtPlace.setText(info.getAddress());
                    txtPlaceType.setText(String.valueOf(info.getSiteTypeName()));
                    txtWorkPrice.setText(StringUtil.formatPrice(info.getWorkPrice()));
                    txtHolidayPrice.setText(StringUtil.formatPrice(info.getHolidayPrice()));
                    txtDeposit.setText(StringUtil.formatPrice(info.getCashPledge()));
                    txtOtherFee.setText(StringUtils.isNotBlank(info.getRemark()) ? info.getRemark() : getString(R.string.promote_none));
                    txtWifi.setText(info.getWifi()? getString(R.string.promote_has) : getString(R.string.promote_none));
                    txtNetwork.setText(info.getWiredNetwork()? getString(R.string.promote_has) : getString(R.string.promote_none));
                    txtBpname.setText(String.format(getString(R.string.promote_append),info.getBpCode1(), info.getBpName1()));
                    txtBpname1.setText(String.format(getString(R.string.promote_append),info.getBpCode(), info.getBpName()));
                    images = info.getPicInfo();
                    if (images == null || images.size() == 0) {
                        layoutImage.setVisibility(View.GONE);
                    } else {
                        ImageAdapter adapter = new ImageAdapter();
                        gridView.setAdapter(adapter);
                    }


                    people = info.getPeopleFlow();
                    if (people == null || people.size() == 0) {
                        layoutFlow.setVisibility(View.GONE);
                    } else {
                        listview.setFocusable(false);
                        PeopleFlowAdapter peopleFlowAdapter = new PeopleFlowAdapter();
                        listview.setAdapter(peopleFlowAdapter);
                        LibViewUtil.setListViewHeight(listview);
                    }

                } else {
                    PublicUtil.showErrorMsg(PromoteDetailActivity.this, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.d("test", "getPromoteInfoHandler result failed. statusCode="
                    + statusCode);
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.promote_detail_title));
            }

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }
        });

    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(TAG);
    }

    class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(mContext).inflate( R.layout.layout_image_for_gridview, null);

            final ImageView imgWork = (ImageView) convertView.findViewById(R.id.img_promote);
            final ImageInfo img = images.get(position);
            if (StringUtils.isNotBlank(img.getFilePath())) {
                kjBitmap.display(imgWork, img.getFilePath(), new BitmapCallBack() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        img.setBitmap(bitmap);
                    }
                });
            }
            return convertView;
        }

    }

    class PeopleFlowAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return people.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate( R.layout.list_item_people_flow, null);
            }
            final PeopleFlow person = people.get(position);
            final TextView txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            final TextView txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            final View line1 = (View) convertView.findViewById(R.id.line_1);
            final View line2 = (View) convertView.findViewById(R.id.line_2);
            if (position != people.size() -1) {
                line1.setVisibility(View.VISIBLE);
                line2.setVisibility(View.GONE);
            } else {
                line1.setVisibility(View.GONE);
                line2.setVisibility(View.VISIBLE);
            }

            String dateStr = String.format(getString(R.string.promote_date_txt),
                    DateUtil.getTime(person.getCreateDate(), "yyyy-MM-dd  EE"), person.getAddress());
            String timeStr = String.format(getString(R.string.promote_time_txt),
                    DateUtil.getTime(person.getStartTime(), "HH:mm"), DateUtil.getTime(person.getEndTime(), "HH:mm"), person.getPeopleFlow());
            txtDate.setText(dateStr);
            txtTime.setText(timeStr);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeopleFlowActivity.class);
                    intent.putExtra("type", 3);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("personFlow", person);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            return convertView;
        }

    }

}
