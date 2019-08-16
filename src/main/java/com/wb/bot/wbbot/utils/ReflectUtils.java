package com.wb.bot.wbbot.utils;

import com.wb.bot.wbbot.controller.GroupController;
import com.wb.bot.wbbot.controller.MessageController;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectUtils {

    /**
     * 通过反射类获取方法参数名
     *
     * @param destinationObject 目标对象
     * @param methodName        方法名
     */
    public static Object invoke(Object destinationObject, String methodName, Map<String, List<String>> parameters) {
        Class<?> destinationClass = destinationObject.getClass();
        Method[] methods = destinationClass.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                Parameter[] params = method.getParameters();
                List<Object> args = getParameterValue(params, parameters);
                try {
                    if (args.size() > 0) {
                        return method.invoke(destinationObject, args.toArray());
                    } else {
                        return method.invoke(destinationObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static List<Object> getParameterValue(Parameter[] params, Map<String, List<String>> parameters) {
        List<Object> args = new ArrayList<>();
        for (Parameter parameter : params) {
            Type type = parameter.getParameterizedType();
            List<String> list = parameters.get(parameter.getName());
            if (list != null) {
                try {
                    args.add(ConvertUtils.convert(list.get(0), Class.forName(type.getTypeName())));
                } catch (ClassNotFoundException e) {
                    args.add(null);
                }
            } else {
                args.add(null);
            }
        }
        return args;
    }
}
