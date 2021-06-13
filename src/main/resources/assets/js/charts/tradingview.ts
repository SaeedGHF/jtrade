import {createChart, CrosshairMode, isBusinessDay, LineStyle} from 'lightweight-charts';
import "moment/locale/ru";
import moment = require("moment");

class Chart implements ChartInterface {
    private readonly instance;
    private lineSeries;

    constructor() {
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
            crosshair: {
                mode: CrosshairMode.Normal,
            },
        });

    }

    public addPriceLine(item) {
        this.lineSeries.createPriceLine({
            price: +item.price,
            color: item.color,
            lineWidth: 2,
            lineStyle: +LineStyle[item.style],
            axisLabelVisible: true,
            title: item.name
        });
    }

    public setData(data, precision = 5) {
        if (this.lineSeries) {
            this.instance.removeSeries(this.lineSeries);
        }
        this.lineSeries = this.instance.addCandlestickSeries();
        this.lineSeries.setData(this.convertSeries(data));
        this.instance.applyOptions({
            priceFormat: {
                type: 'custom',
                minMove: '0.000001',
                formatter: (price) => {
                    if (price < 0.000001) return parseFloat(price).toPrecision(8)
                    else if (price >= 0.000001 && price < 1) return parseFloat(price).toPrecision(6)
                    else return parseFloat(price).toPrecision(6)
                }
            },
            priceScale: {
                autoScale: true
            },
            localization: {
                locale: 'en-US',
                priceFormatter: (price) => {
                    if (price < 0.000001) return parseFloat(price).toPrecision(8)
                    else if (price >= 0.000001 && price < 1) return parseFloat(price).toPrecision(6)
                    else return parseFloat(price).toPrecision(6)
                }
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
        });
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