package com.bonc.usdp.zzb.test.es;

import com.alibaba.fastjson.JSON;
import com.bonc.usdp.zzb.test.DbUtil.ESManager;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EsTest {

    private static void test1() {
        ESManager.es_ip="172.16.11.214";
        String sql = "SELECT * FROM PERSON where PERSONAL_NAME='李达康' and PERSONAL_ID='140322196909042951'";
        QueryRunner runner = ESManager.getInstance().getQueryRunner("mingyi_person");
        try {
           Object res = runner.query(sql, new MapListHandler());
            System.out.println(res);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test1();
    }
}
