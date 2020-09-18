const aedes = require('aedes')();
const server = require('net').createServer(aedes.handle);
const port = 1883;

const PUBLISH_PERMISSION = 0;
const SUBSCRIBE_PERMISSION = 1;
const ALL_PERMISSION = 2;

const USERS = {
    "largetimmo": {
        "password": "password123",
        "role": "default"
    },
    "admin": {
        "password": "adminpassword",
        "role": "superadmin"
    },
    "app1": {
        "password": "pass1",
        "role": "backend-app"
    }
};
const roles = ["superadmin", "default", "backend-app"];

const clients = {};

const permissions = {
    "superadmin": [{
        "path": ".*",
        "operation": ALL_PERMISSION
    }],
    "default": [{
        "path": "^\/client\/thermometer\/degree$",
        "operation": PUBLISH_PERMISSION
    }],
    "backend-app": [{
        "path": "^\/client\/thermometer\/degree$",
        "operation": SUBSCRIBE_PERMISSION
    }, {
        "path": "^\/test\/path\/one$",
        "operation": PUBLISH_PERMISSION,
    }]
};

aedes.authenticate = function (client, un, pw, callback) {
    if (pw.toString() === USERS[un]["password"]) {
        clients[client.id] = USERS[un]["role"]
        callback(null, true)
    } else {
        console.log("Auth error occurred")
        let error = new Error('Auth error')
        error.returnCode = 4
        callback(error, null)
    }
};

aedes.authorizePublish = function (client, packet, callback) {
    let permissionForUser = permissions[clients[client.id]].filter(per => per["operation"] !== SUBSCRIBE_PERMISSION)
    if (permissionForUser.length === 0) {
        console.log(client.id + "violates the publish rule")
        return callback(new Error('permission denied'))
    }
    let pathRexs = permissionForUser.map(p => new RegExp(p["path"]))
    if (pathRexs.filter(re => re.test(packet.topic)).length === 0) {
        console.log(client.id + "violates the publish rule")
        return callback(new Error('permission denied'))
    }
    callback(null)
};

aedes.authorizeSubscribe = function (client, subscription, callback) {
    permissionForUser = permissions[clients[client.id]].filter(per => per["operation"] !== PUBLISH_PERMISSION)
    if (permissionForUser.length === 0) {
        console.log(client.id + "violates the subscribe rule")
        return callback(new Error('permission denied'))
    }
    let pathRexs = permissionForUser.map(perm => new RegExp(perm["path"]))
    if (pathRexs.filter(re => re.test(subscription.topic)).length === 0) {
        console.log(client.id + "violates the subscribe rule")
        return callback(new Error('permission denied'))
    }
    callback(null, subscription)
}

server.listen(port, function () {
    console.log('server started and listening on port ', port)
});

