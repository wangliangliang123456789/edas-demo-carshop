package com.alibaba.edas.carshop.controller;

import com.alibaba.edas.carshop.util.ResultUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 核心发放
 *
 * @author 亮亮
 */
@Controller
@ResponseBody
@RequestMapping("/coreIssue")
@SuppressWarnings("all")
public class CoreIssueController {
    @Value("${PAGESIZE}")
    private Integer pageSize;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${CoreIssueHOSTPORT}")
    private String CoreIssueHOSTPORT;

    /**
     * 12 号场景 收入的金额明细
     *
     * @param month  yyyy-MM月份
     * @param distNo   卡号
     * @return
     */
    @RequestMapping(value = "/getIncomeDetails", method = RequestMethod.GET)
    public JSONObject getIncomeDetails(String month, String distNo) {
        String url = CoreIssueHOSTPORT + "/api/incomeDetails";
        Map<String, Object> map = new HashMap<>(16);
        map.put("month", month);
        map.put("distNo", distNo);
        try {
            Map<String, Object> resultMap = restTemplate.postForObject(url, map, Map.class);
            if (!"0".equals((String) resultMap.get("resultCode"))) {
                return ResultUtil.error((String) resultMap.get("resultCode"), "错误信息");
            }
            Map<String, Object> resultData = (Map<String, Object>) resultMap.get("data");
            return ResultUtil.resultOK(resultData,"data");
        } catch (RestClientException e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "错误信息");
        }
    }

    /**
     * 13号场景 收入发放情况
     *
     * @param receivablesBankAccount 卡号
     * @param month yyyy-MM 月份
     * @return
     */
    @RequestMapping(value = "/getIncomeDistributionInformation", method = RequestMethod.GET)
    public JSONObject incomeDistributionInformation(String month, String distNo) {
        String url = CoreIssueHOSTPORT + "/api/incomeDistributionInformation";
        Map<String, Object> map = new HashMap<>(16);
        map.put("month", month);
        map.put("distNo", distNo);
        try {
            Map<String, Object> resultMap = restTemplate.postForObject(url, map, Map.class);
            if (!"0".equals((String) resultMap.get("resultCode"))) {
                return ResultUtil.error((String) resultMap.get("resultCode"), "错误信息");
            }
            Map<String, Object> resultData = (Map<String, Object>) resultMap.get("data");
            return ResultUtil.resultOK(resultData,"data");
        } catch (RestClientException e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "错误信息");
        }
    }

    /**
     * 14 号场景 劳务收入的汇退情况
     *
     * @param month yyyy-MM 月份
     * @param receivablesBankAccount 卡号
     * @return
     */
    @RequestMapping(value = "/getRefundLaborServicesInformation", method = RequestMethod.GET)
    public JSONObject getRefundLaborServicesInformation(String month, String receivablesBankAccount) {
        String url = CoreIssueHOSTPORT + "/api/refundLaborServicesInformation";
        Map<String, Object> map = new HashMap<>(16);
        map.put("month", month);
        map.put("receivablesBankAccount", receivablesBankAccount);
        map.put("nowPage", 1);
        map.put("pageShow", pageSize);
        try {
            Map<String, Object> resultMap = restTemplate.postForObject(url, map, Map.class);
            if (!"0".equals((String) resultMap.get("resultCode"))) {
                return ResultUtil.error((String) resultMap.get("resultCode"), "错误信息");
            }
            Map<String, Object> resultData = (Map<String, Object>) resultMap.get("data");
            return ResultUtil.resultOK(resultData,"data");
        } catch (RestClientException e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "错误信息");
        }
    }

    /**
     * 16号场景 发票余款额度查询
     *
     * @param month          yyyy-MM          月份
     * @param receivablesBankAccount 卡号
     * @return
     */
    @RequestMapping(value = "/getCheckBalanceInvoice", method = RequestMethod.GET)
    public JSONObject getCheckBalanceInvoice(String month, String receivablesBankAccount) {
        String url = CoreIssueHOSTPORT + "/api/checkBalanceInvoice";
        Map<String, Object> map = new HashMap<>(16);
        map.put("month", month);
        map.put("receivablesBankAccount", receivablesBankAccount);
        map.put("nowPage", 1);
        map.put("pageShow", pageSize);
        try {
            Map<String, Object> resultMap = restTemplate.postForObject(url, map, Map.class);
            if (!"0".equals((String) resultMap.get("resultCode"))) {
                return ResultUtil.error((String) resultMap.get("resultCode"), "错误信息");
            }
            Map<String, Object> resultData = (Map<String, Object>) resultMap.get("data");
            return ResultUtil.resultOK(resultData,"data");
        } catch (RestClientException e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "错误信息");
        }
    }
}
