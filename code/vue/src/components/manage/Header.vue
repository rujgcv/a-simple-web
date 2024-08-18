<template>
  <div style="float: left; font-size: 18px;">
    <el-icon
      style="cursor: pointer; padding-top: 20px; color: #fff;"
      @click="collapse"
    >
      <div v-show="!isCollapse"><Fold /></div>
      <div v-show="isCollapse"><Expand /></div>
    </el-icon>
  </div>

  <div class="toolbar" v-show="infoVisible">
    <div class="demo-basic--circle" style="float: left;">
      <div style="margin-right: 10px;">
        <el-avatar :size="30" :src="adminInfo.avatarUrl+'?t=' + new Date().getTime()" />
      </div>
    </div>
    <el-dropdown style="cursor: pointer;margin-right: 10px;">
      <div style="color: #fff;" v-show="infoVisible">{{ adminInfo.name }}</div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="person()">个人信息</el-dropdown-item>
          <el-dropdown-item @click="toIndex()">去首页</el-dropdown-item>
          <el-dropdown-item @click="exit()">退出</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <el-icon style="color: #fff"><ArrowDown /></el-icon>
  </div>
</template>

<script setup>
  import {
    Fold,
    Expand,
    ArrowDown
  } from "@element-plus/icons-vue";
  import { ElMessage } from "element-plus";
  import { toRefs, defineProps,onBeforeMount,reactive, onMounted,ref } from "vue";
  import axios from 'axios';
  import router from "../../router";

  const props = defineProps({
    collapse: Function,
    isCollapse: Boolean,
  });
  const { collapse } = toRefs(props);
  const { isCollapse } = toRefs(props);

  let adminInfo = reactive({
    id: 0,
    name: '',
    avatarUrl: ''
  })

  const infoVisible = ref(false)
  
  onBeforeMount(() => {
    const token = localStorage.getItem('token')
    console.log(token);
    axios.get("/user/userInfo").then(response => {
      const data = response.data.data
      if(response.data.result){
        if(data.flag){
          adminInfo = data.data
          setTimeout(() => {
            infoVisible.value = true
          },10)
        }else {
          ElMessage.error(data.message)
          return;
        }
      }  
    })
  })

  // 退出
  function exit(){
    localStorage.removeItem('token')
    router.push('/login')
  }

  // 去首页
  function toIndex(){
    router.push('/')
  }

  // 个人中心
  function person(){
    router.push('/system/person')
  }
</script>

<style scoped>
.toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
  margin-right: 40px;
}
</style>
