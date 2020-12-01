<template>
    <a-layout id="components-layout-demo-custom-trigger" :style="{height:'100%'}">
        <a-layout-sider v-model="collapsed" :trigger="null" collapsible>
            <a-menu theme="dark" mode="inline" :default-selected-keys="['1']" @click="menuClickHandler">
                <a-menu-item key="1">
                    <a-icon type="user"/>
                    <span>Device</span>
                </a-menu-item>
                <a-menu-item key="2">
                    <a-icon type="database"/>
                    <span>Role</span>
                </a-menu-item>
                <a-menu-item key="3">
                    <a-icon type="number"/>
                    <span>Permissions</span>
                </a-menu-item>
            </a-menu>
        </a-layout-sider>
        <a-layout>
            <a-layout-header style="background: #fff; padding: 0">
                <a-icon
                        class="trigger"
                        :type="collapsed ? 'menu-unfold' : 'menu-fold'"
                        @click="() => (collapsed = !collapsed)"
                />
            </a-layout-header>
            <a-layout-content
                    :style="{ margin: '24px 16px', padding: '24px', background: '#fff', minHeight: '280px' }"
            >
                <router-view></router-view>
            </a-layout-content>
        </a-layout>
    </a-layout>
</template>
<script>
    export default {
        data() {
            return {
                collapsed: false,
            }
        },
        methods: {
            menuClickHandler(item) {
                let target = ''
                if (item['key'] === '1') {
                    target = 'user'
                } else if (item['key'] === '2') {
                    target = 'role';
                } else if (item['key'] === '3') {
                    target = 'permission'
                }
                if (this.$router.name !== target) {
                    this.$router.push(target)
                }
            }
        }
    };
</script>
<style>
    #components-layout-demo-custom-trigger .trigger {
        font-size: 18px;
        line-height: 64px;
        padding: 0 24px;
        cursor: pointer;
        transition: color 0.3s;
    }

    #components-layout-demo-custom-trigger .trigger:hover {
        color: #1890ff;
    }

    #components-layout-demo-custom-trigger .logo {
        height: 32px;
        background: rgba(255, 255, 255, 0.2);
        margin: 16px;
    }
</style>
