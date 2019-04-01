app.controller('userController',function($scope,$controller,userService){

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

})