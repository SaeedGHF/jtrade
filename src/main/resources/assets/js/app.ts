import {Stomp} from "@stomp/stompjs";
import SockJS = require('sockjs-client');
import "./bootstrap"
import * as $ from "jquery";
import Chart from "./charts/tradingview"


let socket = new SockJS('/ws');
let stompClient = Stomp.over(socket);
let symbol = 2;

stompClient.connect({}, function () {
    $.ajax({
       url:"/api/chart/" + symbol,
       success: function (data){
           let chart = new Chart(data);
           stompClient.subscribe("/charts/" + symbol, function (message) {
               chart.update(JSON.parse(message.body));
           });
       }
    });
});
