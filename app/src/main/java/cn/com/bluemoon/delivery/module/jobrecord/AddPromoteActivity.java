package cn.com.bluemoon.delivery.module.jobrecord;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ImageInfo;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PeopleFlow;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PromoteInfo;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultBpList;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultImageUpload;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.ObservableScrollView;
import cn.com.bluemoon.delivery.utils.AnimationUtils;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.TextWatcherUtils;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.TakePhotoPopView;


/**
 *
 * Created by LIANGJIANGLI on 2016/6/24.
 */
public class AddPromoteActivity extends Activity implements ObservableScrollView.ScrollListener {

    private String TAG = "AddPromoteActivity";
    private CommonProgressDialog progressDialog;
    private EditText etPromotePlace;
    private EditText etArea;
    private EditText etWorkMoney;
    private EditText etHolidayMoney;
    private EditText etDepositMoney;
    private EditText etPromoteOtherFee;
    private TextView txtOutdoor;
    private TextView txtIndoor;
    private CompoundButton cbRent;
    private CompoundButton cbWifi;
    private CompoundButton cbNetWork;
    private LinearLayout layoutRent;
    int layoutRentHeight = 0;
    private Button btnOk;
    private TextView txtBpname;
    private ListView listview;
    private PeopleFlowAdapter peopleFlowAdapter;
    private List<PeopleFlow> flows = new ArrayList<>();
    private List<PeopleFlow> deleteFlows = new ArrayList<>();
    private int index;
    private List<ImageInfo> images = new ArrayList<>();
    private TakePhotoPopView takePhotoPop;
    private ImageAdapter imageAdapter;
    private String bpCode;
    private String bpName;
    private String inOrOut;
    private List<ImageInfo> imgIds = new ArrayList<>();
    private PromoteInfo info;
    private PromoteInfo oldInfo;
    private String oldAddress;
    private boolean isEdit;
    private KJBitmap kjBitmap = new KJBitmap();
    private boolean isModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promote);
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        initCustomActionBar();

        takePhotoPop = new TakePhotoPopView(this,
                Constants.TAKE_PIC_RESULT, Constants.CHOSE_PIC_RESULT);

        etPromotePlace = (EditText) findViewById(R.id.et_promote_place);
        etArea = (EditText) findViewById(R.id.et_area);
        etWorkMoney = (EditText) findViewById(R.id.et_work_money);
        etHolidayMoney = (EditText) findViewById(R.id.et_holiday_money);
        etDepositMoney = (EditText) findViewById(R.id.et_deposit_money);
        etPromoteOtherFee = (EditText) findViewById(R.id.et_promote_other_fee);
        txtOutdoor = (TextView) findViewById(R.id.txt_outdoor);
        txtIndoor = (TextView) findViewById(R.id.txt_indoor);
        cbRent = (CompoundButton) findViewById(R.id.cb_rent);
        cbWifi = (CompoundButton) findViewById(R.id.cb_wifi);
        cbNetWork = (CompoundButton) findViewById(R.id.cb_network);
        layoutRent = (LinearLayout) findViewById(R.id.layout_rent);
        btnOk = (Button) findViewById(R.id.btn_ok);
        listview = (ListView) findViewById(R.id.listview_people_flow);
        LinearLayout layoutCommunitySelect = (LinearLayout) findViewById(R.id
                .layout_community_select);
        ImageView imgRight = (ImageView) findViewById(R.id.img_community_right);
        GridView gridView = (GridView) findViewById(R.id.gridview_img);
        txtBpname = (TextView) findViewById(R.id.txt_bpname);
        layoutRentHeight = AnimationUtils.getTargetHeight(layoutRent);

        TextWatcherUtils.setMaxNumberWatcher(etArea, 6, 1, null);
        TextWatcherUtils.setMaxNumberWatcher(etWorkMoney, 6, 2, null);
        TextWatcherUtils.setMaxNumberWatcher(etHolidayMoney, 6, 2, null);
        TextWatcherUtils.setMaxNumberWatcher(etDepositMoney, 6, 2, null);

        ImageView imgAddFlow = (ImageView) findViewById(R.id.img_add_flow);
        ObservableScrollView root = (ObservableScrollView) findViewById(R.id.root);
        root.setScrollListener(this);
        progressDialog = new CommonProgressDialog(this);
        PublicUtil.setGravity(etPromotePlace);
        PublicUtil.setGravity(etPromoteOtherFee);

        peopleFlowAdapter = new PeopleFlowAdapter();
        listview.setAdapter(peopleFlowAdapter);
        LibViewUtil.setListViewHeight(listview);

        imgAddFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPromoteActivity.this, PeopleFlowActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, 1);
            }
        });

        txtOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtOutdoor.setBackgroundResource(R.drawable.btn_border_pink_shape4);
                txtOutdoor.setTextColor(getResources().getColor(R.color.text_red));
                txtIndoor.setBackgroundResource(R.drawable.btn_border_grep_shape4);
                txtIndoor.setTextColor(getResources().getColor(R.color.text_black_light));
                inOrOut = "outDoor";
            }
        });
        txtIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtIndoor.setBackgroundResource(R.drawable.btn_border_pink_shape4);
                txtIndoor.setTextColor(getResources().getColor(R.color.text_red));
                txtOutdoor.setBackgroundResource(R.drawable.btn_border_grep_shape4);
                txtOutdoor.setTextColor(getResources().getColor(R.color.text_black_light));
                inOrOut = "inDoor";
            }
        });


        cbRent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutRent.setVisibility(View.VISIBLE);
                } else {
                    layoutRent.setVisibility(View.GONE);
                }
            }
        });
        if (!isEdit) {
            layoutCommunitySelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddPromoteActivity.this, CommunitySelectActivity
                            .class);
                    intent.putExtra("bpCode", bpCode);
                    startActivityForResult(intent, 2);
                }
            });
        }


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_add_picture);
        ImageInfo image = new ImageInfo();
        image.setBitmap(bitmap);
        images.add(image);
        imageAdapter = new ImageAdapter();
        gridView.setAdapter(imageAdapter);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info == null) {
                    return;
                }
                String bpname = txtBpname.getText().toString();
                if (StringUtils.isNotBlank(bpname)) {
                    info.setBpCode1(bpCode);
                    info.setBpName1(bpName);
                } else {
                    PublicUtil.showToast(getString(R.string.community_select_txt));
                    return;
                }
                String place = etPromotePlace.getText().toString();
                String area = etArea.getText().toString();
                if (StringUtils.isNotBlank(place) && StringUtils.isNotBlank(area) && StringUtils
                        .isNotBlank(inOrOut)) {
                    info.setAddress(place);
                    info.setUseArea(Double.valueOf(area));
                    info.setSiteType(inOrOut);
                } else {
                    PublicUtil.showToast(getString(R.string.place_info_input));
                    return;
                }
                info.setRentInfo(cbRent.isChecked());
                if (cbRent.isChecked()) {
                    String workMoney = etWorkMoney.getText().toString();
                    String holidayMoney = etHolidayMoney.getText().toString();
                    String depositMoney = etDepositMoney.getText().toString();
                    if (StringUtils.isNotBlank(workMoney) && StringUtils.isNotBlank(holidayMoney)
                            && StringUtils.isNotBlank(depositMoney)) {
                        info.setWorkPrice(Double.valueOf(workMoney));
                        info.setHolidayPrice(Double.valueOf(holidayMoney));
                        info.setCashPledge(Double.valueOf(depositMoney));
                    } else {
                        PublicUtil.showToast(getString(R.string.rent_info_input));
                        return;
                    }

                    String otherFee = etPromoteOtherFee.getText().toString();
                    if (StringUtils.isNotBlank(otherFee)) {
                        info.setRemark(otherFee);
                    }
                } else {
                    info.setWorkPrice(0);
                    info.setHolidayPrice(0);
                    info.setCashPledge(0);
                    info.setRemark("");
                }
                info.setWifi(cbWifi.isChecked());
                info.setWiredNetwork(cbNetWork.isChecked());
                List<PeopleFlow> list = new ArrayList<>();
                list.addAll(deleteFlows);
                list.addAll(flows);
                if (list != null && list.size() > 0) {
                    info.setPeopleFlow(list);
                }
                progressDialog.show();
                btnOk.setEnabled(false);
                if (images.size() > 1 && images.size() > imgIds.size() + 1) {
                    for (int i = imgIds.size(); i < images.size() - 1; i++) {
                        if (images.get(i).getFileid() > -1) {
                            imgIds.add(images.get(i));
                            if (imgIds.size() >= images.size() - 1) {
                                setImageInfo();
                                DeliveryApi.editPromoteInfo(ClientStateManager.getLoginToken
                                        (AddPromoteActivity.this), info, saveHandler);
                                break;
                            }
                        } else {
                            DeliveryApi.uploadPromoteImg(ClientStateManager.getLoginToken
                                    (AddPromoteActivity.this), FileUtil.getBytes(images.get
                                    (imgIds.size()).getBitmap()), uploadHandler);
                            break;
                        }
                    }
                } else {
                    DeliveryApi.editPromoteInfo(ClientStateManager.getLoginToken
                            (AddPromoteActivity.this), info, saveHandler);
                }
            }
        });

        if (isEdit) {
            imgRight.setVisibility(View.GONE);
            progressDialog.show();
            DeliveryApi.getPromoteInfo(ClientStateManager.getLoginToken(this), getIntent()
                    .getStringExtra("bpCode"), getPromoteInfoHandler);
        } else {
            layoutCommunitySelect.setBackgroundResource(R.drawable.btn_white);
            info = new PromoteInfo();
        }
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
                    info = result.getPromoteInfo();
                    oldInfo = result.getPromoteInfo();
                    oldAddress = result.getPromoteInfo().getAddress();
                    bpCode = info.getBpCode2();
                    bpName = info.getBpName2();
                    info.setBpCode1(bpCode);
                    info.setBpName1(bpName);
                    txtBpname.setText(String.format("%s—%s", info.getBpCode2(), info.getBpName2()));
                    etPromotePlace.setText(info.getAddress());
                    etArea.setText(String.valueOf(info.getUseArea()));
                    inOrOut = info.getSiteType();
                    if ("inDoor".equals(inOrOut)) {
                        txtIndoor.setBackgroundResource(R.drawable.btn_border_pink_shape4);
                        txtIndoor.setTextColor(getResources().getColor(R.color.text_red));
                    } else {
                        txtOutdoor.setBackgroundResource(R.drawable.btn_border_pink_shape4);
                        txtOutdoor.setTextColor(getResources().getColor(R.color.text_red));
                    }
                    if (info.getRentInfo()) {
                        layoutRent.setVisibility(View.VISIBLE);
                        cbRent.setChecked(true);
                        etWorkMoney.setText(String.valueOf(info.getWorkPrice()));
                        etHolidayMoney.setText(String.valueOf(info.getHolidayPrice()));
                        etDepositMoney.setText(String.valueOf(info.getCashPledge()));
                        etPromoteOtherFee.setText(info.getRemark());
                    }
                    cbNetWork.setChecked(info.getWiredNetwork());
                    cbWifi.setChecked(info.getWifi());

                    if (info.getPicInfo() != null && info.getPicInfo().size() > 0) {
                        for (ImageInfo picInfo : info.getPicInfo()) {
                            images.add(picInfo);
                            setSortList(images);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }

                    flows.addAll(info.getPeopleFlow());
                    peopleFlowAdapter.notifyDataSetChanged();
                    LibViewUtil.setListViewHeight(listview);
                } else {
                    PublicUtil.showErrorMsg(AddPromoteActivity.this, result);
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

    AsyncHttpResponseHandler uploadHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            progressDialog.dismiss();
            btnOk.setEnabled(true);
            PublicUtil.showToastServerOvertime();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                ResultImageUpload result = JSON.parseObject(responseString, ResultImageUpload
                        .class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setFileid(Long.valueOf(result.getId()));
                    imgIds.add(imageInfo);
                    if (imgIds.size() < images.size() - 1) {
                        DeliveryApi.uploadPromoteImg(ClientStateManager.getLoginToken
                                (AddPromoteActivity.this), FileUtil.getBytes(images.get(imgIds
                                .size()).getBitmap()), uploadHandler);
                    } else {
                        setImageInfo();
                        DeliveryApi.editPromoteInfo(ClientStateManager.getLoginToken
                                (AddPromoteActivity.this), info, saveHandler);
                    }

                } else {
                    progressDialog.dismiss();
                    btnOk.setEnabled(true);
                    PublicUtil.showErrorMsg(AddPromoteActivity.this, result);
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                btnOk.setEnabled(true);
                PublicUtil.showToastServerBusy();
            }
        }
    };

    private void setImageInfo() {
        List<ImageInfo> saveImgList = new ArrayList<>();
        if (imgIds != null && imgIds.size() > 0) {
            for (ImageInfo image : imgIds) {
                ImageInfo saveImage = new ImageInfo();
                saveImage.setFileid(image.getFileid());
                saveImgList.add(saveImage);
            }
            info.setPicInfo(saveImgList);
        }
    }

    AsyncHttpResponseHandler saveHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                throwable) {
            btnOk.setEnabled(true);
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                LogUtils.d(responseString);
                ResultBase result = JSON.parseObject(responseString, ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    progressDialog.dismiss();
                    PublicUtil.showToast(result.getResponseMsg());
                    Intent data = new Intent();
                    if (isEdit) {
                        Bundle bundle = new Bundle();
                        String bpName = info.getBpName();
                        bpName = bpName.replace(oldAddress, "");
                        info.setBpName(bpName + info.getAddress());
                        bundle.putSerializable("promote", info);
                        data.putExtras(bundle);
                    }
                    setResult(1, data);
                    finish();
                } else {
                    btnOk.setEnabled(true);
                    progressDialog.dismiss();
                    PublicUtil.showErrorMsg(AddPromoteActivity.this, result);
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                PublicUtil.showToastServerBusy();
                btnOk.setEnabled(true);
            }
        }
    };

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        btnOk.setEnabled(true);
    }

    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null && resultCode == 1) {
                isModify = true;
                PeopleFlow peopleFlow = (PeopleFlow) data.getSerializableExtra("peopleFlow");
                peopleFlow.setRecordStatus("add");
                flows.add(peopleFlow);
            } else if (data != null && resultCode == 2) {
                PeopleFlow peopleFlow = (PeopleFlow) data.getSerializableExtra("peopleFlow");
                if (StringUtils.isNotBlank(peopleFlow.getFlowId())) {
                    isModify = true;
                    peopleFlow.setRecordStatus("edit");
                }
                flows.set(index, peopleFlow);
            } else if (resultCode == 3) {
                PeopleFlow flow = flows.get(index);
                if (StringUtils.isNotBlank(flow.getFlowId())) {
                    isModify = true;
                    flow.setRecordStatus("delete");
                    deleteFlows.add(flow);
                }
                flows.remove(index);
            }
            peopleFlowAdapter.notifyDataSetChanged();
            LibViewUtil.setListViewHeight(listview);
        } else if (requestCode == 2 && resultCode == 4 && data != null) {
            ResultBpList.Item bp = (ResultBpList.Item) data.getSerializableExtra("community");
            txtBpname.setText(String.format("%s—%s", bp.getBpCode(), bp.getBpName()));
            bpCode = bp.getBpCode();
            bpName = bp.getBpName();
        }

        if (resultCode == Activity.RESULT_OK) {
            Bitmap bm = null;
            switch (requestCode) {
                case Constants.TAKE_PIC_RESULT:
                    if (takePhotoPop != null) {
                        bm = takePhotoPop.getTakeImageBitmap();
                    }
                    break;
                case Constants.CHOSE_PIC_RESULT:
                    if (takePhotoPop != null) {
                        bm = takePhotoPop.getPickImageBitmap(data);
                    }
                    break;
            }
            if (bm != null) {
                isModify = true;
                ImageInfo image = new ImageInfo();
                image.setBitmap(bm);
                images.add(image);
                setSortList(images);
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setSortList(List<ImageInfo> images) {
        if (images != null) {
            ImageInfo second = images.get(images.size() - 2);
            ImageInfo last = images.get(images.size() - 1);
            images.set(images.size() - 2, last);
            images.set(images.size() - 1, second);
        }
    }


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                String title = getResources().getString(R.string.promote_add_title);
                if (isEdit) {
                    title = getResources().getString(R.string.promote_edit_title);
                }
                v.setText(title);
            }

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                showNotSaveDialog();
            }
        });

    }

    @Override
    public void scrollOritention(int oritention) {
        /*int isVisible = btnOk2.getVisibility();
        if (oritention > 0 && isVisible == View.VISIBLE) {
            btnOk2.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
        } else if (isVisible == View.GONE && oritention < 0){
            btnOk2.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.GONE);
        }*/
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

            convertView = LayoutInflater.from(AddPromoteActivity.this).inflate(R.layout
                    .layout_image_for_gridview, null);

            final ImageView imgPromote = (ImageView) convertView.findViewById(R.id.img_promote);
            final ImageView imgDelete = (ImageView) convertView.findViewById(R.id.img_delete);
            final ImageInfo img = images.get(position);
            imgPromote.setImageBitmap(img.getBitmap());

            if (position != images.size() - 1) {
                imgDelete.setVisibility(View.VISIBLE);
            } else {
                imgDelete.setVisibility(View.GONE);
            }

            if (img.getBitmap() == null && StringUtils.isNotBlank(img.getFilePath())) {
                kjBitmap.display(imgPromote, img.getFilePath(), new BitmapCallBack() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        img.setBitmap(bitmap);
                    }
                });
            }


            imgPromote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (img.getBitmap() == null && position != images.size() - 1) {
                        PublicUtil.showToast(getString(R.string.down_image_not_complete));
                    } else {
                        if (position == images.size() - 1 && images.size() < 6) {
                            takePhotoPop.getPic(v);
                            return;
                        }
                        DialogUtil.showPictureDialog(AddPromoteActivity.this, images.get
                                (position).getBitmap());

                    }
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isModify = true;
                    images.remove(position);
                    imageAdapter.notifyDataSetChanged();
                }
            });

            if (position == 5 && position == images.size() - 1) {
                imgPromote.setVisibility(View.GONE);
            } else {
                imgPromote.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

    }


    class PeopleFlowAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return flows.size();
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
                convertView = LayoutInflater.from(AddPromoteActivity.this).inflate(R.layout
                        .list_item_people_flow, null);
            }
            final PeopleFlow person = flows.get(position);
            final TextView txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            final TextView txtTime = (TextView) convertView.findViewById(R.id.txt_time);

            String dateStr = String.format(getString(R.string.promote_date_txt),
                    DateUtil.getTime(person.getCreateDate(), "yyyy-MM-dd EE"), person.getAddress());
            String timeStr = String.format(getString(R.string.promote_time_txt),
                    DateUtil.getTime(person.getStartTime(), "HH:mm"), DateUtil.getTime(person
                            .getEndTime(), "HH:mm"), person.getPeopleFlow());
            txtDate.setText(dateStr);
            txtTime.setText(timeStr);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = position;
                    Intent intent = new Intent(AddPromoteActivity.this, PeopleFlowActivity.class);
                    intent.putExtra("type", 2);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("peopleFlow", person);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1);
                }
            });

            return convertView;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showNotSaveDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showNotSaveDialog() {
        if (isChangeParams()) {
            new CommonAlertDialog.Builder(AddPromoteActivity.this).setMessage(getString(R.string
                    .promote_not_save)).
                    setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setPositiveButton(R.string.btn_cancel, null).show();
        } else {
            finish();
        }


    }

    private boolean isChangeParams() {
        String bpName = txtBpname.getText().toString();
        String place = etPromotePlace.getText().toString();
        String userArea = etArea.getText().toString();
        //inorout
        boolean isCheck = cbRent.isChecked();
        boolean isCheck1 = cbNetWork.isChecked();
        boolean isCheck2 = cbWifi.isChecked();
        if (!isEdit) {
            if (StringUtils.isNotBlank(bpName) || StringUtils.isNotBlank(place) || StringUtils
                    .isNotBlank(userArea) || StringUtils.isNotBlank(inOrOut)
                    || isCheck || isCheck1 || isCheck2 || images.size() > 1 || flows.size() > 0) {
                return true;
            }
        } else {
            if (oldInfo != null) {
                if ((bpCode != null && !bpCode.equals(oldInfo.getBpCode2())) || !place.equals
                        (oldInfo.getAddress()) || Double.valueOf(userArea) != oldInfo.getUseArea()
                        || !inOrOut.equals(oldInfo.getSiteType()) || isCheck != oldInfo
                        .getRentInfo() || isCheck1 != oldInfo.getWiredNetwork()
                        || isCheck2 != oldInfo.getWifi() || isModify) {
                    return true;
                }
            }
        }
        return false;
    }
}
