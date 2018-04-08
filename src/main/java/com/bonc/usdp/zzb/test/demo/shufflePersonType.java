package com.bonc.usdp.zzb.test.demo;

import com.bonc.usdp.odk.common.collection.CollectionUtil;
import com.bonc.usdp.odk.common.file.document.ExcelFileHandler;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class shufflePersonType {
    public static void main(String[] args) {
        ExcelFileHandler handler = new ExcelFileHandler();
        String file = "D:\\offlinetool项目\\涉拐\\data\\演示数据整合.xlsx";
        Map<String, Set<String>> map = Maps.newHashMap();
        try {
            List<List<String>> lists = handler.readExcelByRows(file, 1, 1, 1, -1, 0, 1);
            for (List<String> list : lists) {
                String id = list.get(0);
                String type = list.get(1);
                Set<String> existsType = map.get(id);
                if (CollectionUtil.isEmpty(existsType)) {
                    existsType = Sets.newHashSet(type);
                    map.put(id, existsType);
                } else {
                    existsType.add(type);
                }
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey());
        }
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            System.out.println(Joiner.on(";").join(entry.getValue()));
        }
    }
}
