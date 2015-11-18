$( document ).ready(function() {
			
	 var eventBus = null; 
	 var publishingAddress = 'epms.email.in'; 
	 var subscribeAddress = 'epms.email.out'; 
	 
	 var sendMessage = function(address, messageDts) {
	     if (eventBus) {  
	    	 setLoading(true);
	    	 eventBus.send(address, messageDts); 
	     }
	 };
	 
	 var subscride = function(address) {
         if (eventBus) {
        	 console.log('Subscribed to: ' + address);  
        	 eventBus.registerHandler(address, function(msg) { 
        		 var message = JSON.parse(msg);
        		 enableForm();
        		 setLoading(false);
        		 provideExecutionResult(message);
        		 if(message.status === 'SUCCESS') { 
        			 clearFormValues();
        		 }
        		 console.log(message);
        	 }); 
         }
     }; 
	 
	 var openConnection = function() {
		 if (!eventBus) {
			 
			 if (!window.location.origin) {
				  window.location.origin = 
					  window.location.protocol + "//" + 
				  	  window.location.hostname + 
				  	  (window.location.port ? ':' + window.location.port: '');
			 } 
			  
			 eventBus = new vertx.EventBus(window.location.origin + "/eventbus");
			 eventBus.onopen = function() { 
				 console.log('Connected to server');
				 enableForm();
				 subscride(subscribeAddress);  
			 };
			 eventBus.onclose = function() {
				 console.log('Disconnected from server'); 
				 disableForm();
				 eventBus = null; 
			 };
		 }
	 };
	 
	 var provideExecutionResult = function(message) {
		 $("#status").html('');
		 $("#status").show(); 
		 $("#status").removeClass("alert-success");
		 $("#status").removeClass("alert-danger");
		 $('<p>' + message.message + '</p>').appendTo('#status'); 
		 if(message.status === 'SUCCESS') { 
			 clearFormValues();
			 $("#status").addClass("alert-success");
		 } else if (message.status === 'ERROR'){
			 $("#status").addClass("alert-danger");
			 if(!!message.stackTrace) {
				 $('<p><b>Stacktrace: </b>' + message.stackTrace + '</p>').appendTo('#status');
			 }
		 }
	 };
	   
	 var enableForm = function() {
		 $("#email").attr("disabled", false);
		 $("#subject").attr("disabled", false);
		 $("#message").attr("disabled", false);
		 $("#submitButton").attr("disabled", false);
	 }; 
	 
	 var disableForm = function() {
		 $("#email").attr("disabled", true);
		 $("#subject").attr("disabled", true);
		 $("#message").attr("disabled", true);
		 $("#submitButton").attr("disabled", true);
	 }; 
	 
	 var clearFormValues = function() {
		 $("#email").val('');
		 $("#subject").val('');
		 $("#message").val('');
	 }; 
	 
	 var setLoading = function(isLoading) {
		 if(isLoading) {
			 $("#submitButton").html('Processing your request...');
		 } else {
			 $("#submitButton").html('Send Email');
		 }
	 }
	 
	 var prepareMessageDts = function() {
		 return {
			 recipientEmail : $('#email').val(),
			 subject : $('#subject').val(),
			 body : $('#message').val()
		 }
	 };
	  
	 $("#formSendMessage").validate({
		 rules:{
	        email:{
	            required: true
	        },
	        subject:{
	            required: true,
	            maxlength: 128,
	        },
	        message:{
	            required: true
	        }
		 },
		 messages:{
		    email:{
	            required: "Email can't be empty"
	        },
	        subject:{
	            required: "Subject can't be empty",
	            maxlength: "Subject length shouldn't exceed the length of 128 characters"
	        },
	        message:{
	            required: "Message can't be empty"
	        }
		 }
     }); 
	 
	 $("#submitButton").button().click(function(e){
		 e.preventDefault();
		 $("#status").hide();
		 if($("#formSendMessage").valid()) { 
			 disableForm();
			 sendMessage(publishingAddress, prepareMessageDts());
		 } 
     });
	  
	 $("#status").hide();
	 disableForm();
	 openConnection(); 
});