let mix = require('laravel-mix');


mix.ts('src/main/resources/assets/js/app.ts', 'js')
    .sass('src/main/resources/assets/scss/app.scss', 'css')
    .sass('src/main/resources/assets/scss/vendor.scss', 'css')
    .version()
    .extract()
    .setPublicPath('src/main/resources/static')
    .disableSuccessNotifications();

mix.browserSync({
    proxy: 'localhost:8080',
    ws: true,
    files: ['target/classes/templates/**/*', 'src/main/resources/static/**/*'],
});

// if (mix.inProduction()) {
//     mix.version();
// }
