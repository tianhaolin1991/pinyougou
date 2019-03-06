app.controller('brandController', function ($controller,$scope, brandService) {

    $controller('baseController',{$scope:$scope});

    $scope.searchEntity = {};
    $scope.search = function (pageNum, pageSize) {
        brandService.search(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.list;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };


    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update;
        } else {
            object = brandService.add;
        }
        object($scope.entity).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    };

    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };


    $scope.del = function () {
        brandService.del($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(success.message);
                }
            }
        )
    };

})