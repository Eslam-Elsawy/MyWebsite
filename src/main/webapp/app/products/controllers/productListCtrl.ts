module app.productList {
    interface IProductListModel {
        title: string;
        showImage: boolean;
        products: app.domain.IProduct[];
        toggleImage(): void;
    }

    class ProductListCtrl implements IProductListModel {
        title: string;
        showImage: boolean;
        products: app.domain.IProduct[];

        static $inject = ["dataAccessService"];
        constructor(private dataAccessService: app.common.DataAccessService) {
            this.title = "product list";
            this.showImage = false;
            this.products = [];
            
            var productResource = dataAccessService.getProdcutResource();
            productResource.query((data: app.domain.IProduct[]) => {
                this.products = data;
            });
        }

        toggleImage(): void {
            this.showImage = !this.showImage;
        }
    }

    angular.module("productManagement").controller("ProductListCtrl", ProductListCtrl);
}