import {notify} from 'react-notify-toast';

var appServerAddress = "http://localhost:8100";

export const tokenHeaderName = "Authorization";

export function sendRequest(uri, method, body, callback, errorCallBack = defaultError) {
    let headers = new Headers({
        'Content-Type': 'application/json',
        'Accept-Type': 'application/json'
    });
    sendRequestWithHeader(uri, method, body, headers, callback, errorCallBack);
}

export function sendRequestWithHeader(uri, method, body, headers, callback, errorCallBack = defaultError) {
    let url = appServerAddress + uri;
    let token = getCookie(tokenHeaderName);

    if (token !== '') {
        headers.append(tokenHeaderName, token);
    }

    let initFetch;
    if (method === 'GET') {
        initFetch = {
            method: method,
            headers: headers,
            modelType: 'cors'
        };
    } else {
        initFetch = {
            method: method,
            headers: headers,
            body: body,
            modelType: 'cors'
        };
    }


    fetch(url, initFetch).then(function (response) {
        let statusCode = response.status;
        if (statusCode >= 200 && statusCode <= 299) {
            callback(response)
        } else if (statusCode >= 400 && statusCode <= 499) {
            response.json().then(function (json) {
                errorCallBack(json);
            });
        }
    });
}

export function defaultError(json) {
    let message = json.errorMessage;
    notify.show("" + message, 'error', 10000);
}

export function setCookie(cvalue) {
    const d = new Date();
    d.setTime(d.getTime() + (3 * 60 * 60 * 1000));
    let expires = "expires=" + d.toUTCString();
    document.cookie = tokenHeaderName + "=" + cvalue + ";" + expires + ";path=/";

}

export function getCookie(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}


export function getToken(callback) {
    let token = getCookie(tokenHeaderName);
    callback(token);
}

export function deleteToken() {
    let expires = "expires=" + "Thu, 01 Jan 1970 00:00:00 UTC";
    document.cookie = tokenHeaderName + "=" + ";" + expires + ";path=/";
}

export function getCurrentUser(callback) {
    if (getCookie(tokenHeaderName) !== '') {
        const uri = `/api/users/current-user`;
        sendRequest(uri, 'GET', {}, (response) => {
            response.json().then((json) => {
                callback(json);
            });

        });
    }
    callback(null);
}

export default (sendRequest);
