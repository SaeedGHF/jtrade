import {createChart, CrosshairMode, isBusinessDay} from 'lightweight-charts';

class Chart implements ChartInterface {
    private instance;
    private lineSeries;

    constructor(data) {
        this.instance = createChart("b-chart", {
            localization: {
                dateFormat: 'dd.MM.yyyy',
                timeFormatter: businessDayOrTimestamp => {
                    // console.log(businessDayOrTimestamp);

                    if (isBusinessDay(businessDayOrTimestamp)) {
                        return 'Format for business day';
                    }

                    let date = new Date();
                    date.setSeconds(businessDayOrTimestamp);

                    return date.getHours() + ":" + date.getMinutes();
                },
            },
            timeScale: {
                rightOffset: 12,
                barSpacing: 3,
                fixLeftEdge: true,
                lockVisibleTimeRangeOnResize: true,
                rightBarStaysOnScroll: true,
                borderVisible: false,
                borderColor: '#fff000',
                visible: true,
                timeVisible: true,
                secondsVisible: false,
                tickMarkFormatter: (time, tickMarkType, locale) => {
                    console.log(time, tickMarkType, locale);
                    const year = isBusinessDay(time) ? time.year : new Date(time * 1000).getUTCHours() + ":00";
                    return String(year);
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

    public update(candlestick){
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