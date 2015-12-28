module app.fst {
    interface IFSTAcceptorModel {
        run(): void;
        fst: string;
        input: string;
        output: string;
    }

    class FSTAcceptorCtrl implements IFSTAcceptorModel {
        fst: string;
        input: string;
        output: string;

        static $inject = ["$http"];
        constructor(private $http: ng.IHttpService) {
            // sample input
            let sampleFST = "";
            this.$http({
                method: 'GET',
                url: '/MyWebsite/api/fstacceptor/sample_fst'
            }).then(function successCallback(response) {
                sampleFST = String(response.data);
            }, function errorCallback(response) {
            }).then(() => {
                this.fst = sampleFST;
                let sampleInput = "";
                this.$http({
                    method: 'GET',
                    url: '/MyWebsite/api/fstacceptor/sample_input'
                }).then(function successCallback(response) {
                    sampleInput = String(response.data);
                }, function errorCallback(response) {
                }).then(() => {
                    this.input = sampleInput;
                    this.run();
                });
            });
        }

        run(): void {
            var serverResponse;
            this.$http({
                method: 'POST',
                url: '/MyWebsite/api/fstacceptor',
                data: { fst: this.fst, input: this.input }
            }).then(function successCallback(response) {
                serverResponse = String(response.data);
            }, function errorCallback(response) {
            }).then(() => { this.output = serverResponse });
        }
    }

    angular.module("productManagement").controller("FSTAcceptorCtrl", FSTAcceptorCtrl);
}