package com.alibaba.edas.carshop.controller;

import com.alibaba.edas.carshop.util.ResultUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方接口
 * @author 亮亮
 */
@Controller
@ResponseBody
@RequestMapping("/adapter/thirdParty")
@SuppressWarnings("all")
public class ThirdPartyController {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 23号场景，产品生产日期查询
     *
     * @param repositoryNo 仓库号
     * @param productionNo 产品编号
     * @return
     */
    @RequestMapping(value = "/getProductionDate", method = RequestMethod.GET)
    public JSONObject getProductionDate(String repositoryNo, String productionNo) {
        String url = "";
        List list = null;
        Map<String, Object> map = new HashMap<>(16);
        map.put("agentno", repositoryNo);
        map.put("inv_no", productionNo);
        try {
            Map<String, Object> restObject = restTemplate.getForObject(url, Map.class, map);
            if (!"0".equals((String) restObject.get("resultCode"))) {
                return ResultUtil.error((String) restObject.get("resultCode"), (String) restObject.get("resultMsg"));
            }
            Map<String, Object> o = (HashMap<String, Object>) restObject.get("data");
            list = (ArrayList) o.get("list");
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return ResultUtil.resultOK(list, "data");
    }

    /**
     * 31号场景，促销活动赠品
     * @param repositoryNo
     * @param productionNo
     * @return
     */
    public JSONObject getLargesses(String repositoryNo, String productionNo) {
        String url = "";
        List list = null;
        Map<String, Object> map = new HashMap<>(16);
        map.put("agentno", repositoryNo);
        map.put("cmonth", productionNo);
        try {
            Map<String, Object> restObject = restTemplate.getForObject(url, Map.class, map);
            if (!"0".equals((String) restObject.get("resultCode"))) {
                return ResultUtil.error((String) restObject.get("resultCode"), (String) restObject.get("resultMsg"));
            }
            Map<String, Object> o = (HashMap<String, Object>) restObject.get("data");
            list = (ArrayList) o.get("list");
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return ResultUtil.resultOK(list, "data");
    }




}
