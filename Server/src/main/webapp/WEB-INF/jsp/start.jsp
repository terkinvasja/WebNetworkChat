<!DOCTYPE html>
<html>
<head>
    <title>WebSocket chat</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>Chat</title>
    <link rel="stylesheet" type="text/css" href="./resources/css/bootstrap-reboot.min.css">
    <link rel="stylesheet" type="text/css" href="./resources/css/bootstrap-grid.min.css">
    <link rel="stylesheet" type="text/css" href="./resources/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="./resources/css/style.css">
</head>
<body>
<noscript><h2 class="text-center">Enable Java script and reload this page to run Websocket Chat</h2></noscript>
<h1 class="text-center">WebSocket chat</h1>
<header>

</header>
<div id="connect-page" class="container">
    <form id="form-register">
        <div class="form-group row">
            <label for="inputNickName" class="col-sm-2 col-form-label">Nickname</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="inputNickName" placeholder="Nickname">
            </div>
        </div>
        <fieldset class="form-group">
            <div class="row">
                <legend class="col-form-label col-sm-2 pt-0">Type</legend>
                <div class="col-sm-10">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="typeClient" id="agentRadio" value="0" checked>
                        <label class="form-check-label" for="agentRadio">
                            Agent
                        </label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="typeClient" id="clientRadio" value="1">
                        <label class="form-check-label" for="clientRadio">
                            Client
                        </label>
                    </div>
                </div>
            </div>
        </fieldset>
    </form>
    <div class="form-group row">
        <div class="col-sm-10">
            <button  id="connect" onclick="connect();" class="btn btn-primary">Connect</button>
        </div>
    </div>
</div>
<div id="chat-page" class="container hidden">
    <div class="form-row">
        <div class="col">
            <button onclick="disconnect();" class="btn btn-primary">Disconnect</button>
        </div>
        <div class="col-auto">
            <p id="nickName"></p>
        </div>
    </div>
    <div class="row">
        <!-- Content here -->
        <div class="col mb-2">
            <div class="msg_container_base rounded" id="msg_container">
            </div>
        </div>
    </div>
    <form onsubmit="sendMessageText(); return false;">
    <div class="form-row">
        <div class="input-group mb-3">
            <input id="messageText" type="text" class="form-control" placeholder="Enter text"
                   aria-label="Enter text" aria-describedby="button-send">
            <div class="input-group-append">
                <button class="btn btn-outline-secondary" id="button-send" >Send</button>
            </div>
        </div>
    </div>
    </form>
</div>
<footer>

</footer>
<div>
    <div id="calculationDiv">
        <p id="calResponse"></p>
    </div>
</div>
<script type="text/javascript" src="./resources/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="./resources/js/stomp.js"></script>
<script type="text/javascript" src="./resources/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="./resources/js/bootstrap.min.js"></script>
<script type="text/javascript">

    var stompClient = null;
    var nickName = "";

    var connectPage = document.getElementById('connect-page');
    var formRegister = document.getElementById('form-register');
    var chatPage = document.getElementById('chat-page');
    var inputNickName = document.getElementById('inputNickName');
    var msgContainer = document.getElementById('msg_container');
    var messageText = document.getElementById('messageText');

    formRegister.addEventListener("submit", function (e) {
        e.preventDefault();
        connect();
    }, false);

    function setConnected(connected) {
        if (connected) {
            connectPage.classList.add("hidden");
            chatPage.classList.remove("hidden");
        } else {
            connectPage.classList.remove("hidden");
            chatPage.classList.add("hidden");
        }
        msgContainer.innerHTML = '';
    }

    function connect() {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/user/topic/showResult', function(calResult){
                showResult(JSON.parse(calResult.body).message.data);
            });
            sendMessageDataUser();
        });
    }

    function disconnect() {
        stompClient.disconnect();
        setConnected(false);
        console.log("Disconnected");
    }

    function sendMessageDataUser() {
        var messageType = "";
        nickName = inputNickName.value;
        if (document.getElementById('agentRadio').checked) {
            messageType = "ADD_AGENT";
            document.getElementById('nickName').innerHTML = "Agent: " + nickName;
        } else {
            messageType = "ADD_CLIENT";
            document.getElementById('nickName').innerHTML = "Client: " + nickName;
        }
        stompClient.send("/chatApp/add", {}, JSON.stringify(
            {'chanelId': '0', 'message':
                    {'type': messageType, 'data': '1'}} ));
    }

    function sendMessageText() {
        var msg = nickName + ": " + messageText.value;
        stompClient.send("/chatApp/send", {}, JSON.stringify(
            {'chanelId': '0', 'message':
                    {'type': 'TEXT', 'data': msg}} ));
        document.getElementById('messageText').value = "";
    }

    function showResult(message) {
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(message));
        msgContainer.appendChild(p);
    }
</script>
</body>
</html>