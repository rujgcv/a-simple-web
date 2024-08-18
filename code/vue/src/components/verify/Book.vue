<template>
    <div class="title">
        新增小说板块审核
    </div>
    <el-table
      :data="tableData"
      style="width: 100%;"
      :header-cell-style="{background:'rgb(63 110 152 / 13%)',}"
      v-show="v"
      >
      <el-table-column prop="imgUrl" label="封面" width="100">
        <template v-slot="scope">
          <el-image style="width: 60px; height: 80px" :src="scope.row.imgUrl" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="书名" width="250" />
      <el-table-column prop="writer" label="作者" width="250" />
      <el-table-column prop="type" label="标签" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="250" />
      <el-table-column label="操作">
        <template v-slot="scope">
            <el-button type="primary" size="40" @click="handleEdit(scope.row)">
                <el-icon style="margin-right: 2px;"><Edit /></el-icon>
                审核
            </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="padding: 10px 0 0 10px">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 15, 20]"
        :small="small"
        :disabled="disabled"
        :background="background"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  
    <!-- 表单 -->
    <el-dialog v-model="dialogFormVisible" title="审核新增小说板块" style="width: 1000px;height: 500px;" @close="formCancel">
      <el-form :model="form">
        <el-form-item label="封面：" :label-width="formLabelWidth" >
          <el-upload
            style="width: 120px;height: 160px;"
            class="avatar-uploader"
            action="http://localhost:8080/book/upload"
            ref="uploadRef"
            :auto-upload="false"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :on-change="displayAvatar"
            :data = 'dataImg'
          >
            <img v-if="imageUrl" :src="imageUrl+'?'+Date.now()" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="el-upload__tip" style="margin-left: 10px;">
            jpg/png 文件小于等于 1MB
          </div>
        </el-form-item>
        <el-form-item label="书名：" :label-width="formLabelWidth" >
          <el-input v-model="form.name" class="formWidth" disabled/>
        </el-form-item>
        <el-form-item label="作者：" :label-width="formLabelWidth">
          <el-input v-model="form.writer" class="formWidth" disabled/>
        </el-form-item> 
        <el-form-item label="标签：" :label-width="formLabelWidth">
          <el-select v-model="form.type" class="m-2" placeholder="请输入类别" disabled>
            <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            />
        </el-select>
        </el-form-item>
        <el-form-item label="简介：" :label-width="formLabelWidth" class="pp">
          <el-input v-model="form.description" disabled class="formWidth" type="textarea" :rows="14" style="width: 400px;"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer" style="margin-left: 700px;float: left;">
            <el-button class="btn" type="success" @click="pass()">
                通过
            </el-button>
            <el-button class="btn" type="danger" @click="nopassShow = true">不通过</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="nopassShow" title="未通过理由" style="width: 600px;">
      理由: <el-input v-model="nopassReason" style="width: 500px;"></el-input>
      <div style="margin-top: 10px;margin-left: 460px;">
        <el-button type="info" style="margin-left: -80px;" @click="cancelNo()">取消</el-button>
        <el-button type="danger" @click="nopass()" style="">提交</el-button>
      </div>
    </el-dialog>
  </template>
  
  <script lang="ts" setup>
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
    
    const tableData = ref()
    const total = ref()

    // 分页的
    const small = ref(false);
    const background = ref(false);
    const disabled = ref(false);
    const pageNum = ref(1)
    const pageSize = ref(10)
  
    // 表单
    const dialogFormVisible = ref(false)
    const formLabelWidth = '100px'
  
    let form = reactive({
      id: 0,
      name: '',
      writer: '',
      type: '',
      imgUrl: '',
      description:'',
    })

    const v = ref(false)

    const nopassReason = ref('')

    const nopassShow = ref(false)

    function cancelNo(){
      nopassReason.value = ''
      nopassShow.value = false
    }

    let adminInfo = reactive({
      id:0,
      name: ''
    })
  
    // Methods
    onBeforeMount(() => {
      const token = localStorage.getItem('token')
      axios.get("/user/userInfo").then(response => {
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            adminInfo = data.data
            setTimeout(() => {
              v.value = true
            },10)
          }else {
            ElMessage.error(data.message)
            return;
          }
        }
      })
      load()
    })
  
    // 收缩
    const handleOpen = (key: string, keyPath: string[]) => {
      console.log(key, keyPath);
    };
    const handleClose = (key: string, keyPath: string[]) => {
      console.log(key, keyPath);
    };
  
    function load(){
      axios.get("/booktem/page", {
        params: {
          pageNum: pageNum.value,
          pageSize: pageSize.value
        }
      })
      .then((response) => {
          console.log(response);
          tableData.value = response.data.data
          total.value = response.data.total
      })  
    }
  
    // 分页
    const handleSizeChange = (val: number) => {
      pageSize.value = val;
      load()
    };
    const handleCurrentChange = (val: number) => {
      pageNum.value = val;
      load()
    };

    // 未通过原因
    const message = ref('')

    const bookTemId = ref(0)

    // 删除临时表中的小说
    // 向用户发送投稿成功消息
    function saveSuccess(){
      console.log(111);
      axios.get("/booktem/del", {
        params: {
          id: bookTemId.value,
          uid: adminInfo.id,
          pass: true,
          message: '',
          bookId : dataImg.id
        }
      }).then(response => {
        console.log(response.data);
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            ElMessage({
              message: data.message,
              type: 'success',
            })
            load()
            dialogFormVisible.value = false;
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
  
    // 保存
    function pass(){
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
  
      form.id = -1
      form.imgUrl = ''
      axios.post("/book/update", form).then(response => {
        console.log(response.data);
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            if(imageUrl.value != null && imageUrl.value != ''){
              dataImg.id = data.data
              // 没用的东西
              submitUpload()
            }     
            // 删除临时表中的小说
            // 向用户发送投稿成功消息
            saveSuccess()
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

    function nopass(){
      axios.get("/booktem/del", {
        params: {
          id: bookTemId.value,
          uid: adminInfo.id,
          pass: false,
          message: nopassReason.value,
          bookId : dataImg.id
        }
      }).then(response => {
        console.log(response.data);
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            // ElMessage({
            //   message: data.message,
            //   type: 'success',
            // })
            nopassReason.value='';
            load()
            nopassShow.value = false
            dialogFormVisible.value = false;
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
  
    // 编辑
    function handleEdit(row){
      form = row
      if(row.value !== ""){
        imageUrl.value = form.imgUrl
      }
      dialogFormVisible.value = true
      bookTemId.value = form.id
    }
  
    // 取消表单
    function formCancel(){
      dialogFormVisible.value = false;
      load()
    }

    // 标签下拉选择
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
      width: 120px;
      height: 160px;
      text-align: center;
    }

    .title{
        font-size: 28px;
        margin: 10px;
        font-weight: 550;
        color: #404040;
    }
    .pp{
        margin-top: -320px;
        margin-left: 400px;
    }
    .btn{
        margin-top: -10px;
        width: 100px;
        height: 40px;
        font-size: 18px;
        /* font-weight: bold; */
    }
  </style>
      