package com.github.imaginary11.sqlx;

import com.alibaba.druid.sql.SQLUtils;
import com.p6spy.engine.spy.appender.MultiLineFormat;

/**
 * @author : Imaginary
 * @version : V1.0
 * @date : 2018/8/10 20:50
 */
public class PrettySqlMultiLineFormat extends MultiLineFormat {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {

        return super.formatMessage(connectionId, now, elapsed, category, "", SQLUtils.formatMySql(sql));
    }
}
