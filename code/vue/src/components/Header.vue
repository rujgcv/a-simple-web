<template>
  <div class="header">
    <div @click="toSY()" class="first">
      <div class="tag">
        <img
          src="../assets/img/icon.png"
          style="width: 50px; height: 50px;padding-left: 20px;"
        />
      </div>
      <div class="read">小说会</div>
    </div>

    <div class="search">
      <input type="text" class="search-main" v-model="searchText" @keydown.enter="search()"/>
      <div class="search-aside" @click="search()">
        <el-icon size="large" style="padding: 5px"><Search /></el-icon>
      </div>
    </div>
    
    <div class="demo-basic--circle" style="float: left;">
      <div style="margin: 15px 30px 0 15px;">
        <el-avatar :size="40" style="background-color: #fff;" v-show="!infoVisible"/>
        <el-avatar :size="40" :src="userInfo.avatarUrl+'?t='+new Date().getTime()"  v-show="infoVisible"/>
      </div>
    </div>

    <div class="red" v-show="hasMessage"></div>
    <div class="header-function-first" @click="message()">
      <el-icon size="28px" class="header-function-first-icon"><Message /></el-icon>
      <div style="font-size: 10px;text-align: center;">消息</div>
    </div>
    <div class="header-function" @click="like()">
      <el-icon size="28px" class="header-function-first-icon"><Star /></el-icon>
      <div style="font-size: 10px;text-align: center;">收藏</div>
    </div>
    <div class="header-function setting" @click="setting()">
      <el-icon size="28px" class="header-function-first-icon"><Setting /></el-icon>
      <div style="font-size: 10px;text-align: center;">设置</div>
    </div>
    <div class="header-function" @click="exit()">
      <el-icon size="28px" class="header-function-first-icon"><SwitchButton  /></el-icon>
      <div style="font-size: 10px;text-align: center;">退出</div>
    </div>
    

    <el-button class="last" style="margin: 15px 0 0 80px;" size="large" @click="add()">
      <el-icon size="22px"><Upload /></el-icon>
      <span style="font-size: 20px;">投稿</span>     
    </el-button>
  </div>
</template>

<script setup>
  import { reactive, ref, toRefs,onBeforeMount } from 'vue'
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  import router from '../router';

  defineExpose({
    hasNoReadMessage
  })

  // 后端传
  const circleUrl = ref("'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'")

  let userInfo = reactive({
    id: 0,
    avatarUrl: ''
  })

  const infoVisible = ref(false)

  const searchText = ref('')

  const hasMessage = ref(false)

  function toSY(){
    window.location.href = '../'
  }

  function like(){
    if(document.URL.split('?')[0] === 'http://localhost:5173/favorite'){
      window.location.href = '../favorite'
    }else {
      window.open('../favorite')
    }
  }

  function add(){
    if(document.URL.split('?')[0] === 'http://localhost:5173/addBook'){
      window.location.href = '../addBook'
    }else {
      window.open('../addBook')
    }
  }

  function search(){
    if(searchText.value !== null && searchText.value !== ''){
      if(document.URL.split('?')[0] === 'http://localhost:5173/search'){
        window.location.href = '../search?keyword='+searchText.value
      }else {
        window.open('../search?keyword='+searchText.value)
      }
    }
  }

  function setting(){
    if(document.URL.split('?')[0] === 'http://localhost:5173/setting'){
      window.location.href = '../setting'
    }else {
      window.open('../setting')
    }
  }

  function message(){
    hasNoReadMessage.value = false
    if(document.URL.split('?')[0] === 'http://localhost:5173/message'){
      window.location.href = '../message'
    }else {
      window.open('../message')
    }
  }

  function exit(){
    var is = confirm("确认要退出登录")
    if(is){
      localStorage.removeItem('token')
      router.push('/login')
    }
  }

  function hasNoReadMessage(){
    axios.get("/message/hasNoRead", {params : { id : userInfo.id }}).then(response => {
      console.log(response.data);
      if(response.data > 0){
        hasMessage.value = true
      }
      setTimeout(() => {
        infoVisible.value = true
      },10)
    }) 
  }
  
  onBeforeMount(() => {
    const token = localStorage.getItem('token')
    axios.get("/user/userInfo").then(response => {
      const data = response.data.data
      if(response.data.result){
        if(data.flag){
          userInfo = data.data
          hasNoReadMessage();
        }else {
          ElMessage.error(data.message)
          return;
        }
      }
    }).catch(err => {
      // 401无授权
      // 时间到了，踢出去
      if(err.response.status === 401){
        localStorage.removeItem('token')
        router.push('/login')
        return response;
      }
    })
  })
  

</script>

<style scoped>
.header1 {
  width: 100%;
  height: 70px;
  background-color: #fff;
  box-shadow: 0 0 15px rgb(210, 206, 206);
  float: left;
}

.header {
  width: 100%;
  height: 70px;
  background-color: #fff;
  box-shadow: 0 0 15px rgb(210, 206, 206);
  float: left;
}
.header:after{
  content: '';
  display: block;
  clear: both;
}
.tag {
  padding: 10px;
  float: left;
}
.read {
  font-family: "NSimSun";
  font-size: 30px;
  line-height: 70px;
  color: #000;
  float: left;
}
.search {
  width: 400px;
  height: 40px;
  float: left;
  margin-top: 15px;
  margin-left: 380px;
  /* margin-left: 100px; */
  background-color: #eae9e9;
  border-radius: 15px;
}
.search::after{
  content: '';
  display: block;
  clear: both;
}
.search-main {
  margin-left: 10px;
  padding-left: 10px;
  width: 320px;
  height: 40px;
  border: none;
  font-size: 18px;
  color: #4a4a4a;
  background-color: #eae9e9;
  outline: none;
}
.search-aside {
  float: right;
  margin-top: 5px;
  margin-right: 10px;
  width: 30px;
  height: 30px;
  border-radius: 9px;
}
.search-aside:hover {
  background-color: #c4c2c2;
}
.header-function {
  float: left;
  margin-left: 30px;
  cursor: pointer;
}
.header-function-first {
  float:left;
  cursor: pointer;
}
.header-function:hover {
  color: #26b0f5;
}
.header-function-first:hover {
  color: #26b0f5;
}
.header-function-first-icon {
  z-index: 10;
  padding: 14px 0 0 0 ;
}
.setting {
  position: relative;
}
.menu {
  width: 70px;
  height: 50px;
  left: -26px;
  top: 80px;
  /* border-bottom: 1px solid #e4e7ed; */
  border-radius: 4px;
  position: absolute;
  background-color: #fff;
  padding: 10px;
  color: #2b2b2b;
}
.item {
  padding: 3px;
}
.item:hover {
  color: #1497d8;
  cursor: pointer;
}
.first{
  height: 70px;
  width: 180px;
  float: left;
  /* margin-left: 36px; */
  z-index: 3;
  cursor: pointer;
}::after{
  content: '';
  display: block;
  clear: both;
}

.demo-basic--circle{
  width: 80px;
  margin-left: 20px;
}
.red{
  width: 12px;
  height: 12px;
  top: 14px;
  left: 1058px;
  position: absolute;
  background: rgb(250, 50, 50);
  border-radius: 10px;
  z-index: 999;
}
</style>
