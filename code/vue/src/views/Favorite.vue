<template>
    <div style="background-color: #f9f7f7;">
      <Header class="header"></Header>
      <div class="banner"></div> 
      
      <div class="all" v-show="!v">
        <el-skeleton :rows="50"/>
      </div>
      <div class="all" v-show="v">
        <div class="main">
          <div class="cc">
            我的收藏
          </div>

          <div class="divider"></div>

          <div class="ccc">
            <div class="card" v-for="(data,index) in dataList" :key="index">
              <div class="img" @mousemove="m1(index)" @mouseleave="m2(index)" @click="intoBook(data.id)">
                <el-image style="width: 180px; height: 240px" :src="data.imgUrl+'?t='+new Date().getTime()" fit="cover" />
              </div>
              <div class="bookname">{{ data.name }}</div>
              <div class="cancelLike" @click="cancelLike(index)">取消收藏</div>
            </div>
          </div>
  
  
          <div class="demo-pagination-block" v-if="total !== 0">
            <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            />
          </div>
  
          <div v-else>
            <el-empty :image-size="200" />
          </div>
        </div>
  
        <!-- <div class="aside">
          <CardSix class="cardTwo"></CardSix>
        </div> -->
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
  import { onBeforeMount,ref,reactive } from "vue";
  import Header from '../components/Header.vue';
  import Footer from '../components/Footer.vue';
  import CardSix from "../components/CardSix.vue";
  import router from "../router";
  import axios from "axios";
  import { ElMessage } from "element-plus";

  const keyword = ref('')

  const pageNum = ref(1)
  const pageSize = ref(10)
  const total = ref(100)
  const handleSizeChange = (val: number) => {
    pageSize.value = val;
    load()
  };
  const handleCurrentChange = (val: number) => {
    pageNum.value = val;
    console.log(pageNum.value);
    v.value = false
    load();
  }

  let dataList = reactive([
    {
      id: 0,
      name : '',
      imgUrl : ''
    }
  ])

  const userId = ref()

  const v = ref(false)

  function m1(index){
    document.getElementsByClassName('bookname')[index].setAttribute("style","color:#fc6b0a")
  }

  function m2(index){
    var el = document.getElementsByClassName('bookname')
    for(var i=0;i<el.length;i++){
      el[i].setAttribute("style","color:#000")
    }
  }

  function getBook(){
    axios.get("/book/likebook",{
      params : {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        userId : userId.value
      }
    }).then(response => {
      console.log(response);
      dataList = response.data.data
      total.value = response.data.total
      setTimeout(()=>{
        console.log('fufu');
        v.value = true
      },200)
    })
  }

  function load(){
    v.value=false
    const token = localStorage.getItem('token')
    axios.get("/user/userInfo").then(response => {
      const data = response.data.data
      if(response.data.result){
        if(data.flag){
          userId.value = data.data.id
          getBook();
        }else {
          ElMessage.error(data.message)
          return;
        }
      }
    })
  }

  onBeforeMount(() => {
    document.title = '收藏-小说会'
    load();
  })

  function cancelLike(index){
    var is = confirm("确认要取消收藏?")
    if(is){
      var data = dataList[index]
      axios.get("/book/cancelLike",{params : {bid: data.id,uid: userId.value}}).then(response => {
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
        }
      })
    }
  }

  function intoBook(id){
    window.open('../book?id='+id);
  }
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
    width: 1224px;
  }
  .all{
    width: 1224px;
    margin: 20px 140px 0 140px;
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

  .cc{
    width: 672px;
    height: 20px;
    line-height: 20px;
    /* color: #0db1ed; */
    font-size: 18px;
    font-weight: bold;
    color: #696969;
  }

  .divider {
    height: 1px;
    margin-top: 10px;
    width: 300px;
    background-color: #dcdfe6;
  }

  .card{
    width: 201px;
    height: 300px;
    /* background-color: pink; */
    float: left;
    margin-right: 40px;
    margin-bottom: 50px;
    cursor: pointer;
  }

  .ccc{
    margin-top: 20px;
  }

  .ccc:after{
    content: '';
    display: block;
    clear: both;
  }

  .img:hover{
    transform: scale(1.1);
  }

  .img{
    width: 190px;
    height: 250px;
    padding-left: 10px;
    padding-top: 10px;
    background-color: #fff;
    border: #ccc solid 1px;
    box-shadow: 0 2px 10px #696969;
  }

  .bookname{
    height: 30px;
    line-height: 30px;
    font-size: 16px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-top: 10px;
    /* background-color: orange; */
  }
  .cancelLike{
    color: #1497d8;
    text-align: center;
  }
  .cancelLike:hover{
    color:#006496;
    text-decoration: underline;
  }
</style>