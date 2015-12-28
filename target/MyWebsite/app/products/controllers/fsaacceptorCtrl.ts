module app.fsa {
    interface IFSAAcceptorModel {
        run(): void;
        fsa: string;
        input: string;
        output: string;
    }

    class FSAAcceptorCtrl implements IFSAAcceptorModel {
        fsa: string;
        input: string;
        output: string;

        static $inject = ["$http"];
        constructor(private $http: ng.IHttpService) {
            // sample input
            let sampleFSA = "";
            this.$http({
                method: 'GET',
                url: '/MyWebsite/api/fsaacceptor/sample_fsa'
            }).then(function successCallback(response) {
                sampleFSA = String(response.data);
            }, function errorCallback(response) {
            }).then(() => {
                this.fsa = sampleFSA;
                let sampleInput = "";
                this.$http({
                    method: 'GET',
                    url: '/MyWebsite/api/fsaacceptor/sample_input'
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
                url: '/MyWebsite/api/fsaacceptor',
                data: { fsa: this.fsa, input: this.input }
            }).then(function successCallback(response) {
                serverResponse = String(response.data);
            }, function errorCallback(response) {
            }).then(() => { this.output = serverResponse });
        }
    }

    angular.module("productManagement").controller("FSAAcceptorCtrl", FSAAcceptorCtrl);
}