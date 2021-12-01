package com.jtradeplatform.saas;

import com.jtradeplatform.saas.candlestick.Candlestick;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.*;

public class CandlestickLoader {

    public static List<Candlestick> getReversedData() throws FileNotFoundException {

        String path = "src/test/resources";

        File file = new File(path);
        String absolutePath = file.getAbsolutePath();

        List<Candlestick> candlesticks = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(absolutePath + "/candlesticks.csv"))) {
            while (scanner.hasNextLine()) {
                String row = scanner.nextLine();
                List<Double> numbs = new ArrayList<>();
                String[] strAll = row.split(";");
                Arrays.stream(strAll).skip(1).forEach((str) -> {
                    numbs.add(Double.parseDouble(str));
                });
                candlesticks.add(new Candlestick(
                        Instant.parse(strAll[0]), "1m", "1",
                        numbs.get(0),
                        numbs.get(1),
                        numbs.get(2),
                        numbs.get(3)
                ));
            }
        }
        Collections.reverse(candlesticks);
        return candlesticks;
    }
}
