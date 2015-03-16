package cn.gooday.hrcn.export.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * [com.gooday.hrcn.common]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/25]
 * @Update: [说明本次修改内容] BY[Jon][2015/2/2511:30]
 * @Version: [v1.0]
 */
public class CGLibProxy implements MethodInterceptor{
    private static CGLibProxy instance = new CGLibProxy();
    private CGLibProxy() {}
    public static CGLibProxy getInstance() {
        return instance;
    }
    public <T> T getProxy(Class<T> cls) {
        return (T) Enhancer.create(cls, this);
    }
    @Override
    public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        before();
        Object result = methodProxy.invokeSuper(obj, objects);
        after();
        return result;
    }
    private void before(){}
    private void after(){}
}
