package com.alibaba.edas.carshop.util;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应数据工具类
 * @author 亮亮
 */
public class ResultUtil implements java.io.Serializable {
    public static JSONObject resultOK(){
        JSONObject obj = new JSONObject();
        obj.put("returnCode", "0000");
        obj.put("returnMessage", "成功");
        obj.put("returnContent", null);
        return obj;
    }
    /**
     * 返回封装类型
     * @param list  数据集合
     * @param name  集合名称
     * @return 成功返回数据
     */
    public static JSONObject resultOK(List list, String name) {

        try{
            JSONObject obj = new JSONObject();
            Map<String, Object> map = new HashMap<>();
            obj.put("returnCode", "0000");
            obj.put("returnMessage", "成功");
            map.put("total", list.size());
            map.put(name, list);
            obj.put("returnContent", map);
            return obj;
        }catch (Exception e){
            return  error("0001","请联系管理员-");
        }

    }

    /**
     *
     * @param object 数据对象
     * @param name  数据对象名加复数
     * @return  成功返回数据
     */
    public static JSONObject resultOK(Object object, String name){
        try{
        JSONObject obj = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        obj.put("returnCode", "0000");
        obj.put("returnMessage", "成功");
        List list=new ArrayList();
        list.add(object);
        map.put("total", list.size());
        map.put(name, list);
        obj.put("returnContent", map);
        return obj;
        }catch (Exception e){
            return  error("0001","请联系管理员-");
        }
    }


    /**
     *
     * @param returnCode  异常代码
     * @param returnMessage  异常信息
     * @return
     */
    public static JSONObject error(String returnCode, String returnMessage) {
        JSONObject obj = new JSONObject();
        obj.put("returnCode", returnCode);
        obj.put("returnMessage", returnMessage);
        return obj;
    }

}
