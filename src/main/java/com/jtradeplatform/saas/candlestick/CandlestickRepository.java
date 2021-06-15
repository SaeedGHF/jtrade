package com.jtradeplatform.saas.candlestick;

import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import com.jtradeplatform.saas.configs.InfluxdbConfig;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class CandlestickRepository {

    private final InfluxDBClient client;
    private final InfluxdbConfig influxdbConfig;
    private final QueryApi queryApi;

    CandlestickRepository(InfluxdbConfig config) {
        this.client = config.getClient();
        this.queryApi = client.getQueryApi();
        this.influxdbConfig = config;
    }

    public Candlestick save(Candlestick candlestick) {
        try (WriteApi writeApi = client.getWriteApi()) {
            writeApi.writeMeasurement(WritePrecision.S, candlestick);
        }

        return candlestick;
    }

    public List<Candlestick> findAllBySymbol(int symbol) {
        String flux = String.format("from(bucket: \"%s\")\n" +
                        "  |> range(start: -3d)\n" +
                        "  |> filter(fn: (r) => r[\"_measurement\"] == \"%s\" and r[\"symbol\"] == \"%s\")\n",
                influxdbConfig.getBucket(),
                Candlestick.getMeasureName(),
                symbol
        );

        List<Candlestick> result = queryApi.query(flux, Candlestick.class);
        for (Candlestick c : result) {
            c.parseValue();
        }
        return result;
    }

    public void saveAll(List<Candlestick> list) {
        try (WriteApi writeApi = client.getWriteApi()) {
            writeApi.writeMeasurements(WritePrecision.S, list);
        }
    }

    public void deleteAll() {
        DeleteApi deleteApi = client.getDeleteApi();

        try {
            OffsetDateTime start = OffsetDateTime.now().minusDays(30);
            OffsetDateTime stop = OffsetDateTime.now().plusDays(1);

            deleteApi.delete(start, stop, "", influxdbConfig.getBucket(), influxdbConfig.getOrg());

        } catch (InfluxException ie) {
            System.err.println("InfluxException: " + ie);
        }
    }
}
