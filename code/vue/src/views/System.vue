<template>
  <el-container class="layout-container-demo" style="height: 100%">
    <el-aside
      :width="sideWidth + 'px'"
      :style="{ backgroundColor: sideBgColor }"
    >
      <el-scrollbar>
        <Aside :isCollapse="isCollapse" ></Aside>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <Header :isCollapse="isCollapse" :collapse="collapse"></Header>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  
</template>

<script lang="ts" setup>
  import { ref,onBeforeMount, reactive} from "vue";
  import axios from "axios";
  import Aside from "../components/manage/Aside.vue";
  import Header from "../components/manage/Header.vue";
  import { ElMessage } from "element-plus";

  // 是否折叠
  const isCollapse = ref(false);

  // 侧边栏
  const sideWidth = ref(200);
  const sideBgColor = ref("#3f6e98");

  // 点击收缩按钮触发
  function collapse() {
    isCollapse.value = !isCollapse.value;
    if (isCollapse.value) {
      // 收缩
      sideWidth.value = 64;
      sideBgColor.value = "#355C7D";
    } else {
      // 展开
      sideWidth.value = 200;
      sideBgColor.value = "#3f6e98";
    }
  }

  // 禁用和开启账号(麻烦 不搞了 以后再说)
  // const beforeChange = () => {
  //   loading.value = true
  //   return new Promise((resolve) => {
  //     setTimeout(() => {
  //       loading.value = false
  //       ElMessage.success('Switch success')
  //       return resolve(true)
  //     }, 1000)
  //   })
  // }

  onBeforeMount(() => {
    document.title = '后台管理系统 -小说会'
  })

</script>

<style scoped>
.layout-container-demo .el-header {
  position: relative;
  background-color: #3f6e98;
  /* background-image: linear-gradient(to right,#005AA7,#E4E5E6); */
  color: var(--el-text-color-primary);
}
.layout-container-demo .el-aside {
  height: 100%;
  color: var(--el-text-color-primary);
  /* background: #243e55; */
}

.layout-container-demo .el-main {
    padding: 0;
  }

  .layout-container-demo .el-menu {
    border-right: none;
  }  
</style>
