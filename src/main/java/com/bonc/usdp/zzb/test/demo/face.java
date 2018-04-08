package com.bonc.usdp.zzb.test.demo;

import com.alibaba.fastjson.JSONObject;
import com.bonc.usdp.odk.common.collection.CollectionUtil;
import com.bonc.usdp.odk.elasticsearch.esUtil.exception.SearchException;
import com.bonc.usdp.zzb.test.DbUtil.ESManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class face {
    private static Map<String, String> person_id_map = Maps.newHashMap();

    public static void simulateTracks() throws IOException, InvalidFormatException, ParseException {
//        ESManager.es_ip = "127.0.0.1";
        ESManager.es_ip = "172.16.11.214";
        ESManager.socket_port = 9300;
        ESManager.http_port = 9200;
        List<String> persons = Lists.newArrayList("李达康", "高育良", "高小琴", "王大陆");
        for (String person : persons) {
            String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            JSONObject face = new JSONObject();

            face.put("CREATE_TIMESTAMP", nowDate);
            face.put("UPDATE_TIMESTAMP", nowDate);
            face.put("BLACKLIST_PERSON", "101");
            face.put("CAMERA_ID", 107);
            face.put("CAPTURE_ID", 105);
            face.put("BLACKLIST_TABLE", 106);
            face.put("BLACKLIST_LIB", 102);
            face.put("PICTURE_PATH", person + ".jpg");
            face.put("PICTURE_LEN", "1515997126905");
            face.put("SEMBLANCE", 100);
            face.put("CAPTURE_TIME", nowDate);
            face.put("BLACKLIST_NAME", person);
            face.put("BLACKLIST_PERSON_ID", "200");
            String parentId = getId(person);
            face.put("NESTED_PARENT_ID", parentId);

            try {
                ESManager.getInstance().getSearchService().upsertByJson("mingyi_face", "FACE", UUID.randomUUID().toString(), face.toJSONString());
            } catch (SearchException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getId(String name) {
        if (person_id_map.containsKey(name)) {
            return person_id_map.get(name);
        }
        String sql = "select _id from PERSON where PERSONAL_NAME=?";
        QueryRunner runner = ESManager.getInstance().getQueryRunner("mingyi_person");
        List<String> ids = null;
        try {
            ids = runner.query(sql, new ColumnListHandler<String>(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (CollectionUtil.notEmpty(ids)) {
            if (ids.size() > 1) {
                System.out.println(name + "  duplicated for " + ids.size());
                System.exit(0);
            }
            person_id_map.put(name, ids.get(0));
        } else {
            System.out.println(name + " no matched.");
            System.exit(0);
        }
        return person_id_map.get(name);
    }

    public static void main(String[] args) {
        try {
            simulateTracks();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
