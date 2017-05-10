package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetCooperationList;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultSaveWashClothes;
import cn.com.bluemoon.delivery.module.base.BaseActivity;

/**
 * 选择衣物类型
 */
public class SelectClothesTypeActivity extends BaseActivity implements
        ClothesSelectTypeGrid.IClothesSelectTypeGrid {
    private static final String EXTRA_OUTER_CODE = "EXTRA_OUTER_CODE";

    private static final int REQUEST_CODE_QUERY = 0x777;
    private static final int REQUEST_CODE_SAVE = 0x666;
    public static final String EXTRA_CLOTHES = "EXTRA_CLOTHES";
    @Bind(R.id.elv)
    ExpandableListView elv;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.divList)
    View divList;

    private String outerCode;

    private List<ResultGetCooperationList.WashListBean> list;
    private List<ResultGetCooperationList.GoodsInfoListBean> goodsInfoList;
    private ItemAdapter adapter;

    /**
     * @param outerCode 洗衣订单编码
     */
    public static void actionStart(Activity context, String outerCode, int requestCode) {
        Intent intent = new Intent(context, SelectClothesTypeActivity.class);
        intent.putExtra(EXTRA_OUTER_CODE, outerCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        outerCode = getIntent().getStringExtra(EXTRA_OUTER_CODE);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.select_clothes_type);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sleect_clothes_type;
    }

    @Override
    public void initView() {
        goodsInfoList = new ArrayList<>();
        list = new ArrayList<>();
        adapter = new ItemAdapter();
        adapter.setList(list);
        elv.setAdapter(adapter);
    }

    @Override
    public void initData() {
        showWaitDialog();
        EnterpriseApi.getCooperationList(outerCode, getToken(),
                getNewHandler(REQUEST_CODE_QUERY, ResultGetCooperationList.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 查询数据
            case REQUEST_CODE_QUERY:
                setData((ResultGetCooperationList) result);
                break;

            // 添加衣物成功
            case REQUEST_CODE_SAVE:
                ResultSaveWashClothes clothes = (ResultSaveWashClothes) result;
                Intent i = new Intent();
                i.putExtra(EXTRA_CLOTHES, clothes.clothesInfo);
                setResult(RESULT_OK, i);
                finish();
                break;
        }
    }

    private ClothesSelectTypeHeader header;

    /**
     * 设置数据
     */
    private void setData(ResultGetCooperationList info) {
        if (info == null) {
            return;
        }

        lastOnLevelPosition = -1;
        lastTwoLevelPosition = -1;

        // 头
        goodsInfoList.clear();
        if (info.goodsInfoList != null) {
            goodsInfoList.addAll(info.goodsInfoList);
            if (header == null) {
                header = new ClothesSelectTypeHeader(this);
                header.setListener(this);
                elv.addHeaderView(header);
            }
            header.setData(info.goodsInfoList);
            header.setVisibility(View.VISIBLE);
        } else {
            if (header == null) {
                header.setVisibility(View.GONE);
            }
        }

        list.clear();
        list.addAll(info.washList);
        adapter.notifyDataSetChanged();

        if (list.size() > 0) {
            divList.setVisibility(View.VISIBLE);
        } else {
            divList.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_cancel, R.id.btn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_ok:
                String washCode;
                if (lastOnLevelPosition == ClothesSelectTypeHeader.DEF_ONE_LEVEL) {
                    washCode = goodsInfoList.get(lastTwoLevelPosition).washCode;
                } else if (lastOnLevelPosition > -1 && lastTwoLevelPosition > -1) {
                    washCode = list.get(lastOnLevelPosition).twoLevelList.get
                            (lastTwoLevelPosition).washCode;
                } else {
                    toast(getString(R.string.hint_select_type));
                    return;
                }

                showWaitDialog();
                EnterpriseApi.saveWashClothes(outerCode, washCode, getToken(),
                        getNewHandler(REQUEST_CODE_SAVE, ResultSaveWashClothes.class));
                break;
        }
    }

    private int lastOnLevelPosition = -1;
    private int lastTwoLevelPosition = -1;

    @Override
    public void onSelectedChanged(int onLevelPosition, int twoLevelPosition) {
        // 选中头部
        if (ClothesSelectTypeHeader.DEF_ONE_LEVEL == onLevelPosition) {
            goodsInfoList.get(twoLevelPosition).isSelected = true;
            // 取消之前的选中的头部数据
            if (lastOnLevelPosition == ClothesSelectTypeHeader.DEF_ONE_LEVEL) {
                goodsInfoList.get(lastTwoLevelPosition).isSelected = false;
            }
            // 取消之前的选中的列表
            else {
                if (lastOnLevelPosition > -1 && lastTwoLevelPosition > -1) {
                    list.get(lastOnLevelPosition).twoLevelList.get(lastTwoLevelPosition)
                            .isSelected = false;
                }

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            header.refreshData();
            lastOnLevelPosition = onLevelPosition;
            lastTwoLevelPosition = twoLevelPosition;
        }
        // 选中列表
        else {
            // 上次选中默认项
            if (lastOnLevelPosition == ClothesSelectTypeHeader.DEF_ONE_LEVEL) {
                goodsInfoList.get(lastTwoLevelPosition).isSelected = false;
                header.refreshData();
            }
            // 取消之前的选中的列表
            else {
                if (lastOnLevelPosition > -1 && lastTwoLevelPosition > -1) {
                    list.get(lastOnLevelPosition).twoLevelList.get(lastTwoLevelPosition)
                            .isSelected = false;
                }
            }

            // 选中当前列表项
            list.get(onLevelPosition).twoLevelList.get(twoLevelPosition).isSelected = true;

            //     elv.collapseGroup(lastOnLevelPosition);
            lastOnLevelPosition = onLevelPosition;
            lastTwoLevelPosition = twoLevelPosition;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class ItemAdapter extends BaseExpandableListAdapter {

        private List<ResultGetCooperationList.WashListBean> list;

        public ItemAdapter() {
            super();
        }

        public void setList(List<ResultGetCooperationList.WashListBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        //得到子item需要关联的数据
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list.get(groupPosition).twoLevelList.get(childPosition);
        }

        //得到子item的ID
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //设置子item的组件
        @SuppressLint("DefaultLocale")
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (list == null || list.get(groupPosition).twoLevelList == null || list.get
                    (groupPosition).twoLevelList.isEmpty()) {
                return null;
            }

            ClothesSelectTypeGrid gv;
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectClothesTypeActivity.this).inflate
                        (R.layout.item_select_clothes_type_child, null);

                gv = (ClothesSelectTypeGrid) convertView.findViewById(R.id.gv);
                gv.setListener(SelectClothesTypeActivity.this);
            } else {
                gv = (ClothesSelectTypeGrid) convertView.findViewById(R.id.gv);
            }

            gv.setData(groupPosition, list.get(groupPosition).twoLevelList);

            return convertView;
        }

        //获取当前父item下的子item的个数
        @Override
        public int getChildrenCount(int groupPosition) {
            if (list == null || list.get(groupPosition).twoLevelList == null || list.get
                    (groupPosition)
                    .twoLevelList.isEmpty()) {
                return 0;
            }
            return 1;
        }

        //获取当前父item的数据
        @Override
        public Object getGroup(int groupPosition) {
            return list.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //设置父item组件
        @SuppressLint("DefaultLocale")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            ResultGetCooperationList.WashListBean item = list.get(groupPosition);
            if (item == null) {
                return null;
            }

            if (convertView == null) {
                convertView = LayoutInflater.from(SelectClothesTypeActivity.this).inflate
                        (R.layout.item_select_clothes_type_parent, null);
            }

            TextView tv = (TextView) convertView.findViewById(R.id.tv);
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv);

            tv.setText(item.oneLevelName);
            if (isExpanded) {
                iv.setImageResource(R.mipmap.ic_arrow_up);
            } else {
                iv.setImageResource(R.mipmap.ic_arrow_down);
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
}
