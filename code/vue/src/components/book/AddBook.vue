<template>
  <div class="main">
    <div class="admin">
    <!-- 表单 -->

    <div class="form">
      <el-form :model="form">
        <el-form-item label="封面：" :label-width="formLabelWidth">
          <el-upload
            style="width: 210px;height: 280px;background-color: #f2f2f2;"
            class="avatar-uploader"
            action="http://localhost:8080/booktem/upload"
            ref="uploadRef"
            :auto-upload="false"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :on-change="displayAvatar"
            :data = 'dataImg'
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="el-upload__tip" style="margin-left: 10px;">
            jpg/png 文件小于等于 1MB
          </div>
          </el-form-item>
          <el-form-item label="书名：" :label-width="formLabelWidth">
            <el-input v-model="form.name" class="formWidth" />
          </el-form-item>
          <el-form-item label="作者：" :label-width="formLabelWidth">
            <el-input v-model="form.writer" class="formWidth"/>
          </el-form-item>
          <el-form-item label="标签：" :label-width="formLabelWidth">
            <el-select v-model="form.type" class="m-2" placeholder="请输入类别">
              <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              />
          </el-select>
          </el-form-item>
          <el-form-item label="简介：" :label-width="formLabelWidth" class="tem">
            <el-input v-model="form.description" class="formWidth"  type="textarea" :rows="20" style="width: 580px;"/>
          </el-form-item>
          
        </el-form>

        <el-button type="primary" class="updateBtn" @click="save()"><el-icon size="18"><EditPen /></el-icon>提交</el-button>
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

  let form = reactive({
    id: 0,
    name: '',
    writer: '',
    type: '',
    imgUrl: '',
    description:'',
    uid: 0
  })

  onBeforeMount(() => {
    const token = localStorage.getItem('token')
    axios.get("/user/userInfo").then(response => {
      const data = response.data.data
      if(response.data.result){
        if(data.flag){
          form.uid = data.data.id
        }else {
          ElMessage.error(data.message)
          return;
        }
      }  
    })
  })

  // 保存
  function save(){
    form.name=form.name.trim();
    form.writer=form.writer.trim();
    if (!form.name){
      ElMessage.error('小说名称不能为空！')
      return;
    }

    if (!form.writer){
      ElMessage.error('小说作者不能为空！')
      return;
    }

    if (!form.type){
      ElMessage.error('小说种类不能为空！')
      return;
    }

    if (!form.description){
      ElMessage.error('小说简介不能为空！')
      return;
    }

    if (!imageUrl){
      ElMessage.error('小说封面不能为空！')
      return;
    }

    axios.post("/booktem/addbook", form).then(response => {
      console.log(response.data);
      const data = response.data.data
      if(response.data.result){
        if(data.flag){
          ElMessage({
            message: data.message,
            type: 'success',
          })
          if(form.imgUrl != null){
            dataImg.id = data.data
            submitUpload()
          }
          setTimeout(()=>{
            location.reload();
          },1000)
          // load()
        }else {
          ElMessage.error(data.message)
          return;
        }
      }else {
        ElMessage.error("保存失败")
        return;
      }
    })
  }

  // 小说种类
  const value = ref('')

  const options = [
  {
      value: '穿越',
      label: '穿越',
  },
  {
      value: '重生',
      label: '重生',
  },
  {
      value: '修仙',
      label: '修仙',
  },
  {
      value: '玄幻',
      label: '玄幻',
  },
  {
      value: '耽美',
      label: '耽美',
  },
  {
      value: '言情',
      label: '言情',
  }
  ]


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
    width: 210px;
    height: 280px;
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
    border: 1px dashed var(--el-border-color);
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
    width: 210px;
    height: 280px;
    text-align: center;
  }

    .form {
        margin-top: 50px;
        margin-left: -150px;
    }

    .updateBtn{
        z-index: 3;
        margin-left: 1200px;
        margin-top: 30px;
    }

    .el-form-item{
        width: 628px;
    }

    .tem{
      width: 900px;
      margin-left: 440px;
      margin-top: -480px;
    }
</style>