package cn.com.bluemoon.delivery.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.address.Area;
import cn.com.bluemoon.delivery.app.api.model.address.ResultArea;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.newbase.IHttpResponse;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.jdaddressselectdialog.IItem;
import cn.com.bluemoon.lib_widget.module.jdaddressselectdialog.JdAddressSelectPopDialog;
import cn.com.bluemoon.liblog.LogUtils;
import cn.com.bluemoon.liblog.NetLogUtils;

/**
 * 高仿ios京东地址选择弹窗。没有做onSaveInstanceState，所以需要每次都新建一个dialog（不应将dialog保存到内存）
 * <p>
 * Created by lk on 2017/6/2.
 */

public class AddressSelectPopWindow extends JdAddressSelectPopDialog implements IHttpResponse {

    private static final int REQUEST_CODE_GET_REGION_INIT_PROVINCE = 0x777;
    private static final int REQUEST_CODE_GET_REGION_INIT_CITY = 0x776;
    private static final int REQUEST_CODE_GET_REGION_INIT_COUNTRY = 0x775;
    private static final int REQUEST_CODE_GET_REGION_SELECTED = 0x666;
    private static final int REQUEST_CODE_GET_REGION_CHOOSE = 0x555;

    private String initProvinceId;
    private String initCityId;
    private String initCountyId;

    private Activity activity;

    /**
     * 设置初始化时选中的的省市区
     */
    public static AddressSelectPopWindow newInstance(Activity activity, String provinceId, String
            cityId, String countyId) {
        AddressSelectPopWindow dialog = new AddressSelectPopWindow(activity);
        dialog.initProvinceId = provinceId;
        dialog.initCityId = cityId;
        dialog.initCountyId = countyId;
        dialog.activity = activity;
        return dialog;
    }

    private AddressSelectPopWindow(Context context) {
        super(context);
    }

    @Override
    protected void initData() {
        // 初始化数据
        initAreas = new ArrayList<>();

        DeliveryApi.getRegionSelect(null, null, getHandler(REQUEST_CODE_GET_REGION_INIT_PROVINCE,
                ResultArea.class, this));
    }

    private WithContextTextHttpResponseHandler getHandler(int requestCode, Class clazz,
                                                          final IHttpResponse iHttpResponse) {
        WithContextTextHttpResponseHandler handler = new WithContextTextHttpResponseHandler(
                HTTP.UTF_8, activity, requestCode, clazz) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (iHttpResponse == null) {
                    return;
                }
                try {
                    Object resultObj;
                    resultObj = JSON.parseObject(responseString, getClazz());
                    if (resultObj instanceof ResultBase) {
                        ResultBase resultBase = (ResultBase) resultObj;
                        if (resultBase.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                            NetLogUtils.dNetResponse(Constants.TAG_HTTP_RESPONSE_SUCCESS, getUuid(),
                                    System.currentTimeMillis(), responseString);
                            iHttpResponse.onSuccessResponse(getReqCode(),
                                    responseString, resultBase);
                        } else {
                            NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_NOT_SUCCESS,
                                    getUuid(), System
                                            .currentTimeMillis(), responseString, new Exception
                                            ("resultBase.getResponseCode() = " + resultBase
                                                    .getResponseCode() + "-->" + responseString));
                            iHttpResponse.onErrorResponse(getReqCode(), resultBase);
                        }
                    } else {
                        throw new Exception
                                ("转换ResultBase失败-->" + responseString);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_EXCEPTION, getUuid(),
                            System.currentTimeMillis(), responseString, e);
                    iHttpResponse.onSuccessException(getReqCode(), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_FAILURE, getUuid(), System
                        .currentTimeMillis(), responseString, throwable);

                if (iHttpResponse == null) {
                    return;
                }
                iHttpResponse.onFailureResponse(getReqCode(), throwable);
            }
        };
        return handler;
    }


    /**
     * 初始化数据
     */
    private List<List<Area>> initAreas;

    /**
     * 设置初始化数据
     */
    private void setInitData() {
        int num = initAreas.size();
        // 选中的下标
        List<Integer> selectedPos = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            List<Area> as = initAreas.get(i);

            int selected = -1;
            // 当前选中的是省/市/区
            String selectedId = (i == 0 ? initProvinceId :
                    (i == 1 ? initCityId :
                            (i == 2 ? initCountyId : null)));

            int count = as.size();
            for (int j = 0; j < count; j++) {
                Area a = as.get(j);
                if (a == null || a.getDcode() == null) {
                    LogUtils.e("获取的地址列表数据项为空：i=" + i + ", selectedId=" + selectedId
                            + ", j=" + j);
                } else {
                    if (a.getDcode().equals(selectedId)) {
                        selected = j;
                        break;
                    }
                }
            }

            selectedPos.add(selected);

            // 选中项没有时，此项开始为请选择的列表
            if (selected == -1) {
                break;
            }
        }

        setInitData(initAreas, selectedPos);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 初始化获取省列表
            case REQUEST_CODE_GET_REGION_INIT_PROVINCE:
                ResultArea initAreaProvince = (ResultArea) result;
                if (initAreaProvince.getLists() != null
                        && initAreaProvince.getLists().size() > 0) {
                    initAreas.add(initAreaProvince.getLists());
                    // 初始省不为空，获取其所有的市
                    if (!TextUtils.isEmpty(initProvinceId)) {
                        DeliveryApi.getRegionSelect(initProvinceId, "city",
                                getHandler(REQUEST_CODE_GET_REGION_INIT_CITY,
                                        ResultArea.class, this));
                    }
                    // 初始省为空
                    else {
                        setInitData();
                    }
                }
                // 初始化获取省列表为空，直接报错
                else {
                    LogUtils.e("初始化获取省列表为空");
                    initFail();
                }
                break;
            // 初始化获取市列表
            case REQUEST_CODE_GET_REGION_INIT_CITY:
                ResultArea initAreaCity = (ResultArea) result;
                if (initAreaCity.getLists() != null
                        && initAreaCity.getLists().size() > 0) {
                    initAreas.add(initAreaCity.getLists());
                    // 初始市不为空，获取其所有的区
                    if (!TextUtils.isEmpty(initCityId)) {
                        DeliveryApi.getRegionSelect(initCityId, "county",
                                getHandler(REQUEST_CODE_GET_REGION_INIT_COUNTRY,
                                        ResultArea.class, this));
                    }
                    // 初始市为空
                    else {
                        setInitData();
                    }
                }
                // 初始化获取市列表为空，直接报错
                else {
                    LogUtils.e("初始化获取市列表为空：" + initProvinceId);
                    initFail();
                }
                break;
            // 初始化获取区列表
            case REQUEST_CODE_GET_REGION_INIT_COUNTRY:
                ResultArea initAreaCountry = (ResultArea) result;
                if (initAreaCountry.getLists() != null
                        && initAreaCountry.getLists().size() > 0) {
                    initAreas.add(initAreaCountry.getLists());
                    // 设置数据
                    setInitData();
                }
                // 初始化获取区列表为空，直接报错
                else {
                    LogUtils.e("初始化获取区列表为空：" + initCityId);
                    initFail();
                }
                break;

            // 点击vp列表项，且点击的是请选择列表，没到最后一级
            case REQUEST_CODE_GET_REGION_CHOOSE:
                ResultArea areaChoose = (ResultArea) result;
                sv.addChoose(curListPos, areaChoose.getLists());
                break;
            // 点击vp列表项，且点击的不是请选择列表，没到最后一级
            case REQUEST_CODE_GET_REGION_SELECTED:
                ResultArea areaSelected = (ResultArea) result;
                sv.addSelected(curListPos, areaSelected.getLists());
                break;
        }
    }

    private void initFail() {
        ViewUtil.toast("初始化地址错误");
        dismiss();
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        ViewUtil.toastBusy();
        dismiss();
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        ViewUtil.toastOvertime();
        dismiss();
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        ViewUtil.showErrorMsg(result);
        dismiss();
    }

    public interface IAddressSelectDialog {
        void onSelect(Area province, Area city, Area country);
    }

    public void setListener(IAddressSelectDialog listener) {
        this.listener = listener;
    }

    private IAddressSelectDialog listener;

    /**
     * 选择结束，返回的结果
     *
     * @param selectedItems 选中的项列表
     */
    @Override
    public void onSelectedFinish(List<IItem> selectedItems) {
        if (listener != null && selectedItems != null && !selectedItems.isEmpty()) {
            Area province = (Area) selectedItems.get(0);

            Area city = null;
            if (selectedItems.size() > 1) {
                city = (Area) selectedItems.get(1);
            }

            Area country = null;
            if (selectedItems.size() > 2) {
                country = (Area) selectedItems.get(2);
            }

            listener.onSelect(province, city, country);
        }
        dismiss();
    }

    /**
     * 缓存onClickChooseItem和onClickSelectedItem的curPos
     */
    private int curListPos;

    @Override
    public void onClickChooseItem(int curPos, IItem iItem) {
        if (iItem instanceof Area) {
            Area area = (Area) iItem;
            curListPos = curPos;
            // 不需要手动showWaiting
            DeliveryApi.getRegionSelect(area.getDcode(), area.getChildType(),
                    getHandler(REQUEST_CODE_GET_REGION_CHOOSE, ResultArea.class, this));
        }
    }

    @Override
    public void onClickSelectedItem(int curPos, IItem iItem) {
        if (iItem instanceof Area) {
            Area area = (Area) iItem;
            curListPos = curPos;
            // 不需要手动showWaiting
            DeliveryApi.getRegionSelect(area.getDcode(), area.getChildType(),
                    getHandler(REQUEST_CODE_GET_REGION_SELECTED, ResultArea.class, this));
        }
    }

}
