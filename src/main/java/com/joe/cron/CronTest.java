package com.joe.cron;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.value.SpecialChar;

import static com.cronutils.model.field.expression.FieldExpressionFactory.*;

/**
 * Cron-util
 *
 * @author qkh
 * @version 1.0
 * @date 2021/5/18 16:55
 */
public class CronTest {

    public static void main(String[] args) {
        Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                .withYear(always())
                .withDoM(between(SpecialChar.L, 3))
                .withMonth(always())
                .withDoW(questionMark())
                .withHour(always())
                .withMinute(always())
                .withSecond(on(0))
                .instance();
// Obtain the string expression
        String cronAsString = cron.asString(); // 0 * * L-3 * ? *
        System.out.println(cronAsString);
    }
}
