<template>
    <div style="background-color: #f9f7f7;">
      <Header class="header"></Header>
      <div class="banner"></div> 
      
      <div class="all" v-show="!v">
        <el-skeleton :rows="50" />
      </div>
      <div class="all" v-show="v">
        <div class="book">
          <div class="main">
            <div class="img">
              <el-image class="bookImg" :src="data.imgUrl+'?t='+new Date().getTime()" fit="cover" />
            </div>
            <div class="card-one-context">
              <div class="title">
                <b class="name">{{data.name}}</b>
                <div style="color: #000;margin-top: 5px;font-size: 20px;">{{ data.writer }}</div>
                <div style="color: #696969;margin-top: 10px;">分类: {{ data.type }}</div>
                <div style="color: #696969;margin-top: 5px;" class="time">发布时间: {{ data.createTime.substring(0,10) }}</div>
              </div>
              
              <div class="context">
                <b style="color: #000;margin-bottom: 6px;display: block;">小说简介: </b>
                <div style="white-space: pre-wrap;font-size: 14px;">{{ data.description }}</div>
              </div>
            </div>
            
          </div>

          <div class="aside">
            <div class="grade">
              <h3>评分:</h3>
              <div class="gradeNum">
                <el-rate
                  v-model="grade"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                  size="large"
                />
              </div>
            </div>
            <div style="color: #696969;text-align: center;margin-top: 10px;">已有 {{ totalGrade }} 人评分</div>
            <div class="myGrade">
              <div>我的评分:</div>
              <div style="margin-top: 10px;text-align: center;"><el-rate v-model="userInfo.grade" size="large" @change="userViewUpdate()"/></div>
            </div>
            <div class="o">
              <div class="select" @click="handleRead()" v-show="!userInfo.read"><el-icon size="22"><View /></el-icon> 看过</div>
              <div class="select" v-show="userInfo.read"><el-icon size="22" color="rgb(255, 157, 0)"><View /></el-icon> 已看过</div>
              <div class="select" @click="handleLike()" v-show="!userInfo.like"><el-icon size="22"><Star /></el-icon> 收藏本书</div>
              <div class="select" v-show="userInfo.like"><el-icon size="22" color="rgb(255, 157, 0)"><Star /></el-icon> 已收藏</div>
            </div>
          </div>

          <div class="besides">
            <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
              <el-tab-pane label="评论区" name="Review"></el-tab-pane>
              <!-- <el-tab-pane label="评分" name="Grade"></el-tab-pane> -->
              <el-tab-pane label="文件下载" name="DownLoad"></el-tab-pane>
            </el-tabs>
            <Review v-if="activeName === 'Review'"  :bookId="bookId"></Review>
            <bookUpload v-if="activeName === 'DownLoad'" :bookId="bookId" :userId="userInfo.id"></bookUpload>
          </div>
        </div>
      </div>
  
      <el-backtop :bottom="100">
          <div class="slide">
              <el-icon size="30px"><ArrowUp /></el-icon>
          </div>
      </el-backtop>
  
      <Footer></Footer>
    </div>
  </template>
  
  <script setup lang="ts">
    import { onBeforeMount,ref,reactive, computed } from "vue";
    import Header from '../components/Header.vue';
    import Footer from '../components/Footer.vue';
    import router from "../router";
    import axios from "axios";
    import { ElMessage,TabsPaneContext } from "element-plus";
    import Review from '../components/bookDetail/Review.vue'
    import BookUpload from "../components/bookDetail/BookUpload.vue";

    const activeName = ref('Review')

    const handleClick = (tab: TabsPaneContext, event: Event) => {
      console.log(tab, event)
    }
    
    let data = reactive(
      {
        id: 0,
        name: '',
        writer: '',
        type: '',
        imgUrl: '',
        description:``,
        createTime: '',
        grade: 0,
        read: 0,
        like: 0,
      }
    )
  
    // const v = ref(false)
    const v = ref(true)

    const bookId = ref()
    
    const totalGrade = ref(4)
    // const totalLike = ref(12)

    const grade = ref()

    function handleRead(){
      userInfo.read = 1
      userViewUpdate();
    }

    function handleLike(){
      userInfo.like = 1
      userViewUpdate();
    }

    function userViewUpdate(){
      v.value = false
      axios.get("/book/updateUserView",{
        params : {
          bid : bookId.value,
          uid : userInfo.id,
          read : userInfo.read,
          like : userInfo.like,
          grade : userInfo.grade
        }
      }).then(response => {
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            ElMessage({
              message: data.message,
              type: 'success',
            })
            load()
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

    let userInfo = reactive({
      id: 0,
      name:'',
      avatarUrl: '',
      grade: 0,
      read: 0,
      like: 0,
    })

    function getDetail(){
      axios.get("/book/getMore",{
        params : {
          bid : bookId.value,
          uid : userInfo.id
        }
      }).then(response => {
        console.log(response);
        totalGrade.value = response.data.totalGrade
        userInfo.grade = response.data.userView.grade
        userInfo.read = response.data.userView.read
        userInfo.like = response.data.userView.like
        // getDetail();
        setTimeout(()=>{
          v.value = true
        },80)
      })
    }

    function getBook(){
      axios.get("/book/getBook",{
        params : {
          id : bookId.value
        }
      }).then(response => {
        console.log(response);
        data = response.data
        document.title = data.name + '-小说会'
        grade.value = data.grade.toFixed(2)
        getDetail();
      })
    }
  
    function load(){
      v.value=false
      // 获取用户信息
      const token = localStorage.getItem('token')
      axios.get("/user/userInfo").then(response => {
        const data = response.data.data
        if(response.data.result){
          if(data.flag){
            userInfo = data.data
            getBook()
          }else {
            ElMessage.error(data.message)
            return;
          }
        }
      })
    }
  
    onBeforeMount(() => {
      if(document.URL.split("?")[1] === undefined){
        window.location.href = '../'
      }
      bookId.value = document.URL.split("?")[1].split('=')[1]
      // 根据id获取小说信息
      load();
    })
  </script>
  
<style scoped>
  .banner {
    width: 100%;
    height: 300px;
    background-image: url("../assets/img/mf.jpg");
    /* background-size: 100% 200%; */
    background-repeat: no-repeat;
    background-position: -500px ;   
  }
  .header{
      float: left;
      position: sticky;
      top: 0;
      z-index: 999;
  }
  .main{
    float: left;
    width: 70%;
    height: auto;
    background-color: #fff;
    border: 1px solid #e4e7ed;
    box-shadow: 0 2px 10px #e6e6e6;
    border-radius: 10px;
    padding: 10px;
    /* display: flex;
    flex-wrap: wrap; */
    /* width: 1224px; */
  }
  .aside{
    width: 24%;
    height: 420px;
    margin-left: 2%;
    float: left;
    background-color: #fff;
    border: 1px solid #e4e7ed;
    box-shadow: 0 2px 10px #e6e6e6;
    border-radius: 10px;
    padding: 10px;
  }
  .book{
    height: 440px;
  }
  .all{
    width: 1224px;
    margin: 20px 170px 0 170px;
  }
  .all::after{
      content: '';
      display: block;
      clear: both;
  }
  
  .slide {
      height: 100%;
      width: 100%;
      background-color: #fff;
      border: 1px solid #cdcdcd;
      border-radius: 6px;
      /* box-shadow: var(--el-box-shadow-lighter); */
      text-align: center;
      color: #a8a8a8;
      font-weight: 800px;
      line-height: 53px;  
  }
  .slide:hover {
      background-color: #1497d8;
      color: #fff;
  }

  .demo-pagination-block {
    /* width: 672px; */
    height: 40px;
    margin-top: 10px;
    margin-bottom: 10px;
    line-height: 40px;
    padding-left: 450px;
    background-color: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
  }
  .bookImg{
    width: 190px; 
    height: 278.41px;
    border: 2px solid #d5d5d5;
    border-radius: 10px;  
  }
  .img{
    float: left;
    height: 440px;
  }
  .card-one-context{
    float: left;
    width: 74%;
    height: auto;
    margin-left: 20px;
  }::after{
    content: '';
    display: block;
    clear: both;
  }
  .name{
    font-size: 22px;
  }
  .context{
    margin-top: 20px;
    line-height: 23px;
    margin-bottom: 30px;
  }

  .grade{
    height: 30%;
    /* background-color: pink; */
  }
  .gradeNum{
    margin: 20px auto;
    margin-left: 15px;
  }
  /deep/ .el-rate__icon{
    font-size: 35px;
  }
  /deep/ .el-rate__text{
    margin-left: 20px;
    font-size: 20px;
  }
  /deep/ .el-tabs__item{
    font-size: large;
  }
  .o{
    height: 160px;
    margin-top: 30px;
    /* background-color: pink; */
  }
  .select{
    width: 100px;
    line-height: 50px;
    height: 50px;
    margin-top: 10px;
    /* background-color: olive; */
    margin-left: 20px;
  }
  .select:hover{
    color: rgb(0, 166, 255);
    cursor: pointer;
  }
  .besides{
    float: left;
    margin-top: 20px;
    width: 70%;
    background-color: #fff;
    border: 1px solid #e4e7ed;
    box-shadow: 0 2px 10px #e6e6e6;
    border-radius: 10px;
    padding: 10px;
    margin-bottom: 30px;
  }
  .demo-tabs > .el-tabs__content {
    padding: 32px;
    color: #6b778c;
    font-size: 32px;
    font-weight: 600;
  }
  .myGrade{
    width: 100%;
    margin-top: 30px;
    height: 70px;
    /* background-color: pink; */
  }
</style>