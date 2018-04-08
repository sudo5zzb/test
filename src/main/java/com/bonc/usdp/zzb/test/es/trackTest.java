package com.bonc.usdp.zzb.test.es;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.usdp.odk.common.file.document.ExcelFileHandler;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.elasticsearch.esUtil.exception.SearchException;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.zzb.test.DbUtil.ESManager;
import com.google.common.collect.Maps;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class trackTest {
    private static Map<String, String> person_id_map = Maps.newHashMap();

    public static void simulateTracks() throws IOException, InvalidFormatException, ParseException {
        ESManager.es_ip = "127.0.0.1";
        ExcelFileHandler handler = new ExcelFileHandler();
        String filePath = "D:\\offlinetool项目\\人民名义\\In_the_name_of_people\\tracks.xlsx";
        List<List<String>> lists = handler.readExcelByRows(filePath, 0, 0, 1, -1, 0, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ISearchService searchService = ESManager.getInstance().getSearchService();
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
            String parentId = UUID.randomUUID().toString();
            JSONObject track = new JSONObject();
            track.put("TRIP_MODE", mode);
            track.put("TIME", time);
            track.put("NESTED_PARENT_ID", parentId);
            track.put("ALTITUDE", altitude);
            track.put("COORDINATE", cooridate);
            track.put("ADDRESS", address);

            try {
                searchService.insertByJson("demo_track", "TRACK", UUID.randomUUID().toString(), track.toJSONString());
            } catch (SearchException e) {
                e.printStackTrace();
            }
            System.out.println("Insert " + i++);

        }

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
