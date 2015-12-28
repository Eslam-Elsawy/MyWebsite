var app;
(function (app) {
    var common;
    (function (common) {
        // angular.module("common.services", ["productResourceMock", "ngResource"]);
        angular.module("common.services", ["ngResource"]);
    })(common = app.common || (app.common = {}));
})(app || (app = {}));
