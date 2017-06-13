//package cn.com.bluemoon.delivery.module.newbase;
//
//import android.annotation.TargetApi;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.pm.PackageInfo;
//import android.database.Cursor;
//import android.provider.ContactsContract;
//import android.support.annotation.NonNull;
//
//import cn.com.bluemoon.delivery.AppContext;
//import cn.com.bluemoon.liblog.LogUtils;
//
///**
// * fragment继承层次：2（0顶层）
// * 基础Fragment，权限相关的适配类
// */
//public abstract class BasePermissionFragment extends BaseActionFragment {
//
//    /**
//     * 用户权限申请后的回调。
//     *
//     * @param requestCode  请求码
//     * @param permissions  权限
//     * @param grantResults 结果
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (hasAllPermissionsGranted(grantResults)) {
//            successPermissions(requestCode);
//        } else {
//            onFailPermissions(requestCode);
//        }
//    }
//
//    /**
//     * 是否已含有全部的权限
//     */
//    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
//        return PermissionsUtil.hasAllPermissionsGranted(grantResults);
//    }
//
//    private void successPermissions(int requestCode) {
//        onSuccessPermissions(requestCode);
//    }
//
//    /**
//     * 获取app的TargetVersion
//     */
//    private int getTargetVersion() {
//        try {
//            final PackageInfo info = AppContext.getInstance().getPackageManager()
//                    .getPackageInfo(AppContext.getInstance().getPackageName(), 0);
//            return info.applicationInfo.targetSdkVersion;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//    //////////////// 可选重写 ////////////////
//
//    /**
//     * <b>targetApi以及buildApi >= 23使用。</b><br/>
//     * 获取权限成功
//     */
//    @TargetApi(23)
//    protected void onSuccessPermissions(int requestCode) {
//        LogUtils.d(requestCode + " ==> get permission success");
//    }
//
//    /**
//     * <b>targetApi以及buildApi >= 23使用。</b><br/>
//     * 获取权限失败返回的结果。<br/>
//     * tips：可以调用
//     * {@link  PermissionsUtil#showMissingPermissionDialog(Context, DialogInterface.OnClickListener)}
//     * 方法弹出 去设置 弹窗
//     */
//    @TargetApi(23)
//    protected void onFailPermissions(int requestCode) {
//        LogUtils.e(requestCode + " ==> get permission fail");
//    }
//
//    //////////////// 工具方法 ////////////////
//
//    /**
//     * <b>targetApi以及buildApi >= 23使用。</b><br/>
//     * 检测权限是否存在，并尝试申请缺失权限，
//     * 并回调{@link #onRequestPermissionsResult(int, String[], int[])}
//     *
//     * @param requestCode 不能为{@link PermissionsUtil#PERMISSION_REQUEST_CODE_TODO}、
//     *                    {@link PermissionsUtil#PERMISSION_REQUEST_CODE_UNDO}、
//     *                    {@link #PERMISSION_REQUEST_CODE_INIT}
//     * @return 申请时（非申请后）是否已拥有全部权限
//     */
//    @TargetApi(23)
//    final public boolean checkPermissions(String[] permissions, int requestCode) {
//        if (permissions == null || permissions.length == 0) {
//            return true;
//        } else {
//            return PermissionsUtil.checkPermissions(this, permissions, requestCode);
//        }
//    }
//
//    /**
//     * <b>targetApi或buildApi < 23使用。</b><br/>
//     * 检测是否有读取联系人的权限
//     *
//     * @return true:有权限且联系人不为空，false：无权限或联系人为空
//     */
//    final protected boolean checkContractBelowApi23() {
//        Cursor cursor = null;
//        try { // 获取内容提供器
//            ContentResolver resolver = getContext().getContentResolver();
//            // 查询联系人数据
//            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                    null, null, null);
//            // 遍历联系人列表
//            if (cursor.moveToNext()) {
//                // 获取联系人姓名
//                String name = cursor.getString(cursor.getColumnIndex(ContactsContract
//                        .CommonDataKinds.Phone.DISPLAY_NAME));
//                // 获取联系人手机号
//                String number = cursor.getString(cursor.getColumnIndex(
//                        ContactsContract.CommonDataKinds.Phone.NUMBER));
//            } else {
//                return false;
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                try {
//                    cursor.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return false;
//    }
//
//
//    //
//    //    /**
//    //     * 使用PermissionChecker.checkSelfPermission来检测App是否有权限
//    //     * 1、Android < api23 时检查权限，系统会默认给予App所有的权限，但用户可以去定制的设置中关闭权限
//    //     * 2、Android >= api23 && targetSdkVersion < 23，系统会默认给予App所有的权限，但是用户可以去设置中关闭权限。
//    //     */
//    //    private boolean isLowApi23PermissionCheck(String[] permissions) {
//    //        for (String permission : permissions) {
//    // PermissionChecker.checkSelfPermission api21 没卵用
//    //            if (PermissionChecker.checkSelfPermission(getContext(),
//    //                    permission) != PermissionChecker.PERMISSION_GRANTED) {
//    //                return false;
//    //            }
//    //        }
//    //        return true;
//    //    }
//
//}
