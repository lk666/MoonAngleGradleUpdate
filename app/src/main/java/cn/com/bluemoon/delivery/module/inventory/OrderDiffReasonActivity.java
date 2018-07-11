package cn.com.bluemoon.delivery.module.inventory;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.tagview.TagView;


public class OrderDiffReasonActivity extends BaseActivity {

    public static String KEY_POS = "pos";
    public static String KEY_DICTID = "dictId";
    public static String KEY_DICTNAME = "dictName";
    public static String KEY_DIFFREASONDETAIL = "diffReasonDetail";
    @BindView(R.id.tag_listview)
    TagListView tagListview;
    @BindView(R.id.ed_content)
    EditText edContent;
    private List<Dict> listDict;
    private String dictId;
    private String dictName;
    private String diffReasonDetail;
    private int pos;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_diff_reason;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        listDict = new ArrayList<>();
        pos = getIntent().getIntExtra(KEY_POS, 0);
        dictId = getIntent().getStringExtra(KEY_DICTID);
        dictName = getIntent().getStringExtra(KEY_DICTNAME);
        diffReasonDetail = getIntent().getStringExtra(KEY_DIFFREASONDETAIL);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.text_diff_reason_title);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        showWaitDialog();
        DeliveryApi.queryDifferReasons(ClientStateManager.getLoginToken(), getNewHandler(0, ResultDict.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultDict resultDict = (ResultDict) result;
        if (resultDict != null && resultDict.getDictList() != null) {
            listDict.addAll(resultDict.getDictList());
            init();
        }
    }

    private void init() {

        tagListview.setMode(TagListView.Mode.single);
        List<Tag> mTags = new ArrayList<>();
        for (int i = 0; i < listDict.size(); i++) {
            Tag tag = new Tag();
            tag.setId(i);
            if (!TextUtils.isEmpty(dictId) && dictId.equals(listDict.get(i).getDictId())) {
                tag.setChecked(true);
            }
            tag.setKey(listDict.get(i).getDictId());
            tag.setTitle(listDict.get(i).getDictName());
            mTags.add(tag);

        }
        tagListview.setTags(mTags);
        edContent.setText(diffReasonDetail);
    }

    public static void actStart(Activity context, int requestCode, int pos, String dictId, String dictName, String diffReasonDetail) {
        Intent intent = new Intent(context, OrderDiffReasonActivity.class);
        intent.putExtra(KEY_POS, pos);
        intent.putExtra(KEY_DICTID, dictId);
        intent.putExtra(KEY_DICTNAME, dictName);
        intent.putExtra(KEY_DIFFREASONDETAIL, diffReasonDetail);
        context.startActivityForResult(intent, requestCode);
    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        String dictId;
        if (tagListview.getTagsChecked().size() <= 0) {
            toast(R.string.text_diff_reason);
            return;
        }

        Tag tag = tagListview.getTagsChecked().get(0);
        dictName = tag.getTitle();
        dictId = tag.getKey();
        diffReasonDetail = edContent.getText().toString();

        if (TextUtils.isEmpty(dictName)) {
            toast(R.string.text_diff_reason);
            return;
        }

        Intent it = new Intent();
        it.putExtra(KEY_POS, pos);
        it.putExtra(KEY_DICTID, dictId);
        it.putExtra(KEY_DICTNAME, dictName);//差异原因
        it.putExtra(KEY_DIFFREASONDETAIL, diffReasonDetail);//差异原因说明

        setResult(RESULT_OK, it);
        finish();
    }
}
