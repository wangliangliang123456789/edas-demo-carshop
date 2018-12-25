package com.alibaba.edas.carshop.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 时间操作工具类
 *
 * @author 亮亮
 */
public class DateUtil {
    /**
     * 格式  yyyyMM
     * 获取两个月份之间的所有月份
     *
     * @param minDate 开始月份
     * @param maxDate 结束月份
     * @return
     * @throws ParseException
     */
    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        //格式化为年月
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }

    /**
     * 获取 多少天  以前或 以后的 日期
     * @param data  日期(yyyy-MM-dd)
     * @param days  天数，正数表示获得传入日期以前多少天的日期，负数表示
     *              获取传入日期多少天以后的日期
     * @return
     */
    public static LocalDate getNextDay(String data,Integer days){
        LocalDate localDate1 = LocalDate.parse(data).minusDays(days);
      return localDate1;
    }

}
