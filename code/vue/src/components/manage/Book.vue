<template>

  <div style="padding: 10px 10px 0 10px">
      <el-input
          v-model="selectByName"
          class="w-50 m-2"
          placeholder="请输入名称"
          :prefix-icon="Search"
          style="width: 250px"
          @blur="selectByName == '' ? reset() : ''"
          @keydown.enter="selectSome()"
      />
      <el-input
          v-model="selectByWriter"
          class="w-50 m-2"
          placeholder="请输入作者"
          :prefix-icon="Search"
          style="width: 250px;margin-left: 10px;"
          @keydown.enter="selectSome()"
      />
      <el-select v-model="selectByType" class="m-2" placeholder="请输入类别" style="width: 250px;margin-left: 10px;">
          <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
          />
      </el-select>
      <el-button type="primary" @click="selectSome()" style="margin-left: 8px">搜索</el-button>
      <el-button type="success" @click="reset()" style="margin-left: 8px">重置</el-button>
  </div>

  <div style="padding: 10px 0 10px 10px">
    <el-button type="primary" @click="handleAdd()"
      >新增<el-icon style="padding-left: 5px"><CirclePlus /></el-icon
    ></el-button>
    <el-popconfirm
      width="220"
      confirm-button-text="确定"
      cancel-button-text="取消"
      :icon="InfoFilled"
      icon-color="red"
      title="你确定要删除吗"
      @confirm="delBatch()"
    >
      <template #reference>
        <el-button type="danger"
          >批量删除<el-icon style="padding-left: 5px"><Remove /></el-icon
        ></el-button>
      </template>
    </el-popconfirm>
  </div>

  <el-table
    :data="tableData"
    style="width: 100%"
    :header-cell-style="{background:'rgb(63 110 152 / 13%)',}"
    @selection-change="handleSelectionChange"
  >
    <el-table-column type="selection" width="40" />
    <el-table-column prop="imgUrl" label="封面" width="100">
      <template v-slot="scope">
        <el-image style="width: 60px; height: 80px" :src="scope.row.imgUrl+'?t='+new Date().getTime()" fit="cover" />
      </template>
    </el-table-column>
    <el-table-column prop="name" label="书名" width="200" />
    <el-table-column prop="writer" label="作者" width="200" />
    <el-table-column prop="type" label="标签" width="100" />
    <el-table-column prop="grade" label="评分" width="100" />
    <el-table-column prop="read" label="阅读数" width="100" />
    <el-table-column prop="like" label="喜欢" width="100" />
    <el-table-column prop="createTime" label="创建时间" width="200" />
    <el-table-column label="操作">
      <template v-slot="scope">
        <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
        <el-popconfirm
          width="220"
          confirm-button-text="确定"
          cancel-button-text="取消"
          :icon="InfoFilled"
          icon-color="red"
          title="你确定要删除吗"
          @confirm="handleDel(scope.row)"
        >
          <template #reference>
            <el-button type="danger" size="small">删除</el-button>
          </template>
        </el-popconfirm>
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
  <el-dialog v-model="dialogFormVisible" :title="fromTitle" style="width: 1000px;height: 500px;" @close="formCancel">
    <el-form :model="form">
      <el-form-item label="封面：" :label-width="formLabelWidth">
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
      <el-form-item label="简介：" :label-width="formLabelWidth" class="pp">
          <el-input v-model="form.description" class="formWidth" type="textarea" :rows="14" style="width: 400px;"/>
        </el-form-item>
      </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button class="btn" @click="formCancel()">取消</el-button>
        <el-button class="btn" type="primary" @click="save()">
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
  import { ref,onBeforeMount, reactive, watch} from "vue";
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
  const fromTitle = ref("")

  // 状态开关
  const status = ref(false)
  const loading = ref(false)


  // 分页的
  const small = ref(false);
  const background = ref(false);
  const disabled = ref(false);
  const pageNum = ref(1)
  const pageSize = ref(10)

  // 搜索
  const selectByName = ref(""); // 按名字
  const selectByType = ref(""); // 按分类
  const selectByWriter = ref(""); // 按作者

  // 状态开关只读
  const statusValue = ref(true)

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

  // 多选
  let multipleSelection = reactive([])


  // Methods
  onBeforeMount(() => {
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
    axios.get("/book/page", {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        name: selectByName.value,
        writer: selectByWriter.value,
        type: selectByType.value,
        order: "asc"
      }
    })
      .then((response) => {
        console.log(response);
        for(var i=0;i<response.data.data.length;i++){
          response.data.data[i].grade = response.data.data[i].grade.toFixed(2)
        }
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

  // 查询
  function selectSome(){
    console.log(selectByName,selectByWriter,selectByType);
    load()
  }

  // 重置
  function reset(){
    selectByName.value = ""
    selectByWriter.value = ""
    selectByType.value = ""
    load()
  }

  // 新增
  function handleAdd(){
    fromTitle.value = "新增小说"
    dialogFormVisible.value = true
    form.id = 0
    form.name = ''
    form.writer = ''
    form.type = ''
    form.imgUrl=  ''
    form.description=''
    imageUrl.value=""
  }

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

    if(fromTitle.value === "新增小说"){
      form.id = -1
      axios.post("/book/update", form).then(response => {
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
            // window.location.href = window.location.href
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
    }else if(fromTitle.value === "编辑小说"){
      console.log(form);
      axios.post("/book/update", form).then(response => {
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            ElMessage({
              message: data.message,
              type: 'success',
            })
            console.log(11111);
            if(imageUrl.value != null && imageUrl.value != form.imgUrl){
              console.log(22222);
              dataImg.id = form.id
              submitUpload()
            }
            console.log(33333);
            load()
            dialogFormVisible.value = false;
            setTimeout(() => {
              // window.location.href = window.location.href
            }, 800)
          }else {
            ElMessage.error(data.message)
          }
        }else {
          ElMessage.error("修改失败")
          return;
        }
      })
    }
  }

  // 编辑
  function handleEdit(row){
    form = row
    if(row.value !== ""){
      imageUrl.value = form.imgUrl+'?t='+new Date().getTime()
    }
    fromTitle.value = "编辑小说"
    dialogFormVisible.value = true
  }

  // 删除用户
  function handleDel(row){
    axios.post("/book/del", row).then(response => {
      const data = response.data.data
      console.log(response);
      if(response.data.result){
        if(data.flag){
          ElMessage({
            message: data.message,
            type: 'success',
          })
          load()
        }else {
          ElMessage.error(data.message)
        }
      }else {
        ElMessage.error("删除失败")
        return;
      }
    })
  }

  // 取消表单
  function formCancel(){
    dialogFormVisible.value = false;
    load()
  }

  // 多选
  function handleSelectionChange(val){
    multipleSelection = val
  }

  // 批量删除
  function delBatch(){
    let ids = multipleSelection.map(v => v["id"])
    console.log(ids);
    axios.post("/book/del/batch", ids).then(response => {
      const data = response.data.data
      console.log(response);
      if(response.data.result){
        if(data.flag){
          ElMessage({
            message: data.message,
            type: 'success',
          })
          load()
        }else {
          ElMessage.error(data.message)
        }
      }else {
        ElMessage.error("批量删除失败")
        return;
      }
    })
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
    