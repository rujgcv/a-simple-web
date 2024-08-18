<template>
  <el-container class="layout-container-demo" style="height: 100%">
    <el-container>
      <el-main class="main">
        <div class="admin" v-show="!infoVisible">
        </div>
        <div class="admin" v-show="infoVisible">
          <el-avatar :size="200" :src="adminInfo.avatarUrl" class="adminImg"/>
          <div class="adminName">{{ adminInfo.name}}</div>
          <div style="color: grey;font-size: 40px;margin-top: 10px;text-align: center;">欢迎回来,管理员</div>
        </div>
        <div class="information">
          <div class="item">
            <div class="name">小说总数:</div>
            <div class="content">{{bookCount}} <span class="smallFond">本</span></div>
          </div>
          <div class="item">
            <div class="name">用户总数:</div>
            <div class="content">{{userCount}} <span class="smallFond">位</span></div>
          </div>
          <div class="item">
            <div class="name">今日新增小说:</div>
            <div class="content">{{newBook}} <span class="smallFond">本</span></div>
          </div>
        </div>
        <div class="view">
          <div ref="echarts1" id="echarts1">
            <el-skeleton :rows="5" animated style="margin-top: 100px;"/>
          </div>
        </div>
      </el-main>
    </el-container>
  </el-container>

  
</template>

<script lang="ts" setup>
  import { ref,onBeforeMount, reactive, onMounted} from "vue";
  import axios from "axios";
  import Aside from "../components/manage/Aside.vue";
  import Header from "../components/manage/Header.vue";
  import * as echarts from 'echarts'
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

  let adminInfo = reactive({
    id : 0,
    name : '',
    avatarUrl : ''
  })

  // 管理员信息
  // const id = ref(0)
  // const adminUrl = ref()
  // const adminName = ref()

  const infoVisible = ref(false)

  // 统计信息
  const bookCount = ref(0)
  const userCount = ref(0)
  const newBook = ref(0)

  // 图表
  const echarts1 = ref()
  const data = ref()

  onBeforeMount(() => {
    document.title = '后台管理系统 -小说会'
    const token = localStorage.getItem('token')
    axios.get("/user/userInfo").then(response => {
      const data = response.data.data
      console.log(response,"===========================");
      if(response.data.result){
        if(data.flag){
          adminInfo = data.data
          setTimeout(() => {
            // console.log(adminInfo.name);
            load();
            infoVisible.value = true
          },100)
        }else {
          ElMessage.error(data.message)
          return;
        }
      }  
    })
  })

  function load(){
    axios.get("/home/getAll", {
      params: {id: adminInfo.id}
    }).then(response => {
        bookCount.value = response.data.bookSum
        userCount.value = response.data.userSum
        newBook.value = response.data.bookTodaySum
        data.value = response.data.chartData
      })
  }

  onMounted(() => {
    setTimeout(() => {
      // 初始化表格
      var mychart = echarts.init(document.getElementById("echarts1") as HTMLElement)

      let option = {
        title: {
            text: '小说类型',
            left: 'center'
        },
        series : [
          {
            name: '小说种类',
            type: 'pie',    // 设置图表类型为饼图
            radius: '55%',  // 饼图的半径，外半径为可视区尺寸（容器高宽中较小一项）的 55% 长度。
            data: data.value
          }
        ],
        label:{
          formatter:function(data){
            return `${data.name} ${data.value}`
          }
        }  
      }
      mychart.setOption(option)
    },500)
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

  .main{
    background-color: rgb(234, 232, 233);
  }

  .admin{
    float: left;
    width: 500px;
    height: 630px;
    margin: 10px;
    margin-left: 40px;
    border: 0.3px solid rgb(238, 238, 238);
    background-color: rgb(248, 248, 248);;
    border-radius: 16px;
  }

  .adminImg{
    margin-left: 150px;
    margin-top: 50px;
  }

  .adminName{
    font-size: 50px;
    margin-top: 30px;
    text-align: center;
    font-weight: bold;
    color: #1cb1f6;
  }

  .information{
    float: left;
    margin-top: 10px;
    margin-left: 30px;
    width: 700px;
    height: 200px;
    border: 0.3px solid rgb(238, 238, 238);
    background-color: rgb(248, 248, 248);;
    border-radius: 16px;
  }

  .information .item{
    float: left;
    margin-left: 26px;
    width: 200px;
    height: 200px;
    /* background-color: pink; */
  }
  .information .item .name{
    margin-left: 10px;
    margin-top: 20px;
    font-size: 16px;
  }
  .information .item .content{
    height: 140px;
    line-height: 140px;
    font-size: 40px;
    text-align: center;
    color: rgb(4, 84, 127);
  }
  .view{
    float: left;
    margin-top: 20px;
    margin-left: 30px;
    width: 700px;
    height: 408px;
    border: 0.3px solid rgb(238, 238, 238);
    background-color: rgb(248, 248, 248);;
    border-radius: 16px;
  }

  #echarts1{
    margin-left: 100px;
    width: 500px;
    height: 400px;
  }

  .smallFond{
    font-size: 14px;
    font-weight: bold;
    color: #494949
  }
</style>