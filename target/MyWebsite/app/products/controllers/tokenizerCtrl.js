var app;
(function (app) {
    var tokenizer;
    (function (tokenizer) {
        var TokenizerCtrl = (function () {
            function TokenizerCtrl($http) {
                var _this = this;
                this.$http = $http;
                // sample input
                var sampleInput = "";
                this.$http({
                    method: 'GET',
                    url: '/MyWebsite/api/tokenizer/sample'
                }).then(function successCallback(response) {
                    sampleInput = String(response.data);
                }, function errorCallback(response) {
                }).then(function () {
                    _this.input = sampleInput;
                    _this.tokenize();
                });
            }
            TokenizerCtrl.prototype.tokenize = function () {
                var _this = this;
                var serverResponse;
                this.$http({
                    method: 'POST',
                    url: '/MyWebsite/api/tokenizer',
                    data: this.input
                }).then(function successCallback(response) {
                    serverResponse = String(response.data);
                }, function errorCallback(response) {
                }).then(function () { _this.output = serverResponse; });
            };
            TokenizerCtrl.$inject = ["$http"];
            return TokenizerCtrl;
        })();
        angular.module("productManagement").controller("TokenizerCtrl", TokenizerCtrl);
    })(tokenizer = app.tokenizer || (app.tokenizer = {}));
})(app || (app = {}));
