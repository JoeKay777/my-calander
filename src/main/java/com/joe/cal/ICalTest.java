package com.joe.cal;

import lombok.Data;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.Value;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ical4j测试
 *
 * @author qkh
 * @version 1.0
 * @date 2021/5/17 16:30
 */
public class ICalTest {

    @Data
    public static class ConfCycle {
        private String cycleValue;      // 周期信息，多个值使用","分隔
        private Integer cycleType;  // 周期的类型:1-天，2-周，3-月
        private Integer infiniteFlag = 1; // 是否有截止日期或截止次数；0-有截止日期

        private int repeatCount; // 重复次数

        private Date startDate; // 开始日期
        private Date endDate; // 截止日期
    }

    public static void main(String[] args) throws ParseException {
        ConfCycle confCycle = new ConfCycle();
        confCycle.setCycleType(3);
        confCycle.setCycleValue("1,4,7,17");
//        confCycle.setRepeatCount(3);
        confCycle.setStartDate(new Date());

        confCycle.setEndDate(new Date(new net.fortuna.ical4j.model.Date("20210817").getTime()));
        Recur recur = getRecurByCycle(confCycle);
        System.out.println(recur.toString());
        DateList dates = recur.getDates(new net.fortuna.ical4j.model.Date(new Date()), new net.fortuna.ical4j.model.Date(confCycle.getStartDate()),
                new net.fortuna.ical4j.model.Date(confCycle.getEndDate()), Value.DATE);
        System.out.println(dates);
        System.out.println(dates.get(0).getTime());
        // 下次执行时间的计算逻辑为 >=seed && >startDate的日期，所以实际使用中，可以将这两个值都设置为本次执行的时间，来求下次执行时间
        net.fortuna.ical4j.model.Date nextDate = recur.getNextDate(new net.fortuna.ical4j.model.Date("20210807"), new net.fortuna.ical4j.model.Date("20210817"));
        net.fortuna.ical4j.model.Date nextDate2 = recur.getNextDate(nextDate, nextDate);
        System.out.println("下次执行时间：" + nextDate);
        System.out.println("下下次执行时间：" + nextDate2);
        // 判断当前时间是否满足条件
        DateList dates1 = recur.getDates(new net.fortuna.ical4j.model.Date(new Date()), new net.fortuna.ical4j.model.Date(confCycle.getStartDate()),
                new net.fortuna.ical4j.model.Date(new Date()), Value.DATE, 1);
        System.out.println("当前是否满足：" + dates1);
    }


    public static Recur getRecurByCycle(ConfCycle cycle) {
        if (cycle != null) {
            Recur.Builder builder = new Recur.Builder();
//周期信息
            String cycleInfo = cycle.getCycleValue();
            String infos[] = cycleInfo.split(";");

            int index = 0;
            List<Integer> indexs = new ArrayList<Integer>();
            int seq = 0;
            if (infos.length == 1) {
                String indexstr[] = infos[0].split(",");
                for (int i = 0; i < indexstr.length; i++) {
                    indexs.add(Integer.parseInt(indexstr[i]));
                }
                index = indexs.get(0);

            } else if (infos.length == 2) {
                seq = Integer.parseInt(infos[0]);
                index = Integer.parseInt(infos[1]);
            }

//周期的类型
            int cycleType = cycle.getCycleType().intValue();
            switch (cycleType) {
//按间隔多少天循环
                case 1:
                    builder.frequency(Recur.Frequency.DAILY);
                    builder.interval(index);
                    break;
//按每一周的周几 可以是每一周的一天或者多天
                case 2:
                    builder.frequency(Recur.Frequency.WEEKLY);
                    WeekDayList dayList = new WeekDayList();
                    for (Integer weekday : indexs) {
                        switch (weekday) {
                            case 1:
                                dayList.add(WeekDay.SU);
                                break;
                            case 2:
                                dayList.add(WeekDay.MO);
                                break;
                            case 3:
                                dayList.add(WeekDay.TU);
                                break;
                            case 4:
                                dayList.add(WeekDay.WE);
                                break;
                            case 5:
                                dayList.add(WeekDay.TH);
                                break;
                            case 6:
                                dayList.add(WeekDay.FR);
                                break;
                            case 7:
                                dayList.add(WeekDay.SA);
                                break;
                            default:
                                throw new RuntimeException("un support week index!!!");
                        }
                    }
                    builder.dayList(dayList);
                    break;
//按月循环 可以按每个月的第几天
                case 3:
                    builder.frequency(Recur.Frequency.MONTHLY);
//按每月的第几周的周几
                    if (seq != 0) {
//                        switch (index) {
//                            case 1:
//                                recur.getDayList().add(WeekDay.SU);
//                                break;
//                            case 2:
//                                recur.getDayList().add(WeekDay.MO);
//                                break;
//                            case 3:
//                                recur.getDayList().add(WeekDay.TU);
//                                break;
//                            case 4:
//                                recur.getDayList().add(WeekDay.WE);
//                                break;
//                            case 5:
//                                recur.getDayList().add(WeekDay.TH);
//                                break;
//                            case 6:
//                                recur.getDayList().add(WeekDay.FR);
//                                break;
//                            case 7:
//                                recur.getDayList().add(WeekDay.SA);
//                                break;
//                            default:
//                                throw new RuntimeException("un support week index!!!");
//                        }
//                        recur.getSetPosList().add(seq);

//按每月的第几天
                    } else {
                        NumberList monthDayList = new NumberList();
                        monthDayList.addAll(indexs);
                        builder.monthDayList(monthDayList);
                    }
                    break;
                default:
                    throw new RuntimeException("un know cycle type! ");

            }
            boolean unlimited = cycle.getInfiniteFlag().intValue() == 0 ? false : true;    // 是否有截止日期或截止次数
//设置结束周期
            if (!unlimited) {
                int count = cycle.getRepeatCount();  // 设置重复次数
                if (count > 0) {
                    builder.count(count);
                } else {      // 设置截止日期
                    builder.until(new net.fortuna.ical4j.model.Date(cycle.getEndDate()));
                }
            }
            return builder.build();
        }
        return null;
    }
}
