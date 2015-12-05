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

package cn.chenzhongjin.sample.lib.datatrasfer;

/**
 * @author chenzj
 * @Title: IDataCallBack
 * @Description: 数据返回的回调
 * @date 2015/11/7
 * @email admin@chenzhongjin.cn
 */
public interface IDataCallBack<T extends DreamLinerResponse> {
    void onSuccess(T bean);

    void onError(int errorCode, String errorMes);
}
