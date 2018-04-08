package com.bonc.usdp.zzb.test.es;

import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.zzb.test.DbUtil.ESManager;

import java.util.Set;

public class deleteIndexes {
    public static void main(String[] args) {
        ESManager.es_ip = "127.0.0.1";
        ESManager.socket_port = 9300;
        ESManager.http_port = 9200;

        Set<String> allIndices = ESManager.getInstance().getAllIndices();
        ISearchService searchService = ESManager.getInstance().getSearchService();
        allIndices.forEach(index -> {
            try {
                if (!"mgcdb".equalsIgnoreCase(index)) {
                    searchService.deleteDatabase(index);
                }
            } catch (Exception e) {

            }
        });
    }
}
