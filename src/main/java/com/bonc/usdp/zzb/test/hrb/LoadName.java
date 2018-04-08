package com.bonc.usdp.zzb.test.hrb;

import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.bonc.usdp.odk.neo4j.connection.Neo4jConnectionPool;
import com.bonc.usdp.odk.neo4j.update.impl.JdbcUpdateImpl;
import com.bonc.usdp.zzb.test.DbUtil.ESManager;
import com.google.common.collect.Maps;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LoadName {
    public static void main(String[] args) {
        try {
            LogManager.init(new Log4jTraceParameters("test", "/home/usdp/tmp"));
            ESManager.es_ip = "10.112.89.177";
            ESManager.cluster_name = "mgc-es";
            QueryRunner ldinfoRunner = ESManager.getInstance().getQueryRunner("harbin_table_ldinfor");
            Connection ldinfoconn = ESManager.getInstance().getConnection("harbin_table_ldinfor");
            QueryRunner gnlkRunner = ESManager.getInstance().getQueryRunner("harbin_gnlk");
            Neo4jConnectionPool.initNeo4jConnectionPool("jdbc:neo4j:bolt://10.112.89.177:7687", "neo4j", "123456", 2, 2, 2);
            JdbcUpdateImpl jdbcUpdate = new JdbcUpdateImpl();

            String sql1 = "select _id,QIYEID,QIYEMC from TABLE_LDINFOR";
            List<Map<String, Object>> res = ldinfoRunner.query(sql1, new MapListHandler());
            System.out.println(res.size());
            for (Map<String, Object> record : res) {
                String id = Optional.ofNullable(record.get("_id")).orElse("").toString();
                String bm = Optional.ofNullable(record.get("QIYEID")).orElse("").toString();
                String name = Optional.ofNullable(record.get("QIYEMC")).orElse("").toString();
                if (StringUtil.isEmpty(bm) || StringUtil.isNotEmpty(name)) {
                    continue;
                }
                String getNameSql = "select QIYEMC from GNLK where QIYEBIANMA=?";
                String upname = gnlkRunner.query(getNameSql, new ScalarHandler<String>(), bm);
                if (StringUtil.isNotEmpty(upname)) {
                    String upSql = "UPDATE harbin_table_ldinfor.TABLE_LDINFOR SET QIYEMC='" + upname + "',QIYEID='',QIYEBIANMA='" + bm + "' WHERE _id='" + id + "'";
                    System.out.println(upSql);
                    Statement statement = ldinfoconn.createStatement();
                    statement.execute(upSql);
                    statement.close();
                    HashMap<String, String> map = Maps.newHashMap();
                    map.put("master_object_id", id);
                    map.put("QIYEMC", upname);
                    jdbcUpdate.updateNodeByKeyProperty("TABLE_LDINFOR", "master_object_id", id, map);
                }
            }
            ESManager.closeDataSource("harbin_table_ldinfor");
            ESManager.closeDataSource("harbin_gnlk");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
