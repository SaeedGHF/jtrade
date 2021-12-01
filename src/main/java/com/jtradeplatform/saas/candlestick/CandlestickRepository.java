package com.jtradeplatform.saas.candlestick;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import com.jtradeplatform.saas.configs.InfluxdbConfig;
import com.jtradeplatform.saas.symbol.Symbol;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class CandlestickRepository {

    private final InfluxDBClient client;
    private final InfluxdbConfig influxdbConfig;
    private final QueryApi queryApi;
    private final int CANDLESTICK_DAYS_LIFETIME = 3;

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

    public List<Candlestick> findAllBySymbol(Symbol symbol) {
        String flux = String.format("from(bucket: \"%s\")\n" +
                        "  |> range(start: -" + CANDLESTICK_DAYS_LIFETIME + "d)\n" +
                        "  |> filter(fn: (r) => r[\"_measurement\"] == \"%s\" and r[\"symbol\"] == \"%s\")\n",
                influxdbConfig.getBucket(),
                Candlestick.getMeasureName(),
                symbol.getId()
        );

        List<Candlestick> result = queryApi.query(flux, Candlestick.class);
        for (Candlestick c : result) {
            c.parseValue();
        }
        return result;
    }

    public Candlestick findLastBySymbol(Symbol symbol) {
        String flux = String.format("from(bucket: \"%s\")\n" +
                        " |> range(start: -" + CANDLESTICK_DAYS_LIFETIME + "d)\n" +
                        " |> filter(fn: (r) => r[\"_measurement\"] == \"%s\" and r[\"symbol\"] == \"%s\")\n" +
                        " |> sort(columns: [\"_time\"], desc: true)" +
                        " |> limit(n: 1)",
                influxdbConfig.getBucket(),
                Candlestick.getMeasureName(),
                symbol.getId()
        );

        List<Candlestick> result = queryApi.query(flux, Candlestick.class);
        return result.size() != 0 ? result.get(0) : createEmptyCandlestick(symbol);
    }

    private Candlestick createEmptyCandlestick(Symbol symbol) {
        return new Candlestick(
                Instant.now().minus(CANDLESTICK_DAYS_LIFETIME, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MINUTES),
                CandlestickInterval.ONE_MINUTE.toString(),
                symbol.getId().toString(),
                0D, 0D, 0D, 0D
        );
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
