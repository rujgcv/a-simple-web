<template>
  <div class="background">
    <div class="main">
      <div class="bg"></div>
      <div class="a">
        <!-- 登录 -->
        <div class="form" v-show="!registerVisible">
          <h1>Login<span class="to" @click="formChange">|Register</span></h1>
          <input type="text" class="e" v-model="form.name" placeholder="用户名/手机号">
          <input type="password" class="e" v-model="form.password" placeholder="密码">
          <div class="besides">
            <el-tooltip
              class="box-item"
              effect="dark"
              content="30天免登录"
              placement="right-start"
            >
              <label class="c1">
                <input type="checkbox" v-model="form.rememberPwd"> 记住密码
              </label>
            </el-tooltip>
            <label class="c2">
              <input type="checkbox" v-model="form.admin"> 管理员
            </label>
          </div>
          <button class="g" @click="login">登 录</button>

          <div class="to1" @click="formChange">没有账号，去注册</div>
        </div>
        <!-- 注册 -->
        <div class="form" v-show="registerVisible">
          <h1>Register<span class="to" @click="formChange">|Login</span></h1>
          <input type="text" class="e1" v-model="form2.name" placeholder="用户名">
          <input type="text" class="e1" v-model="form2.phone" placeholder="手机号">
          <input type="password" class="e1" v-model="form2.password" placeholder="密码">
          <input type="password" class="e1" v-model="password2" placeholder="重复密码">
          <button class="g" @click="register">注册</button>

          <div class="to1" style="margin-top: 10px;" @click="formChange">已有账号，去登录</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { onBeforeMount, reactive,ref } from "vue";
  import Mock from 'mockjs';
  import { ElMessage } from "element-plus";
  import axios from "axios";
  import router from "../router";

  const form = reactive({
    name: '',
    password: '',
    admin: false,
    rememberPwd: false
  })

  const form2 = reactive({
    name: '',
    password: '',
    phone: ''
  })

  const registerVisible = ref(false)

  //重复密码
  const password2 = ref('')

  // 切换表单
  function formChange(){
    registerVisible.value = !registerVisible.value
  }

  // 登录
  function login(){
    // 判断是否是管理员
    form.name = form.name.trim()
    form.password = form.password.trim()
    console.log(form);
    if (!form.name){
      ElMessage.error('用户名或者手机号不能为空！')
      return;
    }
    if (!form.password){
      ElMessage.error('密码不能为空！')
      return;
    }
    axios.post("/user/login", form).then(response => {
      const data = response.data.data
      console.log(response);
      if(response.data.result){
        if(data.flag){
          ElMessage({
            message: data.message,
            type: 'success',
          })
          localStorage.setItem("token",data.data)
          if(form.admin){
            router.push("/system/home")
          }else {
            router.push("/")
          }
        }else {
          ElMessage.error(data.message)
          return;
        }
      }  
    })
  }

  // 已登录，直接跳转
  onBeforeMount(() => {
    document.title = '登录/注册 -小说会'
    let token = localStorage.getItem('token')
    if(!(token === null || token === '')) {
      axios.get("/user/userInfo").then(response => {
        const data = response.data.data
        console.log(response);
        if(response.data.result){
          if(data.flag){
            ElMessage({
              message: '登陆成功',
              type: 'success',
            })
            router.push("/system/home")
          }else {
            ElMessage.error(data.message)
            return;
          }
        }  
      })
    }
  })

  // 注册
  function register(){
    form2.name=form2.name.trim();
    form2.phone=form2.phone.trim();
    form2.password=form2.password.trim();
    password2.value=password2.value.trim();
    if (!form2.name){
      ElMessage.error('用户名不能为空！')
      return;
    }else {
      if(form2.name.length<4||form2.name.length>14){
        ElMessage.error("用户名长度必须要在[4~14]位之间!")
        return;
      }
    }

    if (!form2.phone){
      ElMessage.error('电话不能为空！')
      return;
    }else {
      var mPattern = /^(?:\+?86)?1\d{10}$/; 
      if(!mPattern.test(form2.phone)){
        ElMessage.error('非法的电话格式!')
        return;
      }
    }

    if (!form2.password){
      ElMessage.error('密码不能为空！')
      return;
    }else if(form2.password.length<4||form2.password.length>14){
      ElMessage.error("密码长度必须要在[4~14]位之间!")
      return;
    }

    if(form2.password != password2.value){
      ElMessage.error("两次密码不一致")
      return;
    }

    axios.post("/user/register", form2).then(response => {
      const data = response.data.data
      console.log(response);
      if(response.data.result){
        if(data.flag){
          ElMessage({
            message: data.message,
            type: 'success',
          })
          setTimeout(() => {
            location.reload();
          }, 1000)
        }else {
          ElMessage.error(data.message)
          return;
        }
      }  
    })
  }
</script>

<style scoped>
    .background{
        width: 100%;
        height: 100%;
        background-image: linear-gradient(to left,
        #ACB6E5,#f05053);
        display: flex;
        justify-content: center;
    }
    .main{
        width: 1100px;
        height: 550px;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        box-shadow: 0 5px 15px rgba(0,0,0,.8);
        display: flex;
        border-radius: 10px;
    }
    .bg{
      width: 800px;
      height: 550px;
      background-image: url(../assets/img/bg.JPG);
      background-position: center;
      background-size: cover;
      border-top-left-radius: 10px;
      border-bottom-left-radius: 10px;
    }
    .a{
      width: 300px;
      height: 550px;
      background-color: #fff;
      border-top-right-radius: 10px;
      border-bottom-right-radius: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .form{
      width: 250px;
      height: 500px;
    }
    .form h1 {
      font: 900 30px '';
      margin-bottom: 20px;
    }
    .e{
      width: 230px;
      margin: 20px 0;
      outline: none;
      border: 0;
      padding: 10px;
      border-bottom: 3px solid rgb(80,80,170);
      font: 900 16px '';
    }
    .e1{
      width: 230px;
      margin: 10px 0;
      outline: none;
      border: 0;
      padding: 10px;
      border-bottom: 3px solid rgb(80,80,170);
      font: 900 16px '';
    }
    .g{
      position: absolute;
      margin: 20px;
      bottom: 40px;
      display: block;
      width: 200px;
      height: 60px;
      font: 900 30px '';
      text-decoration: none;
      line-height: 60px;
      border-radius: 30px;
      background-image: linear-gradient(to left,
      #ACB6E5,#f05053);
      text-align: center;
      color: #fafdb9;
      border:0;
      cursor: pointer;
    }
    .besides {
      width: 250px;
      margin: 10px 0;
      font-size: 14px;
      color: rgb(103, 103, 103);
    }::after{
      clear: both;
    }
    .c1{
      cursor: pointer;
      float: left;
    }
    .c2{
      cursor: pointer;
      float: right;
    }
    .to{
      color: #c4c4c4;
      font-size: 18px;
      cursor: pointer;
    }
    .to1 {
      color: #8f8e8e;
      font-size: 14px;
      position: absolute;
      /* margin-top: 240px;
      margin-left: 60px; */
      margin-top: 30px;
      margin-left: 140px;
      cursor: pointer;
    }
    .to1:hover{
      color: rgb(0, 64, 255);
      text-decoration:underline;
    }
</style>