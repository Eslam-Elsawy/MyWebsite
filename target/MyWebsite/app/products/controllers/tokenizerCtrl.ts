module app.tokenizer {
    interface ITokenizerModel {
        tokenize(): void;
        input: string;
        output: string;
    }

    class TokenizerCtrl implements ITokenizerModel {
        input: string;
        output: string;

        static $inject = ["$http"];
        constructor(private $http: ng.IHttpService) {
            
            // sample input
            let sampleInput = "";
            this.$http({
                method: 'GET',
                url: '/MyWebsite/api/tokenizer/sample'
            }).then(function successCallback(response) {
                sampleInput = String(response.data);
            }, function errorCallback(response) {
            }).then(() => {
                this.input = sampleInput;
                this.tokenize();
            });
        }

        tokenize(): void {

            var serverResponse;
            this.$http({
                method: 'POST',
                url: '/MyWebsite/api/tokenizer',
                data: this.input
            }).then(function successCallback(response) {

                serverResponse = String(response.data);
            }, function errorCallback(response) {

            }).then(() => { this.output = serverResponse });
        }
    }

    angular.module("productManagement").controller("TokenizerCtrl", TokenizerCtrl);
}