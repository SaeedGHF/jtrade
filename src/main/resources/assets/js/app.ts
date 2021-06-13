import {Stomp} from "@stomp/stompjs";
import SockJS = require('sockjs-client');
import "./bootstrap"
import * as $ from "jquery";
import Chart from "./charts/tradingview"
import * as _ from "underscore";

class App {
    private socket = new SockJS('/ws');
    private stompClient = Stomp.over(this.socket);
    private chartSubscription = null;
    private selectedEvent = null;
    private eventList = [];
    private audio = new Audio('event.mp3');
    private eventTemplate = _.template($('#event-template').html());
    private chart = new Chart();

    constructor() {
        this.stompClient.debug = ()=>{}
        this.connect();
        this.enableHandlers();
    }

    private enableHandlers() {
        $('#events').on('click', '.event', (e) => {
            let eventTarget = $(e.currentTarget);
            let json = eventTarget.data('json');
            if (this.selectedEvent && json.id === this.selectedEvent.id) {
                return;
            }
            this.selectEvent(json);
        })

        $('#refresh-symbol-history').on('click', (e) => {
            e.preventDefault();
            if (!this.selectedEvent) {
                alert("Не выбран символ")
                return;
            }

            $.ajax({
                type: "POST",
                url: "/api/chart/" + this.selectedEvent.symbol.id + "/refresh",
                success: () => {
                    alert("История обновлена");
                    this.selectEvent(this.selectedEvent);
                },
            });
        });
    }

    private selectEvent(newSelectedEvent) {
        this.selectedEvent = newSelectedEvent;
        this.subscribe();
        this.renderSelectedEvent();
    }

    private addEvent(event) {
        this.eventList.push(event);
        event.data = JSON.parse(event.data);

        this.eventList = _.map(_.indexBy(this.eventList, 'id'), function (obj) {
            return obj
        })

        // sort reverse
        this.eventList = this.eventList.sort(function (a, b) {
            if (a.id > b.id) {
                return -1;
            }
            if (a.id < b.id) {
                return 1;
            }
            return 0;
        });

        this.renderEvents();
    }

    renderSelectedEvent() {
        $('#selected-event-symbol').html(this.selectedEvent.symbol.name);
        $('#selected-event-pattern').html(this.selectedEvent.pattern);
    }

    private renderEvents() {
        let container = $('#events');
        container.empty();
        container.html(this.eventTemplate({events: this.eventList}));
        if (this.selectedEvent) {
            $('#event-' + this.selectedEvent.id).addClass(' event--active');
        }
    }

    private subscribe() {
        this.subscribeTradeEvents();
        this.subscribeChartEvents()
    }

    private connect() {
        this.stompClient.connect({}, () => {
            this.subscribe();
        }, (err) => {
            console.error(err);
            this.reconnect();
        }, (err) => {
            console.error(err);
            this.reconnect();
        });
    }

    private reconnect() {
        setTimeout(() => {
            console.log("Reconnecting websockets...")
            this.connect();
        }, 5000)
    }

    private subscribeChartEvents() {
        if (!this.selectedEvent) {
            return;
        }
        let chartChannel = "/charts/" + this.selectedEvent.symbol.id;
        if (this.chartSubscription) {
            this.chartSubscription.unsubscribe();
        }

        $.ajax({
            url: "/api/chart/" + this.selectedEvent.symbol.id,
            success: (data) => {
                this.chart.setData(data);
                this.selectedEvent.data.trendLines.forEach((item) => {
                    this.chart.addPriceLine(item);
                });
                this.chartSubscription = this.stompClient.subscribe(chartChannel, (message) => {
                    this.chart.update(JSON.parse(message.body));
                });
            }
        });
    }

    //public addTestLine(price){
    //    this.chart.addPriceLine(price);
    //}

    private subscribeTradeEvents() {
        $.ajax({
            url: "/api/events",
            success: (data) => {
                _.each(data, item => this.addEvent(item));
                this.stompClient.subscribe("/events", (message) => {
                    this.audio.pause();
                    this.audio.currentTime = 0;
                    this.audio.play();
                    this.addEvent(JSON.parse(message.body));
                });
            }
        });
    }
}

let app = new App();

declare global {
    interface Window {
        app: any;
        moment: any;
    }
}

window.app = window.app || app;
window.moment = window.moment || require("moment");

