<#assign title = "JTrade Platform">
<#include "layout.ftlh">
<#import "components/content.ftlh" as b>
<@page>
    <@b.content>
        <div class="row">
            <div class="col-md-10">
                <div class="card mb-4 rounded-3 shadow-sm" style="height: 800px">
                    <div class="card-header py-3 align-content-between d-flex">
                        <div>
                            <h4 class="my-0 fw-normal" id="selected-event-symbol">
                                <i>Уведомление не выбрано</i>
                            </h4>
                        </div>
                        <div class="text-end ms-auto" id="selected-event-pattern">...</div>
                    </div>
                    <div class="card-body">
                        <div id="b-chart"></div>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="card mb-4 rounded-3 shadow-sm" style="height: 800px">
                    <div class="card-header py-3">
                        <h4 class="my-0 fw-normal">Уведомления</h4>
                    </div>
                    <div class="card-body" id="events" style="overflow-y: scroll">

                    </div>
                </div>
            </div>
        </div>
    </@b.content>

    <script id="event-template" type="text/html">
        <% _.each(events, function(item){ %>
        <div class="card mb-3 event" data-json='<%=JSON.stringify(item)%>' id="event-<%=item.id%>">
            <div class="card-body">
                <strong class="card-title"><%=item.symbol.name%></strong><br>
                <span class="card-text"><%=item.pattern%></span><br>
                <span><%=moment(item.createdAt).format('LLL')%></span>
            </div>
        </div>
        <% }); %>
    </script>

    <script src="/js/app.js"></script>
</@page>