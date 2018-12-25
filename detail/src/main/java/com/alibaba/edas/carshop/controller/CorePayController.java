package com.alibaba.edas.carshop.controller;

import com.alibaba.edas.carshop.util.DateUtil;
import com.alibaba.edas.carshop.util.ResultUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心计酬
 *
 * @author 亮亮
 */
@Controller
@ResponseBody
@RequestMapping("/adapter/corePay")
@SuppressWarnings("all")
public class CorePayController {
    @Value("${PAYHOSTPORT}")
    private String PAYHOSTPORT;
    @Value("${PAGESIZE}")
    private Integer pageSiza;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 2号场景和4号场景
     *
     * @param distNo 卡号
     * @param month  月份  201211
     * @return
     */
    @RequestMapping(value = "/getMonthIntegral", method = RequestMethod.GET)
    public JSONObject getMonthIntegral(String distNo, String month) {
        Map<String, Object> map = new HashMap<>();
        String url = PAYHOSTPORT+"/api/queryFirstactive/{distNo}/{month}";
        map.put("distNo", distNo);
        map.put("month", month);
        Map<String, Object> forObject = null;
        try {
            forObject = restTemplate.getForObject(url, Map.class, map);
            if (!"0".equals(forObject.get("code").toString())) {
                return ResultUtil.error((String) forObject.get("code").toString(), "错误信息");
            }
            Map<String, Object> data = (Map<String, Object>) forObject.get("data");
            List<Map<String, Object>> list = (List) data.get("list");
            for (int i = 0; i < list.size(); i++) {
                list.get(i).put("month", month);
            }
            return ResultUtil.resultOK(list, "data");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "错误");
        }
    }

    /**
     * 3 号场景 个人累计分
     *
     * @param distNo     卡号
     * @param bonusMonth 月份
     * @return
     */
    @RequestMapping(value = "/getIndividualPoint ", method = RequestMethod.GET)
    public JSONObject getIndividualPoint(String distNo, String bonusMonth) {
        String url =PAYHOSTPORT+"/api/xyz/account2/{distNo}/{bonusMonth}";
        Map<String, String> map = new HashMap<>(16);
        map.put("distNo", distNo);
        map.put("bonusMonth", bonusMonth);
        Map resultMap = null;
        try {
            resultMap = restTemplate.getForObject(url, Map.class, map);
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
            return ResultUtil.error("0001", "访问异常");
        }
        resultMap = restTemplate.getForObject(url, Map.class, map);
        if (!"0".equals(resultMap.get("code").toString())) {
            return ResultUtil.error((String) resultMap.get("code").toString(), "错误信息");
        }
        Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
        return ResultUtil.resultOK(data, "data");
    }

    /**
     * 5号场景 级别（多月）
     *
     * @param distNo
     * @param bonusMonth
     * @return
     */
    @RequestMapping(value = "/getLevels", method = RequestMethod.GET)
    public JSONObject getLevels(String distNo, String startDate, String endDate) {
        //多月级别
        List resultList = new ArrayList();
        Map<String, Object> map = new HashMap<>();
        String url =  PAYHOSTPORT+"/api/queryFirstactive/{distNo}/{month}";
        map.put("distNo", distNo);
        try {
            List<String> dateList = DateUtil.getMonthBetween(startDate, endDate);
            for (int i = 0; i < dateList.size(); i++) {
                map.put("month", dateList.get(i));
                Map<String, Object> forObject = null;
                try {
                    forObject = restTemplate.getForObject(url, Map.class, map);
                    if (!"0".equals(forObject.get("code").toString())) {
                        return ResultUtil.error((String) forObject.get("code").toString(), "错误信息");
                    }
                    Map<String, Object> data = (Map<String, Object>) forObject.get("data");
                    List<Map<String, Object>> list = (List) data.get("list");
                    for (int j = 0; j < list.size(); j++) {
                        list.get(j).put("month", dateList.get(i));
                    }
                    resultList.addAll(list);
                } catch (Exception e) {
                    e.printStackTrace();
                    //当前月份发生异常，进入下一月份
                    continue;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ResultUtil.resultOK(resultList, "data");
    }


    /**
     * 6号 ，7 号接口 持续进步奖的分值
     *
     * @param disNo     会员卡号
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    @RequestMapping(value = "/getCountPassNum", method = RequestMethod.GET)
    public JSONObject getCountPassNum(String distNo, String monthStart, @RequestParam(name = "monthEnd", required = false) String monthEnd) {
        Object distName = null;
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = null;
        Integer totalScore = 0;
        String url = PAYHOSTPORT+"/api/xyz/account8/{distNo}";
        map.put("distNo", distNo);
        map.put("monthStart", monthStart);
        if (monthEnd == null) {
            map.put("monthEnd", monthStart);
        } else {
            map.put("monthEnd", monthEnd);
        }
        Map<String, Object> forObject = null;
        try {
            forObject = restTemplate.getForObject(url, Map.class, map);
            if (!"0".equals(forObject.get("code").toString())) {
                return ResultUtil.error((String) forObject.get("code").toString(), "错误信息");
            }
            Map<String, Object> data = (Map<String, Object>) forObject.get("data");
            list = (List) data.get("list");
            for (int i = 0; i < list.size(); i++) {
                Integer passNum_ = (Integer) list.get(i).get("countPassNum");
                totalScore += passNum_;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "错误");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalScore", totalScore);
        resultMap.put("list", list);
        resultMap.put("monthStart", monthStart);
        resultMap.put("monthEnd", monthEnd);
        resultMap.put("distName", distName);
        return ResultUtil.resultOK(resultMap, "data");
    }
}
