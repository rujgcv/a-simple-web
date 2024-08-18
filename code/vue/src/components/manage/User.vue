<template>

  <div style="padding: 10px 0 0 10px">
    <el-input
      v-model="selectByName"
      class="w-50 m-2"
      placeholder="请输入名称"
      :prefix-icon="Search"
      style="width: 250px"
      @blur="selectByName == '' ? reset() : ''"
      @keydown.enter="selectSome()"
    />
    <el-button type="primary" @click="selectSome()" style="margin-left: 8px"
      >搜索</el-button
    >
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
    <el-table-column prop="avatarUrl" label="头像" width="100" >
      <template v-slot="scope">
        <el-avatar :size="40" :src="scope.row.avatarUrl+'?t='+new Date().getTime()" />
      </template>
    </el-table-column>
    <el-table-column prop="name" label="用户名" width="200" />
    <el-table-column prop="phone" label="电话" width="200" />
    <el-table-column prop="email" label="邮箱" width="200" />
    <el-table-column prop="createTime" label="创建时间" width="350" />
    <el-table-column label="操作">
      <template v-slot="scope">
        <el-button type="primary" size="small" @click="handleEdit(scope.row)"
          >编辑</el-button
        >
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
  <el-dialog v-model="dialogFormVisible" :title="fromTitle" style="width: 500px;" @close="formCancel">
    <el-form :model="form">
      <el-form-item label="用户名：" :label-width="formLabelWidth">
        <el-input v-model="form.name" class="formWidth" />
      </el-form-item>
      <el-form-item label="电话：" :label-width="formLabelWidth">
        <el-input v-model="form.phone" class="formWidth"/>
      </el-form-item>
      <el-form-item label="邮箱：" :label-width="formLabelWidth">
        <el-input v-model="form.email" class="formWidth"/>
      </el-form-item>
      
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="formCancel()">取消</el-button>
        <el-button type="primary" @click="save()">
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
  import { ref,onBeforeMount, reactive} from "vue";
  import { ElMessage, ElTable } from 'element-plus'
  import {
    Menu as IconMenu,
    Message,
    Setting,
    Search,
    InfoFilled
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

  // 状态开关只读
  const statusValue = ref(true)

  // 表单
  const dialogFormVisible = ref(false)
  const formLabelWidth = '100px'

  let form = reactive({
    id: 0,
    name: '',
    phone: '',
    email: '',
    admin: false
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
    axios.get("/user/page", {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        name: selectByName.value,
        isAdmin: form.admin
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
    load()
  }

  // 重置
  function reset(){
    selectByName.value = ""
    load()
  }

  // 新增
  function handleAdd(){
    fromTitle.value = "新增用户"
    dialogFormVisible.value = true
    form.id = 0
    form.email = ""
    form.name = ""
    form.phone = ""
  }

  // 保存
  function save(){
    form.name=form.name.trim();
    form.phone=form.phone.trim();
    form.email=form.email.trim();
    if (!form.name){
      ElMessage.error('用户名不能为空！')
      return;
    }else {
      if(form.name.length<4||form.name.length>14){
        ElMessage.error("用户名长度必须要在[4~14]位之间!")
        return;
      }
    }

    if (!form.phone){
      ElMessage.error('电话不能为空！')
      return;
    }else {
      var mPattern = /^(?:\+?86)?1\d{10}$/; 
      if(!mPattern.test(form.phone)){
        ElMessage.error('非法的电话格式!')
        return;
      }
    }

    if(form.email.length != 0){
      var ePattern = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/
      if(!ePattern.test(form.email)){
        ElMessage.error('非法的邮箱格式!')
        return;
      }
    }

    const params = { isAdmin: form.admin }

    if(fromTitle.value === "新增用户"){
      form.id = -1
      axios.post("/user/update", form, {params}).then(response => {
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
    }else if(fromTitle.value === "编辑用户"){
      axios.post("/user/update", form, {params}).then(response => {
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
          }
        }else {
          ElMessage.error("修改失败")
          return;
        }
      })
    }
    
    load()
  }

  // 编辑
  function handleEdit(row){
    form = row
    fromTitle.value = "编辑用户"
    dialogFormVisible.value = true
  }

  // 删除用户
  function handleDel(row){
    axios.post("/user/del", row).then(response => {
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
    axios.post("/user/del/batch", ids).then(response => {
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
</style>
