var app;
(function (app) {
    var morph;
    (function (morph) {
        var MorphAcceptorCtrl = (function () {
            function MorphAcceptorCtrl($http) {
                var _this = this;
                this.$http = $http;
                var sampleLexicon = "";
                this.$http({
                    method: 'GET',
                    url: '/MyWebsite/api/morphacceptor/sample_lexicon'
                }).then(function successCallback(response) {
                    sampleLexicon = String(response.data);
                }, function errorCallback(response) {
                }).then(function () {
                    _this.lexicon = sampleLexicon;
                    var sampleInput = "";
                    _this.$http({
                        method: 'GET',
                        url: '/MyWebsite/api/morphacceptor/sample_input'
                    }).then(function successCallback(response) {
                        sampleInput = String(response.data);
                    }, function errorCallback(response) {
                    }).then(function () {
                        _this.input = sampleInput;
                        var sampleMorphotactics = "";
                        _this.$http({
                            method: 'GET',
                            url: '/MyWebsite/api/morphacceptor/sample_morphotactics'
                        }).then(function successCallback(response) {
                            sampleMorphotactics = String(response.data);
                        }, function errorCallback(response) {
                        }).then(function () {
                            _this.morphotactics = sampleMorphotactics;
                            _this.run();
                        });
                    });
                });
            }
            MorphAcceptorCtrl.prototype.run = function () {
                var _this = this;
                var serverResponse;
                this.$http({
                    method: 'POST',
                    url: '/MyWebsite/api/morphacceptor',
                    data: { lexicon: this.lexicon, morphotactics: this.morphotactics, input: this.input }
                }).then(function successCallback(response) {
                    serverResponse = response.data;
                }, function errorCallback(response) {
                }).then(function () {
                    _this.expandedMorphotactics = serverResponse.expandedMorphotactics;
                    _this.output = serverResponse.output;
                });
            };
            MorphAcceptorCtrl.$inject = ["$http"];
            return MorphAcceptorCtrl;
        })();
        angular.module("productManagement").controller("MorphAcceptorCtrl", MorphAcceptorCtrl);
    })(morph = app.morph || (app.morph = {}));
})(app || (app = {}));
