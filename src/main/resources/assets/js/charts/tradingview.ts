import {createChart, CrosshairMode, isBusinessDay} from 'lightweight-charts';
import "moment/locale/ru";
import moment = require("moment");

class Chart implements ChartInterface {
    private readonly instance;
    private readonly lineSeries;

    constructor(data) {
        this.instance = createChart("b-chart", {
            localization: {
                dateFormat: 'dd.MM.yyyy',
                timeFormatter: businessDayOrTimestamp => {
                    if (isBusinessDay(businessDayOrTimestamp)) {
                        return 'Format for business day';
                    }
                    return moment.unix(businessDayOrTimestamp).format('LLL');
                },
            },
            timeScale: {
                rightOffset: 50,
                barSpacing: 3,
                fixLeftEdge: true,
                lockVisibleTimeRangeOnResize: true,
                rightBarStaysOnScroll: true,
                borderVisible: false,
                borderColor: '#fff000',
                visible: true,
                timeVisible: true,
                secondsVisible: false,
                tickMarkFormatter: (time) => {
                    return moment.unix(time).format('LT');
                },
            },
            crosshair: {
                mode: CrosshairMode.Normal,
            },
        });
        this.lineSeries = this.instance.addCandlestickSeries();
        this.lineSeries.setData(this.convertSeries(data));
    }

    public appendData(data) {
        this.instance.add();
    }

    public clear() {
        this.instance.removeSeries(this.lineSeries);
    }

    public update(candlestick) {
        this.lineSeries.update(Chart.convertCandlestick(candlestick));
    }


    private convertSeries(data) {
        if (Array.isArray(data)) {
            let convertedData = [];
            data.forEach((item) => {
                convertedData.push(Chart.convertCandlestick(item));
            });
            return convertedData;
        } else {
            return Chart.convertCandlestick(data);
        }
    }

    private static convertCandlestick(item) {
        item.time = Math.floor((new Date(item.time)).getTime() / 1000);
        return item;
    }

}

export default Chart;