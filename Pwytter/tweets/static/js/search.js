/**
 * Created by darksnow on 2/26/16.
 */
$('#search-form').submit(function(e){
   $.post('/tweets/search/', $(this).serialize(), function(data){
       $('.tweets').html(data);
    });
    e.preventDefault();
});
