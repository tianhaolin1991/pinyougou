app.controller('contentController',function($scope,$controller,contentService){
    $controller('baseController',{$scope:$scope});//继承
    //变量
    $scope.contentCategoryList = [];//categoryList数组,每一个元素都对应一个数组

    //方法函数
    //1.根据categoryID查询contentList
    $scope.findByCategoryId = function(categoryId){
        contentService.findByCategoryId(categoryId).success(
            function(response){
                $scope.contentCategoryList[categoryId] = response;
            }
        )
    }
})