var searchData = [];
$(document).ready(function(){

    $("#form\\:searchField").keypress(function(event){
       if(event.which==13){ //pressing enter has to be prevented to do any action, otherwise it triggered the first button on the form and added new row to the table
           event.preventDefault();
           return false;
       };
   }) ;

});

function employeeFilter(){
        $.ajax({
            url: "./acServlet",
            data: {item: "upload"},
            type: "GET",
            dataType : "json",
        })
        .done(function( json, textStatus, jqXHR ) {
            if(jqXHR.getResponseHeader("state")=="filled"){
                var i=0;
                searchData = [];
                while (item = json.employees[i]){
                    searchData.push({ label: item.label, value: item.value, desc: item.desc });
                    i++;
                }
                $("#form\\:searchField").autocomplete({
                    source:searchData,
                    delay:1000,
                    minLength:3,
                    select: function(event,ui){
                        //employeeFilterServer(ui.item.value);  Couldn't handle ConcurrentException
                        $("#form\\:searchField").val(ui.item.value);
                        $("#form\\:acGo").trigger("click");
                        offAutocomplete();
                        }

                })
                .autocomplete( "instance" )._renderItem = function( ul, item ) {
                    return $( "<li>" )
                        .append( "<div>" + item.value + " | " + "<i><small>" + item.desc + "</small></i>   </div></li>" )
                        .appendTo( ul );
                };
            }
        })
        .fail(function( xhr, status, errorThrown ) {
            /*alert( "Error: " + errorThrown );*/
        });
}

function offAutocomplete(){
  try{
    $("#form\\:searchField").autocomplete( "destroy" );
  }catch (err){}
    $("#form\\:acGo").attr("disabled",true);
}

function employeeFilterServer(val){
    $.ajax({
        url: "./acServlet",
        data: {item: "refresh", value: val},
        type: "GET",

    })
    .done(function( s ) {
        refreshFields();
    })
    .fail(function( xhr, status, errorThrown ) {
        alert( "Error: " + errorThrown );
    });
}

function preparePushBack(data){
    $("#formheader\\:pushText").val(data);
}

function prepareLogoutPushBack(data){
//   rcLogout();  //Not able to call login.logout method somehow.
window.location.replace("http://localhost:8080/HRadminJSFPF/ns/timeout.html");
}

function logOut(){
    rcLogout();
}
        


