import Vue from 'vue'
import App from './App.vue'
import Antd from 'ant-design-vue';
import router from './router'
import axios from 'axios'
import 'ant-design-vue/dist/antd.css';

Vue.use(Antd);

Vue.config.productionTip = false

axios.defaults.timeout = 30000;
axios.defaults.baseURL = 'http://localhost:8082';
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
Vue.prototype.$axios = axios;

// eslint-disable-next-line
new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
