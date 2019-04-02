app.controller('userController',function($scope,$controller,userService,areaService,uploadService){

    $controller('baseController',{$scope:$scope});

    $scope.entity= {};
    $scope.password = "";

    $scope.authentication = function(){
        if($scope.entity.password!=$scope.password) {
            alert("两次密码不一致!");
            $scope.entity.password = "";
            $scope.password = null;
            return false;
        }else if(!$scope.entity.password){
            alert("密码为空");
            return false;
        }else{
            return true;
        }
    }

    $scope.register = function(){
        if($scope.authentication()){
            userService.register($scope.entity,$scope.checkCode).success(
                function(response){
                    if(response.success){
                        alert("注册成功");
                    }else{
                        alert("注册失败");
                    }
                }
            )
        }

    }

    $scope.createCheckCode = function(){
        if($scope.entity.phone){
            userService.createCheckCode($scope.entity.phone).success(
                function(response){
                    alert(response.message);
                }
            )
        }else{
            alert("手机号不能为空");
        }
    }


    //以下是修改个人信息网页使用的变量和js
    $scope.findProvinceList = function(){
        areaService.findProvinceList().success(
            function(response){
                $scope.provinceList = response;
            }
        )
        $scope.findByUsername();
    }
    $scope.$watch('user',function(newValue,oldValue){
        initSex($scope.user.sex);
    })
   

    $scope.findByUsername = function(){
        userService.findByUsername().success(
            function(response){
                $scope.user = response;
                var birthday = new Date($scope.user.birthday);
                $scope.year = birthday.getFullYear();
                $scope.month = birthday.getMonth()+1;
                $scope.date = birthday.getDate();
            }
        )
    }


    $scope.$watch('user.provinceId',function(newValue,oldValue){
        areaService.findCityListByProvinceId(newValue).success(
            function(response){
                $scope.cityList=[];
                $scope.areaList=[];
                $scope.cityList = response;
            }
        )
    })
    $scope.$watch('user.cityId',function(newValue,oldValue){
        areaService.findAreaListByCityId(newValue).success(
            function(response){
                $scope.areaList=[];
                $scope.areaList = response;
            }
        )
    })

    $scope.setInfo = function(){
       var birthDayStr = $scope.year+"/"+$scope.month+"/"+$scope.date
       $scope.user.birthDay = new Date(birthDayStr);
        userService.setInfo($scope.user).success(
            function(response){
                if(response.success){
                    alert(response.message);
                }else{
                    alert(response.message);
                }
            }
        )
    }

    $scope.uploadFile = function(){
        uploadService.uploadFile().success(
            function(response){
                if(response.success){
                    $scope.user.headPic = response.message;
                }else{
                    alert(response.message);
                }
            }
        )
    }
})