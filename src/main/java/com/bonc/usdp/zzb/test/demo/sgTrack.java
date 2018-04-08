package com.bonc.usdp.zzb.test.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.usdp.odk.common.collection.CollectionUtil;
import com.bonc.usdp.odk.common.file.document.ExcelFileHandler;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.elasticsearch.esUtil.exception.SearchException;
import com.bonc.usdp.zzb.test.DbUtil.ESManager;
import com.google.common.collect.Maps;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class sgTrack {
    private static Map<String, String> person_id_map = Maps.newHashMap();

    public static void simulateTracks() throws IOException, InvalidFormatException, ParseException {
//        ESManager.es_ip = "127.0.0.1";
        ESManager.es_ip = "172.16.11.43";
        ESManager.socket_port=9300;
        ESManager.http_port=9200;

//        ESManager.es_ip = "172.16.11.148";
//        ESManager.socket_port=8300;
//        ESManager.http_port=8200;
        ExcelFileHandler handler = new ExcelFileHandler();
        String filePath = "D:\\offlinetool项目\\人民名义\\In_the_name_of_people\\tracks.xlsx";
        List<List<String>> lists = handler.readExcelByRows(filePath, 0, 0, 1, -1, 0, -1);
        QueryRunner runner = ESManager.getInstance().getQueryRunner("mingyi_track");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into TRACK(TRIP_MODE,TIME,NESTED_PARENT_ID,ALTITUDE,COORDINATE,ADDRESS) values(?,?,?,?,?,?)";
        int i = 0;
        for (List<String> row : lists) {
            String person = row.get(0);
            if (StringUtil.isEmpty(person)) {
                continue;
            }
            String mode = row.get(1);
            String time = row.get(2);
            JSONArray cooridate = new JSONArray();
            cooridate.add(Double.valueOf(row.get(3).split(",")[0]));
            cooridate.add(Double.valueOf(row.get(3).split(",")[1]));
            Integer altitude = Integer.valueOf(StringUtil.isEmpty(row.get(4)) ? "0" : row.get(4));
            String address = row.get(5);
            String parentId = getId(person);
            JSONObject track = new JSONObject();
            track.put("TRIP_MODE", mode);
            track.put("TIME", time);
            track.put("NESTED_PARENT_ID", parentId);
            track.put("ALTITUDE", altitude);
            track.put("COORDINATE", cooridate);
            track.put("ADDRESS", address);

            try {
                ESManager.getInstance().getSearchService().upsertByJson("mingyi_track", "TRACK", UUID.randomUUID().toString(), track.toJSONString());
            } catch (SearchException e) {
                e.printStackTrace();
            }
            System.out.println("Insert " + i++);

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
