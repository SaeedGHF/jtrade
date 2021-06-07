/* Imports */
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";

class Amcharts implements ChartInterface{

    private instance = am4core.create("b-chart", am4charts.XYChart);

    constructor() {
        am4core.useTheme(am4themes_animated);
        //
        this.instance.padding(0, 0, 0, 0);
        this.instance.hiddenState.properties.opacity = 0;
        this.instance.dateFormatter.inputDateFormat = "dd.MM.yyyy";
        this.instance.cursor = new am4charts.XYCursor();

        this.configAxis();
        this.configLineSeries();
        this.configSeries();
    }

    appendData(data, remove = 0) {
        this.instance.addData(data, remove);
    }

    update(){

    };

    drawHorizontalLine(data) {

    }

    drawTrendLine(data) {
        let trend = this.instance.series.push(new am4charts.LineSeries());
        trend.dataFields.valueY = "value";
        trend.dataFields.dateX = "time";
        trend.strokeWidth = 2
        trend.stroke = am4core.color("#c00");
        trend.data = data;

        let bullet = trend.bullets.push(new am4charts.CircleBullet());
        bullet.strokeWidth = 2;
        bullet.stroke = am4core.color("#fff")
        bullet.circle.fill = trend.stroke;
    }

    private configLineSeries() {
        // a separate series for scrollbar
        let lineSeries = this.instance.series.push(new am4charts.LineSeries());
        lineSeries.dataFields.dateX = "time";
        lineSeries.dataFields.valueY = "close";
        // need to set on default state, as initially series is "show"
        lineSeries.defaultState.properties.visible = false;
        // hide from legend too (in case there is one)
        lineSeries.hiddenInLegend = true;
        lineSeries.fillOpacity = 0.5;
        lineSeries.strokeOpacity = 0.5;
        let scrollbarX = new am4charts.XYChartScrollbar();
        scrollbarX.series.push(lineSeries);
        this.instance.scrollbarX = scrollbarX;
        // bullet at the front of the line
        let bullet = lineSeries.createChild(am4charts.CircleBullet);
        bullet.circle.radius = 5;
        bullet.fillOpacity = 1;
        bullet.fill = this.instance.colors.getIndex(0);
        bullet.isMeasured = false;

        lineSeries.events.on("validated", function() {
            bullet.moveTo(lineSeries.dataItems.last.point);
            bullet.validatePosition();
        });
    }

    private configAxis() {
        //
        let dateAxis = this.instance.xAxes.push(new am4charts.DateAxis());
        dateAxis.renderer.grid.template.location = 0;
        dateAxis.interpolationDuration = 500;
        dateAxis.rangeChangeDuration = 500;
        //
        let valueAxis = this.instance.yAxes.push(new am4charts.ValueAxis());
        valueAxis.tooltip.disabled = true;
        //
    }

    private configSeries() {
        let series = this.instance.series.push(new am4charts.CandlestickSeries());
        series.dataFields.dateX = "time";
        series.dataFields.valueY = "close";
        series.dataFields.openValueY = "open";
        series.dataFields.lowValueY = "low";
        series.dataFields.highValueY = "high";
        series.simplifiedProcessing = true;
        series.tooltipText = "Open: {openValueY.value}\nLow: {lowValueY.value}\nHigh: {highValueY.value}\nClose: {valueY.value}";
    }
}

let Instance = new Amcharts();
export default Instance;