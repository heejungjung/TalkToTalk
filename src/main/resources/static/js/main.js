//User Name Page Variable
var username = document.getElementById('username').innerText;
var nickname = document.getElementById('nickname').innerText;

//Chat Room Page Variable
var roomPage = document.querySelector('#room-page');
var listOfRoom = document.querySelector('#listRoom');
var roomName = document.querySelector('#roomName');

//Chat RoomMk Page Variable
var roommkPage = document.querySelector('#roommk-page');
var createRoomForm = document.querySelector('#createRoomForm');
var createRoomName = document.querySelector('#createRoomName');
var createRoomType = document.querySelector('#createRoomType');
var createRoomHash = document.querySelector('#createRoomhash');

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

// Result form subscribe function of listRoom is processed here
function onListofRoom(payload)
{
	document.getElementById('listRoom').innerText = "";
	var rooms = JSON.parse(payload.body);
	
	if(rooms.length == 0){
		alert("현재 개설 된 방이 없습니다.😭💔");
	}else{
		for(var i=0,len = rooms.length; i<len ; i++){
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
	        textElement.classList.add('float-right');
	        //textElement.setAttribute("style","margin-right: 5%;");
	        var roomText = document.createTextNode(rooms[i].roomid);
	        textElement.appendChild(roomText);
	
	        var peopleElement = document.createElement('label');
	        peopleElement.setAttribute("style","margin-right: 50px");
	        var roomText = document.createTextNode(rooms[i].nowpp+"/"+rooms[i].maxpp);
	        peopleElement.appendChild(roomText);
	        peopleElement.classList.add('float-right');
			peopleElement.setAttribute("style","margin-left:20%;");
	
	        var typeElement = document.createElement('input');
	        typeElement.setAttribute("type","button");
	        typeElement.classList.add('float-right');
	        typeElement.setAttribute("id",rooms[i].roomid);
	        typeElement.setAttribute("name",rooms[i].nowpp<rooms[i].maxpp);
	        typeElement.setAttribute("value",rooms[i].roomtype=='v'?'📺':'💬');
	        typeElement.setAttribute("style","background:none;border: none;");
			typeElement.onclick=function join(event){
					event.preventDefault();
					if (event.type == 'click'&event.target.name=="true"){
						var roomNameValue = event.target.id; //그 줄의 방제
						if(event.target.value=='💬'){
					        roomPage.classList.add('hidden');
					        chatPage.classList.remove('hidden');
					        enterRoom(roomNameValue);
						}else{
							location.href="https://192.168.10.146:3000/?a="+roomNameValue+"&b="+nickname;
						}
					}else{
						alert("방이 가득 찼습니다.😭💔");
					}
			};
			
	        formElement.appendChild(num_textElement);
	        formElement.appendChild(textElement);
	        formElement.appendChild(peopleElement);
	        formElement.appendChild(typeElement);
	
	        roomElement.appendChild(formElement);
	
	        listOfRoom.appendChild(roomElement);
	        listOfRoom.scrollTop = listOfRoom.scrollHeight;
		}
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
		JSON.stringify({sender: nickname, messageType: 'JOIN', roomid: roomId})
	);
}

function onPreviousMessage(payload)
{
	/*
    var messages = JSON.parse(payload.body).messages;
	noticeArea.textContent = JSON.parse(payload.body).notice;
    for (var i=0, len=messages.length;i<len;i++ )
    {
        showMessage(messages[i]);
    }*/
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
      messageType: 'CHAT'
    };
    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
  }
  	messageInput.value = '';
  	event.preventDefault();
}

function onMessageReceived(payload) {
	if(JSON.parse(payload.body).messageType=="NOTICE"){
    	noticeArea.textContent = JSON.parse(payload.body).content;
	}
	
	var message = JSON.parse(payload.body);
	showMessage(message);
}
function showMessage(message)
{
    var messageElement = document.createElement('li');

    if(message.messageType === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 입장하셨습니다.';

	    var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

    	messageElement.appendChild(textElement);
    } else if (message.messageType === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 퇴장하셨습니다.';

	    var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

    	messageElement.appendChild(textElement);
    }
	else if(message.messageType === 'NOTICE'){
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
				                alert("존재하지 않는 ID입니다😿");
				                return false;
				            }
							$("#userinfo_nickname").html(message.sender);
							if(data.sex=="F"){
								$("#userinfo_sex").html("🧚‍♀");
							}
							else{
								$("#userinfo_sex").html("🧚‍♂");
							}
							$("#userinfo_city").html(data.city);
							$("#userinfo_birthday").html(data.birthday);
							if(data.bio!=null){
								$("#userinfo_bio").html(data.bio);
							}
							else{
								$("#userinfo_bio").html('');
							}
							$("#userinfo_pic").attr("src",data.picture);
							$("#userinfo_pic_a").attr("href",data.picture);
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
		
		if(message.messageType === 'EMOJI'){
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
		    var timeElement = document.createElement('span');
			timeElement.setAttribute('style','position: absolute;left: 0;bottom: -15px;color: rgba(255,255,255,0.5);font-size: 10px;');
		    textElement.appendChild(messageText);
		    textElement.appendChild(timeElement);
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
      roomtype: 'LEAVE',
      roomid: currentRoom
    };

    stompClient.send(`${topic}/leaveuser`,
		{},
		JSON.stringify(chatRoom)); 
	currentRoom = null;
    listRoom();
	document.getElementById('searchbtn').click(); 

    while (messageArea.firstChild) {
        messageArea.removeChild(messageArea.firstChild);
    }
}

function notice(notice){
    var noticeMessage = {
		roomid: currentRoom,
        sender: nickname,
        content: notice,
        messageType: 'NOTICE'
    };
    stompClient.send(`${topic}/notice`, {}, JSON.stringify(noticeMessage));
    alert("공지 등록 완료📢");
    noticeArea.textContent = notice;
}

function createRoom(){
    var createRoomNameValue = createRoomName.value.trim();
	var maxpp = $('#maxpp').val();
	if(createRoomType.checked){
    	var createRoomTypeValue = 'v';
	}else{
    	var createRoomTypeValue = 'c';
	}
    if(createRoomNameValue){
        var chatRoom = {
			roomid: createRoomNameValue,
			roomtype: createRoomTypeValue,
			maxpp: maxpp,
			hash: createRoomHash.value.trim()
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
document.getElementById('refrestbtn').addEventListener('click', function refresh(){
	document.getElementById('searchbtn').click(); 
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
$(document).on('click', '.modal_close', function(){
		EmojiBox.classList.add('hidden');
});
$(document).on('click', '.Emoji', function(){
	var emoji = $(this).attr('value');
    var chatMessage = {
		senderid: username,
		sender: nickname,
		content: "/images/emoji/"+emoji+".png",
		messageType: "EMOJI"
    };
    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
});