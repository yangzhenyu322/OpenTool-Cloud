package com.opentool.dashboard.common.domain;

import java.io.Serializable;
import com.opentool.dashboard.common.constant.Constants;
import lombok.Data;

/**
 *   通用响应类
 * / @Author: ZenSheep
 * / @Date: 2023/7/9 20:35
 */
@Data
public class R<T> implements Serializable {
    // 序列化版本标识符，用于在反序列化时验证序列化对象的版本一致性；如不显示声明它将根据类的结构自动创建，若类结构发生改变可能导致反序列化失败
    private static final long serialVersionUID = 1L;

    /** 成功 */
    private static final int SUCCESS = Constants.SUCCESS;

    /** 失败 */
    private static final int FAIL = Constants.FAIL;

    private int code;
    private String msg;
    private T data;

    // 第一个<T>用来声明泛型类型参数 T 的。它的作用是告诉编译器该方法是一个泛型方法，并且该方法的返回类型和参数类型中可以使用这个泛型类型参数
    // 去掉第一个 <T>，即不声明泛型类型参数，编译器将无法识别 R<T> 中的 T，并会报错
    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, null);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> R<T> fail() {
        return restResult(null, FAIL, null);
    }

    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, null);
    }

    public static <T> R<T> fail(T data, String msg) {
        return restResult(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> R<T>restResult(T data, int code, String msg){
        R<T> apiResult = new R<>();
        apiResult.setData(data);
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public static <T> Boolean isSuccess(R<T> ret) {
        return R.SUCCESS == ret.getCode();
    }

    public static <T> Boolean isFail(R<T> ret) {
        return !isSuccess(ret);
    }
}
