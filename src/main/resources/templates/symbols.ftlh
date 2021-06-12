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
                                <button type="button" class="btn btn-outline-secondary">Активировать все</button>
                                <button type="button" class="btn btn-outline-secondary">Деактивировать все</button>
                                <button type="button" class="btn btn-outline-secondary">Обновить историю (m1 - 3d)
                                </button>
                                <button type="button" class="btn btn-outline-secondary">Обновить список (Binance)
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Название</th>
                                <th scope="col">Статус</th>
                                <th scope="col">Дата обновления</th>
                                <th scope="col">Действия</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list model["symbols"] as symbol>
                                <tr data-id="${symbol.id}">
                                    <th scope="row">${symbol?counter}</th>
                                    <td>${symbol.name}</td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
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
</@page>