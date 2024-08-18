import { createApp, onMounted } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './assets/css/main.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import locale from 'element-plus/es/locale/lang/zh-cn'
import request from './utils/request'
import axios from 'axios'
import Vuex from 'vuex'
// import VueAxios from 'vue-axios'

const app = createApp(App)

app.config.globalProperties.axios = request
axios.defaults.baseURL = "http://127.0.0.1:8080"
// axios.defaults.timeout = 200
// axios.defaults.headers.post["Content-Type"] = 'application/json'
axios.interceptors.request.use(
  config => {
    if (localStorage.getItem('token')) {
      config.headers.Authorization = localStorage.getItem('token');
    }

    return config;
  },
  error => {
    return Promise.reject(error);
  });

app.use(router)
app.use(ElementPlus,{locale})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')

// 删除过期邮件 & 无用的记录
delMessage()
delUserView()
setInterval(()=>{
  delMessage();
  delUserView();
},1000 * 60 * 60 * 24)

function delMessage(){
  axios.get("/message/del").then(response => {
  }) 
}

function delUserView(){
  axios.get("/book/delUserView").then(response => {
  })
}