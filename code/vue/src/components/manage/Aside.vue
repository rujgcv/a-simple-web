<template>
  <el-menu
    :default-openeds="['1', '3']"
    style="overflow-x: hidden"
    background-color="#355C7D"
    text-color="#fff"
    active-text-color="#ffe100"
    :collapse-transition="false"
    :collapse="isCollapse"
    @open="handleOpen"
    @close="handleClose"
  >
    <div style="height: 60px; line-height: 60px; text-align: center">
      <img
        src="../../assets/img/icon.png"
        alt=""
        style="width: 20px; position: relative; top: 5px"
      />
      <b style="color: #fff; margin-left: 5px" v-show="!isCollapse"
        >后台管理系统</b
      >
    </div>
    <router-link to="/system/home" class="routerText">
      <el-sub-menu index="1" class="index">
        <template #title>
          <el-icon><HomeFilled /></el-icon>
          <span v-show="!isCollapse"> 首页 </span>
        </template>
      </el-sub-menu>
    </router-link>
    <el-sub-menu index="2">
      <template #title>
        <el-icon><icon-menu /></el-icon>
        <span>系统管理</span>
      </template>
      <router-link to="/system/manage/user">
        <el-menu-item index="2-1"
        ><el-icon> <User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      </router-link> 
      <router-link to="/system/manage/admin">
        <el-menu-item index="2-2"
        ><el-icon><Avatar /></el-icon>管理员管理</el-menu-item
      >
      </router-link>
      <router-link to="/system/manage/book">
        <el-menu-item index="2-3"
          ><el-icon><Collection /></el-icon>小说管理</el-menu-item
        >
      </router-link>
      <router-link to="/system/manage/review">
        <el-menu-item index="2-4"
          ><el-icon><Comment /></el-icon>评论管理</el-menu-item
        >
      </router-link>
      <router-link to="/system/manage/txt">
        <el-menu-item index="2-5"
          ><el-icon><Comment /></el-icon>TXT文件管理</el-menu-item
        >
      </router-link>
    </el-sub-menu>
    <el-sub-menu index="3">
      <template #title>
        <el-icon><Platform /></el-icon>
        <span>待审核</span>
      </template>
      <router-link to="/system/verify/books">
        <el-menu-item index="3-1">
        <el-icon><DocumentAdd /></el-icon>小说新增审核</el-menu-item>
      </router-link>
      <!-- <el-menu-item index="3-2"
        ><el-icon><Picture /></el-icon>用户头像审核</el-menu-item
      > -->
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import {
  Menu as IconMenu,
  Message,
  Setting,
  Search,
  InfoFilled,
} from "@element-plus/icons-vue";
import { toRefs, defineProps } from "vue";

const props = defineProps({
  isCollapse: Boolean,
});
//使用父组件传递过来的值
const { isCollapse } = toRefs(props);
</script>

<style>
.index .el-sub-menu__icon-arrow svg path {
  display: none;
}
a {
  color: #fff;
  text-decoration: none;
}
</style>
