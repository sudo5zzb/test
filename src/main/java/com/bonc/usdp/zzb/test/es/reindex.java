package com.bonc.usdp.zzb.test.es;

import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.zzb.test.DbUtil.ESManager;
import com.google.common.collect.Sets;

public class reindex {
    private static void test() {
        ESManager.es_ip = "127.0.0.1";
        ISearchService searchService = ESManager.getInstance().getSearchService();
        String mapping = "{\"properties\":{\"ALTITUDE\":{\"type\":\"integer\",\"index\":\"not_analyzed\",\"boost\":1.0},\"ADDRESS\":{\"type\":\"string\",\"index\":\"not_analyzed\",\"boost\":1.0},\"TRIP_MODE\":{\"type\":\"string\",\"index\":\"not_analyzed\",\"boost\":1.0},\"COORDINATE\":{\"type\":\"geo_point\"},\"TIME\":{\"type\":\"date\",\"format\":\"yyyy-MM-dd HH:mm:ss\",\"index\":\"not_analyzed\",\"boost\":1.0}},\"_all\":{\"enabled\":false}}";
        searchService.createDatabase("test1");
        searchService.createTable("test1", "TRACK", mapping);
        System.out.println("111111111111");
        try {
            Thread.currentThread().sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        searchService.reIndex("demo_track_20171227094406", "TRACK", Sets.newHashSet(), Sets.newHashSet("NESTED_PARENT_ID"), "test1", null);

    }

    public static void main(String[] args) {
        test();
    }
}
