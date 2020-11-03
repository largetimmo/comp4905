const HOME = r => require.ensure([], () => r(require('../page/Home')), 'Home');
const ROLEVIEW = r => require.ensure([], () => r(require('../page/RoleView')), 'RoleView');
const USERVIEW = r => require.ensure([], () => r(require('../page/UserView')), 'UserView');
const PERMISSIONVIEW = r => require.ensure([], () => r(require('../page/PermissionView')), 'PermissionView');


export default [
    {
        path:'/',
        component: HOME,
        children:[
            {
                path: 'role',
                component:ROLEVIEW
            },
            {
                path: 'user',
                component:USERVIEW
            },
            {
                path:'permission',
                component:PERMISSIONVIEW
            }
        ]
    }
]