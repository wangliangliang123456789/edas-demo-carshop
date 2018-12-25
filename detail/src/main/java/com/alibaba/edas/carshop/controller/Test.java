package com.alibaba.edas.carshop.controller;

import com.alibaba.edas.carshop.util.DateUtil;
import com.alibaba.edas.carshop.util.RestClient;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Test {
    @org.junit.Test
    public void test02() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        //开始日期
        LocalDate startDate = LocalDate.parse("20170102", formatter);
        //结束日期
        LocalDate endDate = LocalDate.parse("20180306", formatter);
        System.out.println();
//        LocalDate date2 = LocalDate.parse("20121212", formatter);
        System.out.println(startDate);
        System.out.println(endDate);
//        while (startDate.with(TemporalAdjusters.firstDayOfMonth()).equals(endDate.with(TemporalAdjusters.firstDayOfMonth()))){
//
//        }

//        System.out.println(date.with(TemporalAdjusters.firstDayOfNextMonth()));


    }


    @org.junit.Test
    public void test01() {
        RestTemplate restTemplate = RestClient.getClient();
//        String url="http://leoceshi.natapp1.cc/api/xyz/account8/{distNo}";
//        Map<String ,Object> map=new HashMap<>();
//        map.put("distNo","P001");
//        map.put("monthStart","201708");
//        map.put("monthEnd","201708");
////        map.put("pageNum",1);
////        map.put("pageSize",1);
//        Map<String,Object> rest1 = rest.getForObject(url, Map.class, map);
//        System.out.println(JSON.toJSONString(rest1,true));
//        Map<String, Object> map = new HashMap<>();
//        String url = "http://leoceshi.natapp1.cc/api/xyz1/account0/P001/201707?pin=1&pointDistNo=1&personalAmount=1";
////            map.put("distNo", distNo);
////            map.put("bonusMonth", bonusMonth);
////            map.put("pin", 1);
////            map.put("pointDistNo", 1);
////            map.put("personalAmount", 1);
//        Map<String,Object> forObject=null;
//        try{
//            forObject=  restTemplate.getForObject(url, Map.class);
//            System.out.println(forObject.get("code").toString());
//            if (!"0".equals(forObject.get("code").toString())) {
//
//            }
//            Map<String,Object> data = (Map<String,Object>) forObject.get("data");
//            List list = (List) data.get("list");
//            for (int i = 0; i < list.size(); i++) {
//                System.out.println(list.get(i));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }


    @org.junit.Test
    public void test03() throws ParseException {
        List<String> list = DateUtil.getMonthBetween("201202", "201302");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }


    }


}
