package com.jtradeplatform.saas.candlestick;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import com.jtradeplatform.saas.configs.InfluxdbConfig;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class CandlestickRepository {
    private final InfluxDBClient client;
    private InfluxdbConfig influxdbConfig;

    CandlestickRepository(InfluxdbConfig cnfg) {
        client = cnfg.getClient();
        influxdbConfig = cnfg;
    }

    public Candlestick save(Candlestick candlestick) {
        WriteApi writeApi = client.getWriteApi();
        writeApi.writeMeasurement(WritePrecision.S, candlestick);
        return candlestick;
    }

    public void saveAll(List<Candlestick> list) {
        WriteApi writeApi = client.getWriteApi();
        writeApi.writeMeasurements(WritePrecision.S, list);
    }

    public void deleteAll() {
        DeleteApi deleteApi = client.getDeleteApi();

        try {
            OffsetDateTime start = OffsetDateTime.now().minus(1, ChronoUnit.HOURS);
            OffsetDateTime stop = OffsetDateTime.now().plusDays(1);

            deleteApi.delete(start, stop, "", influxdbConfig.getBucket(), influxdbConfig.getOrg());

        } catch (InfluxException ie) {
            System.out.println("InfluxException: " + ie);
        }
    }
}
