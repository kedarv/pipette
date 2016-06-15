var socket = io.connect();
// socket.on('updateChatData', function (dt) { messageList.updateChatData.apply(messageList, [dt])});

var allChatDataFull = [];
var allChatData = [];

socket.emit('getAllChat', function (ar, all) {
	allChatData = ar;
	allChatDataFull = all;
	console.log(allChatData);
	// console.log(allChatDataFull);
});

socket.emit('getChat', 2, callback);
function callback(test) {
	console.log(test);
}