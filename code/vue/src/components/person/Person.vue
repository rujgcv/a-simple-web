<template>
  <div class="main">
    <div class="admin">
    <!-- 表单 -->

    <div class="form">
        <el-form :model="adminInfo" >
            <el-form-item label="头像：" :label-width="formLabelWidth">
            <el-upload
            style="width: 162px;height: 162px;border-radius: 50%;"
            class="avatar-uploader"
            action="http://localhost:8080/user/upload"
            ref="uploadRef"
            :auto-upload="false"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :on-change="displayAvatar"
            :data = 'dataImg'
            >
            <!-- imageUrl -->
            <el-avatar :size="160" v-if="imageUrl" :src="imageUrl" class="adminImg"/>
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="el-upload__tip" style="margin-left: 10px;">
            jpg/png 文件小于等于 1MB
            </div>
            </el-form-item>
            <el-form-item label="用户名：" :label-width="formLabelWidth" style="margin-top: 40px;">
                <el-input v-model="adminInfo.name" class="formWidth" />
            </el-form-item>
            <el-form-item label="手机号：" :label-width="formLabelWidth" style="margin-top: 20px;">
                <el-input v-model="adminInfo.phone" class="formWidth"/>
            </el-form-item>
            <el-form-item label="邮箱：" :label-width="formLabelWidth" style="margin-top: 20px;">
                <el-input v-model="adminInfo.email" class="formWidth"/>
            </el-form-item>
            <el-form-item label="密码：" :label-width="formLabelWidth" style="margin-top: 20px;">
                <div class="forgetPwd" @click="forgetPwd()">忘记密码</div>
            </el-form-item>
        </el-form>

        <el-button type="success" class="resetBtn" @click="reset()">重置</el-button>
        <el-button type="primary" class="updateBtn" @click="updateUser()">修改</el-button>

        <div class="fufu" @click="clickfufu()"><img src="../../assets/img/11.gif"/></div>

        <el-dialog v-model="dialogFormVisible" title="修改密码" style="width: 600px;" show-close>
        <el-form>
            <el-form-item label="原密码：" :label-width="formLabelWidth" style="margin-left: -150px;">
                <el-input type="password" v-model="oldPwd" show-password/>
            </el-form-item>
            <el-form-item label="新密码：" :label-width="formLabelWidth" style="margin-left: -150px;margin-top: 30px;">
                <el-input type="password" v-model="newPwd" show-password/>
            </el-form-item>
            <el-form-item label="确认新密码：" :label-width="formLabelWidth" style="margin-left: -150px;margin-top: 30px;">
                <el-input type="password" v-model="newPwd2" show-password/>
            </el-form-item>
        </el-form>
        <template #footer>
        <span class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取消</el-button>
            <el-button type="primary" @click="updataPwd()">
            确认
            </el-button>
        </span>
        </template>
    </el-dialog>
    </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref,onBeforeMount, reactive} from "vue";
  import { ElMessage, ElTable,UploadInstance,UploadProps } from 'element-plus'
  import {
    Menu as IconMenu,
    Message,
    Setting,
    Search,
    InfoFilled,
    Plus
  } from "@element-plus/icons-vue";
  import axios from "axios";

  const formLabelWidth = "300px"

  const dialogFormVisible = ref(false)

  // 密码
  const oldPwd = ref('')
  const newPwd = ref('')
  const newPwd2 = ref('')

  let adminInfo = reactive({
    id: 0,
    name: '',
    avatarUrl: '',
    phone: '',
    email: '',
    admin: 0
  })

  onBeforeMount(() => {
    const token = localStorage.getItem('token')
    axios.get("/user/userInfo").then(response => {
      const data = response.data.data
      if(response.data.result){
        if(data.flag){
          adminInfo.id = data.data.id
          adminInfo.name = data.data.name
          adminInfo.phone = data.data.phone
          adminInfo.email = data.data.email
          adminInfo.avatarUrl = data.data.avatarUrl
          adminInfo.admin = data.data.admin
          imageUrl.value = data.data.avatarUrl+'?t='+new Date().getTime()
          console.log(adminInfo.admin);
        }else {
          ElMessage.error(data.message)
          return;
        }
      }  
    })
  })

  // 重置
  function reset(){
    location.reload()
  }

  // 忘记密码
  function forgetPwd(){
    oldPwd.value = ""
    newPwd.value = ""
    newPwd2.value = ""
    dialogFormVisible.value = true
  }

  // 修改密码
  function updataPwd(){
    if(oldPwd.value === '' || newPwd.value === '' || newPwd2.value === ''){
        ElMessage.error("密码不能为空!")
        return;
    }

    if(oldPwd.value.length<4 || oldPwd.value.length>14){
      ElMessage.error("密码长度必须要在[4~14]位之间!")
      return;
    }

    if(newPwd.value.length<4 || newPwd.value.length>14){
      ElMessage.error("密码长度必须要在[4~14]位之间!")
      return;
    }

    if(newPwd.value !== newPwd2.value){
        ElMessage.error("两次输入密码不一致!")
        return;
    }

    var params = {
        id : adminInfo.id, 
        oldPassword : oldPwd.value,
        newPassword : newPwd.value
    }

    console.log(params);

    axios.post('/user/updatePwd', params).then(response => {
        const data = response.data.data
        console.log(response);
        if(response.data.result){
            if(data.flag){
            ElMessage({
                message: data.message,
                type: 'success',
            })
            dialogFormVisible.value = false
            }else {
            ElMessage.error(data.message)
            }
        }else {
            ElMessage.error("修改失败(未知错误)!")
            return;
        }
    })
  }

  // 修改用户信息
  function updateUser(){
    adminInfo.name=adminInfo.name.trim();
    adminInfo.phone=adminInfo.phone.trim();
    adminInfo.email=adminInfo.email.trim();
    if (!adminInfo.name){
      ElMessage.error('用户名不能为空！')
      return;
    }else {
      if(adminInfo.name.length<4||adminInfo.name.length>14){
        ElMessage.error("用户名长度必须要在[4~14]位之间!")
        return;
      }
    }

    if (!adminInfo.phone){
      ElMessage.error('电话不能为空！')
      return;
    }else {
      var mPattern = /^(?:\+?86)?1\d{10}$/; 
      if(!mPattern.test(adminInfo.phone)){
        ElMessage.error('非法的电话格式!')
        return;
      }
    }

    if(adminInfo.email.length != 0){
      var ePattern = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/
      if(!ePattern.test(adminInfo.email)){
        ElMessage.error('非法的邮箱格式!')
        return;
      }
    }
    
    console.log(imageUrl.value);

    axios.post("/user/update/person", adminInfo).then(response => {
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            if(imageUrl.value != null && imageUrl.value != adminInfo.avatarUrl){
              dataImg.id = adminInfo.id
              submitUpload()
            }
            localStorage.clear
            localStorage.setItem("token",data.data)
            ElMessage({
              message: data.message,
              type: 'success',
            })
            setTimeout(() => {
              location.reload()
            },1200)
          }else {
            ElMessage.error(data.message)
            setTimeout(() => {
              window.location.href = window.location.href
            },1500)
          }
        }else {
          ElMessage.error("修改失败")
          return;
        }
    })
  }

  function clickfufu(){
    ElMessage({
        message: '功德+1',
        type: 'success',
    })
  }

    // 一堆封面
  const imageUrl = ref('')
  const dataImg = reactive({id: 0})

  function displayAvatar(file) {
    imageUrl.value = URL.createObjectURL(file.raw);
  }
  
  const handleAvatarSuccess: UploadProps['onSuccess'] = (
    response,
    uploadFile
  ) => {
    imageUrl.value = URL.createObjectURL(uploadFile.raw!)
    ElMessage({
      message: '封面上传成功!',
      type: 'success',
    })
  }
  
  const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
    if (rawFile.type !== 'image/jpeg') {
      ElMessage.error('封面图片必须是JPG或PNG格式!')
      return false
    } else if (rawFile.size / 1024 / 1024 > 1) {
      ElMessage.error('封面图片不能超过1MB!')
      return false
    }
    return true
  }

  const uploadRef = ref<UploadInstance>()

  const submitUpload = () => {
    uploadRef.value!.submit()
  }
</script>

<style scoped>
    .main{
        width: 100%;
        height: 100%;
        background-color: rgb(234, 232, 233);
    }

    .admin{
        float: left;
        width: 1290px;
        height: 616px;
        margin: 20px;
        border: 0.3px solid rgb(238, 238, 238);
        background-color: rgb(255, 255, 255);
        border-radius: 16px;
    }

    .formWidth {
        width: 320px;
    }

    .avatar-uploader .avatar {
    width: 120px;
    height: 160px;
    display: cover;
    }

    .avatar-uploader{
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
    }

    .avatar-uploader .el-upload {
        /* border: 1px dashed var(--el-border-color); */
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
    }

    .avatar-uploader .el-upload:hover {
        border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        background-color: rgb(227, 226, 226);
        width: 162px;
        height: 162px;
        border-radius: 50%;
        text-align: center;
    }

    .form {
        margin-top: 50px;
        margin-left: -150px;
    }

    .updateBtn{
        z-index: 3;
        margin-left: 10px;
        margin-top: 30px;
    }

    .resetBtn{
        float: left;
        margin-left: 490px;
        margin-top: 30px;
    }

    .forgetPwd{
        color: #606266;
        font-weight: bold;
        cursor: pointer;
    }

    .forgetPwd:hover{
        color: rgb(23, 112, 244);
        text-decoration: underline;
    }

    .fufu{
        float: left;
        margin-left: 800px;
        margin-top: -400px;
        cursor: pointer;
    }

    .el-form-item{
        width: 628px;
    }
</style>