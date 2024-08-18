<template>
    <div class="all" v-show="!show">
        <el-skeleton :rows="50"/>
    </div>
    <div class="all" v-show="show">
        <div class="main">
            <div class="one">
                <CardOne class="cardOne" :bookId="bookData.mainBookList[0]" @click="intoBook(bookData.mainBookList[0])" ></CardOne>
                <CardOne class="cardOne" :bookId="bookData.mainBookList[1]" @click="intoBook(bookData.mainBookList[1])" style="margin-left: 20px;"></CardOne>
                <CardOne class="cardOne" :bookId="bookData.mainBookList[2]" @click="intoBook(bookData.mainBookList[2])" style="margin-top: 20px;"></CardOne>
                <CardOne class="cardOne" :bookId="bookData.mainBookList[3]" @click="intoBook(bookData.mainBookList[3])" style="margin-left: 20px;margin-top: 20px;"></CardOne>
            </div>
            <div class="two">
                <div style="position: relative;">
                    <CardThree :bookList="bookData.typeBookList[0]" class="cardThree"></CardThree>
                    <CardThree :bookList="bookData.typeBookList[1]" class="cardThree" style="left: 315px;"></CardThree>
                    <CardThree :bookList="bookData.typeBookList[2]" class="cardThree"  style="left: 630px;"></CardThree>
                    <CardThree :bookList="bookData.typeBookList[3]" class="cardThree cardThree-second"></CardThree>
                    <CardThree :bookList="bookData.typeBookList[4]" class="cardThree cardThree-second" style="left: 315px;"></CardThree>
                    <CardThree :bookList="bookData.typeBookList[5]" class="cardThree cardThree-second"  style="left: 630px;"></CardThree>
                </div>
            </div>
            <div class="three">
                <CardFour :bookList="bookData.newBookList"></CardFour>
            </div>
        </div>

        <div class="aside">
            <CardTwo :type="'热门小说'" :bookList="bookData.popularBookList" class="cardTwo"></CardTwo>
            <CardTwo :type="'广受好评'" :bookList="bookData.mostGradeBookList" class="cardTwo"></CardTwo>
            <CardTwo :type="'最多阅读'" :bookList="bookData.mostReadBookList" class="cardTwo"></CardTwo>
            <CardTwo :type="'最多收藏'" :bookList="bookData.mostLikeBookList" class="cardTwo"></CardTwo>
        </div>

        <div class="footer">
            <span style="color: #696969;font-weight: bold;">友情链接</span>
            <span class="link">
                <!-- <a href="https://www.biqugen.net/" target="_blank">笔趣阁</a> -->
                <a href="https://www.jjwxc.net/fenzhan/noyq/" target="_blank">晋江文学城</a>
                <a href="https://www.qidian.com/" target="_blank">起点中文网</a>
            </span>
        </div>

    </div>
    
      
</template>

<script setup>
    import { onBeforeMount, onMounted, reactive, ref } from 'vue'
    import { createApp } from 'vue';
    import CardOne from './CardOne.vue' 
    import CardTwo from './cardtwo.vue'
    import CardThree from './CardThree.vue'
    import CardFour from './CardFour.vue';
    import axios from 'axios';

    let bookData = reactive({
        mainBookList : [],
        typeBookList : {},
        newBookList : [],
        popularBookList : [],
        mostGradeBookList : [],
        mostLikeBookList : [],
        mostReadBookList : [],
    })

    const show = ref(false)
    
    onBeforeMount(() => {
        console.log("父组件");
        axios.get("/book/getAll").then(response => {
            console.log(response);
            bookData = response.data
            // 定时器
            setTimeout(() => {
                show.value = true
            },200)
        })
    })

    onMounted(()=> {
        console.log("父组件2");
    })

    function intoBook(id){
        window.open('../book?id='+id);
    }

</script>

<style scoped>
    .all::after{
        content: '';
        display: block;
        clear: both
    }
    .aside {
        width: 280px;
        float: left;
        margin: 10px 0 0 20px;
    }
    .asid:after{
        content: '';
        display: block;
        clear: both;
    }
    .main {
        width: 924px;
        float: left;
    }
    .main:after{
        content: '';
        display: block;
        clear: both;
    }
    .one {
        width: 924px;
        height: 432px;
        margin-bottom: 20px;
    }
    .cardOne{
        float: left;       
    }
    
    .one::after {
        content: '';
        display: block;
        clear: both;
    }
    .cardTwo {
        margin-top: 20px;
    }
    .cardTwo:first-child {
        margin: 0;
    }
    .cardThree {
        height: 344px;
        width: 282px;
        margin-bottom: 20px;
        position: absolute;
    }
    .two {
        height: 710px;
        margin-bottom: 20px;
    }
    .cardThree-second {
        top: 366px 
    }
    .footer {
        width: 1204px;
        height: 60px;
        background-color: #fff;
        margin: 20px 0;
        padding: 10px 10px 0 10px;    
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        float: left;
    }
    .link {
        color: #696969;
        display: block;
        margin-top: 6px;
        font-size: 14px;
    }
    a {
        text-decoration: none;
        color: #696969;
        margin-right: 10px;
    }
    a:hover {
        color: #fc6b0a;
        cursor: pointer;
        text-decoration-line: underline;
    }
</style>