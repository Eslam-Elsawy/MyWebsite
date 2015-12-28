var app;
(function (app) {
    var main = angular.module("productManagement", ["common.services", "ngRoute"]);
    main.config(routeConfig);
    routeConfig.$inject = ["$routeProvider"];
    function routeConfig($routeProvider) {
        $routeProvider
            .when("/productList", {
            templateUrl: "/MyWebsite/app/products/views/productListView.html",
            controller: "ProductListCtrl as vm"
        })
            .when("/productDetail/:productId", {
            templateUrl: "/MyWebsite/app/products/views/productDetailView.html",
            controller: "ProductDetailCtrl as vm"
        })
            .when("/", {
            templateUrl: "/MyWebsite/app/products/views/homepage.html",
        })
            .when("/tokenizer", {
            templateUrl: "/MyWebsite/app/products/views/tokenizer.html",
            controller: "TokenizerCtrl as vm"
        })
            .when("/fsaacceptor", {
            templateUrl: "/MyWebsite/app/products/views/fsaacceptor.html",
            controller: "FSAAcceptorCtrl as vm"
        })
            .when("/fstacceptor", {
            templateUrl: "/MyWebsite/app/products/views/fstacceptor.html",
            controller: "FSTAcceptorCtrl as vm"
        })
            .when("/morphacceptor", {
            templateUrl: "/MyWebsite/app/products/views/morphacceptor.html",
            controller: "MorphAcceptorCtrl as vm"
        })
            .otherwise("/");
    }
})(app || (app = {}));
