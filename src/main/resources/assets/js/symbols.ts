import * as $ from "jquery";

$(".refresh-history").on('click', function (e) {
    e.preventDefault();

    let symbol = $(this).closest("tr").data("id");
    $.ajax({
        url: `/api/charts/${symbol}/refresh`,
        type: 'POST',
        success: function () {
            alert("История обновлена")
        }, error: function () {
            alert("Ошибка обновления истории")
        }
    })
});

$('#symbols-refresh').on('click', function (){
    $.ajax({
        url: `/api/symbols/refresh`,
        type: 'POST',
        success: function () {
            alert("Список криптовалютных пар обновлен")
            window.location.reload();
        }, error: function () {
            alert("Ошибка обновления криптовалютных пар")
        }
    })
});

$('#charts-refresh').on('click', function (){
    $.ajax({
        url: `/api/charts/refresh`,
        type: 'POST',
        success: function () {
            alert("История обновлена")
        }, error: function () {
            alert("Ошибка обновления истории")
        }
    })
});