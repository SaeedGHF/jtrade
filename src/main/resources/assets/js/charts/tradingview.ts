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

    public addPriceLine(price: number){
        const priceLine = this.lineSeries.createPriceLine({
            price: price,
            color: 'green',
            lineWidth: 2,
            lineStyle: LineStyle.Dashed,
            axisLabelVisible: true,
            title: 'P/L 500',
        });

        //priceLine.applyOptions({
        //    price: price + 100,
        //    color: 'red',
        //    lineWidth: 3,
        //    lineStyle: LineStyle.Dashed,
        //    axisLabelVisible: false,
        //    title: 'P/L 600',
        //});
    }

    public setData(data) {
        if(this.lineSeries){
            this.instance.removeSeries(this.lineSeries);
        }
        this.lineSeries = this.instance.addCandlestickSeries();
        this.lineSeries.setData(this.convertSeries(data));
        this.instance.applyOptions({
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