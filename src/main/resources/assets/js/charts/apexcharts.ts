import * as ApexCharts from "apexcharts";

class Chart implements ChartInterface{

    private instance = null;

    constructor(seriesData) {
        let options = {
            series: [{
                data: this.convertSeries(seriesData)
            }],
            chart: {
                type: 'candlestick'
            },
            plotOptions: {
                candlestick: {
                    wick: {
                        useFillColor: true,
                    }
                }
            },
            xaxis: {
                type: 'datetime'
            },
            yaxis: {
                tooltip: {
                    enabled: true
                }
            }
        };

        this.instance = new ApexCharts(document.querySelector('#b-chart'), options);
        this.instance.render()
    }

    public appendData(data, precision) {
        this.instance.appendData(this.convertSeries(data));
    }
    public update(data){

    }

    private convertSeries(data) {
        let convertedData = [];
        if (Array.isArray(data)) {
            data.forEach((item) => {
                convertedData.push(Chart.convertCandlestick(item));
            });
        } else {
            convertedData.push(Chart.convertCandlestick(data));
        }

        console.log(convertedData);

        return convertedData;
    }

    private static convertCandlestick(item) {
        let time = Math.floor((new Date(item.time)).getTime() / 1000);
        return [time, item.open, item.high, item.low, item.close];
    }

    setData(data, precision) {
    }

}

export default Chart;
