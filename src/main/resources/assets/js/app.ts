import {Stomp} from "@stomp/stompjs";
import SockJS = require('sockjs-client');
import "./bootstrap"
import * as $ from "jquery";
import Chart from "./charts/tradingview"

class App {
    private socket = new SockJS('/ws');
    private stompClient = Stomp.over(this.socket);
    private selectedEvent = null;
    private eventList = [];

    constructor() {
        this.connect();
        this.enableHandlers();
    }

    private enableHandlers() {
        $('.event').on('click', () => {
            this.selectEvent(JSON.parse($(this).data('json')));
        })
    }

    private selectEvent(newSelectedEvent) {
        this.selectedEvent = newSelectedEvent;
        this.connect();
        this.renderSelectedEvent();
    }

    private addEvents(events) {
        if (Array.isArray(events)) {
            this.eventList = this.eventList.concat(events);
        }
        {
            this.eventList.push(events);
        }

        this.renderEvents();
    }

    renderSelectedEvent() {

    }

    private renderEvents() {
        // TODO: create template for event
    }

    private connect() {
        this.stompClient.connect({}, () => {
            this.subscribeTradeEvents();
            this.subscribeChartEvents()
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
        $.ajax({
            url: "/api/chart/" + this.selectedEvent.symbol.id,
            success: (data) => {
                let chart = new Chart(data);
                this.stompClient.subscribe("/charts/" + this.selectedEvent.symbol.id, function (message) {
                    chart.update(JSON.parse(message.body));
                });
            }
        });
    }

    private subscribeTradeEvents() {
        $.ajax({
            url: "/api/events",
            success: (data) => {
                this.addEvents(data);
                this.stompClient.subscribe("/events", (message) => {
                    this.addEvents(JSON.parse(message.body));
                });
            }
        });
    }
}

let app = new App();

declare global {
    interface Window {
        app: any;
    }
}

window.app = window.app || app;

