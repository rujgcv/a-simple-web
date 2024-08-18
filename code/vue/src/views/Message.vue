<template>
    <div class="main">
        <Header class="header" ref="headerRef"></Header>
        <!-- <div class="body" style="height:714px"></div> -->
        <div class="body" :style="'height:' + bh" >
            <div class="cc">我的邮箱 
                <span style="color:rgb(178, 178, 178)">(共{{ total }}条)</span>
            </div>
            <div class="divider"></div>
            <div class="card" v-for="data in dataList" > 
                <div class="title">
                    <div class="title_time">
                        [{{ data.createTime }}]
                    </div>
                    <div class="title_t">
                        {{ data.title }}
                    </div>
                </div>
                <div class="message" v-html="data.message">
                </div>
                <div class="m1" v-if="time(nowDate,data.createTime) != 0">{{ time(nowDate,data.createTime) }}天后过期</div>
                <div class="m1" style="color:brown" v-else>即将过期</div>
            </div>
        </div>

        <el-backtop :bottom="100">
            <div class="slide">
                <el-icon size="30px"><ArrowUp /></el-icon>
            </div>
        </el-backtop>
    </div>
</template>

<script setup>
    import { computed, onBeforeMount, onMounted, reactive, ref } from 'vue';
    import Header from '../components/Header.vue';
    import Person from '../components/person/Person.vue';
    import axios from 'axios';

    const headerRef = ref()

    const reloadMessage = () => {
        headerRef.value.hasNoReadMessage()
    }

    let dataList = reactive([])

    const id = ref()

    const total = ref(0)
    
    const bh = ref('714px') 

    const v = ref(false)

    let nowDate = computed(() => {
        var currentDate = new Date();
        var year = currentDate.getFullYear();
        var month = currentDate.getMonth()+1;
        var date = currentDate.getDate();
        var nowDate = year + "-" + month + "-" + date
        return nowDate
    })

    function resetBh(){
        if(total.value > 5){
            bh.value = ''
        }else{
            bh.value = '714px'
        }
    }

    function time(t1,t2){
        var new_date = new Date(t1.substring(0,10));
        var old_date = new Date(t2.substring(0,10));
        
        var difftime = (new_date - old_date)/1000/60/60/24; 
        return 30 - difftime
    }

    onBeforeMount(()=>{
        document.title = '个人邮箱 -小说会' 

        const token = localStorage.getItem('token')
        axios.get("/user/userInfo").then(response => {
            const data = response.data.data
            if(response.data.result){
                    if(data.flag){
                    id.value = data.data.id
                    getMessage();
                }else {
                    ElMessage.error(data.message)
                    return;
                }
            }  
    
        })
    })    

    function getMessage(){
        axios.get("/message/getAll", {params : { id : id.value }}).then(response => {
            console.log(response);
            reloadMessage();
            dataList = response.data.messages
            total.value = response.data.total
            setTimeout(() => {
                resetBh();
                v.value = true
            },20)
        })    
    }

</script>

<style scoped>
    .header{
        float: left;
        position: sticky;
        top: 0;
        z-index: 999;
    }::after{
        content: '';
        display: block;
        clear: both;
    }
    .main{
        height: auto;
        background-image: linear-gradient(to bottom,#7fd7ea,#FFFDE4);
        position: relative;
    }
    .body{
        width: 800px;
        /* height: 714px; */
        /* overflow: auto; */
        background-color: rgb(250, 250, 250);
        margin: 0 auto;
    }::after{
        content: '';
        display: block;
        clear: both;
    }
    .divider {
        height: 1px;
        margin-top: 10px;
        float: left;
        width: 300px;
        background-color: #bbbdc3;
    }
    .cc{
    width: 672px;
    height: 20px;
    line-height: 20px;
    /* color: #0db1ed; */
    float: left;
    font-size: 18px;
    font-weight: bold;
    color: #696969;
    margin: 20px 0 0 20px;
  }
  .card {
    margin-top: 20px;
    margin-left: 20px;
    float: left;
    width: 760px;
    /* height: 300px; */
    background-color: #fff;
    border-radius: 10px;
    border:#e3e3e3 solid 1px;
  }
  .card:last-child{
    margin-bottom: 20px;
  }
  .title{
    height: 24px;
    font-size: 20px;
    line-height: 24px;
    background-color: #d2eaf2;
    padding: 5px;
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    color: #1f1f1f;
  }::after{
    content: '';
    display: block;
    clear: both;
  }
  .title_time{
    width: 150px;
    float: left;
    font-size: 14px;
    color:#696969;
  }
  .title_t{
    width: 600px;
    float: left;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .message{
    padding: 5px;
    white-space: pre-wrap
  }

  .m1{
    padding-left: 5px;
    padding-bottom: 5px;
    color:#696969;
    font-size: 14px;
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
</style>