package com.dreamliner.simplifyokhttp.callback;

import okhttp3.Call;
import okhttp3.Request;

/**
 * @author chenzj
 * @Title: HttpCallBack
 * @Description: 类的描述 - okhttp的callback回调之后的回调处理抽象类
 * @date 2016/3/19 18:45
 * @email admin@chenzhongjin.cn
 */
public abstract class HttpCallBack<T> {

    public abstract void onError(int errorCode, String errorMes, Call call, Exception e);

    public abstract void onResponse(T response);

    /**
     * Thread Pool Thread
     *
     * @param baseResponse
     */
    public abstract T parseNetworkResponse(BaseResponse baseResponse) throws Exception;

    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter() {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress) {
    }

    public static HttpCallBack HttpCallBackDefault = new HttpCallBack() {

        @Override
        public Object parseNetworkResponse(BaseResponse baseResponse) throws Exception {
            return null;
        }

        @Override
        public void onError(int errorCode, String errorMes, Call call, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }
    };

}