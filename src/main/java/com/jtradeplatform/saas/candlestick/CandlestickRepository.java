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

    public final String MEASUREMENT_NAME = "candlestick";

    private final InfluxDBClient client;
    private final InfluxdbConfig influxdbConfig;

    CandlestickRepository(InfluxdbConfig config) {
        this.client = config.getClient();
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
                        "  |> range(start: -7d)\n" +
                        "  |> pivot(\n" +
                        "    rowKey:[\"_time\"],\n" +
                        "    columnKey: [\"_field\"],\n" +
                        "    valueColumn: \"_value\"\n" +
                        "  )" +
                        "  |> filter(fn: (r) => r[\"_measurement\"] == \"%s\" and r[\"symbol\"] == \"%s\")\n",
                influxdbConfig.getBucket(),
                MEASUREMENT_NAME,
                symbol
        );

        QueryApi queryApi = client.getQueryApi();
        return queryApi.query(flux, Candlestick.class);
    }

    public void saveAll(List<Candlestick> list) {
        try (WriteApi writeApi = client.getWriteApi()) {
            writeApi.writeMeasurements(WritePrecision.MS, list);
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
