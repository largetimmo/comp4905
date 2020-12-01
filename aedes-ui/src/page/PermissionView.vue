<template>
    <div>
        <a-row type="flex" justify="end">
            <a-button type="primary" @click="showAddPermission">Add Permission</a-button>
        </a-row>
        <a-table :columns="cols" :data-source="permissions" :rowKey="(record)=>record.id">
            <span slot="action" slot-scope="record">
                <a @click="showEditPermission(record)">Edit</a>
                <a-divider type="vertical"/>
                <a @click="showDeletePermission(record)">Delete</a>
            </span>
            <span slot="accessControl" slot-scope="record">
                {{translateIntToAccessTxt(record.operation)}}
            </span>
            <span slot="regex" slot-scope="record">
                {{translateToStandard(record.regex)}}
            </span>
        </a-table>

        <a-modal title="Edit permission"
                 :visible="editPermissionVisible"
                 @ok="handleEditPermission"
                 @cancel="()=>{this.editPermissionVisible=false}"
                 :okButtonProps=editModalAvailability>
<!--                 cancelButtonProps={{disabled=true}}>-->
            <div>Editing permission # <span style="font-weight:bold ">{{targetPermission.id}}</span> :
                <a-input v-model="targetPermission.regex" placeholder="Regular expression"/>
                <a-row v-if="targetPermission.regex.endsWith('+')">
                    <span style="color: red">Permission expression cannot end with +</span>
                </a-row>
                <a-row v-if="targetPermission.regex.includes('#') && targetPermission.operation !== 1">
                    <span style="color: red">Wildcards(#) only works in SUBSCRIBE mode </span>
                </a-row>
                <a-dropdown>
                    <a-button>{{translateIntToAccessTxt(targetPermission.operation)}}
                        <a-divider type="vertical"/>
                        <a-icon type="down"/>
                    </a-button>
                    <a-menu slot="overlay">
                        <a-menu-item v-for="(acc) in accesses" @click="updatePermissionAccess(acc)">{{acc.txt}}
                        </a-menu-item>
                    </a-menu>
                </a-dropdown>
            </div>
        </a-modal>

        <a-modal title="Delete permission confirmation"
                 :visible="deletePermissionVisible"
                 @ok="handleDeletePermission"
                 @cancel="()=>deletePermissionVisible=false">
            <div>
                Confirm you want to delete permission #
                <span style="font-weight:bold ">{{targetPermission.id}}</span>
            </div>
        </a-modal>
        <a-modal title="Add permission"
                 :visible="addPermissionVisible"
                 @ok="handleAddPermission"
                 @cancel="()=>addPermissionVisible=false"
                 :okButtonProps=newModalAvailability>
            <div>
                <a-input v-model="newPermission.regex" placeholder="Regular expression"/>
                <a-row v-if="newPermission.regex.endsWith('+')">
                    <span style="color: red">Permission expression cannot end with +</span>
                </a-row>
                <a-row v-if="newPermission.regex.includes('#') && newPermission.operation !== 1">
                    <span style="color: red">Wildcards(#) only works in SUBSCRIBE mode </span>
                </a-row>
                <a-row>
                    <a-dropdown>
                        <a-button>{{translateIntToAccessTxt(newPermission.operation)}}
                            <a-divider type="vertical"/>
                            <a-icon type="down"/>
                        </a-button>
                        <a-menu slot="overlay">
                            <a-menu-item v-for="(acc) in accesses" @click="()=>newPermission.operation = acc.id">
                                {{acc.txt}}
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
        name: "PermissionView",
        data() {
            return {
                cols: [
                    {
                        dataIndex: 'id',
                        key: 'permissionId',
                        title: 'Permission Id'
                    },
                    {
                        key: 'regex',
                        title: 'Regular expression',
                        scopedSlots: {customRender: 'regex'}
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
                accesses: [
                    {
                        id: 0,
                        txt: 'Publish'
                    },
                    {
                        id: 1,
                        txt: 'Subscribe',
                    },
                    {
                        id: 2,
                        txt: 'Publish/Subscribe'
                    }
                ],
                permissions: [],
                targetPermission: {
                    id: -1,
                    regex: '',
                    operation: -1
                },
                addPermissionVisible: false,
                editPermissionVisible: false,
                deletePermissionVisible: false,
                newPermission: {
                    regex: '',
                    operation: -1
                },

            }
        },
        methods: {
            loadAll() {
                this.$axios.get('/api/permissions').then((response) => {
                    this.permissions = response.data
                })
            },
            showAddPermission() {
                this.addPermissionVisible = true
            },
            showEditPermission(permission) {
                this.editPermissionVisible = true;
                this.targetPermission.regex = this.translateToStandard(permission.regex);
                this.targetPermission.operation = permission.operation;
                this.targetPermission.id = permission.id
            },
            showDeletePermission(permission) {
                this.deletePermissionVisible = true;
                this.targetPermission.regex = this.translateToStandard(permission.regex);
                this.targetPermission.operation = permission.operation;
                this.targetPermission.id = permission.id
            },
            handleEditPermission() {
                this.$axios.put('/api/permission', {
                    id: this.targetPermission.id,
                    regex: this.translateFromStandard(this.targetPermission.regex),
                    operation: this.targetPermission.operation
                }).then((response) => {
                    this.$notification.open({
                        message: 'Update success'
                    });
                    this.loadAll();
                    this.editPermissionVisible = false;
                }).catch((err) => {
                    this.$notification.open({
                        message: 'Update failed'
                    });
                })
            },
            translateIntToAccessTxt(num) {
                if (num === 0) {
                    return 'Publish'
                }
                if (num === 1) {
                    return 'Subscribe'
                }
                if (num === 2) {
                    return 'Publish/Subscribe'
                }
            },
            updatePermissionAccess(access) {
                this.$set(this.targetPermission, 'operation', access.id)
            },
            handleDeletePermission() {
                this.$axios.delete('/api/permission/' + this.targetPermission.id).then((response) => {
                    this.$notification.open({
                        message: 'Delete permission success'
                    });
                    this.loadAll();
                    this.deletePermissionVisible = false;
                }).catch((err) => {
                    this.$notification.open({
                        message: 'Delete permission failed'
                    });
                });
            },
            handleAddPermission() {
                this.$axios.post('/api/permission', {
                    regex: this.translateFromStandard(this.newPermission.regex),
                    operation: this.newPermission.operation
                }).then((response) => {
                    this.$notification.open({
                        message: 'Create permission success'
                    });
                    this.loadAll();
                    this.addPermissionVisible = false;
                    this.newPermission.regex = ''
                    this.newPermission.operation = ''

                }).catch((err) => {
                    this.$notification.open({
                        message: 'Create permission failed'
                    });
                });
            },
            translateToStandard(regexString) {
                return regexString.replace(".*", "#").replace("[^/#]*/", "+/");
            },
            translateFromStandard(regexString) {
                return regexString.replace("+", "[^/#]*").replace("#", ".*");
            }
        },
        mounted() {
            this.loadAll()
        },
        computed:{
            editModalAvailability: function () {
                return {
                    props: {
                        disabled: this.targetPermission.regex.includes("+") && (this.targetPermission.regex.lastIndexOf("+") > this.targetPermission.regex.lastIndexOf("/"))
                    }
                }
            },
            newModalAvailability: function () {
                return {
                    props: {
                        disabled: this.newPermission.regex.includes("+") && (this.newPermission.regex.lastIndexOf("+") > this.newPermission.regex.lastIndexOf("/"))
                    }
                }
            }
        }
    }
</script>

<style scoped>

</style>