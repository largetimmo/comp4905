<template>
    <div>
        <a-row type="flex" justify="end">
            <a-button type="primary" @click="showAddRole">Add Role</a-button>
        </a-row>
        <a-table :columns="cols" :data-source="roles" :rowKey="(record)=>record.name">
             <span slot="associatedPermissions" slot-scope="record">
              {{record.permissions.length}}
            </span>
            <span slot="action" slot-scope="record">
                <a @click="showEditPermissions(record)">Edit permissions</a>
                <a-divider type="vertical"/>
                <a @click="showDeleteRole(record.name)"> Delete </a>
            </span>
        </a-table>
        <a-modal title="Delete role confirmation"
                 :visible="deleteRoleVisible"
                 @ok="handleDeleteRole"
                 @cancel="()=>deleteRoleVisible=false">
            <div>
                Confirm you want to delete role
                <span style="font-weight:bold; color: red">{{targetRoleName}}</span>
                <a-row>
                    <span style="font-weight:bold ">
                        Note: this role has to be disassociated with all the users
                    </span>
                </a-row>
            </div>
        </a-modal>
        <a-modal title="Add new role"
                 :visible="addRoleVisible"
                 @ok="handleAddRole"
                 @cancel="()=>addRoleVisible=false"
                 width="60%">
            <div>
                <a-input v-model="newCreatedRole.name" placeholder="Role name"/>
                <a-table :columns="newRolePermissionsCols" :data-source="newCreatedRole.permissions"
                         :rowKey="(record)=>record.id">
                    <span slot="accessControl" slot-scope="record">
                        <span v-if="record.operation === 0"> Publish Only</span>
                        <span v-if="record.operation === 1"> Subscribe Only</span>
                        <span v-if="record.operation === 2"> Publish/Subscribe</span>
                    </span>
                    <span slot="action" slot-scope="record">
                        <a-icon type="close-circle" @click="handleDeletePermissionFromNewRole(record.id)"/>
                    </span>
                    <span slot="footer" style="width: 100%">
                        <a-dropdown>
                            <a-button>
                                <span>{{newCreatedRole.newPermissionsSelected}}</span>
                                <a-divider type="vertical"/>
                                <a-icon type="down"/>
                            </a-button>
                            <a-menu slot="overlay">
                                <a-menu-item v-for="(per) in permissions"
                                             @click="handleSelectPermissionForNewRole(per)">
                                    id:{{per.id}} Regex: {{per.regex}} Access:
                                    <span v-if="per.operation === 0"> Publish</span>
                                    <span v-if="per.operation === 1"> Subscribe</span>
                                    <span v-if="per.operation === 2"> Publish/Subscribe</span>
                                </a-menu-item>
                            </a-menu>
                        </a-dropdown>
                        <a-button type="primary" @click="handleAddPermissionFromNewRole">Add</a-button>
                    </span>
                </a-table>
            </div>
        </a-modal>
        <a-modal title="Edit role"
                 :visible="editPermissionsVisible"
                 @ok="handleUpdateRole"
                 @cancel="()=>editPermissionsVisible=false"
                 width="60%">
            <div>
                <span>
                    Editing permissions for role <span style="font-weight: bold">{{editPermissionRole.name}}</span>
                </span>
                <a-table :columns="newRolePermissionsCols" :data-source="editPermissionRole.permissions"
                         :rowKey="(record)=>record.id">
                    <span slot="accessControl" slot-scope="record">
                        <span v-if="record.operation === 0"> Publish Only</span>
                        <span v-if="record.operation === 1"> Subscribe Only</span>
                        <span v-if="record.operation === 2"> Publish/Subscribe</span>
                    </span>
                    <span slot="action" slot-scope="record">
                        <a-icon type="close-circle" @click="handleDeletePermissionFromExistingRole(record.id)"/>
                    </span>
                    <span slot="footer" style="width: 100%">
                        <a-dropdown>
                            <a-button>
                                <span>{{editPermissionRole.newPermissionsSelected}}</span>
                                <a-divider type="vertical"/>
                                <a-icon type="down"/>
                            </a-button>
                            <a-menu slot="overlay">
                                <a-menu-item v-for="(per) in permissions"
                                             @click="handleSelectPermissionForExistingRole(per)">
                                    id:{{per.id}} Regex: {{per.regex}} Access:
                                    <span v-if="per.operation === 0"> Publish</span>
                                    <span v-if="per.operation === 1"> Subscribe</span>
                                    <span v-if="per.operation === 2"> Publish/Subscribe</span>
                                </a-menu-item>
                            </a-menu>
                        </a-dropdown>
                        <a-button type="primary" @click="handleAddPermissionFromExistingRole">Add</a-button>
                    </span>
                </a-table>
            </div>
        </a-modal>
    </div>
</template>

<script>
    export default {
        name: "RoleView",
        data() {
            return {
                cols: [
                    {
                        dataIndex: 'name',
                        key: 'roleName',
                        title: 'RoleName'
                    }, {
                        dataIndex: 'associatedUser',
                        key: 'associatedUser',
                        title: 'Associated User'
                    }, {
                        scopedSlots: {customRender: 'associatedPermissions'},
                        key: 'associatedPermissions',
                        title: 'Associated Permissions'
                    }, {
                        key: 'action',
                        title: 'Action',
                        scopedSlots: {customRender: 'action'}
                    }
                ],
                newRolePermissionsCols: [
                    {
                        dataIndex: 'id',
                        key: 'permissionId',
                        title: 'Permission Id'
                    },
                    {
                        dataIndex: 'regex',
                        key: 'regex',
                        title: 'Regular expression'
                    },
                    {
                        scopedSlots: {customRender: 'accessControl'},
                        key: 'accessControl',
                        title: 'Access Control'
                    },
                    {
                        scopedSlots: {customRender: 'action'},
                        key: 'action',
                        title: 'Action'
                    }
                ],
                roles: [],
                addRoleVisible: false,
                editPermissionsVisible: false,
                deleteRoleVisible: false,
                newCreatedRole: {
                    name: '',
                    permissions: [],
                    newPermissionsSelected: '',
                    newPermission: {}
                },
                targetRoleName: '',
                permissions: [],
                editPermissionRole: {
                    name: '',
                    permissions: [],
                    newPermissionsSelected: '',
                    newPermission: {}
                }
            }
        },
        methods: {
            showEditPermissions(role) {
                this.editPermissionRole.name = role.name;
                this.editPermissionRole.permissions = role.permissions;
                this.editPermissionRole.newPermissionsSelected = '';
                this.editPermissionRole.newPermission = {};
                this.editPermissionsVisible = true;

            },
            showDeleteRole(roleName) {
                this.deleteRoleVisible = true;
                this.targetRoleName = roleName;
            },
            loadRoles() {
                this.$axios.get('/api/roles?all=true').then((response) => {
                    this.roles = response.data;
                })
            },
            handleDeleteRole() {
                if (this.roles.find(r => r.name === this.targetRoleName).associatedUser > 0) {
                    this.$notification.open({
                        message: 'Cannot delete this role. One or more users are still assigned with this role'
                    });
                } else {
                    this.$axios.delete('/api/role/' + this.targetRoleName).then((response) => {
                        this.$notification.open({
                            message: 'Delete role success'
                        })
                    }).catch((err) => {
                        this.$notification.open({
                            message: 'Delete role failed'
                        })
                    })
                }
            },
            showAddRole() {
                this.addRoleVisible = true;
                this.newCreatedRole = {
                    name: '',
                    permissions: [],
                    newPermissionsSelected: '',
                    newPermission: {}
                }
            },
            handleAddRole() {
                this.$axios.post('/api/role', {
                    name: this.newCreatedRole.name,
                    permissions: this.newCreatedRole.permissions.map(i => {
                        return {
                            id: i.id
                        }
                    })
                }).then((response) => {
                    this.$notification.open({
                        message: 'create role ' + this.newCreatedRole.name + ' success'
                    });
                    this.addRoleVisible = false;
                    this.loadRoles();
                }).catch((err) => {
                    this.$notification.open({
                        message: 'Create role failed'
                    })
                })
            },
            loadPermissions() {
                this.$axios.get('/api/permissions').then((response) => {
                    this.permissions = response.data
                }).catch((err) => {
                    this.$notification.open({
                        message: 'Failed to load permissions'
                    })
                })
            },
            handleDeletePermissionFromNewRole(id) {
                this.newCreatedRole.permissions = this.newCreatedRole.permissions.filter(ele => ele.id !== id)
            },
            handleSelectPermissionForNewRole(permission) {
                let parsedPermission = this.permissionToStr(permission);
                this.newCreatedRole.newPermission = permission;
                this.newCreatedRole.newPermissionsSelected = parsedPermission;
            },
            handleAddPermissionFromNewRole() {
                if (Object.keys(this.newCreatedRole.newPermission).length === 0) {
                    this.$notification.open({
                        message: 'Cannot add empty permission'
                    });
                    return;
                }
                this.newCreatedRole.permissions = this.newCreatedRole.permissions.filter(per => per.id !== this.newCreatedRole.newPermission.id);
                this.newCreatedRole.permissions.push(this.newCreatedRole.newPermission);
                this.newCreatedRole.newPermission = {};
                this.newCreatedRole.newPermissionsSelected = ""
            },
            handleDeletePermissionFromExistingRole(perId) {
                this.editPermissionRole.permissions = this.editPermissionRole.permissions.filter(per => per.id !== perId)
            },
            handleSelectPermissionForExistingRole(permission) {
                let parsedPermission = this.permissionToStr(permission);
                this.editPermissionRole.newPermission = permission;
                this.editPermissionRole.newPermissionsSelected = parsedPermission;
            },
            handleAddPermissionFromExistingRole() {
                if (Object.keys(this.editPermissionRole.newPermission).length === 0) {
                    this.$notification.open({
                        message: 'Cannot add empty permission'
                    });
                    return;
                }
                this.editPermissionRole.permissions = this.editPermissionRole.permissions.filter(per => per.id !== this.editPermissionRole.newPermission.id);
                this.editPermissionRole.permissions.push(this.editPermissionRole.newPermission);
                this.editPermissionRole.newPermission = {};
                this.editPermissionRole.newPermissionsSelected = ""
            },
            permissionToStr(permission) {
                let parsedPermission = "";
                parsedPermission += 'Id:';
                parsedPermission += permission.id;
                parsedPermission += ' Regex:';
                parsedPermission += permission.regex;
                parsedPermission += " Access:";
                if (permission === 0) {
                    parsedPermission += 'Publish'
                } else if (permission === 1) {
                    parsedPermission += 'Subscribe'
                } else {
                    parsedPermission += 'Publish/Subscribe'
                }
                return parsedPermission;
            },
            handleUpdateRole() {
                this.$axios.post('/api/role', {
                    name: this.editPermissionRole.name,
                    permissions: this.editPermissionRole.permissions.map(per => {return{
                        id: per.id
                    }})
                }).then((response) => {
                    this.$notification.open({
                        message: 'update role ' + this.newCreatedRole.name + ' success'
                    });
                    this.editPermissionsVisible = false;
                    this.loadRoles();
                }).catch((err) => {
                    this.$notification.open({
                        message: 'update role failed'
                    });
                })
            }

        },
        mounted() {
            this.loadRoles();
            this.loadPermissions();
        }

    }
</script>

<style scoped>

</style>