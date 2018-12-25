package com.alibaba.edas.carshop.controller;

import com.alibaba.edas.carshop.model.Product;
import com.alibaba.edas.carshop.util.DateUtil;
import com.alibaba.edas.carshop.util.ResultUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtyunxi.rest.RestResponse;
import com.github.pagehelper.PageInfo;
import com.perfect.center.inventory.api.dto.request.MortgageOrderQueryDto;
import com.perfect.center.inventory.api.dto.response.MortgageItemRespDto;
import com.perfect.center.inventory.api.dto.response.MortgageOrderRespDto;
import com.perfect.center.inventory.api.query.IMortgageOrderQueryApi;
import com.perfect.center.item.api.dto.response.PerfectItemRespDto;
import com.perfect.center.item.api.dto.response.PerfectListItemRespDto;
import com.perfect.center.item.api.query.IPerfectItemQueryApi;
import com.perfect.third.integration.api.dto.response.ProConDayFareDataRespDto;
import com.perfect.third.integration.api.dto.response.ProDeliveryProductDto;
import com.perfect.third.integration.api.dto.response.ProDeliveryRecordDto;
import com.perfect.third.integration.api.dto.response.ProOrderDeliveryRespDto;
import com.perfect.third.integration.api.query.IProcedureQueryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("all")
@Controller
@ResponseBody
@RequestMapping("/adapter/repository")
public class RepositoryController {
    /**
     * 压货单明细查询服务
     */
    @Autowired
    private IMortgageOrderQueryApi iMortgageOrderQueryApi;
    /**
     * 仓库服务
     */
    @Autowired
    private IProcedureQueryApi hsfProceduceQueryApi;
    /**
     * 根据产品id查询产品信息
     */
    @Autowired
    private IPerfectItemQueryApi iperfectItemQueryApi;

    @Value("${PAGESIZE}")
    private Integer pageSize;

    /**
     * 24号接口：押货单发货查询
     *
     * @param shopNo  网点编号
     * @param creDate 开单日期  格式2018-12-12
     * @return
     */
    @RequestMapping(value = "/getOrderDelivery", method = RequestMethod.GET)
    public JSONObject getOrderDelivery(String shopNo, String creDate) {
        //发货单发货列表
        List list = new ArrayList();
        List<MortgageOrderRespDto> respDtoList = null;
        MortgageOrderQueryDto dto = new MortgageOrderQueryDto();
        //设置开单日期
        dto.setStartTime(creDate);
        dto.setEndTime(DateUtil.getNextDay(creDate,-1).toString());
        //网点编号
        dto.setServiceCenterCode(shopNo);
        try {
            //订货单号列表
            RestResponse<PageInfo<MortgageOrderRespDto>> page = iMortgageOrderQueryApi.queryDetailByPage(dto, 1, pageSize);
            if (!"0".equals(page.getResultCode())) {
                return ResultUtil.error("0001", page.getResultMsg());
            }
            respDtoList = page.getData().getList();
            if (respDtoList == null || respDtoList.size() == 0) {
                return ResultUtil.resultOK();
            }
//            遍历订单列表
            for (MortgageOrderRespDto order : respDtoList) {
                String mortgageOrderNo = order.getMortgageOrderNo();
                try {
                    RestResponse<ProOrderDeliveryRespDto> delivery = hsfProceduceQueryApi.getOrderDelivery(order.getMortgageOrderNo());
                    if (!"0".equals(delivery.getResultCode())) {
                        //如果当前订单查不到数据  进入下一个订单
                        System.out.println("错误信息：" + delivery.getResultMsg());
                        continue;
                    }
                    //当前压货单数据
                    ProOrderDeliveryRespDto respDto = delivery.getData();
                    //当前押货数据加入集合
                    list.add(respDto);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("当前订单查询失败，进入下一个订单"+order.getMortgageOrderNo());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "信息错误");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orders", list);
        //合同期查询
        try {
            RestResponse<ProConDayFareDataRespDto> daysAndFare = hsfProceduceQueryApi.getContractDaysAndFare(shopNo);
            if (!"0".equals(daysAndFare.getResultCode())) {
                return ResultUtil.error("0001", daysAndFare.getResultMsg());
            }
            resultMap.put("days", daysAndFare.getData().getDays());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("合同期查询失败");
        }
        return ResultUtil.resultOK(resultMap, "data");
    }

    /**
     * 26号场景： 合同期查询
     * 还没有数据，带联调
     *
     * @param shopNo 网点编号
     * @return
     */
    @RequestMapping(value = "/getContractDays", method = RequestMethod.GET)
    public JSONObject getContractDays(String shopNo) {
        try {
            RestResponse<ProConDayFareDataRespDto> daysAndFare = hsfProceduceQueryApi.getContractDaysAndFare(shopNo);
            if (!"0".equals(daysAndFare.getResultCode())) {
                return ResultUtil.error("0001", daysAndFare.getResultMsg());
            }
            return ResultUtil.resultOK(daysAndFare.getData().getDays(), "data");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "查询异常");
        }
    }

    /**
     * 25号场景 ：核对发货明细
     *
     * @param shopNo  网点编号
     * @param creDate 开单日期
     * @return
     */
    @RequestMapping(value = "/checkDelivery", method = RequestMethod.GET)
    public JSONObject checkDelivery(String shopNo, String creDate) {
        //发货单发货列表
        List list = new ArrayList();
        List<MortgageOrderRespDto> respDtoList = null;
        MortgageOrderQueryDto dto = new MortgageOrderQueryDto();
        //设置开单日期
        dto.setStartTime(creDate);
        dto.setEndTime(DateUtil.getNextDay(creDate,-1).toString());
        //网点编号
        dto.setServiceCenterCode(shopNo);
        try {
            //订货单号列表
            RestResponse<PageInfo<MortgageOrderRespDto>> page = iMortgageOrderQueryApi.queryDetailByPage(dto, 1, pageSize);
            if (!"0".equals(page.getResultCode())) {
                return ResultUtil.error("0001", page.getResultMsg());
            }
            respDtoList = page.getData().getList();
            if (respDtoList == null || respDtoList.size() == 0) {
                return ResultUtil.resultOK();
            }
            //遍历订单列表
            for (MortgageOrderRespDto order : respDtoList) {
                //商品总数量Map  商品编码 为Key，商品数量为value
                Map<String, Integer> itemMap = new HashMap<>();
                //获取订单号
                String mortgageOrderNo = order.getMortgageOrderNo();
                //获取订单中的产品列表
                List<MortgageItemRespDto> items = order.getMortgageItems();
                for (int i = 0; i < items.size(); i++) {
                    try {
                        //根据产品skuId查询产品的详细信息
                        RestResponse<PerfectListItemRespDto> responseData = iperfectItemQueryApi.queryPerfectItemBySkuId(items.get(i).getSkuId());
                        if ("0".equals(responseData.getResultCode())) {
                            PerfectListItemRespDto data = responseData.getData();
                            //查询该订单下所有单个产品的总数量
                            PerfectItemRespDto itemSkuRespDto = data.getPerfectItemRespDto();
                            System.out.println(JSON.toJSONString(itemSkuRespDto));
                            itemMap.put(itemSkuRespDto.getCode()
                                    , items.get(i).getNum());
                        } else {
                            //查询不到数据，进入下个商品
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("查询单个商品失败");
                        //进入下一个商品
                        continue;
                    }
                }
                try {
                    //已发货的订单信息
                    RestResponse<ProOrderDeliveryRespDto> delivery = hsfProceduceQueryApi.getOrderDelivery(mortgageOrderNo);
                    //未发货的订单信息
                    if (!"0".equals(delivery.getResultCode())) {
                        //如果当前订单查不到数据  进入下一个订单
                        continue;
                    }
                    //当前压货单数据
                    ProOrderDeliveryRespDto respDto = delivery.getData();
                    //压货单的商品列表
                    List<ProDeliveryRecordDto> pros = respDto.getDeliveryRecord();
                    for (int i=0;i<pros.size();i++) {
                        //原先产品集合
                        List<ProDeliveryProductDto> products = pros.get(i).getDeliveryProduct();
                        //扩展产品集合
                        List<ProDeliveryProductDto> productsVo = new ArrayList<>();
                        for (int j = 0; j < products.size(); j++) {
                            Product productvo = new Product(products.get(j));
                            productvo.setMap(new HashMap<>());
                            System.out.println(JSON.toJSONString(productvo));
                            //发货总数
                            Integer num = itemMap.get(productvo.getProductCode());
                            //已发货数
                            Integer num1 = Math.toIntExact(productvo.getNum());
                            //剩余数量
                            Integer residue = num - num1;
                            //设置剩余数量
                            productvo.getMap().put("residue", residue);
                            //重置总发货数
                            itemMap.put(productvo.getProductCode(), residue);
                            productsVo.add(productvo);
                        }
                        //清空原先产品列表数据
                        products.clear();
                        //添加扩展 产品集合
                        products.addAll(productsVo);
                        pros.get(i).setDeliveryProduct(products);
                    }
                    respDto.setDeliveryRecord(pros);
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("orderNo", mortgageOrderNo);
                    resultMap.put("orderInfo", respDto);
                    list.add(resultMap);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("失败订单号：" + mortgageOrderNo);
                    //查询订单失败，进入下一个订单
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "信息错误");
        }
        return ResultUtil.resultOK(list, "data");
    }

    /**
     * 27号场景 货运跟踪
     * 没有测试数据，待联调
     *
     * @param shopNo  网点编号
     * @param creDate 开单日期
     * @return
     */
    @RequestMapping(value = "/getCargoTracking", method = RequestMethod.GET)
    public JSONObject getCargoTracking(String shopNo, String creDate) {
        //发货单发货列表
        List list = new ArrayList();
        List<MortgageOrderRespDto> respDtoList = null;
        MortgageOrderQueryDto dto = new MortgageOrderQueryDto();
        //设置开单日期
        dto.setStartTime(creDate);
        dto.setEndTime(DateUtil.getNextDay(creDate,-1).toString());
        //网点编号
        dto.setServiceCenterCode(shopNo);
        try {
            //订货单号列表
            RestResponse<PageInfo<MortgageOrderRespDto>> page = iMortgageOrderQueryApi.queryDetailByPage(dto, 1, pageSize);
            if (!"0".equals(page.getResultCode())) {
                return ResultUtil.error("0001", page.getResultMsg());
            }
            respDtoList = page.getData().getList();
            if (respDtoList == null || respDtoList.size() == 0) {
                return ResultUtil.resultOK();
            }
//            遍历订单列表
            for (MortgageOrderRespDto order : respDtoList) {
//            获取压货单号
                String mortgageOrderNo = order.getMortgageOrderNo();
                RestResponse<ProOrderDeliveryRespDto> delivery = hsfProceduceQueryApi.getOrderDelivery(mortgageOrderNo);
                if (!"0".equals(delivery.getResultCode())) {
                    //如果当前订单查不到数据  进入下一个订单
                    System.out.println("错误");
                    continue;
                }
                //当前压货单数据
                ProOrderDeliveryRespDto respDto = delivery.getData();
                //当前押货数据加入集合
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("orderNo", order.getMortgageOrderNo());
                //录单日期 对应押货日期
                resultMap.put("orderTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(order.getMortgageTime()));
                resultMap.put("orderInfo", respDto);
                list.add(resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("0001", "信息错误");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orders", list);
        //合同期查询
        try {
            RestResponse<ProConDayFareDataRespDto> daysAndFare = hsfProceduceQueryApi.getContractDaysAndFare(shopNo);
            if (!"0".equals(daysAndFare.getResultCode())) {
                return ResultUtil.error("0001", daysAndFare.getResultMsg());
            }
            resultMap.put("days", daysAndFare.getData().getDays());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return ResultUtil.resultOK(resultMap, "data");
    }


}
