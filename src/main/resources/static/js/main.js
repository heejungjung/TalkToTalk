//User Name Variable
var username = document.getElementById('username').innerText;
var nickname = document.getElementById('nickname').innerText;

//Chat Room Page Variable
var roomPage = document.querySelector('#room-page');
var listOfRoom = document.querySelector('#listRoom');

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
var infoPage = document.querySelector('#info-page');

var stompClient = null;
var currentSubscription = null;
var roomId = null;
var topic = null;
var currentRoom = null;

$(document).ready(function(){connect();});

//연결
function connect() {
    if(nickname) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
}

//연결됨
function onConnected() {
  listRoom();
  connectingElement.classList.add('hidden');
}
//연결 오류
function onError(error) {
    connectingElement.textContent = '연결에 실패했습니다! 다시 시도해주십시오. :)';
    connectingElement.style.color = 'red';
}

//연결되면 리스트 부르기
function listRoom()
{
    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }
    currentSubscription = stompClient.subscribe(`/chatapp/chat/rooms`, onListofRoom);
}

//방 리스트
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
	        formElement.setAttribute("style", "display: flex;align-items: center;margin: 0;");
	
			//방 번호
	        var num_textElement = document.createElement('label');
	        num_textElement.setAttribute("id","list_num_css");
	        num_textElement.setAttribute("style", "width: 13%;text-align: center;");
	        var num_roomText = document.createTextNode(i+1);
	        num_textElement.appendChild(num_roomText);
	
			//방 이름
	        var textElement = document.createElement('label');
	        textElement.classList.add('float-right');
	        textElement.setAttribute("style", "width: 60%;text-align: center;");
	        var roomText = document.createTextNode(rooms[i].roomid);
	        textElement.appendChild(roomText);
	
			//방 타입에 따라 입장 버튼 모양
	        var typeElement = document.createElement('input');
	        typeElement.setAttribute("type","button");
	        typeElement.setAttribute("id",rooms[i].roomid);
	        typeElement.setAttribute("name",rooms[i].nowpp<rooms[i].maxpp);
	        typeElement.setAttribute("value",rooms[i].roomtype=='v'?'📺':'💬');
	        typeElement.setAttribute("style","background:none;border: none;width: 10%;text-align: right;");
			typeElement.onclick=function join(event){
					event.preventDefault();
					if (event.type == 'click'&event.target.name=="true"){
						var roomNameValue = event.target.id; //그 줄의 방제
						if(event.target.value=='💬'){
					        roomPage.classList.add('hidden');
					        chatPage.classList.remove('hidden');
					        enterRoom(roomNameValue);
						}else{
					        $.ajax({
					            type: "GET",
					            url: "/ipget",
					            data: {
					    	    	hi: "hi"
					            },
					            success: function (responseData) {
						            var data = JSON.parse(responseData);
									//location.href="https://"+data.ip+":3000/?a="+roomNameValue+"&b="+nickname;
									location.href="https://talktotalk-video.herokuapp.com/?a="+roomNameValue+"&b="+nickname;
					            },
					    		error : function(err) {
					    			alert("실패");
					    		}
					        })
						}
					}else{
						alert("방이 가득 찼습니다.😭💔");
					}
			};
	
			//방 인원
	        var peopleElement = document.createElement('label');
	        var roomText = document.createTextNode("("+rooms[i].nowpp+"/"+rooms[i].maxpp+")");
	        peopleElement.appendChild(roomText);
			
	        formElement.appendChild(num_textElement);
	        formElement.appendChild(textElement);
	        formElement.appendChild(typeElement);
	        formElement.appendChild(peopleElement);
	
	        roomElement.appendChild(formElement);
	
	        listOfRoom.appendChild(roomElement);
	        listOfRoom.scrollTop = listOfRoom.scrollHeight;
		}
	}
}

//방 입장
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

//이전 메시지 수신
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

//메시지 전송
function sendMessage(event) {
  var messageContent = messageInput.value.trim();

  //입장 메시지 일 때
  if (messageContent.startsWith('/join ')) {
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
  //퇴장 메시지 일 때
  else if(messageContent.startsWith('/leave'))
  {
	leave();
  }

  //공지 메시지 일 때
  else if (messageContent.startsWith('/notice ')){
    var trimStr = messageInput.value.substring(8);
	notice(trimStr);
  }

  //일반 메시지 일 때
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

//메시지 받았을 때
function onMessageReceived(payload) {
	if(JSON.parse(payload.body).messageType=="NOTICE"){
    	noticeArea.textContent = JSON.parse(payload.body).content;
	}
	
	var message = JSON.parse(payload.body);
	showMessage(message);
}

//받은 메시지 띄우기
function showMessage(message)
{
    var messageElement = document.createElement('li');

	//입장 메시지
    if(message.messageType === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 입장하셨습니다.';

	    var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

    	messageElement.appendChild(textElement);
	//퇴장 메시지
    } else if (message.messageType === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 퇴장하셨습니다.';

	    var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

    	messageElement.appendChild(textElement);
    }
	//공지 메시지
	else if(message.messageType === 'NOTICE'){
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 공지를 남겼습니다.';

        var textElement = document.createElement('p');
	    var messageText = document.createTextNode(message.content);
	    textElement.appendChild(messageText);

        messageElement.appendChild(textElement); 
	//일반 메시지
	} else {
		//내가 보낸 메시지라면,
		if(message.sender === nickname){
        	messageElement.classList.add('chat-message-group');
        	messageElement.classList.add('writer-user');
		//내가 아닌 다른 사람이 보낸 메시지라면,
		} else{
			messageElement.classList.add('chat-message-group');
		}
        var div_avatar = document.createElement('div');
        	div_avatar.classList.add('chat-thumb');

		//메시지 보낸 유저의 프로필 사진
        var figure = document.createElement('figure');
        	figure.classList.add('image');
        	figure.classList.add('is-32x32');
        var image = document.createElement('img');
			image.setAttribute('src',message.pic);
			image.setAttribute('value',message.sender);
			image.addEventListener('click', function() { //메시지 보낸 사람 프로필 사진 클릭하면 유저 정보 페이지 띄움
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

		//성별에 따라 테두리 색상 설정(남-블루,여-핑크)
		var color = null;
		if (message.sex=='M'){color='RGB(138,153,212)';}
		else{color="RGB(229,127,229)";}
			image.setAttribute('style','border:2px solid '+color);
        figure.appendChild(image);
        div_avatar.appendChild(figure);
        messageElement.appendChild(div_avatar);

		//채팅 메시지
        var div_msg = document.createElement('div');
        	div_msg.classList.add('chat-messages');
        var usernameElement = document.createElement('span');
        	usernameElement.classList.add('from');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        div_msg.appendChild(usernameElement);
        messageElement.appendChild(div_msg);
		
		//보낸 메시지가 이모티콘이라면,
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

//방 만들기 창 띄우기
function roommk(){
    roomPage.classList.add('hidden');
    roommkPage.classList.remove('hidden');
}

//방 나가기
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

//공지 띄우기
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

//방 만들기
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
document.getElementById('createRoomButton').addEventListener('click', roommk);
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

//이모티콘
document.getElementById('EmojiButton').addEventListener('click', function a(){
	var EmojiBox = document.getElementById('EmojiBox');
	if(EmojiBox.classList.contains('hidden')){
		EmojiBox.classList.remove('hidden');
	} else{
		EmojiBox.classList.add('hidden');
	}
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