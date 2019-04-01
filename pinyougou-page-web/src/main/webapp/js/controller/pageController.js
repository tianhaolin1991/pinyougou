app.controller('pageController',function($scope,$http){

    $scope.spec = {};
    $scope.initSpec = function(){
        if(itemList.length>0){
            $scope.item = itemList[0];
            $scope.spec = JSON.parse(JSON.stringify($scope.item.spec));
        }
    }
    $scope.selectSpec = function (name,value){
        $scope.spec[name] = value;
    }

    $scope.isSelected = function(name,value){
        if($scope.spec[name]==value){
            return true;
        }else{
            return false;
        }
    }

    $scope.updateSpecification=function(name,value){
        $scope.spec[name]=value;
        $scope.searchItem();//读取sku
    }

    $scope.num = 1;
    $scope.updateNum = function(value){
        if(value==-1&&$scope.num<=0){
            $scope.num = $scope.num;
        }else{
            $scope.num += value;
        }
    }

    //判断选择的item
    $scope.searchItem = function(){
        for(i=0;i<itemList.length;i++){
            if(matchObject(itemList[i].spec,$scope.spec)){
                $scope.item = itemList[i];
                return;
            }
        }
        $scope.item={id:0,title:'--------',price:0};//如果没有匹配的
    }

    matchObject=function(map1,map2){
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    }

    $scope.addToCart = function(){
        $http.get('http://localhost:9107/cart/addItemToCartList.do?itemId='+$scope.item.id+'&num='+$scope.num,{'withCredentials':true})
            .success(
                function(response){
                    if(!response.success){
                        alert(response.message);
                    }
                }
            )
    }

})