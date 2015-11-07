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

package cn.chenzhongjin.simplifynet.datatrasfer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.chenzhongjin.simplifynet.entity.Weather;
import cn.chenzhongjin.simplifynet.httputil.BaseBuilder;
import cn.chenzhongjin.simplifynet.httputil.BaseResponse;
import cn.chenzhongjin.simplifynet.httputil.DreamLinerException;
import cn.chenzhongjin.simplifynet.httputil.ExecutorDelivery;
import cn.chenzhongjin.simplifynet.httputil.HttpURL;
import cn.chenzhongjin.simplifynet.httputil.IHttpCallBack;
import cn.chenzhongjin.simplifynet.util.NetworkType;

/**
 * @author chenzj
 * @Title: CommonRequest
 * @Description: 访问请求类.拼装url并且访问成功之后返回结果.各种层次的错误都回调返回对应的状态码和错误信息
 * @date 2015/11/7
 * @email admin@chenzhongjin.cn
 */
public class CommonRequest {
    private static Context mContext = null;
    public static final String TAG = "DreamLiner";
    private static CommonRequest singleton;
    private int mPagesize = 20;
    private String mAppkey = "";
    private String mAppid = "";
    private String mDeviceid = "";
    private String mMac = "";
    private String mPackageName = "";
    private String mSimName = "";
    private String mNetWorkType = "";
    private String mDisplay = "";
    public static Handler mHandler = new Handler(Looper.getMainLooper());
    private static ExecutorDelivery delivery;

    static {
        delivery = new ExecutorDelivery(mHandler);
    }

    private String appsecret = "";

    private CommonRequest() {
    }

    public static CommonRequest getInstanse() {
        if (singleton == null) {
            Class var0 = CommonRequest.class;
            synchronized (CommonRequest.class) {
                if (singleton == null) {
                    singleton = new CommonRequest();
                }
            }
        }

        return singleton;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Handler getHandler() {
        return mHandler;
    }

    private Context getAplication() throws DreamLinerException {
        if (mContext == null) {
            throw new DreamLinerException(600, "you must call #CommonRequest.init");
        } else {
            return mContext.getApplicationContext();
        }
    }

    public String getAppKey() throws DreamLinerException {
        if (this.mAppkey.equals("")) {
            ApplicationInfo appInfo = null;

            try {
                appInfo = this.getAplication().getPackageManager().getApplicationInfo(this.getAplication().getPackageName(), 128);
                this.mAppkey = appInfo.metaData.getString("app_key");
            } catch (Exception var3) {
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
                appInfo = this.getAplication().getPackageManager().getApplicationInfo(this.getAplication().getPackageName(), 128);
                this.mAppid = appInfo.metaData.getString("pack_id");
            } catch (PackageManager.NameNotFoundException var3) {
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

    public Map<String, String> assembleCommonParams() throws DreamLinerException {
        HashMap map = new HashMap();
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

    public void destroy() {
        singleton = null;
    }

    public static Map<String, String> CommonParams(Map<String, String> specificParams) throws DreamLinerException {
        HashMap params = new HashMap();
        params.putAll(getInstanse().assembleCommonParams());
        params.putAll(specificParams);
        return params;
    }

    public static void getWeatherMsg(Map<String, String> specificParams, final IDataCallBack<Weather> callback) {
        HashMap params = new HashMap();
        params.putAll(specificParams);
        Request request = null;

        try {
            //didn't need sign
            request = BaseBuilder.urlGet(HttpURL.BASE_WEATHER_URL, CommonParams(params)).build();
        } catch (DreamLinerException var6) {
            callback.onError(var6.getErrorCode(), var6.getErrorMessage());
            return;
        }

        OtpBaseCall.doAsync(request, new IHttpCallBack() {
            public void onResponse(Response response) {
                BaseResponse basicResponse = new BaseResponse(response);
                Type listType = (new TypeToken<Weather>() {
                }).getType();

                //具体根据后台的定义来回调执行onSuccess/onFailure
                try {
                    Weather weather = (Weather) basicResponse.getResponseBodyStringToObject(listType);
                    CommonRequest.delivery.postSuccess(callback, weather);
                } catch (Exception var5) {
                    CommonRequest.delivery.postError(1, "获取天气失败,请检查你输入的城市是否正确", callback);
                }
            }

            public void onFailure(int errorCode, String errorMessage) {
                callback.onError(errorCode, errorMessage);
            }
        });
    }

    public DataErrorCategory parseResponseHandler(BaseResponse basicResponse) {
        Gson gson = new Gson();

        try {
            DataErrorCategory e = (DataErrorCategory) gson.fromJson(basicResponse.getResponseBodyToString(), DataErrorCategory.class);
            return e;
        } catch (Exception var4) {
            return null;
        }
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
}

