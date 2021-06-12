<#macro page>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>${title!"В разработке"} - jtradeplatform.com</title>
        <#include "components/styles.ftlh">
    </head>
    <body>
    <#include "components/header.ftlh">
    <#nested>
    <#include "components/footer.ftlh">
    <#include "components/scripts.ftlh">
    </body>
    </html>
</#macro>