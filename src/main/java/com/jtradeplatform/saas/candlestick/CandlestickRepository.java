package com.jtradeplatform.saas.candlestick;

import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.jtradeplatform.saas.configs.InfluxdbChartConfig;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class CandlestickRepository {

    public final String MEASUREMENT_NAME = "candlestick";
    public final int HISTORY_HOURS = 48;

    private final InfluxDBClient client;
    private final InfluxdbChartConfig influxdbConfig;

    CandlestickRepository(InfluxdbChartConfig config) {
        this.client = config.getClient();
        this.influxdbConfig = config;
    }

    public Candlestick save(Candlestick candlestick) {
        try (WriteApi writeApi = client.getWriteApi()) {
            writeApi.writeMeasurement(WritePrecision.S, candlestick);
        }

        return candlestick;
    }

    public void findAllBySymbol(Integer symbol) {
        String flux = String.format("from(bucket: \"%s\")\n" +
                        "  |> range(start: %s, stop: %s)\n" +
                        "  |> filter(fn: (r) => r[\"_measurement\"] == \"%s\")\n" +
                        "  |> filter(fn: (r) => r[\"symbol\"] == \"%s\")",
                influxdbConfig.getBucket(),
                Instant.now().minus(HISTORY_HOURS, ChronoUnit.HOURS),
                Instant.now(),
                MEASUREMENT_NAME,
                symbol
        );

        QueryApi queryApi = client.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord);
            }
        }
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
