'use strict';

myApp.controller('MockController', ['$scope',  '$filter', '$modal', 'MockService', function($scope, $filter,  $modal, MockService) {
    
	var self = this;
    self.mockRequest={id:'',resource:'',url:'',method:'',type:'',operationId:'',input:'',output:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockCreateRequest= {id:'',resource:'',method:'',type:'',url:'',operationId:'',input:'',output:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockRequests=[];
    self.additionalParamKey ='';
    self.additionalParamValue ='';
    self.additionalParams ={};
    self.responseHeaderParams={};
    self.additionalParamList =[];
    self.responseHeaderParamList=[];
    self.responseHeaderParamKey ='';
    self.responseHeaderParamValue ='';
    self.mockLoadRequests=[];
    self.mockLoadRequest='';
    self.kafkaTopics =[];
    self.selectedOperationId ='';
    self.edit = edit;
    self.remove = remove;
    self.message ='';
    self.typeWarning = false;
    self.filtered = {};
    self.searchText ='';
    self.currentPage = 1;
    self.viewby = 5;
    self.perPage = 5;
    self.maxSize = 5;
	self.prettyJson = '';
	self.treeJson = '';
	self.appName = "Virtualan!!";
	getAppName();
	loadAllMockRequest();
    loadAllTopics();

    self.isNotEmpty = function() {
       return (Object.keys(self.mockLoadRequests).length > 0);
    }

    self.setPage = function (pageNo) {
    	self.currentPage = pageNo;
    };
    
     function getAppName(){	
    	MockService.readApplicationName()
            .then(
            function(d) {
            	self.appName = d.appName ;
            },
            function(errResponse){
                console.error('Error while fetching app-name');
            }
       );
    };

    var self = this;

    self.input = "Enter the Json here to get formatted";
    self.output = "You will see the Formatted Output or Error";
    self.jsonOutputObj = JSON.parse("{  \"errorCode\": \"____NOT_FOUND\",  \"errorMessage\": \" Missing?????\"}");

    self.jsonFormatter = function() {
        try {
            self.output = JSON.parse(self.input);
            self.jsonOutputObj = JSON.parse(self.input);
        } catch (throw_error) {
            self.output = throw_error.message;
        }
    }

   self.jsonObj = JSON.parse("{  \"errorCode\": \"____NOT_FOUND\",  \"errorMessage\": \" Missing?????\"}");
   self.groovyObj = "NO Data ";

    self.copyToClipboard = function(value) {
      var $temp = $("<input type='hidden'>");
      $("body").append($temp);
      $temp.val(JSON.parse(value.toString())).select();
      document.execCommand("copy");
      $temp.remove();
    };

    self.loadJson = function (value) {
      console.log(value);
      self.jsonObj = JSON.parse(value);
      self.jsonStr = JSON.parse(value.toString());
    };


    self.loadGroovy = function (value) {
      console.log(value);
      self.groovyObj = value;
    };

    $scope.$watch(self.searchText, function (term) {
      var obj = term;
      self.filterList = $filter('filter')(self.mockRequests, obj);
      self.currentPage = 1;
    }); 
    
    self.isDefined = function (value) {
    		return typeof value !== 'undefined';
    }

    
    self.addParam = function(mockRequest, key, value) {
    	if(self.isDefined(mockRequest.additionalParams)){
    		mockRequest.additionalParams[key] = value;
    	} else {
    		mockRequest.additionalParams = {};
    		mockRequest.additionalParams[key] = value;
    	}
        self.additionalParamKey ='';
        self.additionalParamValue ='';
    };
    
    self.addResponseHeaderParam = function(mockRequest, key, value) {
    	if(self.isDefined(mockRequest.responseHeaderParams)){
    		mockRequest.responseHeaderParams[key] = value;
    	} else {
    		mockRequest.responseHeaderParams = {};
    		mockRequest.responseHeaderParams[key] = value;
    	}
        self.responseHeaderParamKey ='';
        self.responseHeaderParamValue ='';
    };
    
    self.removeParam = function(item, params) {
    	delete params[item];
    };
    
    self.setItemsPerPage = function(num) {
	    self.perPage = num;
	    self.currentPage = 1; //reset to first page
    };
        
    self.loadData = fetchAllMockRequest();
    	  
    function fetchAllMockRequest(){
    	MockService.fetchAllMockRequest()
            .then(
            function(d) {
                self.mockRequests = d;
                self.filterList = self.mockRequests;
            },
            function(errResponse){
                console.error('Error while fetching Mocks');
            }
        );
    };
    
    
    function loadAllMockRequest(){
        MockService.loadAllMockRequest()
            .then(
            function(d) {
            	self.mockLoadRequests = d;
           	 console.log("ALL API's", Object.keys(self.mockLoadRequests).length);
            },
            function(errResponse){
                console.error('Error while fetching mockLoadRequests');
            }
        );
    }


    function loadAllTopics(){
            MockService.loadAllTopics()
                .then(
                function(d) {
                	self.kafkaTopics = d;
               	 console.log("ALL kafka's ", self.kafkaTopics.length);
                },
                function(errResponse){
                    console.error('Error while fetching kafkaTopics');
                }
            );
        }

	self.showDialog = false;
	
	self.showAlert = function(operationId) {
		if(operationId === self.selectedOperationId){
			self.closeAlertDialog = true;
			return true;
		}
		return false;
	}
	
	self.typeDialog = function() { 
		if(self.typeWarning) {
			return "warning";
		} 
			return "success";
	}
	
	self.closeAlert = function(operationId) {
		self.typeWarning = false;
		self.showDialog = false;
	}
	
	
	
	
	self.showMessage = function(operationId) {
		if(operationId === self.selectedOperationId){
			return self.message;
		}
		return;
	}
    
    function createMockRequest(mockRequest){
		self.showDialog = false;
    	self.typeWarning = false;
    	self.selectedOperationId = mockRequest.operationId;
		MockService.createMockRequest(mockRequest)
            .then(
            		fetchAllMockRequest,
            function(errResponse){
                self.showDialog = true;
            	self.typeWarning = true;
            	self.message = errResponse.data.code;
                console.error('Error while creating MockRequest');
            }
        );
    	self.showDialog = true;
    	self.message = 'Mock response added successfully for the given request parameter(s)!!!!';
    }

 function createMockMsgRequest(mockRequest){
		self.showDialog = false;
    	self.typeWarning = false;
    	self.selectedOperationId = mockRequest.operationId;
		MockService.createMockMsgRequest(mockRequest)
            .then(
            		fetchAllMockRequest,
            function(errResponse){
                self.showDialog = true;
            	self.typeWarning = true;
            	self.message = errResponse.data.code;
                console.error('Error while creating createMockMsgRequest');
            }
        );
    	self.showDialog = true;
    	self.message = 'Message Mock response added successfully for the given request parameter(s)!!!!';
    }


    function updateMockRequest(mockRequest, id){
        MockService.updateMockRequest(mockRequest, id)
            .then(
            		fetchAllMockRequest,
            function(errResponse){
                console.error('Error while updating MockRequest');
            }
        );
    }

    function deleteMockRequest(id){
        MockService.deleteMockRequest(id)
            .then(
            		fetchAllMockRequest,
            function(errResponse){
                console.error('Error while deleting MockRequest');
            }
        );
    }

    self.mergeParams = function (mockRequest){
    	
    	self.availableParamsWithAdditionalParam = [];
    	
    	angular.forEach(mockRequest.availableParams, function(data){
    		self.availableParamsWithAdditionalParam.push(data);
    	});
    	if(self.isDefined(mockRequest.additionalParams)) {
    		self.moveParams(mockRequest.additionalParams, self.availableParamsWithAdditionalParam);
    	}
    	return self.availableParamsWithAdditionalParam;
    }

    self.moveParams = function (paramMap, params){
    	if(self.isDefined(paramMap)) {
		    Object.keys(paramMap).forEach(function(key) {
			    self.value = paramMap[key];
			    params.push(self.addParamObj(key, self.value))
			});
    	}
    }
    
    
	
    self.addParamObj = function(key, value) {
    	self.additionalParam={};
        self.additionalParam['key'] =  key;
    	self.additionalParam['value'] =  value;
		return self.additionalParam;
    }
    
    self.addResParam = function(key, value) {
    	self.responseHeaderParam = {};
	    self.responseHeaderParam['key'] =  key;
		self.responseHeaderParam['value'] =  value;
		return self.responseHeaderParam;
    }
    
    self.submit= function (mockRequest) {
        console.log('Saving New mockRequest', mockRequest);
        self.responseHeaderParamList = [];
        self.moveParams(mockRequest.responseHeaderParams, self.responseHeaderParamList)
        self.mockCreateRequest= {id:'', 
        			resource:mockRequest.resource,
        			url:mockRequest.url,
        			type:mockRequest.type,
                    rule:mockRequest.rule,
                    operationId:mockRequest.operationId,
        			input:mockRequest.input,
        			output:mockRequest.output,
        			excludeList:mockRequest.excludeList, 
        			httpStatusCode:mockRequest.httpStatusCode,
        			method:mockRequest.method,
        			availableParams:self.mergeParams(mockRequest),
        			headerParams:self.responseHeaderParamList};
        console.log('Saving New mockRequest', self.mockCreateRequest);
        createMockRequest(self.mockCreateRequest);

    }


    self.submitMessage= function (mockRequest) {
            console.log('Saving New mock message Request', mockRequest);
            self.responseHeaderParamList = [];
            self.moveParams(mockRequest.responseHeaderParams, self.responseHeaderParamList)
            self.mockCreateRequest= {id:'',
            			resource:mockRequest.resource,
            			url:mockRequest.url,
            			operationId:mockRequest.operationId,
            			input:mockRequest.input,
            			output:mockRequest.output,
            			excludeList:mockRequest.excludeList,
            			method:mockRequest.method,
            			availableParams:self.mergeParams(mockRequest),
            			headerParams:self.responseHeaderParamList};
            console.log('Saving New mock message Request', self.mockCreateRequest);
            createMockMsgRequest(self.mockCreateRequest);

        }

    function edit(id){
        console.log('id to be edited', id);
        for(var i = 0; i < self.mockRequests.length; i++){
            if(self.mockRequests[i].id === id) {
                self.mockRequest = angular.copy(self.mockRequests[i]);
                break;
            }
        }
    }

    function remove(id){
        console.log('id to be deleted', id);
        if(self.mockRequest.id === id) {//clean form if the mockRequest to be deleted is shown there.
            reset();
        }
        deleteMockRequest(id);
    }


    self.reset = function (myForm, mockRequest){
        self.message ='';
        self.classCode = '';
        mockRequest.id = null;
        mockRequest.output= null;
        mockRequest.excludeList = null;
        mockRequest.httpStatusCode ='';
        self.type = null;
        for (var parm in mockRequest.availableParams) {
        	mockRequest.availableParams[parm].value =  '';
        }
        if(mockRequest.input != null) {
        	mockRequest.input = '';
    	}
        myForm.$setPristine(); //reset Form
    }

}]);

