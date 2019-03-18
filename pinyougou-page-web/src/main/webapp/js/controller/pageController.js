app.controller('pageController',function($scope){

    $scope.itemNum = 1;
    $scope.specification = {};
    $scope.item = itemList[0];

    //动态增减商品
    $scope.addItemNum = function(){
        $scope.itemNum = $scope.itemNum + 1;
    }
    $scope.minusItemNum = function(){
        if($scope.itemNum>1){
            $scope.itemNum = $scope.itemNum - 1;
        }
    }

    //动态选择商品
    $scope.updateSpecification = function(attributeName,attributeValue){
        $scope.specification[attributeName] = attributeValue;
        findCurrentSKU();
    }

    $scope.isSelected = function(name,value){
        if($scope.specification[name]==value){
            return true;
        }
        return false;
    }

    isEqual = function(map1,map2){
        for(key in map1){
            if(map1[key] != map2[key]){
                return false;
            }
        }
        for(key in map2){
            if(map2[key]!=map1[key]){
                return false;
            }
        }
        return true;
    }

    findCurrentSKU = function(){
        for(var i = 0; i< itemList.length;i++){
            if(isEqual(itemList[i].spec,$scope.specification)){
                $scope.item = itemList[i];
                return;
            }
        }
        return $scope.item={id:0,title:'--------',price:0};//如果没有匹配的	
    }

    $scope.load = function(){
        $scope.specification = JSON.parse(JSON.stringify($scope.item.spec));
    }

    $scope.addToCart = function(){
        alert('item.id:'+ $scope.item.id);
    }
})