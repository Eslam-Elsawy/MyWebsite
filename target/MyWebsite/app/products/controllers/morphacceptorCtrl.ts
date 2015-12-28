module app.morph {
    interface IMorphAcceptorModel {
        run(): void;
        lexicon: string;
        morphotactics: string;
        input: string;
        expandedMorphotactics: string;
        output: string;
    }

    class MorphAcceptorCtrl implements IMorphAcceptorModel {
        lexicon: string;
        morphotactics: string;
        input: string;
        expandedMorphotactics: string;
        output: string;

        static $inject = ["$http"];
        constructor(private $http: ng.IHttpService) {

            let sampleLexicon = "";
            this.$http({
                method: 'GET',
                url: '/MyWebsite/api/morphacceptor/sample_lexicon'
            }).then(function successCallback(response) {
                sampleLexicon = String(response.data);
            }, function errorCallback(response) {
            }).then(() => {
                this.lexicon = sampleLexicon;
                let sampleInput = "";
                this.$http({
                    method: 'GET',
                    url: '/MyWebsite/api/morphacceptor/sample_input'
                }).then(function successCallback(response) {
                    sampleInput = String(response.data);
                }, function errorCallback(response) {
                }).then(() => {
                    this.input = sampleInput;
                    let sampleMorphotactics = "";
                    this.$http({
                        method: 'GET',
                        url: '/MyWebsite/api/morphacceptor/sample_morphotactics'
                    }).then(function successCallback(response) {
                        sampleMorphotactics = String(response.data);
                    }, function errorCallback(response) {
                    }).then(() => {
                        this.morphotactics = sampleMorphotactics;
                        this.run();
                    });
                });
            });
        }

        run(): void {
            var serverResponse;
            this.$http({
                method: 'POST',
                url: '/MyWebsite/api/morphacceptor',
                data: { lexicon: this.lexicon, morphotactics: this.morphotactics, input: this.input }
            }).then(function successCallback(response) {
                serverResponse = response.data;
            }, function errorCallback(response) {
            }).then(() => {
                this.expandedMorphotactics = serverResponse.expandedMorphotactics;
                this.output = serverResponse.output;
            });
        }
    }

    angular.module("productManagement").controller("MorphAcceptorCtrl", MorphAcceptorCtrl);
}