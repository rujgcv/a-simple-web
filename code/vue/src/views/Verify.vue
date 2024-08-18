<template>
    <el-container class="layout-container-demo" style="height: 100%">

        <el-container>
        <el-main>
            <router-view />
        </el-main>
        </el-container>
    </el-container>

</template>

<script lang="ts" setup>
import { ref,onBeforeMount, reactive} from "vue";
import axios from "axios";

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
  