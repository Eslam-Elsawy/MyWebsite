var app;
(function (app) {
    var fsa;
    (function (fsa) {
        var FSAAcceptorCtrl = (function () {
            function FSAAcceptorCtrl($http) {
                var _this = this;
                this.$http = $http;
                // sample input
                var sampleFSA = "";
                this.$http({
                    method: 'GET',
                    url: '/MyWebsite/api/fsaacceptor/sample_fsa'
                }).then(function successCallback(response) {
                    sampleFSA = String(response.data);
                }, function errorCallback(response) {
                }).then(function () {
                    _this.fsa = sampleFSA;
                    var sampleInput = "";
                    _this.$http({
                        method: 'GET',
                        url: '/MyWebsite/api/fsaacceptor/sample_input'
                    }).then(function successCallback(response) {
                        sampleInput = String(response.data);
                    }, function errorCallback(response) {
                    }).then(function () {
                        _this.input = sampleInput;
                        _this.run();
                    });
                });
            }
            FSAAcceptorCtrl.prototype.run = function () {
                var _this = this;
                var serverResponse;
                this.$http({
                    method: 'POST',
                    url: '/MyWebsite/api/fsaacceptor',
                    data: { fsa: this.fsa, input: this.input }
                }).then(function successCallback(response) {
                    serverResponse = String(response.data);
                }, function errorCallback(response) {
                }).then(function () { _this.output = serverResponse; });
            };
            FSAAcceptorCtrl.$inject = ["$http"];
            return FSAAcceptorCtrl;
        })();
        angular.module("productManagement").controller("FSAAcceptorCtrl", FSAAcceptorCtrl);
    })(fsa = app.fsa || (app.fsa = {}));
})(app || (app = {}));
