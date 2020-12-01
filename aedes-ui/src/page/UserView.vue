<template>
    <div>
        <a-row type="flex" justify="end">
            <a-button type="primary" @click="showAddUser" style="margin-right: 10px">Add Client</a-button>
            <a-button type="primary" @click="showAddUserBulk">Add Clients Bulk</a-button>
        </a-row>
        <a-table :columns="cols" :data-source="users" :rowKey="(record)=>record.username">
            <span slot="action" slot-scope="record">
                <a @click="showChangePassword(record.username)">Change password</a>
                <a-divider type="vertical"/>
                <a @click="showChangeRole(record.username)"> Change role </a>
                <a-divider type="vertical"/>
                <a @click="showDeleteUser(record.username)">Delete</a>
            </span>
        </a-table>
        <a-modal title="Change password"
                 :visible="changePasswordVisible"
                 @ok="handleChangePassword"
                 @cancel="()=>{this.newPassword='';this.changePasswordVisible=false}">
            <div>New password for <span style="font-weight:bold ">{{targetUserUsername}}</span> :
                <a-input v-model="newPassword" type="password" autocomplete="false"/>
            </div>
        </a-modal>

        <a-modal title="Change role"
                 :visible="changeRoleVisible"
                 @ok="handleChangeRole"
                 @cancel="()=>{this.changeRoleVisible=false}">
            <div>New role for <span style="font-weight:bold ">{{targetUserUsername}}</span> :
                <a-dropdown>
                    <a-button>{{targetUserRole}}
                        <a-divider type="vertical"/>
                        <a-icon type="down"/>
                    </a-button>
                    <a-menu slot="overlay">
                        <a-menu-item v-for="(role) in roles" @click="updateRole(role.name)">{{role.name}}</a-menu-item>
                    </a-menu>
                </a-dropdown>
            </div>
        </a-modal>

        <a-modal title="Delete user confirmation"
                 :visible="deleteUserVisible"
                 @ok="handleDeleteUser"
                 @cancel="()=>deleteUserVisible=false">
            <div>
                Confirm you want to delete user
                <span style="font-weight:bold ">{{targetUserUsername}}</span>
            </div>
        </a-modal>
        <a-modal title="Add client bulk" :visible="addUserBulkVisible" @ok="handleAddUserBulk"
                 @cancel="addUserBulkVisible=false">
            <div>
                <a-input v-model="userBulkGenerate.count" placeholder="Number of clients to be generated"/>
                <a-row>
                    <a-dropdown>
                        <a-button>{{userBulkGenerate.role}}
                            <a-divider type="vertical"/>
                            <a-icon type="down"/>

                        </a-button>
                        <a-menu slot="overlay">
                            <a-menu-item v-for="(role) in roles" @click="()=>userBulkGenerate.role = role.name">
                                {{role.name}}
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </a-row>
            </div>
        </a-modal>
        <a-modal title="Add client"
                 :visible="addUserVisible"
                 @ok="handleAddUser"
                 @cancel="()=>addUserVisible=false">
            <div>
                <a-input v-model="createdNewUser.username" placeholder="username"/>
                <a-input v-model="createdNewUser.password" placeholder="password"/>
                <a-row>
                    <a-dropdown>
                        <a-button>{{createdNewUser.role}}
                            <a-divider type="vertical"/>
                            <a-icon type="down"/>
                        </a-button>
                        <a-menu slot="overlay">
                            <a-menu-item v-for="(role) in roles" @click="()=>createdNewUser.role = role.name">
                                {{role.name}}
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </a-row>
            </div>
        </a-modal>
    </div>

</template>

<script>
    export default {
        name: "UserView",
        data() {
            return {
                cols: [
                    {
                        dataIndex: 'username',
                        key: 'username',
                        title: 'Username'
                    },
                    {
                        title: 'Password',
                        dataIndex: 'password',
                        key: 'password'
                    },
                    {
                        title: 'Role',
                        dataIndex: 'role',
                        key: 'role'
                    },
                    {
                        title: 'Violation Count',
                        key: 'violationCount',
                        dataIndex: 'violationCount'
                    },
                    {
                        title: 'Action',
                        key: 'action',
                        scopedSlots: {customRender: 'action'},
                    }
                ],
                users: [
                    {
                        username: "abc",
                        password: "qwe",
                        role: "123",
                        violationCount: 0
                    }
                ],
                changePasswordVisible: false,
                changeRoleVisible: false,
                deleteUserVisible: false,
                addUserVisible: false,
                addUserBulkVisible: false,
                targetUserUsername: '',
                passwordChangeUsername: "",
                newPassword: "",
                roles: [],
                targetUserRole: '',
                createdNewUser: {
                    username: '',
                    password: '',
                    role: ''
                },
                userBulkGenerate: {
                    count: 0,
                    role: ''
                }
            }
        },
        methods: {
            loadUsers() {
                this.$axios.get('/api/users').then((response) => {
                    this.users = response.data
                })
            },
            loadRoles() {
                this.$axios.get('/api/roles').then((response) => {
                    this.roles = response.data
                })
            },
            updateRole(newRoleName) {
                this.targetUserRole = newRoleName;
                this.$axios.put('/api/user/role', {
                    username: this.targetUserUsername,
                    role: this.targetUserRole
                }).then((res) => {
                    //notification
                    console.log("update success");
                })
                    .catch((err) => {
                        console.log(err);
                        //notification
                    })
            },
            handleChangePassword(e) {
                console.log(e)
                this.$axios.put('/api/user/' + this.targetUserUsername + '/password', {
                    password: this.newPassword
                }).then((response) => {
                    console.log("success")
                }).catch((err) => {
                    console.log(err)
                });
            },
            handleChangeRole(e) {
                this.loadUsers();
                this.changeRoleVisible = false;
            },
            handleDeleteUser(e) {
                this.$axios.delete('/api/user/' + this.targetUserUsername).then(() => {
                    this.loadUsers();
                }).catch((err) => {
                    console.log(err)
                });
                this.deleteUserVisible = false
            },
            handleAddUser(e) {
                this.$axios.post('/api/user', this.createdNewUser).then((response) => {
                    this.sendNotification("Create user success");
                    this.loadUsers();
                    this.addUserVisible = false;
                }).catch((err) => {
                    this.sendNotification("Create user failed")
                })
            },
            showChangePassword(record) {
                this.targetUserUsername = record;
                this.changePasswordVisible = true;
            },
            showChangeRole(username) {
                this.targetUserUsername = username;
                this.changeRoleVisible = true;
                this.targetUserRole = this.users.find(u => u.username === username).role
            },
            showDeleteUser(username) {
                this.targetUserUsername = username;
                this.deleteUserVisible = true;
            },
            showAddUser() {
                this.addUserVisible = true;
                let defaultRole = this.getFirstRole();
                this.createdNewUser = {
                    username: '',
                    password: '',
                    role: defaultRole
                }
            },
            getFirstRole() {
                if (this.roles.length === 0) {
                    this.sendNotification("You don't have any role yet");
                    return "";
                } else {
                    return this.roles[0].name
                }
            },
            sendNotification(message) {
                this.$notification.open({
                    message: message
                })
            },
            showAddUserBulk() {
                this.addUserBulkVisible = true;
                this.userBulkGenerate.count = 0;
                this.userBulkGenerate.role = '';
            },
            handleAddUserBulk() {
                if (this.userBulkGenerate.count > 300) {
                    this.sendNotification('Generating ' + this.userBulkGenerate.count + ' credentials. This may take some time.')
                }
                this.$axios.post('/api/user/bulk?count=' + this.userBulkGenerate.count, {
                    role: this.userBulkGenerate.role
                }).then((response) => {
                    this.sendNotification('Credential generation finished');
                    this.loadUsers();
                    this.addUserBulkVisible=false;
                })
            }

        },
        mounted() {
            this.loadUsers();
            this.loadRoles();
        }
    }
</script>

<style scoped>

</style>