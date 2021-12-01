<#assign title = "Криптовалютные пары">
<#include "layout.ftlh">
<#import "components/content.ftlh" as c>
<@page>
    <@c.content>
        <div class="row">
            <div class="col-md-12">
                <div class="card mb-4 rounded-3 shadow-sm">
                    <div class="card-header py-3 align-content-between d-flex">
                        <div>
                            <h4 class="my-0 fw-normal" id="selected-event-symbol">Криптовалютные пары</h4>
                        </div>
                        <div class="text-end ms-auto" id="selected-event-pattern">
                            <div class="btn-group" role="group" aria-label="Basic checkbox toggle button group">
                                <button type="button" class="btn btn-outline-secondary" id="charts-refresh">
                                    Обновить историю (m1 - 3d)
                                </button>
                                <button type="button" class="btn btn-outline-secondary" id="symbols-refresh">
                                    Обновить список (Binance)
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                            <tr>
                                <th scope="col" width="5%">#</th>
                                <th scope="col" width="10%">Название</th>
                                <th scope="col">Дата обновления</th>
                                <th scope="col">Лог</th>
                                <th scope="col">Действия</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list model["symbols"] as symbol>
                                <tr data-id="${symbol.id}">
                                    <th scope="row">${symbol?counter}</th>
                                    <td>${symbol.name}</td>
                                    <td>${symbol.updatedAt}</td>
                                        <td>
                                            <#if true></#if>
                                        </td>
                                    <td>
                                        <a class="btn btn-sm btn-outline-secondary refresh-history">Обновить историю</a>
                                    </td>
                                </tr>
                            <#else >
                                <tr>
                                    <td rowspan="5">Криптовалютные пары не найдены</td>
                                </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </@c.content>

    <script src="/js/symbols.js"></script>
</@page>