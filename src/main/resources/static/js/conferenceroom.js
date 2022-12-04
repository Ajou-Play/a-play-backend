/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

'use strict';
var participants = {};
var userId;
var channelId;
var stompClient = null;
var accessToken;

window.onbeforeunload = function() {
	stompClient.disconnect();
};

async function register() {
	var email = document.getElementById('userEmail').value;
	var password = document.getElementById('password').value;
	var loginMessage = {
		"email" : email,
		"password" : password
	}

	await axios.post('https://www.aplay.n-e.kr/api/v1/users/local/signin', loginMessage)
		.then((response) => {
			accessToken = response.data.data.accessToken;
			userId = response.data.data.userId;
		}).catch(error => {
			console.log(error);
		});
	// 로그인 후 토큰 받아서 유저 아이디 얻고, 토큰 설정하기.

	channelId = document.getElementById('channelId').value;

	document.getElementById('room-header').innerText = 'ROOM ' + channelId;
	document.getElementById('join').style.display = 'none';
	document.getElementById('room').style.display = 'block';
	const errorCallback = function(error) {
		console.log(error);
	}
	const connectCallback = function(frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe('/sub/meeting/user/'+userId+'/existingUsers', function (message) {
			const parsedMessage = JSON.parse(message.body);
			console.info('Received message: ' + message.body);
			onExistingParticipants(parsedMessage);
		});
		stompClient.subscribe('/sub/meeting/user/'+userId+'/newUserArrived', function (message){
			const parsedMessage = JSON.parse(message.body);
			console.info('Received message: ' + message.body);
			onNewParticipant(parsedMessage);
		});
		stompClient.subscribe('/sub/meeting/user/'+userId+'/userLeft', function (message){
			const parsedMessage = JSON.parse(message.body);
			console.info('Received message: ' + message.body);
			onParticipantLeft(parsedMessage);
		});
		stompClient.subscribe('/sub/meeting/user/'+userId+'/receiveVideoAnswer', function (message){
			const parsedMessage = JSON.parse(message.body);
			console.info('Received message: ' + message.body);
			receiveVideoResponse(parsedMessage);
		});
		stompClient.subscribe('/sub/meeting/user/'+userId+'/iceCandidate', function (message){
			const parsedMessage = JSON.parse(message.body);
			console.info('Received message: ' + message.body);
			participants[parsedMessage.userId].rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
				if (error) {
					console.error("Error adding candidate: " + error);
				}
			});
		});
		var message = {
			eventType : 'joinMeeting',
			userId : userId,
			channelId : channelId,
		}
		sendMessage(message, message.eventType);
	}
	const connectHeader = {
		'accessToken' : accessToken
	};

	var socket = new SockJS('https://www.aplay.n-e.kr/api/socket/meeting');
	stompClient = Stomp.over(socket);
	await stompClient.connect(connectHeader, connectCallback, errorCallback);
}

function onNewParticipant(request) {
	receiveVideo(request.user.userId);
}

function receiveVideoResponse(result) {
	participants[result.user.userId].rtcPeer.processAnswer (result.sdpAnswer, function (error) {
		if (error) return console.error (error);
	});
}

function callResponse(message) {
	if (message.response != 'accepted') {
		console.info('Call not accepted by peer. Closing call');
		stop();
	} else {
		webRtcPeer.processAnswer(message.sdpAnswer, function (error) {
			if (error) return console.error (error);
		});
	}
}

function onExistingParticipants(msg) {
	var userIds = msg.data.map((user) => user.userId);
	var constraints = {
		audio : true,
		video : {
			mandatory : {
				maxWidth : 320,
				maxFrameRate : 15,
				minFrameRate : 15
			}
		}
	};
	console.log("userId : " + userId + " registered in room " + channelId);
	var participant = new Participant(userId);
	participants[userId] = participant;
	var video = participant.getVideoElement();

	var options = {
	      localVideo: video,
	      mediaConstraints: constraints,
	      onicecandidate: participant.onIceCandidate.bind(participant)
	    }
	participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(options,
		function (error) {
		  if(error) {
			  return console.error(error);
		  }
		  this.generateOffer (participant.offerToReceiveVideo.bind(participant));
	});

	userIds.forEach(receiveVideo);
}

function leaveRoom() {
	sendMessage({
		id : 'leaveRoom'
	});

	for ( var key in participants) {
		participants[key].dispose();
	}

	document.getElementById('join').style.display = 'block';
	document.getElementById('room').style.display = 'none';

	stompClient.disconnect();
}

function receiveVideo(sender) {
	var participant = new Participant(sender);
	participants[sender] = participant;
	var video = participant.getVideoElement();
	var options = {
      remoteVideo: video,
      onicecandidate: participant.onIceCandidate.bind(participant)
    }

	participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
			function (error) {
			  if(error) {
				  return console.error(error);
			  }
			  this.generateOffer (participant.offerToReceiveVideo.bind(participant));
	});
	console.log(video);
}

function onParticipantLeft(request) {
	let userId = request.user.userId;
	console.log('Participant ' + userId + ' left');
	var participant = participants[userId];
	participant.dispose();
	delete participants[userId];
}

function sendMessage(message, eventType) {
	var jsonMessage = JSON.stringify(message);
	console.log('Sending message: ' + jsonMessage);
	stompClient.send('/pub/meeting/'+eventType, {'accessToken' : accessToken}, jsonMessage);
}

