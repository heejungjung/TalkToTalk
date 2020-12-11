//User Name Page Variable
var username = document.getElementById('username').innerHTML;
var nickname = document.getElementById('nickname').innerHTML;

//Chat Room Page Variable
var roomPage = document.querySelector('#room-page');
var listOfRoom = document.querySelector('#listRoom');
var roomName = document.querySelector('#roomName');

//Chat RoomMk Page Variable
var roommkPage = document.querySelector('#roommk-page');
var createRoomForm = document.querySelector('#createRoomForm');
var createRoomName = document.querySelector('#createRoomName');

//Chat Page Variable
var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var roomIdDisplay = document.querySelector('#room-id-display');
var noticeArea = document.querySelector('#noticeArea');

//Chat Page Variable
var infoPage = document.querySelector('#info-page');

var stompClient = null;
var currentSubscription = null;
var roomId = null;
var topic = null;
var currentRoom = null;

$(document).ready(function(){connect();});

function connect() {
    if(nickname) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

//아래 두개를 위에 콜백 함수
function onConnected() {
  listRoom();
  connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = '연결에 실패했습니다! 다시 시도해주십시오. :)';
    connectingElement.style.color = 'red';
}

//Function Called from On COnnected Function
function listRoom()
{
    if (currentSubscription) {
        currentSubscription.unsubscribe();
		//그 방의 사람수도 -1
    }
    currentSubscription = stompClient.subscribe(`/chatapp/chat/rooms`, onListofRoom);
}

//search 함수
function searchlistRoom()
{
    currentSubscription = stompClient.subscribe(`/chat/search`, onListofRoom); //
}

// Result form subscribe function of listRoom is processed here
function onListofRoom(payload)
{
	document.getElementById('listRoom').innerHTML = "";
	
	var rooms = JSON.parse(payload.body);
	
	if( rooms.length == 0){
		alert("현재 개설 된 방이 없습니다." + rooms.length);
	}

     for(var i=0,len = rooms.length; i<len ; i++)
     {
        var roomElement = document.createElement('li');
        roomElement.classList.add('list-group-item');
        roomElement.setAttribute("id", i);

        var formElement = document.createElement('form');
        formElement.setAttribute("id", "joinroom");
        formElement.setAttribute("name", "joinroom");
        formElement.setAttribute("style", "display: flex;align-items: center;");

		/*방번호 나오도록*/
        var num_textElement = document.createElement('label');
        num_textElement.setAttribute("id","list_num_css");
        var num_roomText = document.createTextNode(i+1);
        num_textElement.appendChild(num_roomText);

        var textElement = document.createElement('label');
        textElement.setAttribute("style","margin-right: 5%;");
        var roomText = document.createTextNode(rooms[i].roomid);
        textElement.appendChild(roomText);

        var peopleElement = document.createElement('label');
        peopleElement.setAttribute("style","margin-right: 50px");
        var roomText = document.createTextNode(rooms[i].nowpp+"/"+rooms[i].maxpp);
        peopleElement.appendChild(roomText);
		peopleElement.setAttribute("style","width:30px; margin:0; margin-left: 20%; padding:0;");
		
		var div1 = document.createElement('div');
		var div2 = document.createElement('div');
		var div3 = document.createElement('div');
        div1.classList.add('room-page-bar-no');
        div2.classList.add('room-page-bar-title');
        div3.classList.add('room-page-bar-people');
        div1.setAttribute("style","background:none;");
        div2.setAttribute("style","background:none;");
        div3.setAttribute("style","background:none;");
        div1.appendChild(num_textElement);
        div2.appendChild(textElement);

		if(rooms[i].nowpp<rooms[i].maxpp){
	        var buttonElement = document.createElement('input');
	        buttonElement.setAttribute("type","image");
			buttonElement.setAttribute("src","/images/logo/chat.png");
			buttonElement.setAttribute("style","width:30px; margin:0; padding:0");
	        buttonElement.setAttribute("value",rooms[i].roomid);

			buttonElement.onclick=function join(event){
				if (event.type == 'click') {
					    var roomNameValue = event.target.value; //그 줄의 방제
						if(roomNameValue)
					    {
					        roomPage.classList.add('hidden');
					        chatPage.classList.remove('hidden');
					        enterRoom(roomNameValue);
					    }
					    event.preventDefault();
				}
			};
        	div2.appendChild(buttonElement);
		}

        div3.appendChild(peopleElement);
		
        formElement.appendChild(div1);
        formElement.appendChild(div2);
        formElement.appendChild(div3);

        roomElement.appendChild(formElement);

        listOfRoom.appendChild(roomElement);
        listOfRoom.scrollTop = listOfRoom.scrollHeight;
     }
}
		
function enterRoom(newRoomId) {
	var roomId = newRoomId;
	Cookies.set("roomId", roomId);
	roomIdDisplay.textContent = roomId;
	topic = `/chatapp/chat/${newRoomId}`;

	if (currentSubscription) {
		currentSubscription.unsubscribe();
	}
	
	stompClient.subscribe(`/chatapp/chat/${roomId}/getPrevious`, onPreviousMessage);
	currentSubscription = stompClient.subscribe(`/room/${roomId}`, onMessageReceived);
	currentRoom = roomId;
	
	stompClient.send(`${topic}/addUser`,
		{},
		JSON.stringify({sender: nickname, type: 'JOIN', roomid: roomId})
	);
}

function onPreviousMessage(payload)
{
    var messages = JSON.parse(payload.body).messages;
	noticeArea.textContent = JSON.parse(payload.body).notice;
	//alert("onPreviousMessage:"+messages);
    for (var i=0, len=messages.length;i<len;i++ )
    {
        showMessage(messages[i]);
    }
}

function sendMessage(event) {
  var messageContent = messageInput.value.trim();

  if (messageContent.startsWith('/join ')) {
		//꽉 찬 방 못 들어가게 어떻게 하지?????????????
		leave();
		
		setTimeout("", 3000); //3초
		
		var newRoomId = messageContent.substring('/join '.length);
		var chatRoom = {
			roomid: newRoomId
		};
	    stompClient.send("/chatapp/chat/rooms", {}, JSON.stringify(chatRoom));

		setTimeout("", 3000); //3초
		
	    enterRoom(newRoomId);
	    while (messageArea.firstChild) {
			messageArea.removeChild(messageArea.firstChild);
	    }
  }
  else if(messageContent.startsWith('/leave'))
  {
	leave();
  }

  else if (messageContent.startsWith('/notice ')){
    var trimStr = messageInput.value.substring(8);
	notice(trimStr);
  }

  else if (messageContent && stompClient) {
    
    var chatMessage = {
      senderid: username,
      sender: nickname,
      content: messageInput.value,
      type: 'CHAT'
    };
    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
  }
  	messageInput.value = '';
  	event.preventDefault();
}

function onMessageReceived(payload) {
	if(JSON.parse(payload.body).type=="NOTICE"){
    	noticeArea.textContent = JSON.parse(payload.body).content;
	}
	
	var message = JSON.parse(payload.body);
	showMessage(message);
}
function showMessage(message)
{
    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 입장하셨습니다.';

	    var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

    	messageElement.appendChild(textElement);
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 퇴장하셨습니다.';

	    var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

    	messageElement.appendChild(textElement);
    }
	else if(message.type === 'NOTICE'){
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 공지를 남겼습니다.';

        var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

        messageElement.appendChild(textElement); 
	} else {
		if(message.sender === nickname){
        	messageElement.classList.add('chat-message-group');
        	messageElement.classList.add('writer-user');
		} else{
			messageElement.classList.add('chat-message-group');
		}
        var div_avatar = document.createElement('div');
        	div_avatar.classList.add('chat-thumb');

        var figure = document.createElement('figure');
        	figure.classList.add('image');
        	figure.classList.add('is-32x32');
        var image = document.createElement('img');
			image.setAttribute('src',message.pic);
			image.setAttribute('value',message.sender);
			image.addEventListener('click', function() {
			        $.ajax({
			            type: "GET",
			            url: "/infopaging",
			            data: {
			                username: message.senderid
			            },
			            success: function (responseData) {
				            $("#ajax").remove();
				            var data = JSON.parse(responseData);
				            if(!data){
				                alert("존재하지 않는 ID입니다");
				                return false;
				            }
							$("#userinfo_nickname").html(message.sender);
							$("#userinfo_sex").html(data.sex);
							$("#userinfo_city").html(data.city);
							$("#userinfo_birthday").html(data.birthday);
							if(data.bio!=null){
								$("#userinfo_bio").html(data.bio);
							}
							else{
								$("#userinfo_bio").html('');
							}
							$("#userinfo_pic").attr("src",data.picture);
							chatPage.classList.add('hidden');
							infoPage.classList.remove('hidden');
			            }
			        })
			});

		var color = null;
		if (message.sex=='M'){color='RGB(138,153,212)';}
		else{color="RGB(229,127,229)";}
			image.setAttribute('style','border:2px solid '+color);
        figure.appendChild(image);
        div_avatar.appendChild(figure);
        messageElement.appendChild(div_avatar);

        var div_msg = document.createElement('div');
        	div_msg.classList.add('chat-messages');
        var usernameElement = document.createElement('span');
        	usernameElement.classList.add('from');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        div_msg.appendChild(usernameElement);
        messageElement.appendChild(div_msg);
		
		if(message.type === 'EMOJI'){
		    var emojiElement = document.createElement('img');
			emojiElement.setAttribute('src',message.content);
			emojiElement.setAttribute('style','width:80px;');
	        emojiElement.classList.add('message');
		    div_msg.appendChild(emojiElement);
		    messageElement.appendChild(div_msg);
		} else{
		    var textElement = document.createElement('p');
		    var messageText = document.createTextNode(message.content);
	        	textElement.classList.add('message');
		    textElement.appendChild(messageText);
		    div_msg.appendChild(textElement);
		    messageElement.appendChild(div_msg);
		}
	}
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function popup(){
    roomPage.classList.add('hidden');
    roommkPage.classList.remove('hidden');
}

function leave(){
    chatPage.classList.add('hidden');
    roomPage.classList.remove('hidden');
	
    var chatRoom = {
      sender: nickname,
      type: 'LEAVE',
      roomid: currentRoom
    };

    stompClient.send(`${topic}/leaveuser`,
		{},
		JSON.stringify(chatRoom)); 
	currentRoom = null;
    listRoom();

    while (messageArea.firstChild) {
        messageArea.removeChild(messageArea.firstChild);
    }
}

function notice(notice){
    var noticeMessage = {
		roomid: currentRoom,
        sender: nickname,
        content: notice,
        type: 'NOTICE'
    };
    stompClient.send(`${topic}/notice`, {}, JSON.stringify(noticeMessage));
    alert("공지등록:"+notice);
    noticeArea.textContent = notice;
}
function createRoom(){
    var createRoomNameValue = createRoomName.value.trim(); 
	var maxpp = $('#maxpp').val();

    if(createRoomNameValue){
        var chatRoom = {
			roomid: createRoomNameValue,
			maxpp: maxpp 
        };
        stompClient.send("/chatapp/chat/rooms", {}, JSON.stringify(chatRoom));
	}
    roommkPage.classList.add('hidden');
    roomPage.classList.remove('hidden');
}

messageForm.addEventListener('submit', sendMessage, true);
createRoomForm.addEventListener("submit", createRoom, true);
document.getElementById('createRoomButton').addEventListener('click', popup);
document.getElementById('chatoutButton').addEventListener('click', leave);
document.getElementById('roommkoutButton').addEventListener('click', function roommkout(){
	roomPage.classList.remove('hidden');
	roommkPage.classList.add('hidden');
});
document.getElementById('profileoutButton').addEventListener('click', function profileout(){
	chatPage.classList.remove('hidden');
	infoPage.classList.add('hidden');
});

//Emoji
document.getElementById('EmojiButton').addEventListener('click', function a(){
	var EmojiBox = document.querySelector('#EmojiBox');
	if(EmojiBox.classList.contains('hidden')){
		EmojiBox.classList.remove('hidden');
	} else{
		EmojiBox.classList.add('hidden');
	}
});
$(document).on('click', '.modal_close', function(event){
		EmojiBox.classList.add('hidden');
});
$(document).on('click', '.Emoji', function(event){
	var emoji = $(this).attr('value');
    var chatMessage = {
      senderid: username,
      sender: nickname,
      content: "/images/emoji/"+emoji+".png",
      type: 'EMOJI'
    };
    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
});