var app;
(function (app) {
    var fst;
    (function (fst) {
        var FSTAcceptorCtrl = (function () {
            function FSTAcceptorCtrl($http) {
                var _this = this;
                this.$http = $http;
                // sample input
                var sampleFST = "";
                this.$http({
                    method: 'GET',
                    url: '/MyWebsite/api/fstacceptor/sample_fst'
                }).then(function successCallback(response) {
                    sampleFST = String(response.data);
                }, function errorCallback(response) {
                }).then(function () {
                    _this.fst = sampleFST;
                    var sampleInput = "";
                    _this.$http({
                        method: 'GET',
                        url: '/MyWebsite/api/fstacceptor/sample_input'
                    }).then(function successCallback(response) {
                        sampleInput = String(response.data);
                    }, function errorCallback(response) {
                    }).then(function () {
                        _this.input = sampleInput;
                        _this.run();
                    });
                });
            }
            FSTAcceptorCtrl.prototype.run = function () {
                var _this = this;
                var serverResponse;
                this.$http({
                    method: 'POST',
                    url: '/MyWebsite/api/fstacceptor',
                    data: { fst: this.fst, input: this.input }
                }).then(function successCallback(response) {
                    serverResponse = String(response.data);
                }, function errorCallback(response) {
                }).then(function () { _this.output = serverResponse; });
            };
            FSTAcceptorCtrl.$inject = ["$http"];
            return FSTAcceptorCtrl;
        })();
        angular.module("productManagement").controller("FSTAcceptorCtrl", FSTAcceptorCtrl);
    })(fst = app.fst || (app.fst = {}));
})(app || (app = {}));
