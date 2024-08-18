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
    <!-- <el-button type="primary" @click="handleAdd()"
      >新增<el-icon style="padding-left: 5px"><CirclePlus /></el-icon
    ></el-button> -->
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
    <el-table-column prop="name" label="书名" width="200" />
    <el-table-column prop="writer" label="作者" width="200" />
    <el-table-column prop="type" label="标签" width="100" />
    <el-table-column prop="username" label="贡献者" width="200" />
    <el-table-column prop="create_time" label="创建时间" width="200" />
    <el-table-column label="操作">
      <template v-slot="scope">
        <el-button type="success" size="small" @click="look(scope.row)">预览</el-button>
        <el-button type="primary" size="small" @click="down(scope.row)">下载</el-button>
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
  
  let tableData = reactive([
    {
      create_time:''
    }
  ])
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

  // 多选
  let multipleSelection = reactive([])


  // Methods
  onBeforeMount(() => {
    load()
  })

  const isOnLine = ref(false)

  function download(data){
    if(data.url === '' || data.url === undefined){
        ElMessage.error('未知错误!')
        return false
    }
    window.open('http://127.0.0.1:8080/book/txt/download?url='+data.url + '&isOnLine=' + isOnLine.value, '_blank');        
  }

  function look(data){
    isOnLine.value = true
    download(data)
  }

  function down(data){
    isOnLine.value = false
    download(data)
  }

  // 收缩
  const handleOpen = (key: string, keyPath: string[]) => {
    console.log(key, keyPath);
  };
  const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath);
  };

  function load(){
    axios.get("/book/txt/page", {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        name: selectByName.value,
        writer: selectByWriter.value,
        type: selectByType.value,
      }
    })
      .then((response) => {
        tableData= response.data.data
        console.log(response);
        for(var i=0;i<tableData.length;i++){
          tableData[i].create_time = tableData[i].create_time.substring(0,10);
        }
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
    load()
  }

  // 重置
  function reset(){
    selectByName.value = ""
    selectByWriter.value = ""
    selectByType.value = ""
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
    axios.post("/book/txt/del/batch", ids).then(response => {
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

  function handleDel(data){
    axios.get("/book/txt/del", {params: {id : data.id}}).then(response => {
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
    