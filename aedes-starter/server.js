const aedes = require('aedes')();
const server = require('net').createServer(aedes.handle);
const port = 1883;

const mysql = require('mysql2');

const PUBLISH_PERMISSION = 0;
const SUBSCRIBE_PERMISSION = 1;
const ALL_PERMISSION = 2;

const MYSQL_CONFIG = {
    host: 'localhost',
    port: 32770,
    user: 'root',
    password: 'password',
    database: 'aedes'
};

const MYSQL_CONNECTION = mysql.createConnection(MYSQL_CONFIG);

const USERS = {};
const permissions = {};


MYSQL_CONNECTION.connect(function (err) {
    if (err) throw err;
    MYSQL_CONNECTION.query('SELECT * FROM user', function (err, rows, fields) {
        for (let i = 0; i < rows.length; i++) {
            USERS[rows[i].username] = {"password": rows[i].password, "role": rows[i].role_name, "violationCount":rows[i].violation_count}
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

const clients = {};

aedes.authenticate = function (client, un, pw, callback) {
    if(USERS[un] === undefined){
        console.log("Auth error occurred");
        let error = new Error('Auth error');
        error.returnCode = 4;
        callback(error, null);
    }else{
        if(USERS[un].violationCount >=3){
            console.log("User "+ un +" attempted login with "+USERS[un].violationCount + " violation recorded");
            let error = new Error('Auth error');
            error.returnCode = 4;
            callback(error, null);
        }
        if (pw.toString() === USERS[un]["password"] && clients[client.id] === undefined) {
            clients[client.id] = {role: USERS[un]["role"], un: un};
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
    }
};

aedes.authorizePublish = function (client, packet, callback) {
    let permissionForUser = permissions[clients[client.id].role].filter(per => per["operation"] !== SUBSCRIBE_PERMISSION)
    if (permissionForUser.length === 0) {
        console.log(client.id + " violates the publish rule");
        USERS[clients[client.id].un].violationCount += 1;
        updateUserViolation(USERS[clients[client.id].un].violationCount,clients[client.id].un);
        return callback(new Error('permission denied'));
    }
    let pathRexs = permissionForUser.map(p => new RegExp(p["path"]));
    if (pathRexs.filter(re => re.test(packet.topic)).length === 0) {
        console.log(client.id + " violates the publish rule");
        updateUserViolation(new Promise((resolve)=>{
            USERS[clients[client.id].un].violationCount += 1;
            resolve(USERS[clients[client.id].un].violationCount);
        }),clients[client.id].un);
        return callback(new Error('permission denied'))
    }
    callback(null)
};

aedes.authorizeSubscribe = function (client, subscription, callback) {
    permissionForUser = permissions[clients[client.id].role].filter(per => per["operation"] !== PUBLISH_PERMISSION)
    if (permissionForUser.length === 0) {
        console.log(client.id + "violates the subscribe rule");
        updateUserViolation(USERS[clients[client.id].un].violationCount,clients[client.id].un);
        return callback(new Error('permission denied'))
    }
    let pathRexs = permissionForUser.map(perm => new RegExp(perm["path"]))
    if (pathRexs.filter(re => re.test(subscription.topic)).length === 0) {
        console.log(client.id + "violates the subscribe rule")
        updateUserViolation(USERS[clients[client.id].un].violationCount,clients[client.id].un);
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

async function updateUserViolation(updatedViolationCount, username) {
    let vc = await updatedViolationCount;
    console.log(vc);
    console.log(username);
    let connection = mysql.createConnection(MYSQL_CONFIG);
    connection.connect(function (err) {
        if (err) throw err
        connection.query('update user u set u.violation_count = ?  where u.username = ?',[vc,username],function (err) {
            connection.close();
            if (err) throw err;
        })
    })
}
