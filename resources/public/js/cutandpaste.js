function push(){
  document.getElementById("my-button").style.backgroundColor='gray';
}
function release(){
  document.getElementById("my-button").style.backgroundColor='lightgray';
}
var client = new ZeroClipboard(document.getElementById("my-button")  );
client.on( 'ready', function(event) {

  client.on( 'aftercopy', function(event) {
   } );
  document.getElementById("my-button").style.display = "inline";
});
