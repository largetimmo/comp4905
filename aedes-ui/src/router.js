import Vue from 'vue'
import Router from 'vue-router'
import routes from './route/Router'

Vue.use(Router);

export default new Router({
    routes,
    scrollBehavior(){
        return { x: 0, y: 0 }
    }
})