<template>

  <div style="padding: 10px 10px 0 10px">
      <el-input
          v-model="selectByUser"
          class="w-50 m-2"
          placeholder="请输入用户名"
          :prefix-icon="Search"
          style="width: 250px"
          @keydown.enter="selectSome()"
      />
      <el-input
          v-model="selectByBook"
          class="w-50 m-2"
          placeholder="请输入书名"
          :prefix-icon="Search"
          style="width: 250px;margin-left: 10px;"
          @keydown.enter="selectSome()"
      />
      <el-input
          v-model="selectByKeyword"
          class="w-50 m-2"
          placeholder="请输入评论关键字"
          :prefix-icon="Search"
          style="width: 250px;margin-left: 10px;"
          @keydown.enter="selectSome()"
      />
      <el-button type="primary" @click="selectSome()" style="margin-left: 8px">搜索</el-button>
      <el-button type="success" @click="reset()" style="margin-left: 8px">重置</el-button>
  </div>

  <div style="padding: 10px 0 10px 10px">
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
    <el-table-column prop="avatarUrl" label="头像" width="100" >
      <template v-slot="scope">
        <el-avatar :size="40" :src="scope.row.avatarUrl+'?t='+new Date().getTime()" />
      </template>
    </el-table-column>
    <el-table-column prop="username" label="用户名" width="200" />
    <el-table-column prop="bookname" label="书名" width="200" />
    <el-table-column prop="content" label="内容" width="300" />
    <el-table-column prop="createTime" label="创建时间" width="200" />
    <el-table-column prop="isDel" label="是否删除" width="100">
      <template v-slot="scope">
        <div v-if="scope.row.isDel === 1">已删除</div>
        <div v-else></div>
      </template>
    </el-table-column>
    <el-table-column label="操作">
      <template v-slot="scope">
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

  // 分页的
  const small = ref(false);
  const background = ref(false);
  const disabled = ref(false);
  const pageNum = ref(1)
  const pageSize = ref(10)

  // 搜索
  const selectByUser = ref(""); // 按用户
  const selectByBook = ref(""); // 按小说
  const selectByKeyword = ref(""); // 按关键字

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

  function load(){ //都是模糊查询
    // return
    axios.get("/review/page", {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        username: selectByUser.value,
        bookname: selectByBook.value,
        keyword:  selectByKeyword.value
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

  // 查询
  function selectSome(){
    console.log(selectByUser,selectByBook,selectByKeyword);
    load()
  }

  // 重置
  function reset(){
    selectByUser.value = ""
    selectByBook.value = ""
    selectByKeyword.value = ""
    load()
  }

  // 删除用户
  function handleDel(row){
    axios.post("/review/del", row).then(response => {
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

  // 多选
  function handleSelectionChange(val){
    multipleSelection = val
  }

  // 批量删除
  function delBatch(){
    let ids = multipleSelection.map(v => v["id"])
    console.log(ids);
    axios.post("/review/del/batch", ids).then(response => {
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
    