//Chat Room Page Variable
var popupCreateRoomForm = document.querySelector('#popupCreateRoomForm');
var roomName = document.querySelector('#roomName');

var stompClient = null;
var roomId = null;

$(document).ready(function(){connect();});

// Function Triggered After clicking ENter the application Button
function connect() {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
}

function onConnected() {
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function createRoom(){
    var roomNameValue = roomName.value.trim(); 
	var maxpp = $('#maxpp').val();

    if(roomNameValue){
        var chatRoom = {
			roomid: roomNameValue,
			maxpp: maxpp 
        };
        stompClient.send("/chatapp/chat/rooms", {}, JSON.stringify(chatRoom));
	}
	window.close();
}

popupCreateRoomForm.addEventListener("submit", createRoom, true);