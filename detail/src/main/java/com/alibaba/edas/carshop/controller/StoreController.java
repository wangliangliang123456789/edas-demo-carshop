package com.alibaba.edas.carshop.controller;


import com.alibaba.edas.carshop.util.DateUtil;
import com.alibaba.edas.carshop.util.ResultUtil;
import com.alibaba.fastjson.JSONObject;
import com.dtyunxi.rest.RestResponse;
import com.github.pagehelper.PageInfo;
import com.perfect.center.inventory.api.dto.request.MortgageAccountQueryDto;
import com.perfect.center.inventory.api.dto.request.MortgageOrderQueryDto;
import com.perfect.center.inventory.api.dto.request.StorageSnapshotQueryDto;
import com.perfect.center.inventory.api.dto.response.MortgageAccountRespDto;
import com.perfect.center.inventory.api.dto.response.MortgageItemRespDto;
import com.perfect.center.inventory.api.dto.response.MortgageOrderRespDto;
import com.perfect.center.inventory.api.dto.response.StorageSnapshotRespDto;
import com.perfect.center.inventory.api.query.IMortgageAccountQueryApi;
import com.perfect.center.inventory.api.query.IMortgageOrderQueryApi;
import com.perfect.center.inventory.api.query.IStorageSnapshotQueryApi;
import com.perfect.center.item.api.dto.response.PerfectItemRespDto;
import com.perfect.center.item.api.dto.response.PerfectListItemRespDto;
import com.perfect.center.item.api.query.IPerfectItemQueryApi;
import com.perfect.center.shop.api.query.IPerfectServiceCenterQueryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Controller
@RequestMapping("/adapter/store")
@ResponseBody
@SuppressWarnings("all")
public class StoreController {
    @Value("${PAGESIZE}")
    private Integer pageSize;
    /**
     * 信誉额查询/押货保证金最大限额
     */
    @Autowired
    private IMortgageAccountQueryApi iMortgageAccountQueryApi;

    /**
     * 2.	押货单明细查询
     */
    @Autowired
    private IMortgageOrderQueryApi iMortgageOrderQueryApi;
    /**
     * 3.	核对产品库存
     */
    @Autowired
    private IStorageSnapshotQueryApi iStorageSnapshotQueryApi;


    /**
     * 查询服务中心基本信息
     */
    @Autowired
    private IPerfectServiceCenterQueryApi iPerfectServiceCenterQueryApi;

    /**
     * 根据产品id查询产品信息
     */
    @Autowired
    private IPerfectItemQueryApi iperfectItemQueryApi;


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true)); // true:允许输入空值，false:不能为空值
    }

    /**
     * 压货单明细查询
     *
     * @param shopNo  网点编号
     * @param creDate 开单日期
     * @return
     */
    @RequestMapping(value = "/getmortgageInfo", method = RequestMethod.GET)
    public JSONObject selectMortgageInfo(String shopNo, String creDate) {
        List<MortgageOrderRespDto> respDtoList = null;
        MortgageOrderQueryDto dto = new MortgageOrderQueryDto();
        //设置开单日期
        dto.setStartTime(creDate);
        dto.setEndTime(DateUtil.getNextDay(creDate, -1).toString());
        //网点编号
        dto.setServiceCenterCode(shopNo);
        try {
            RestResponse<PageInfo<MortgageOrderRespDto>> page = iMortgageOrderQueryApi.queryDetailByPage(dto, 1, pageSize);
            if (!"0".equals(page.getResultCode())) {
                return ResultUtil.error("0001", page.getResultMsg());
            }
            respDtoList = page.getData().getList();
            for (MortgageOrderRespDto order : respDtoList) {
                List<MortgageItemRespDto> items = order.getMortgageItems();
                //为每个产品添加必要属性
                for (int i = 0; i < items.size(); i++) {
                    RestResponse<PerfectListItemRespDto> itemRespDto = iperfectItemQueryApi.queryPerfectItemBySkuId(items.get(i).getSkuId());
                    if (!"0".equals(itemRespDto.getResultCode())) {
                        System.err.println("查询商品：" + items.get(i).getSkuId() + "异常");
                        continue;
                    }
                    PerfectItemRespDto respDto = itemRespDto.getData().getPerfectItemRespDto();
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", respDto.getName());
                    map.put("code", respDto.getCode());
                    map.put("packingSpecifications", respDto.getPackingSpecifications());
                    items.get(i).setExtFields(map);
                }
            }
            return ResultUtil.resultOK(respDtoList, "data");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResultUtil.error("0001", e.getStackTrace().toString());
        }
    }


    /**
     * 核对产品库存
     * 有问题  待修改
     * @param shopNo    网点编号
     * @param meansDate 资料月份 2018-02
     * @param itemCode  产品编码
     * @return
     */
    @RequestMapping(value = "/checkProductInventory", method = RequestMethod.GET)
    public JSONObject checkProductInventory(@RequestParam(value = "companyNo", required = false) String companyNo, String shopNo, String meansDate, String itemCode) {
        List<StorageSnapshotRespDto> list = null;
        Long id = null;
        PerfectItemRespDto item = null;
        StorageSnapshotQueryDto dto = new StorageSnapshotQueryDto();
        //根据产品编码 获取产品信息
        try {
            RestResponse<PerfectItemRespDto> items = iperfectItemQueryApi.queryPerfectItemByCode(itemCode);
            RestResponse<PerfectListItemRespDto> testItem = iperfectItemQueryApi
                    .queryPerfectItemBySkuId(1191355023222776800L);

            if (!"0".equals(items.getResultCode())) {
                return ResultUtil.error("0001", "没有产品信息");
            }
            item = items.getData();
            LocalDate _meansDate = LocalDate.parse(meansDate + "-01");
            //根据网点编号查询服务中心id
            id = iPerfectServiceCenterQueryApi.queryShopIdByCode(shopNo).getData().getId();
            id = 1190080987846026273L;
            if (id == null) {
                return ResultUtil.error("0001", "网点编号错误");
            }
            dto.setServiceCenterId(id);
            List<Long> longList = new ArrayList<>();
//            longList.add(item.getSkuId());
//            longList.add(1191355023222776800L);
            dto.setSkuIds(longList);
            //资料月份
            dto.setStartTime(_meansDate.with(TemporalAdjusters.firstDayOfMonth()).toString());
            dto.setEndTime(DateUtil.getNextDay(_meansDate.with(TemporalAdjusters.lastDayOfMonth()).toString(),-1).toString());
        } catch (Exception e) {
            return ResultUtil.error("0001", e.getMessage());
        }
        //设置产品skuID
        try {
            RestResponse<PageInfo<StorageSnapshotRespDto>> restResponse = iStorageSnapshotQueryApi.queryByPage(dto, 1, 100);
            if (!"0".equals(restResponse.getResultCode())) {
                return ResultUtil.error(restResponse.getResultCode(), restResponse.getResultMsg());
            }
            PageInfo<StorageSnapshotRespDto> data = restResponse.getData();
            list = data.getList();
//        添加商品属性
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", item.getName());
                map.put("code", item.getCode());
                list.get(i).setExtFields(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("查询错误！！");
        }
        return ResultUtil.resultOK(list, "data");
    }


    /**
     * 押货保证金最大限额
     *
     * @param companyNo 分公司编号
     * @param shopNo    网点编号
     * @param month     资料月份
     * @return
     */
    @RequestMapping(value = "/maxquota", method = RequestMethod.GET)
    public JSONObject maxQuota(String companyNo, String shopNo, String month) {
        LocalDate localDate = LocalDate.parse(month + "-01");
        MortgageAccountQueryDto dto = new MortgageAccountQueryDto();
        //开始时间
        dto.setStartTime(localDate.with(TemporalAdjusters.firstDayOfMonth()).toString());
        //结束时间
        dto.setEndTime(DateUtil.getNextDay(localDate.with(TemporalAdjusters.lastDayOfMonth()).toString(), -1).toString());
        //类型(1押货额度,2信誉额,3已汇款总额)
        dto.setType(1);
        //网点编号
        dto.setServiceCenterCode(shopNo);
        RestResponse<MortgageAccountRespDto> mortgageAccountRespDto = iMortgageAccountQueryApi.queryMortgageAccount(dto);
        if (!"0".equals(mortgageAccountRespDto.getResultCode())) {
            return ResultUtil.error(mortgageAccountRespDto.getResultCode(), mortgageAccountRespDto.getResultMsg());
        }
        MortgageAccountRespDto respDto = mortgageAccountRespDto.getData();
        return ResultUtil.resultOK(respDto, "data");
    }


}