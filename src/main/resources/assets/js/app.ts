import {Stomp} from "@stomp/stompjs";
import SockJS = require('sockjs-client');
import "./bootstrap"
import "./chart"

let socket = new SockJS('/ws');
let stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log("Connected");
    console.log(frame);
});
