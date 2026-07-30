function createChatSocket(matchId, options) {
    options = options || {};
    var onConnected = options.onConnected || function () {};
    var onMessageReceived = options.onMessageReceived || function () {};
    var onError = options.onError || function () {};
    var wsBaseUrl = options.wsBaseUrl || 'http://localhost:8081';

    var state = { isConnected: false, error: null };
    var client = null;

    if (matchId === null || matchId === undefined) {
        return {
            sendMessage: function () {},
            disconnect: function () {},
            get isConnected() { return false; },
            get error() { return null; }
        };
    }

    var token = window.NEXUS_WS_TOKEN;

    if (token === null || token === undefined) {
        console.error('createChatSocket: window.NEXUS_WS_TOKEN não foi definido — a página precisa injetar o token via th:inline antes de carregar este script.');
        return {
            sendMessage: function () {},
            disconnect: function () {},
            get isConnected() { return false; },
            get error() { return 'Token de autenticação ausente.'; }
        };
    }

    client = new StompJs.Client({
        webSocketFactory: function () {
            return new SockJS(wsBaseUrl + '/ws');
        },
        connectHeaders: {
            Authorization: `Bearer ${window.NEXUS_WS_TOKEN}`
        },
        reconnectDelay: 5000,

        onConnect: function () {
            state.isConnected = true;
            state.error = null;

            client.subscribe('/topic/chat/' + matchId, function (frame) {
                var message = JSON.parse(frame.body);
                onMessageReceived(message);
            });

            client.subscribe('/user/queue/errors', function (frame) {
                var payload = JSON.parse(frame.body);
                state.error = payload.error || 'Erro desconhecido no chat.';
                onError(state.error);
            });

            onConnected();
        },

        onDisconnect: function () {
            state.isConnected = false;
        },

        onStompError: function (frame) {
            state.error = (frame.headers && frame.headers['message']) || 'Erro de protocolo STOMP.';
            onError(state.error);
        },

        onWebSocketError: function () {
            state.isConnected = false;
            state.error = 'Falha na conexão com o servidor de chat.';
            onError(state.error);
        }
    });

    client.activate();

    function sendMessage(content) {
        if (!client || !state.isConnected) return;
        client.publish({
            destination: '/app/chat/' + matchId + '/send',
            body: JSON.stringify({ content: content })
        });
    }

    function disconnect() {
        state.isConnected = false;
        if (client) client.deactivate();
    }

    window.addEventListener('beforeunload', disconnect);

    return {
        sendMessage: sendMessage,
        disconnect: disconnect,
        get isConnected() { return state.isConnected; },
        get error() { return state.error; }
    };
}
