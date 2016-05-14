/*
 *
 * Copyright (c) 2015 [admin@chenzhongjin | chenzhongjin@vip.qq.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.chenzhongjin.sample.net;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dreamliner.simplifyokhttp.OkHttpUtils;
import com.dreamliner.simplifyokhttp.callback.BaseResponse;
import com.dreamliner.simplifyokhttp.callback.DataCallBack;
import com.dreamliner.simplifyokhttp.callback.HttpCallBack;
import com.dreamliner.simplifyokhttp.utils.DreamLinerException;
import com.dreamliner.simplifyokhttp.utils.ErrorCode;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.chenzhongjin.sample.AppContext;
import cn.chenzhongjin.sample.entity.Weather;
import cn.chenzhongjin.sample.net.utils.NetUtils;
import cn.chenzhongjin.sample.net.utils.NetworkType;
import okhttp3.Call;

/**
 * @author chenzj
 * @Title: NetRequest
 * @Description: 访问请求类.拼装url并且访问成功之后返回结果.各种层次的错误都回调返回对应的状态码和错误信息
 * @date 2015/11/7
 * @email admin@chenzhongjin.cn
 */
public class NetRequest {
    private static Context mContext = null;
    public static final String TAG = "DreamLiner";
    private static NetRequest singleton;
    private int mPagesize = 20;
    private String mAppkey = "";
    private String mAppid = "";
    private String mDeviceid = "";
    private String mMac = "";
    private String mPackageName = "";
    private String mSimName = "";
    private String mNetWorkType = "";
    private String mDisplay = "";

    private String appsecret = "";

    private NetRequest() {
    }

    public static NetRequest getInstanse() {
        if (singleton == null) {
            Class<NetRequest> commonRequestClass = NetRequest.class;
            synchronized (NetRequest.class) {
                if (singleton == null) {
                    singleton = new NetRequest();
                    singleton.init(AppContext.getInstance());
                }
            }
        }
        return singleton;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private Context getAplication() throws DreamLinerException {
        if (mContext == null) {
            throw new DreamLinerException(600, "you must call #NetRequest.init");
        } else {
            return mContext.getApplicationContext();
        }
    }

    public String getAppKey() throws DreamLinerException {
        if (this.mAppkey.equals("")) {
            ApplicationInfo appInfo = null;

            try {
                appInfo = AppContext.getInstance().getPackageManager().getApplicationInfo(AppContext.getInstance().getPackageName(),
                        PackageManager.GET_META_DATA);
                this.mAppkey = appInfo.metaData.getString("app_key");
            } catch (Exception exception) {
                throw new DreamLinerException(600, "get appkey error");
            }
        }

        return this.mAppkey;
    }

    public String getLocalMacAddress() throws DreamLinerException {
        if (this.mMac.equals("")) {
            WifiManager wifi = (WifiManager) this.getAplication().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            this.mMac = info.getMacAddress();
        }

        if (TextUtils.isEmpty(this.mMac)) {
            throw new DreamLinerException(600, "get mac address error");
        } else {
            return this.mMac;
        }
    }

    public String getDeviceId() throws DreamLinerException {
        if (this.mDeviceid.equals("")) {
            this.mDeviceid = Settings.Secure.getString(this.getAplication().getContentResolver(), "android_id");
        }

        if (TextUtils.isEmpty(this.mDeviceid)) {
            throw new DreamLinerException(600, "get deviceid error");
        } else {
            return this.mDeviceid;
        }
    }

    public String getPackId() throws DreamLinerException {
        if (this.mAppid.equals("")) {
            ApplicationInfo appInfo = null;

            try {
                appInfo = this.getAplication().getPackageManager().getApplicationInfo(this.getAplication().getPackageName(),
                        PackageManager.GET_META_DATA);
                this.mAppid = appInfo.metaData.getString("pack_id");
            } catch (PackageManager.NameNotFoundException exception) {
                throw new DreamLinerException(600, "get packid error");
            }
        }

        return this.mAppid;
    }

    public String getPackageName() {
        if (this.mPackageName.equals("")) {
            this.mPackageName = mContext.getPackageName();
        }

        return this.mPackageName;
    }

    public String getSimName() {
        if (this.mSimName.equals("")) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            this.mSimName = tm.getSimOperatorName();
        }

        return this.mSimName;
    }

    public String getNetWorkType() {
        if (this.mNetWorkType.equals("")) {
            this.mNetWorkType = NetworkType.getNetWorkType(mContext).getName();
        }

        return this.mNetWorkType;
    }

    public String getDisplay() {
        return this.mDisplay;
    }

    public void setDefaultPagesize(int size) {
        if (this.mPagesize != size) {
            this.mPagesize = size;
        }
    }

    public int getDefaultPagesize() {
        return this.mPagesize;
    }

    public String getSdkVersion() {
        return "v1.0";
    }

    public int getClientOsType() {
        return 2;
    }

    public void setPageSize(Map<String, String> params) {
        if (!params.containsKey("count")) {
            params.put("count", String.valueOf(this.getDefaultPagesize()));
        }
    }

    // TODO: 2015/10/25 get APpsecret.
    public String getAppsecret() {
        return appsecret;
    }

    public void destroy() {
        singleton = null;
    }

    public Map<String, String> assembleCommonParams() throws DreamLinerException {
        HashMap<String, String> map = new HashMap<String, String>();
        //添加服务器可能需要统计/校验的参数
        /*
        map.put("app_key", getInstanse().getAppKey());
        map.put("device_id", this.getDeviceId());
        map.put("pack_id", getInstanse().getPackId());
        map.put("sdk_version", getInstanse().getSdkVersion());
        map.put("client_os_type", String.valueOf(getInstanse().getClientOsType()));
        */
        return map;
    }

    public static Map<String, String> addCommonParams(Map<String, String> specificParams) throws DreamLinerException {
        HashMap<String, String> params = new HashMap<>();
        params.putAll(getInstanse().assembleCommonParams());
        params.putAll(specificParams);
        return params;
    }

    public static <T> boolean checkNetStatus(DataCallBack<T> callback) {
        if (!NetUtils.isNetworkAvailable(AppContext.getInstance())) {
            OkHttpUtils.getInstance().postErro(ErrorCode.NET_DISABLE, "请检查你的网络状态!", callback);
            return false;
        }
        return true;
    }

    public static <T> Map<String, String> addCommonParams(Map<String, String> specificParams, DataCallBack<T> callback) {

        Map<String, String> finalparams;
        try {
            finalparams = addCommonParams(specificParams);
        } catch (DreamLinerException e) {
            callback.onError(e.getErrorCode(), e.getErrorMessage());
            return null;
        }
        return finalparams;
    }

    public static void getWeatherMsg(Map<String, String> specificParams, Object object, final DataCallBack<Weather> callback) {

        //这里进行网络状态判断.無网络直接回调onError.return该请求
        if (!checkNetStatus(callback)) {
            return;
        }
        //这里可以进行是否登录的校验.如果没有Token就回调onEror.并且return该请求(类似登录接口/查询无需登录权限的业务就无需调用该方法)
        Map<String, String> finalparams = addCommonParams(specificParams, callback);
        if (null == finalparams) {
            return;
        }

        try {
            OkHttpUtils.get().url(HttpUrl.BASE_WEATHER_URL).addHeader("apikey", "15f0d14ed33720b6b73ec8a3f7bb4d46")
                    .params(finalparams).tag(object).build().execute(new HttpCallBack() {

                @Override
                public void onError(int errorCode, String errorMes, Call call, Exception e) {
                    callback.onError(errorCode, errorMes);
                }

                @Override
                public void onResponse(Object response) {
                    callback.onSuccess((Weather) response);
                }

                @Override
                public Object parseNetworkResponse(BaseResponse baseResponse) throws Exception {

                    //可以保存报文到本地出问题的时候方便调试
                    Type type = (new TypeToken<Weather>() {
                    }).getType();
                    Weather weather = (Weather) baseResponse.getResponseBodyStringToObject(type);

                    if (null == weather) {
                        throw new DreamLinerException(ErrorCode.EXCHANGE_DATA_ERROR, "解释天气数据失败");
                    }
                    return weather;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OkHttpUtils.getInstance().postErro(ErrorCode.ERROR_PARMS, "请求参数本地异常", callback);
        }
    }

}

