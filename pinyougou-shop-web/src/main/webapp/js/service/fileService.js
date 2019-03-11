app.service('fileService',function($http){
	
	this.upload = function(){
		var formdata = new FormData();
		formdata.append('file',file.files[0]);
		return $http({
			url:'../upload.do',
			method:'post',
			data:formdata,
			headers:{'Content-type':undefined},
			transformRequest:angular.identity
		});
	}
})