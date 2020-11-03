//TODO:
//1. ban the client(username) with 3 violations
//2. Rename user to device on the management interface
//3. Auto generate client credentials/permissions
//4. Support # and +
//5. # only works for sub (check invalid cases)
//6 test regex special inputs
//7. Send final demo to professor and outline the final report

const aedes = require('aedes')();
const server = require('net').createServer(aedes.handle);
const port = 1883;

const mysql = require('mysql2');

const PUBLISH_PERMISSION = 0;
const SUBSCRIBE_PERMISSION = 1;
const ALL_PERMISSION = 2;

const MYSQL_CONNECTION = mysql.createConnection({
    host: 'localhost',
    port: 32770,
    user: 'root',
    password: 'password',
    database: 'aedes'
});

const USERS = {}
const permissions = {}


MYSQL_CONNECTION.connect(function (err) {
    if (err) throw err;
    MYSQL_CONNECTION.query('SELECT * FROM user', function (err, rows, fields) {
        for (let i = 0; i < rows.length; i++) {
            USERS[rows[i].username] = {"password": rows[i].password, "role": rows[i].role_name}
        }
        console.log(USERS)
    });
    MYSQL_CONNECTION.query('select role_name,availability,regex from role left join role_permissions rp on role.name = rp.role_name left join permission p on rp.permissions_id = p.id', function (err, rows, field) {
        for (let i = 0; i < rows.length; i++) {
            if (!permissions[rows[i].role_name]) {
                permissions[rows[i].role_name] = []
            }
            permissions[rows[i].role_name].push({"path": rows[i].regex, "role": rows[i].role_name, "operation":rows[i].availability})
        }
        console.log(permissions)
    });
});

const roles = ["superadmin", "default", "backend-app"];

const clients = {};

aedes.authenticate = function (client, un, pw, callback) {
    if (pw.toString() === USERS[un]["password"] && clients[client.id] === undefined) {
        clients[client.id] = USERS[un]["role"];
        callback(null, true);
    } else if (clients[client.id] !== undefined) {
        let error = new Error('Duplicated client id');
        error.returnCode = 2;
        callback(error, null);
    } else {
        console.log("Auth error occurred");
        let error = new Error('Auth error');
        error.returnCode = 4;
        callback(error, null);
    }
};

aedes.authorizePublish = function (client, packet, callback) {
    let permissionForUser = permissions[clients[client.id]].filter(per => per["operation"] !== SUBSCRIBE_PERMISSION)
    if (permissionForUser.length === 0) {
        console.log(client.id + "violates the publish rule");
        return callback(new Error('permission denied'));
    }
    let pathRexs = permissionForUser.map(p => new RegExp(p["path"]));
    if (pathRexs.filter(re => re.test(packet.topic)).length === 0) {
        console.log(client.id + "violates the publish rule");
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
};

aedes.on('clientDisconnect', function (client) {
    console.log(client.id + 'disconnected');
    delete clients[client.id]
});

server.listen(port, function () {
    console.log('server started and listening on port ', port)
});


