package com.bonc.usdp.zzb.test.DbUtil;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bonc.usdp.odk.common.collection.MapUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.impl.SearchServiceImpl;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.dbutils.QueryRunner;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ESManager {

    private static ESManager instance = null;

    private static ISearchService searchService;

    private static Map<String, DruidDataSource> esDataSources;

    public static String es_ip = "172.16.11.43";
    public static Integer socket_port = 9300;
    public static Integer http_port = 9200;
    public static String cluster_name = "elasticsearch";
    private static TransportClient client;

    private ESManager() {
        try {
            searchService = new SearchServiceImpl(es_ip, socket_port, http_port,
                    cluster_name);
            Settings settings = Settings.builder().put("cluster.name", cluster_name).build();
            client = TransportClient.builder().settings(settings).build();
            try {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(es_ip), Integer.valueOf(socket_port)));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            LogManager.Exception(e);
        }
    }


    public Set<String> getAllIndices() {
        ActionFuture<IndicesStatsResponse> isr = client.admin().indices().stats(new IndicesStatsRequest().all());
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        Map<String, IndexStats> indexStatsMap = isr.actionGet().getIndices();
        Set<String> set = isr.actionGet().getIndices().keySet();
        return set;
    }

    private String getEsIndexUrl(String index) {
        return "jdbc:sql4es://" + es_ip + ":" + socket_port + "/" + index + "?"
                + "cluster.name=" + cluster_name;

    }

    public static synchronized ESManager getInstance() {
        if (instance == null) {
            instance = new ESManager();
            addShutDownHook();
        }
        return instance;
    }

    public ISearchService getSearchService() {
        return searchService;
    }

    public void searchClose() {
        searchService.close();
    }

    private synchronized DruidDataSource getDataSource(String index) {
        if (StringUtil.isEmpty(index)) {
            return null;
        }
        if (esDataSources == null) {
            esDataSources = Maps.newHashMap();
        }
        DruidDataSource dataSource = esDataSources.get(index);
        if (dataSource == null) {
            try {
                ImmutableMap<String, String> conf = ImmutableMap.<String, String>builder()
                        .put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, "com.bonc.usdp.sql4es.jdbc.ESDriver")
                        .put(DruidDataSourceFactory.PROP_URL, getEsIndexUrl(index))
                        .put(DruidDataSourceFactory.PROP_INITIALSIZE, "1")
                        .put(DruidDataSourceFactory.PROP_TESTWHILEIDLE, "true")
                        .put(DruidDataSourceFactory.PROP_VALIDATIONQUERY, "SHOW TABLES")
                        .build();
                dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(conf);
                esDataSources.put(index, dataSource);
            } catch (Exception e) {
                LogManager.Exception(e);
            }
        }
        return dataSource;
    }

    private static void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!MapUtil.isEmpty(esDataSources)) {
                    esDataSources.forEach((index, ds) -> ds.close());
                }
                if (searchService != null) {
                    searchService.close();
                }
            }
        });
    }

    public Connection getConnection(String index) {
        if (StringUtil.isEmpty(index)) {
            return null;
        }
        Connection conn = null;
        DruidDataSource dataSource = getDataSource(index);
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            LogManager.Exception(e);
        }
        return conn;
    }

    public QueryRunner getQueryRunner(String index) {
        DruidDataSource dataSource = getDataSource(index);
        return new QueryRunner(dataSource);
    }

    public static synchronized void closeDataSource(String index) {
        if (StringUtil.isEmpty(index) || esDataSources == null) {
            return;
        }
        DruidDataSource dataSource = esDataSources.get(index);
        if (dataSource != null) {
            dataSource.close();
            esDataSources.remove(index);
        }
    }


    public static void close(Connection conn, Statement statement, ResultSet resultSet) {
        try {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException e) {
            LogManager.Exception(e);
        }
    }

}
