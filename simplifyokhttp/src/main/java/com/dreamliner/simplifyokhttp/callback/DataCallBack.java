/*
 * Copyright (c) 2016  DreamLiner Studio
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
 */

package com.dreamliner.simplifyokhttp.callback;

/**
 * @author chenzj
 * @Title: DataCallBack
 * @Description: 类的描述 - 最终解释到的数据回调
 * @date 2016/3/19 18:45
 * @email admin@chenzhongjin.cn
 */
public interface DataCallBack<T> {

    void onSuccess(T bean);

    void onError(int errorCode, String errorMes);
}
