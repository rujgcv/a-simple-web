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
          <span style="color:#ff9900">{{ keyword }}</span>的相关作品
        </div>

        <!-- 乱的一批 -->
        <div style="margin-top: 10px" v-for="data in dataList" :key="data.id">
          <div class="card-one">
            <el-image style="width: 110px; height: 145px" class="img" :src="data.imgUrl+'?t='+new Date().getTime()" fit="cover" @click="intoBook(data.id)" />
            <div class="card-one-context">
            <div class="title">
              <b class="name" @click="intoBook(data.id)">《
                <span v-if="data.nameList !== undefined" v-for="n in data.nameList">
                  <span v-if="n != data.nameList[data.nameList.length-1]">
                    {{ n }}<span style="color: #fc6b0a;">{{ keyword }}</span>
                  </span>
                  <span v-else>{{ n }}</span>
                </span>
                <span v-else>
                  {{ data.name }}
                </span>
                》</b>
                <el-divider direction="vertical" />
                <span style="color: #696969;" v-if="data.writerList !== undefined" v-for="w in data.writerList">
                  <span v-if="w != data.writerList[data.writerList.length-1]">
                    {{ w }}<span style="color: #fc6b0a;">{{ keyword }}</span>
                  </span>
                  <span v-else>{{ w }}</span>
                </span>
                <span style="color: #696969;" v-else>
                  {{ data.writer }}
                </span>
              <div style="display: inline-block;float: right;padding-right: 20px;">
                <span style="color:#ff9900">{{ data.grade.toFixed(2) }}</span>
              </div>
            </div>
              
            <div class="context">
              <b style="color: #000;">小说简介: </b>
              {{ data.description }}
            </div>

            <span class="detail">
              <b>发布时间: {{ data.createTime }}</b>
            </span>
            </div>
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

      <div class="aside">
        <CardSix class="cardTwo"></CardSix>
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
  import { onBeforeMount,ref,reactive } from "vue";
  import Header from '../components/Header.vue';
  import Footer from '../components/Footer.vue';
  import CardSix from "../components/CardSix.vue";
  import router from "../router";
  import axios from "axios";

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
      imgUrl : '',
      writer : '',
      type : '',
      description : '',
      createTime : '',
      grade : 0.00,
      nameList: [''],
      writerList: ['']
    }
  ])

  const v = ref(false)

  function load(){
    v.value=false
    axios.get("/book/findBook", {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        name: keyword.value,
        writer: keyword.value,
        order: "asc"
      }
    })
    .then((response) => {
      console.log(response);
      dataList = response.data.data
      total.value = response.data.total
      setTimeout(()=>{
        for(var i=0;i<dataList.length;i++){
          var data = dataList[i]
          if(data.name.indexOf(keyword.value) !== -1){
            data.nameList = data.name.split(keyword.value)
          }
          if(data.writer.indexOf(keyword.value) !== -1){
            data.writerList = data.writer.split(keyword.value)
          }
        }

        v.value = true
      },200)
    })  
    window.scrollTo({
      top: 0, //回到顶部
      left: 0,
      behavior: "smooth",
    });
  }

  onBeforeMount(() => {
    if(document.URL.split("?")[1] === undefined){
      window.location.href = '../'
    }
    keyword.value = document.URL.split("?")[1].split('=')[1]
    keyword.value = decodeURIComponent(keyword.value)
    document.title = keyword.value + '-搜索-小说会'
    load();
  })

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
    width: 924px;
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
  .aside{
    height: 440px;
    width: 280px;
    float: left;
    margin: 10px 0 0 20px;
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
    width: 672px;
    height: 40px;
    margin-top: 10px;
    margin-bottom: 10px;
    line-height: 40px;
    padding-left: 250px;
    background-color: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
  }

  .cc{
    width: 672px;
    height: 20px;
    line-height: 20px;
    color: #696969;
  }

  .card-one {
    width: 882px;
    height: 144px;
    background-color: #fff;
    position: relative;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    padding: 20px;
  }

  .card-one-context {
    width: 750px;
    height: 144px;
    display: inline-block;
    position: absolute;
    left: 150px;
  }

  .title {
    color: #000;
  }

  .title:after {
    content: '';
    display: block;
    clear: both;
  }

  .context {
    width: 750px;
    height: 84px;
    margin: 20px 0 2px 0;
    font-size: 13px;
    color: #5b5b5b;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .detail {
    font-size: 12px;
    color: rgb(255, 111, 0)
  }

  .name:hover {
    color: #fc6b0a;
    cursor: pointer;
    text-decoration-line: underline;
  }

  .img:hover{
    cursor: pointer;
  }

  /* .cardTwo .box-card .header{
    width: 300px;
    height: 30px;
    margin-bottom: 10px;
    display: block;
  } */
</style>